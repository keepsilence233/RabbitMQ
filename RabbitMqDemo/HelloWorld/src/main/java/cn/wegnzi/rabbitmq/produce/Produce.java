package cn.wegnzi.rabbitmq.produce;

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
    //队列名称
    private static final String QUEUE_NAME = "HelloWorld";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂对象
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");           //主机地址,默认 localhost
        factory.setPort(AMQP.PROTOCOL.PORT);    //服务端口,默认 5672
        factory.setUsername("admin");           //用户名,默认 guest
        factory.setPassword("admin");           //密码,默认 guest
        factory.setVirtualHost("/admin_host");  //虚拟主机,默认 /


        //创建连接
        Connection connection = factory.newConnection();

        //创建频道
        Channel channel = connection.createChannel();

        /***
         * 声明队列
         * 1.队列名称
         * 2.消息是否持久化
         * 3.当前消息队列是否属于当前连接对象独有
         * 4.在消息使用完毕后,是否删除该消息
         * 5.添加附加参数
         */
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        //创建一个消息
        String message = "Hello RabbitMQ";

        /***
         * 发送消息
         * 1.消息发送的交换机对象,不写默认使用Default Exchange
         * 2.当前路由地址,简单消息模式 路由地址可以直接写成队列地址
         * 3.附加参数,可以不写
         * 4.要发送的消息
         */
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        System.out.println("Produce Produce a message:" + message);

        //关闭资源
        channel.close();
        connection.close();

    }
}
