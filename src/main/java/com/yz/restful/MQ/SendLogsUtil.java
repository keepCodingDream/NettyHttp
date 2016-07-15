package com.yz.restful.MQ;

import java.io.IOException;
import org.apache.log4j.Logger;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yz.restful.Common.CommonConstants;

/**
 * <p>
 * Title:SendLogsUtil
 * </p>
 * <p>
 * This class for all the applications send different types of logs message to
 * MQ
 * <p>
 * 
 * @author 陆仁杰
 * @date 2016年1月5日
 */
public class SendLogsUtil {
	private static final Logger logger = Logger.getLogger(SendLogsUtil.class);
	private static SendLogsUtil util;
	private Connection connection;
	private ConnectFactory factory;
	private Channel channel;
	private String exchange;

	private SendLogsUtil() {
		exchange = CommonConstants.SYSTEMLOGS;
		factory = new ConnectFactory();
		connection = factory.getConnection();
		try {
			channel = connection.createChannel();
			channel.exchangeDeclare(CommonConstants.SYSTEMLOGS, CommonConstants.DIRECT, false);
		} catch (Exception e) {
			logger.error("MQ Init error:", e);
		}

	}

	public synchronized static SendLogsUtil getInstance() {
		if (util == null) {
			util = new SendLogsUtil();
		}
		return util;
	}

	/**
	 * Send Run logs to MQ
	 * 
	 * @param message
	 * @return This action is success or not 2016年1月5日
	 */
	public boolean sendRunLogs(String message) {
		return sendMessage(message, CommonConstants.RUN);
	}

	/**
	 * Send operation logs to MQ
	 * 
	 * @param message
	 * @return This action is success or not 2016年1月5日
	 */
	public boolean sendOperationLogs(String message) {
		return sendMessage(message, CommonConstants.OPERATION);
	}

	/**
	 * Send transfer logs to MQ
	 * 
	 * @param message
	 * @return This action is success or not 2016年1月5日
	 */
	public boolean sendTransferLogs(String message) {
		return sendMessage(message, CommonConstants.TRANSFER);
	}

	/**
	 * Send update logs to MQ
	 * 
	 * @param message
	 * @return This action is success or not 2016年1月5日
	 */
	public boolean sendUpdateLogs(String message) {
		return sendMessage(message, CommonConstants.UPDATE);
	}

	/**
	 * Send the message to MQ by the routing
	 * 
	 * @param message
	 * @param routing
	 * @return The message sent is success or not 2016年1月5日
	 */
	private synchronized boolean sendMessage(String message, String routing) {
		try {
			channel.basicPublish(exchange, routing, null, message.getBytes("UTF-8"));
		} catch (IOException e) {
			logger.error("Error in send message to MQ:", e);
			return false;
		}
		return true;
	}
}
