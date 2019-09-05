package cn.weng.rabbitmq.receiver;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息接收者1
 * 接收所有授权日志、所有info、warning级别的日志
 */
public class Receiver1 {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] argv) throws IOException, InterruptedException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(AMQP.PROTOCOL.PORT);
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/admin_host");


        // 创建一个连接
        Connection connection = factory.newConnection();
        // 创建一个频道
        Channel channel = connection.createChannel();

        //声明exchange
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        // 声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();

        //关注所有的授权日志、所有info和waring级别的日志
        String[] routingKeys = {"auth.*", "*.info", "#.warning"};
        for (String routingKey : routingKeys) {
            //关注所有级别的日志（多重绑定）
            channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
        }
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 创建队列消费者
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received [" + envelope.getRoutingKey() + "] :'" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
