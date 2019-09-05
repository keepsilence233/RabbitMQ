package cn.wengzi.rabbitmq.receiver;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者1
 * 消费者一关注所有日志级别:{"error","info","warning"};
 */
public class Receiver1 {
    private final static String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(AMQP.PROTOCOL.PORT);
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/admin_host");
        //创建一个连接
        Connection connection = factory.newConnection();
        // 创建一个频道
        Channel channel = connection.createChannel();
        // 指定转发——广播
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        //声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();

        //所有日志严重性级别
        String[] routingKeys={"error","info","warning"};
        for (String routingKey : routingKeys) {
            //关注所有级别的日志（多重绑定）
            channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
        }
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //创建队列消费者
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "utf-8");
                System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };

        //监听消息
        channel.basicConsume(queueName, true, consumer);

    }
}
