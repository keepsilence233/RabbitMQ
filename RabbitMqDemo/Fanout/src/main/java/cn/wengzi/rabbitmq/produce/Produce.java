package cn.wengzi.rabbitmq.produce;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/***
 * 消息生产者
 */
public class Produce {
    private final static String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂对象
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");           //主机地址,默认 localhost
        factory.setPort(AMQP.PROTOCOL.PORT);    //服务端口,默认 5672
        factory.setUsername("admin");           //用户名,默认 guest
        factory.setPassword("admin");           //密码,默认 guest
        factory.setVirtualHost("/admin_host");  //虚拟主机,默认 /

        // 创建一个连接
        Connection connection = factory.newConnection();
        // 创建一个频道
        Channel channel = connection.createChannel();

        // 指定转发——广播 转换器类型为 Fanout(扇形广播)
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        for (int i = 0; i < 10; i++) {
            /**
             * 发送的消息
             * 广播类型不需要routingKey，交换机会将所有的消息都发送到每个绑定的队列中去。
             */
            String message = "Hello RabbitMQ " + i;
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
            System.out.println("Producer Produce a message:" + message);
        }

        // 关闭频道和连接
        channel.close();
        connection.close();
    }
}
