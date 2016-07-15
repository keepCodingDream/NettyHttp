package com.yz.restful.MQ;

import org.apache.log4j.Logger;
import com.yz.restful.Common.CommonConstants;
import com.yz.restful.HandlerInterface.MessageHandlerInterface;

public class SendMessageFactory {
	private static final Logger logger = Logger.getLogger(ConnectFactory.class);
	private SendFountMessageToMQ fountMessageToMQ;
	private SendQueueMessageToMQ queueMessageToMQ;
	public SendMessageFactory() {
		try {
			fountMessageToMQ = new SendFountMessageToMQ();
			queueMessageToMQ=new SendQueueMessageToMQ();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		
	}
	public MessageHandlerInterface getSendInstance(String uri) {
		if (CommonConstants.LOGSBOBY.equalsIgnoreCase(uri)) {
           return queueMessageToMQ;
		}else{
			return fountMessageToMQ;
		}
	}
}
