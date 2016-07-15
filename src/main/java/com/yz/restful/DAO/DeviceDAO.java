package com.yz.restful.DAO;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;
import com.yz.restful.Common.CommonConstants;
import com.yz.restful.DTO.Device;

public class DeviceDAO extends BaseDAO{
	private static DeviceDAO deviceDAOInstance;
    private MongoCollection<Document> collection;
	private DeviceDAO() {
	}
	public static DeviceDAO getInstance() {
		if (deviceDAOInstance != null) {
			return deviceDAOInstance;
		}
		return new DeviceDAO();
	}
	public void closeConnection(){
		mongoClient.close();
		
	}
	public Object insert(String request) throws Exception {
		collection = dataBase.getCollection(CommonConstants.DEVICE);
		Gson gson=new Gson();
		Device device=gson.fromJson(request, Device.class);
		String physicalId=device.getPhysicalId();
		collection.insertOne(Document.parse(request));
		BasicDBObject query=new BasicDBObject();
		query.put("physicalId", physicalId);
		MongoCursor<Document> cursor=collection.find(query).iterator();
		if(cursor.hasNext()){
			Document obj=cursor.next();
			return obj.get("_id");
		}
		return null;
	}
	public long update(String request) throws Exception{
		collection = dataBase.getCollection(CommonConstants.DEVICE);
		DBObject device=(DBObject) JSON.parse(request);
		String string_id=device.get("deviceId").toString();
		ObjectId obj_id=new ObjectId(string_id);
		BasicDBObject query=new BasicDBObject();
		query.put("_id", obj_id);
		BasicDBObject update=new BasicDBObject();
		update.put("physicalId", device.get("physicalId"));
		update.put("ipcs", device.get("ipcs"));
		update.put("deviceIp", device.get("deviceIp"));
		update.put("location", device.get("location"));
		update.put("deviceName", device.get("deviceName"));
		update.put("checkPointName", device.get("checkPointName"));
		update.put("gateway", device.get("gateway"));
		update.put("DNS", device.get("DNS"));
		update.put("configURL", device.get("configURL"));
		update.put("mask", device.get("mask"));
		update.put("ability", device.get("ability"));
		update.put("isAuthorization", device.get("isAuthorization"));
		update.put("longitude", device.get("longitude"));
		update.put("latitude", device.get("latitude"));
		UpdateResult result=collection.updateOne(query, new BasicDBObject().append("$set", update));
		return result.getMatchedCount();
	}
	public List<String> getAllDevices(){
		List<String> response=new ArrayList<>();
		collection = dataBase.getCollection(CommonConstants.DEVICE);
		MongoCursor<Document>  cursor = collection.find(new BasicDBObject()).iterator();
		while(cursor.hasNext()){
			Document obj=cursor.next();
			response.add(obj.get("_id").toString());
		}
		return response;
	}
	/**
	 * @param physical
	 * @return  If the physical id is exists return false;Otherwise return true
	 * 2015年11月26日
	 */
	public String judgePhysicalIdIsExist(String physical){
		collection = dataBase.getCollection(CommonConstants.DEVICE);
		MongoCursor<Document> cursor=collection.find(new BasicDBObject().append("physicalId", physical)).iterator();
		if(cursor.hasNext()){
			return cursor.next().get("_id").toString();
		}else {
			return "";
		}
	}
}
