package com.basic.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * locate com.basic.activemq.pb
 * Created by mastertj on 2018/5/14.
 */
public class Publish {
    //单列模式
    private static Publish instance=null;

    private static synchronized Publish getInstance(){
        if(instance==null){
            instance=new Publish();
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

    public Publish() {
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

    public void sendMessage(String topic){
        try {
            Destination destination = session.createTopic(topic);
            TextMessage textMessage= session.createTextMessage("我是内容");
            messageProducer.send(destination,textMessage);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Publish publish=Publish.getInstance();
        publish.sendMessage("PublishSubscribe");
        publish.close();
    }
}
