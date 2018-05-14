package com.basic.activemq.p2p;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * locate com.basic.activemq.p2p
 * Created by mastertj on 2018/5/14.
 * 生产者
 */
public class Producer {
    //单列模式
    private static Producer instance=null;

    private static synchronized Producer getInstance(){
        if(instance==null){
            instance=new Producer();
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

    public Producer() {
        try {
            this.connectionFactory=new ActiveMQConnectionFactory(
                    ActiveMQConnectionFactory.DEFAULT_USER,
                    ActiveMQConnectionFactory.DEFAULT_PASSWORD,
                    "tcp://ubuntu2:61616"
            );
            this.connection=connectionFactory.createConnection();
            connection.start();
            this.session=connection.createSession(Boolean.FALSE,Session.AUTO_ACKNOWLEDGE);
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

    public void send1(String queueName){
        try {
            for(int i=0;i<100;i++){
                Destination destination = session.createQueue(queueName);
                MapMessage mapMessage=session.createMapMessage();
                mapMessage.setString("name","谭杰"+i);
                mapMessage.setInt("age",23+i);
                mapMessage.setString("addresss","武汉");
                this.messageProducer.send(destination,mapMessage);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Producer producer = Producer.getInstance();
        producer.send1("students");
        producer.close();
    }
}
