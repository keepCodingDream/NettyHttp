package com.yz.restful.MQ;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public class ConnectFactory {
	private static final Logger logger = Logger.getLogger(ConnectFactory.class);
	private Properties prop = new Properties();
	private InputStream in = ConnectFactory.class.getClassLoader().getResourceAsStream("rabbitMQ.properties");
	private ConnectionFactory factory=null;
	private String userName = "yunzhi";
	private String password = "yunzhi";
	private String host = "192.168.1.139";
	private String port = "5672";
	private Connection conn;
	public ConnectFactory(){
		try {
			if (in != null) {
				prop.load(in);
				userName = prop.getProperty("userName").trim();
				password = prop.getProperty("password").trim();
				host = prop.getProperty("host").trim();
				port = prop.getProperty("port").trim();
			}

		} catch (IOException e) {
			logger.error("error in read properities for MQ");
			e.printStackTrace();
		}
		factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(Integer.parseInt(port));
		factory.setUsername(userName);
		factory.setPassword(password);
		factory.setAutomaticRecoveryEnabled(true);
		factory.setNetworkRecoveryInterval(10000);
		try {
			conn = factory.newConnection();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public Connection getConnection(){
		return conn;
	}
	
}
