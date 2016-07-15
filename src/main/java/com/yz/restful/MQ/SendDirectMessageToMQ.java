package com.yz.restful.MQ;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yz.restful.Common.CommonConstants;
import com.yz.restful.HandlerInterface.MessageHandlerInterface;

public class SendDirectMessageToMQ implements MessageHandlerInterface {
	private static final Logger logger = Logger.getLogger(ConnectFactory.class);
	private Connection connection;
	private ConnectFactory factory;
	private List<Channel> channel;
    private String exchange;
    private volatile int flag=0;
	public SendDirectMessageToMQ() throws IOException, TimeoutException {
		factory=new ConnectFactory();
		channel=Collections.synchronizedList(new LinkedList<Channel>());
		connection = factory.getConnection();
		for(int i=0;i<50;i++){
		   channel.add(connection.createChannel());	
		}
		exchange = CommonConstants.COMMON;
		channel.get(0).exchangeDeclare(exchange,CommonConstants.DIRECT,true);
	}
	public synchronized boolean sendMessage(String message, String routing){
		try {
			channel.get(flag).basicPublish(exchange, routing, null, message.getBytes("UTF-8"));
			if(++flag>49){
				flag=0;
			}
		} catch (IOException e) {
			logger.error(e);
			return false;
		}
		return true;
	}
}
