package com.yz.restful.DAO;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.yz.restful.HandlerInterface.MessageHandlerInterface;

public class TrafficDAOImpl extends BaseDAO implements MessageHandlerInterface{
	private static TrafficDAOImpl trafficDAOInstance;
    private MongoCollection<Document> collection;
	private TrafficDAOImpl() {
	}
	public static TrafficDAOImpl getInstance() {
		if (trafficDAOInstance != null) {
			return trafficDAOInstance;
		}
		return new TrafficDAOImpl();
	}
	public void closeConnection(){
		mongoClient.close();
		
	}
	public boolean sendMessage(String message,String routing) throws Exception {
		collection = dataBase.getCollection(routing);
		Document item;
		item=Document.parse(message);
		collection.insertOne(item);
		return true;
	}
	
}
