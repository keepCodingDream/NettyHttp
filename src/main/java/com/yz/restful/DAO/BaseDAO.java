package com.yz.restful.DAO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;


public class BaseDAO {
	private static final Logger logger = Logger.getLogger(BaseDAO.class);
	protected static MongoClient mongoClient;
	protected static MongoDatabase dataBase;
	private static String host = "192.168.1.181";
	private static String port = "27017";
	private static String db = "xjvau";
	static{
		Properties prop = new Properties();
		InputStream in = BaseDAO.class.getClassLoader().getResourceAsStream("mongo.properties");
		
		try {
			if (in != null) {
				prop.load(in);
				host = prop.getProperty("host").trim();
				port = prop.getProperty("port").trim();
				db = prop.getProperty("dataBase").trim();
			}

		} catch (IOException e) {
			logger.error("error in load mongo.properties");
		}
		mongoClient=new MongoClient(host, Integer.parseInt(port));
		dataBase = mongoClient.getDatabase(db);
	}
    protected void reconnect() throws Exception{
    	Thread.sleep(15000);
    	mongoClient=new MongoClient(host, Integer.parseInt(port));
		dataBase = mongoClient.getDatabase(db);
    }

}
