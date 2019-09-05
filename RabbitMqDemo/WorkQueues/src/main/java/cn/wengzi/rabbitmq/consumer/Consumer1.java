package cn.wengzi.rabbitmq.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 接收消息
 */
public class Consumer1 {
    //队列名称
    private static final String WORK_QUEUE = "HelloWorld";

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

        //声明队列
        channel.queueDeclare(WORK_QUEUE, true, false, false, null);
        System.out.println("Consumer Wating Receive Message");

        channel.basicQos(1);//保证一次只分发一个

        //创建消费者,并设置消息处理
        Consumer consumer = new DefaultConsumer(channel) {

            /***
             * 消息处理
             * @param consumerTag
             * @param envelope      队列信息封装
             * @param properties    额外参数
             * @param body          接收到的消息
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [C] Received 1 " + message + "'");
                boolean autoAck = false;
            }
        };


        /***
         * 消息监听
         * 1.队列名称
         * 2.消息消费应答模式
         * 3.读取消息,并处理
         */
        channel.basicConsume(WORK_QUEUE, false, consumer);

        //关闭资源(不建议关闭资源,建议一直监听消息)
    }
}
