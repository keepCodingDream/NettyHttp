package com.yz.restful.MQ;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yz.restful.Common.CommonConstants;
import com.yz.restful.Common.CommonUtil;
import com.yz.restful.DTO.DeviceStatusMessage;
import com.yz.restful.HandlerInterface.MessageHandlerInterface;

public class SendFountMessageToMQ implements MessageHandlerInterface {

	private static final Logger logger = Logger.getLogger(SendFountMessageToMQ.class);
	private Connection connection;
	private ConnectFactory factory;
	private Channel channel;
	public ConcurrentLinkedQueue<DeviceStatusMessage> queue = new ConcurrentLinkedQueue<DeviceStatusMessage>();

	public SendFountMessageToMQ() throws IOException, TimeoutException {
		factory = new ConnectFactory();
		connection = factory.getConnection();
		channel = connection.createChannel();
		channel.exchangeDeclare(CommonConstants.LOGS, CommonConstants.FANOUT, false);
		channel.exchangeDeclare(CommonConstants.DEVICESTATUS, CommonConstants.FANOUT, true);
		channel.exchangeDeclare(CommonConstants.EVENTRESOLVED, CommonConstants.FANOUT, true);
		channel.exchangeDeclare(CommonConstants.EVENTUPDATE, CommonConstants.FANOUT, false);
		for (int i = 0; i < 50; i++) {
			new Thread(new Worker()).start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yz.restful.HandlerInterface.MessageHandlerInterface#sendMessage(java
	 * .lang.String, java.lang.String) Exchange logs for all the events,
	 * exchange eventUpdate for jam events update Else for this new
	 * function,exchange name equals uri
	 */
	public boolean sendMessage(String message, String routing) throws Exception {
		try {
			String exchange = "";
			if (CommonConstants.EVENTS.equalsIgnoreCase(routing)) {
				exchange = CommonConstants.LOGS;
			} else if (CommonConstants.EVENTUPDATE.equalsIgnoreCase(routing)) {
				exchange = CommonConstants.EVENTUPDATE;
			} else if (CommonConstants.DEVICESTATUS.equalsIgnoreCase(routing)) {
				exchange = CommonConstants.DEVICESTATUS;
			} else if (CommonConstants.EVENTRESOLVED.equalsIgnoreCase(routing)) {
				exchange = CommonConstants.EVENTRESOLVED;
			}
			synchronized (queue) {
				DeviceStatusMessage deviceStatusMessage=new DeviceStatusMessage();
				deviceStatusMessage.setExchange(exchange);
				deviceStatusMessage.setMessage(message);
				queue.offer(deviceStatusMessage);
				queue.notify();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	private class Worker implements Runnable {
		private Channel channel_thread;
		public Worker() {
			try {
				channel_thread = connection.createChannel();
			} catch (IOException e) {
			}
		}
		@Override
		public void run() {
			while (true) {
				DeviceStatusMessage message=null;
				synchronized (queue) {
					if (!queue.isEmpty()) {
						message = queue.poll();
					}else {
						try {
							queue.wait();
						} catch (InterruptedException e) {
							logger.error(e);
						}
					}
				}
				if (message != null&&!CommonUtil.isEmptyString(message.getExchange())) {
					try {
						channel_thread.basicPublish(message.getExchange(), "", null, message.getMessage().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						logger.error(e);
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
		}

	}
}
