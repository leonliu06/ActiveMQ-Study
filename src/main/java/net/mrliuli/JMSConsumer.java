package net.mrliuli;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by li.liu on 2018/3/23.
 */
public class JMSConsumer {

    public static void main(String[] args) {

        // 连接工厂
        ConnectionFactory connectionFactory;

        // 连接
        Connection connection = null;

        // 会话 接受或者发送消息的线程
        Session session;

        // 消息的目的地
        Destination destination;

        // 消息的消费者
        MessageConsumer messageConsumer;

        // 实例化连接工厂
        connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, ActiveMQConnection.DEFAULT_BROKER_URL);

        try {

            // 通过连接工厂获取连接
            connection = connectionFactory.createConnection();

            // 启动连接
            connection.start();

            // 创建session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 创建一个连接HelloWorld的消息队列
            destination = session.createQueue("HelloWorld");

            // 创建消息消费者
            /**
             * 创建一个指定目的地的 ActiveMQMessageConsumer，用于接收目的地发布的消息。
             * 如果目的地是一个 Topic，该方法可以指定它的连接是否应当传递它发布的消息。
             *
             * 参数 destination：要访问的目的地（Queue 或 Topic）
             * 参数 messageSelector：只有匹配消息选择器表达式的消息才被传递。null 或空字符串表示消息费者没有消息选择器。
             * 参数 noLocal：如果是真，且目的地是 Topic，则禁止消息传递。如果目的地是 Queue，NoLocal 的行为不指定。
             * 参数 messageListener：用于异步消费消息的监听器
             */
            messageConsumer = session.createConsumer(destination);

            while (true) {
                /**
                 * 接收在指定的超时间隔内到达的下一个消息 ActiveMQMessage。
                 * 该方法调用时会阻塞，直到接收到消息、超时或该消息消费者关闭。
                 * 参数 long timeout：超时时间（毫秒）。为 0 表示永不超时。
                 * 返回：为消息消费者生成的一个消息，如果超时或者该消息消费者同时关闭，则返回 null。
                 */
                TextMessage textMessage = (TextMessage) messageConsumer.receive(5000);
                if(textMessage != null){
                    System.out.println("收到的消息:" + textMessage.getText());
                }else {
                    System.out.println("break");
                    break;
                }
            }

        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try{
                if(connection != null){
                    connection.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
}
