package cn.weng.rabbitmq.receiver;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息接收者2
 * 只接受核心错误(error)
 */
public class Receiver2 {
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

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        // 声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        String severity = "kern.error";//只关注核心错误级别的日志，然后记录到文件中去。
        channel.queueBind(queueName, EXCHANGE_NAME, severity);

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
