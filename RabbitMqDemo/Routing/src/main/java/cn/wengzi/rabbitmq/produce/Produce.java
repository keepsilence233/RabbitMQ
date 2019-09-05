package cn.wengzi.rabbitmq.produce;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息生产者
 * 生产者产生三种日志级别:{"error","info","warning"}
 */
public class Produce {
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

        //所有日志严重性级别
        String[] routingKeys={"error","info","warning"};
        for (int i = 0; i < 3; i++) {
            String routingKey = routingKeys[i % 3];//每一次发送一条不同严重性的日志

            // 发送的消息
            String message = "Hello World" + (i+1);
            //参数1：exchange name
            //参数2：routing key
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
            System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");
        }

        // 关闭频道和连接
        channel.close();
        connection.close();
    }
}
