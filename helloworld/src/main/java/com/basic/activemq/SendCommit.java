package com.basic.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * locate com.basic.activemq.helloworld
 * Created by mastertj on 2018/5/14.
 */
public class SendCommit {
    public static void main(String[] args) throws JMSException {
        //第一步：建立ConnectionFactory工厂对象，需要填入用户名、密码、以及连接的地址，均使用默认即可，默认端口为：“tcp://localhost:61616”
        ActiveMQConnectionFactory connectionFactory=new ActiveMQConnectionFactory(
                ActiveMQConnectionFactory.DEFAULT_USER,
                ActiveMQConnectionFactory.DEFAULT_PASSWORD,
                "tcp://ubuntu2:61616"
        );

        //第二步：通过ConnectionFactory工厂对象创建一个Connection对象，并且调用Connection的start方法开启连接，Connection默认是关闭的
        Connection connection = connectionFactory.createConnection();
        connection.start();

        //第三步：通过Connection创建Session会话(上下文环境)，用于接受消息，参数配置为TRUE 开启事务，参数配置为1为签收模式，一般我们设置为自动签收
        Session session=connection.createSession(Boolean.TRUE,Session.AUTO_ACKNOWLEDGE);

        //第四步：通过Session创建Destination对象，指的是一个客户端用来指定生产消息目标和消费消息的来源,再PTP模式中，Destination被称为Queue即队列
        Destination destination = session.createQueue("first");

        //第五步：我们通过session创建消息的发送和接受对象（生产者和消费者） MessageProducer/MessageConsumer
        MessageProducer producer=session.createProducer(null);

        //第六步：我们可以使用MessageProducer 的setDelivereyMode()方法将其持久化特性和非持久化特性（DelivereyMode）
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        //第七步：最后我们使用JMS的规范的TextMessage形式创建数据(通过Session对象)，并且通过MessageProducer的send方法发送数据，同理客户端使用receive接受数据
        for(int i=0;i<100;i++){
            TextMessage message=session.createTextMessage("我是消息内容。。。。"+i);
            //第一个参数目标地址
            //第二个参数具体的发送数据消息
            //第三个参数传送数据的优先级
            //第四个参数 优先级
            //第五个参数 消息的过期时间
            producer.send(destination,message);
        }

        //提交事务
        session.commit();

        for(int i=0;i<5;i++){
            MapMessage mapMessage=session.createMapMessage();
            mapMessage.setString("name","谭杰1");
            mapMessage.setInt("age",20+i);
            mapMessage.setString("addresss","武汉");
            producer.send(destination,mapMessage,DeliveryMode.NON_PERSISTENT,0,1000*1000L);
        }

        //提交事务
        session.commit();

        if(connection!=null){
            connection.close();
        }
    }
}
