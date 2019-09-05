package cn.weng.rabbitmq.produce;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息生产者
 * 每个设备,每个级别发送一条消息
 */
public class Produce {
    private final static String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
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
        // 指定转发——广播
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        //所有设备和日志级别
        String[] facilities = {"auth", "cron", "kern", "auth.A"};
        String[] severities = {"error", "info", "warning"};

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                //每一个设备，每种日志级别发送一条日志消息
                //System.out.println(severities[j % 3]);
                String routingKey = facilities[i] + "." + severities[j % 3];

                // 发送的消息
                String message = " Hello World!" + (i + 1);
                //参数1：exchange name
                //参数2：routing key
                channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
                System.out.println(" [x] Sent [" + routingKey + "] : '" + message + "'");
            }
        }
        // 关闭频道和连接
        channel.close();
        connection.close();
    }
}
