package com.basic.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * locate com.basic.activemq.p2p
 * Created by mastertj on 2018/5/14.
 * 消费者
 */
public class BalanceConsumerB {
    //单列模式
    private static BalanceConsumerB instance=null;

    //选择器
    private final String SELECTOR_2="receiver = 'B'";

    private static synchronized BalanceConsumerB getInstance(){
        if(instance==null){
            instance=new BalanceConsumerB();
        }
        return instance;
    }

    // 1.连接工厂
    private ConnectionFactory connectionFactory;

    // 2.连接对象
    private Connection connection;

    // 3.Session 对象
    private Session session;

    // 4.生产者
    private MessageConsumer messageConsumer;

    //5.目标地址
    private Destination destination;

    public BalanceConsumerB() {
        try {
            this.connectionFactory=new ActiveMQConnectionFactory(
                    ActiveMQConnectionFactory.DEFAULT_USER,
                    ActiveMQConnectionFactory.DEFAULT_PASSWORD,
                    "tcp://ubuntu2:61616"
            );
            this.connection=connectionFactory.createConnection();
            connection.start();
            this.session=connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            this.destination=session.createQueue("balanceStudents");
            this.messageConsumer=session.createConsumer(destination,SELECTOR_2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 获取连接
     * @return
     */
    public Session getSession() {
        return session;
    }

    /**
     * 关闭连接
     */
    public void close(){
        try {
            if(connection!=null)
                connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void receiver() throws JMSException {
            messageConsumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    ExecutorService executorService= Executors.newCachedThreadPool();
                    executorService.execute(new MessageTask((MapMessage) message));
                }
            });
    }

    public static void main(String[] args) throws JMSException {
        BalanceConsumerB consumer= BalanceConsumerB.getInstance();
        consumer.receiver();
        System.out.println("-------------over-------------");
    }
}
