package com.basic.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.UUID;

/**
 * locate com.basic.activemq
 * Created by mastertj on 2018/5/15.
 */
public class Client {
    //单列模式
    private static Client instance=null;

    private static synchronized Client getInstance(){
        if(instance==null){
            instance=new Client();
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
    private MessageProducer messageProducer;

    //5.服务器响应消费者
    private MessageConsumer responseConsumer;

    //6.临时队列
    private Destination tempDest;

    public Client() {
        try {
            this.connectionFactory=new ActiveMQConnectionFactory(
                    ActiveMQConnectionFactory.DEFAULT_USER,
                    ActiveMQConnectionFactory.DEFAULT_PASSWORD,
                    "tcp://ubuntu2:61616"
            );
            this.connection=connectionFactory.createConnection();
            connection.start();
            this.session=connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            this.messageProducer=session.createProducer(null);

            this.tempDest= session.createTemporaryQueue();
            this.responseConsumer = session.createConsumer(tempDest);
            this.responseConsumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if(message instanceof TextMessage){
                        try {
                            TextMessage textMessage= (TextMessage) message;
                            System.out.println("收到来自服务器的消息 "+textMessage.getText());
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
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

    public void sendMessage(String queueName){
        try {
            Destination destination = session.createQueue(queueName);
            TextMessage textMessage=session.createTextMessage("hello Server!");

            //设置一个关联ID，所以当你得到一个响应时，你知道是哪个发送的消息的响应
            String correlationId = UUID.randomUUID().toString();
            textMessage.setJMSCorrelationID(correlationId);

            //为textMessage 设置JMS Replay Queue 以便服务器知道将消息回复发送给哪个对列
            textMessage.setJMSReplyTo(tempDest);
            this.messageProducer.send(destination,textMessage);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client=Client.getInstance();
        client.sendMessage("requestResponse");
    }
}
