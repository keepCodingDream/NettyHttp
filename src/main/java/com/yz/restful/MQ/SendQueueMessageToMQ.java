package com.yz.restful.MQ;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yz.restful.Common.CommonConstants;
import com.yz.restful.HandlerInterface.MessageHandlerInterface;

public class SendQueueMessageToMQ implements MessageHandlerInterface{
	private static final Logger logger = Logger.getLogger(SendQueueMessageToMQ.class);
	private Connection connection;
	private ConnectFactory factory;
	private Channel channel;
    private String queueName;
	public SendQueueMessageToMQ() throws IOException, TimeoutException {
		factory=new ConnectFactory();
		connection = factory.getConnection();
		channel = connection.createChannel();
		queueName = CommonConstants.LOGSBOBY;
		channel.queueDeclare(queueName, false, false, false, null);
	}
	public synchronized boolean sendMessage(String message, String routing) throws Exception {
		try {
			channel.basicPublish("", queueName, null,message.getBytes("UTF-8"));
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
	public void closeConnect(){
		try {
			channel.close();
			connection.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
			return;
		} catch (TimeoutException e) {
			logger.error(e.getMessage());
			return;
		}
	}
}
