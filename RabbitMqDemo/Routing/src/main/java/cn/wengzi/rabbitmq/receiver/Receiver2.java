package cn.wengzi.rabbitmq.receiver;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者2
 * 消费者2只关注error日志级别:"error"
 */
public class Receiver2 {
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

        // 声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();

        //只关注error级别的日志，然后记录到文件中去
        String routingKey = "error";
        channel.queueBind(queueName, EXCHANGE_NAME, routingKey);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

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
