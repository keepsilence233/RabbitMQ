package cn.wengzi.rabbitmq.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer1 {
    private final static String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂对象
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");           //主机地址,默认 localhost
        factory.setPort(AMQP.PROTOCOL.PORT);    //服务端口,默认 5672
        factory.setUsername("admin");           //用户名,默认 guest
        factory.setPassword("admin");           //密码,默认 guest
        factory.setVirtualHost("/admin_host");  //虚拟主机,默认 /

        // 打开连接和创建频道，与发送端一样
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        //转换器类型为 Fanout(扇形广播)
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        // 声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();
        //将转发器和随即队列绑定
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 创建队列消费者
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };

        channel.basicConsume(queueName, true, consumer);

        //关闭资源(不建议关闭资源,建议一直监听消息)
    }
}
