package com.yz.restful.service;


import org.apache.log4j.Logger;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.util.CharsetUtil;

import com.google.gson.Gson;
import com.yz.restful.Common.CommonUtil;
import com.yz.restful.DAO.DeviceDAO;
import com.yz.restful.DTO.Device;
import com.yz.restful.HandlerInterface.HandlerPOSTRequestInterface;

public class Handler_DEVICEREGIST implements HandlerPOSTRequestInterface{
	private static final Logger logger = Logger.getLogger(Handler_DEVICEREGIST.class);
	private Gson gson=new Gson();
	@Override
	public boolean handlePOSTRequest(DefaultFullHttpRequest fullHttpRequest, StringBuilder responseContent) throws Exception{
		String jsonString=fullHttpRequest.content().toString(CharsetUtil.UTF_8);
		Device device=gson.fromJson(jsonString, Device.class);
		DeviceDAO deviceDAO=DeviceDAO.getInstance();
		String deviceId=deviceDAO.judgePhysicalIdIsExist(device.getPhysicalId());
		if(!CommonUtil.isEmptyString(deviceId)){
			 responseContent.append("{\"status\":\"success\",\"deviceId\":"+"\""+deviceId+"\"}");
			 logger.info("Device is exist! DeviceId is:"+deviceId);
			 return true;
		}
		try {
			 Object response=deviceDAO.insert(jsonString);
			 if(response!=null){
				 CommonUtil.addDeviceid(response.toString());
			 }else {
				logger.error("Something wrong in regist device!");
			    throw new Exception("Something wrong in regist device!");	
			 }
			 logger.info("Device has regist! DeviceId is:"+deviceId);
			 responseContent.append("{\"status\":\"success\",\"deviceId\":"+"\""+response+"\"}");
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

}
