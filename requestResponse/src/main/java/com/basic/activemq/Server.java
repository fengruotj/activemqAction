package com.basic.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * locate com.basic.activemq
 * Created by mastertj on 2018/5/15.
 */
public class Server {
    //单列模式
    private static Server instance=null;

    //选择器
    private final String SELECTOR_2="age>50";

    private static synchronized Server getInstance(){
        if(instance==null){
            instance=new Server();
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

    // 6.生产者 (服务器将消息发往相应的客户端)
    private MessageProducer replyProducer ;

    public Server() {
        try {
            this.connectionFactory=new ActiveMQConnectionFactory(
                    ActiveMQConnectionFactory.DEFAULT_USER,
                    ActiveMQConnectionFactory.DEFAULT_PASSWORD,
                    "tcp://ubuntu2:61616"
            );
            this.connection=connectionFactory.createConnection();
            connection.start();
            this.session=connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            this.destination=session.createQueue("requestResponse");
            this.messageConsumer=session.createConsumer(destination);
            this.replyProducer=session.createProducer(null);
            this.replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
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
                try {
                    TextMessage responseMessage= session.createTextMessage();
                    if(message instanceof TextMessage){
                        TextMessage textMessage= (TextMessage) message;
                        System.out.println("收到来自客户端的消息 "+textMessage.getText());
                    }
                    responseMessage.setText("Hello! Client~~~");
                    responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());
                    replyProducer.send(message.getJMSReplyTo(),responseMessage);
                } catch (JMSException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static void main(String[] args) throws JMSException {
        Server server=Server.getInstance();
        server.receiver();
    }
}
