package net.mrliuli;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by li.liu on 2018/3/23.
 */
public class JMSProducer {

    private static final int SENDNUM = 10;

    public static void main(String[] args){

        // 连接工厂
        ConnectionFactory connectionFactory;

        // 连接
        Connection connection = null;

        // 会话 接受或者发送消息的线程
        Session session;

        // 消息的目的地
        /**
         * 可以是一个 Queue，也可以是一个 Topic，本例是一个 ActiveMQQueue
         */
        Destination destination;

        // 消息生产者
        MessageProducer messageProducer;

        // 实例化连接工厂
        /**
         * ConnectionFactory是一个受管对象，用于创建连接Connection。
         * ActiveMQConnectionFactory也实现了QueueConnectionFactory和TopicConnectionFactory。
         * 因此ActiveMQConnectionFactory可以创建QueueConnection和TopicConnection。
         */
        connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, ActiveMQConnection.DEFAULT_BROKER_URL);

        try{

            // 通过连接工厂获取连接
            /**
             * 返回一个 ActiveMQConnection，会抛出 JMSException 异常
             */
            connection = connectionFactory.createConnection();

            // 启动连接
            /**
             * 启动（或重启）该连接的消息传递。如果连接已经启动，再次调用start，将会忽略调用。
             * 如果由于某些内部错误使得启动消息传递失败，将会抛出 JMSException 异常。
             */
            connection.start();

            // 创建session
            /**
             * 创建一个 ActiveMQSession 对象。
             * 第一个参数 transacted ：指示会话是否是事务性的。如是，则忽略第二个参数。
             * 第二个参数 acknowledgedMode: 指示消费者或客户端是否告之它已接收到消息。如果会话是事务性的，则该参数会被忽略。
             */
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

            // 创建一个名称为HelloWorld的消息队列，队列名用来标识不同身份队列。
            /**
             * 如果队列名称以“ID:”开头，则返回一个 ActiveMQTempQueue 队列对象，否则返回一个 ActiveMQQueue 队列对象。
             * 该方法用于客户端需要动态控制队列身份的极少情况。它允许创建指定名称的队列。依赖于这一能力的客户端不够便携。
             * 注意该方法不用于物理队列的创建。物理队列的创建是管理任务，不是JMS API指示的。
             * 一个参数 queueName ：要创建队列的名称。
             */
            destination = session.createQueue("HelloWorld");

            // 创建消息生产者
            /**
             * 创建一个指定目的地的 ActiveMQMessageProducer，用于发送消息到指定的目的地（ActiveMQQueue）。
             * 客户端通过消息生产者 ActiveMQMessageProducer 对象来发送消息到目的地。
             *
             * 一个参数 destination：由于队列 Queue 和 主题 Topic 都是目的地 Destination 的实现，
             * 所以它们都可以作为该方法的参数用来创建一个消息生产者对象。
             */
            messageProducer = session.createProducer(destination);

            // 发送消息
            for(int i = 0; i < JMSProducer.SENDNUM; i++){
                // 创建一条文本消息
                /**
                 * 创建一个 ActiveMQTextMessage 对象。用于发送一条包含字符串的消息。
                 */
                TextMessage message = session.createTextMessage("ActiveMQ 发送消息" + i);
                System.out.println("发送消息：Activemq 发送消息" + i);

                // 通过消息生产者发出消息
                /**
                 * 发送消息到目的地，标明传递模式，优先级和存活时间。
                 * 通常，消息生产者在创建的时候会分配一个目的地，
                 * 然而，JMS API 也支持没有目的地的消息生产者，
                 * 这意味着，每当消息生产者需要发送消息时，都要提供一个目的地。
                 *
                 * 参数 destination：发送消息要去的目的地
                 * 参数 message：要发送的消息
                 * 参数 deliveryMode：使用的传递模式
                 * 参数 priority：消息优先级
                 * 参数 timeToLive：消息的存活时间（毫秒）
                 */
                messageProducer.send(message);
            }

            // 提交会话，否则消息不会发送到 destination
            /**
             * 提交该事务中所有完成的消息，并释放当前持有的一些锁。
             *  如果提交会话失败，抛出 JMSException异常
             *  如果提交过程中如果发生事务回滚，抛出 TransactionRolledBackException 异常
             *  如果该方法没有被事务性会话调用，抛出 javax.jms.IllegalStateException 异常
             */
            session.commit();

        }catch (JMSException e){
            e.printStackTrace();
        }finally {
            /**
             *
             */
            if(connection != null){
                try{
                    /**
                     * 关闭连接。
                     * JMS提供者通常会在JVM之外分配资源，所以客户端应在不需要提供者时关闭资源。
                     * 依赖GC最终回收这些资源不够及时。
                     * 不需要关闭一个关着的连接的会话，消息生产者和消息消费者。
                     * 关闭连接会使所有的临时队列删除。
                     *
                     * 关闭连接会使正在处理的会话事务回滚。
                     *
                     */
                    connection.close();
                }catch (JMSException e){
                    e.printStackTrace();
                }
            }
        }

    }

}
