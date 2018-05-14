package com.basic.activemq.helloworld;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * locate com.basic.activemq.helloworld
 * Created by mastertj on 2018/5/14.
 *
 */
public class ReceiverCommit {
    public static void main(String[] args) throws JMSException {
        ActiveMQConnectionFactory connectionFactory=new ActiveMQConnectionFactory(
                ActiveMQConnectionFactory.DEFAULT_USER,
                ActiveMQConnectionFactory.DEFAULT_PASSWORD,
                "tcp://ubuntu2:61616"
        );

        //第二步：通过ConnectionFactory工厂对象创建一个Connection对象，并且调用Connection的start方法开启连接，Connection默认是关闭的
        Connection connection = connectionFactory.createConnection();
        connection.start();

        //第三步：通过Connection创建Session会话(上下文环境)，用于接受消息，参数配置为0开启事务，参数配置为1为签收模式，一般我们设置为自动签收
        Session session=connection.createSession(Boolean.FALSE,Session.AUTO_ACKNOWLEDGE);

        //第四步：通过Session创建Destination对象，指的是一个客户端用来指定生产消息目标和消费消息的来源,再PTP模式中，Destination被称为Queue即队列
        Destination destination = session.createQueue("first");

        //第五步：创建消费者
        MessageConsumer consumer = session.createConsumer(destination);

        while (true){
            Message receive = consumer.receive();
            if(receive instanceof TextMessage){
                TextMessage textMessage= (TextMessage) receive;
                System.out.println("接受到数据："+textMessage.getText());
            }else if(receive instanceof MapMessage){
                MapMessage mapMessage= (MapMessage) receive;
                System.out.println("接受到数据："+mapMessage.toString());
            }
        }

    }
}
