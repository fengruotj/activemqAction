package com.basic.activemq.loadbalance;

import javax.jms.MapMessage;

/**
 * locate com.basic.activemq.action
 * Created by mastertj on 2018/5/14.
 */
public class MessageTask implements Runnable {
    private MapMessage mapMessage;

    public MessageTask(MapMessage message) {
        this.mapMessage = message;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500);
            System.out.println("当前线程是"+Thread.currentThread().getName()+" id: "+mapMessage.getInt("id")+"name: "+mapMessage.getString("name")+" age: "+mapMessage.getInt("age")+" address: "+mapMessage.getString("addresss"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
