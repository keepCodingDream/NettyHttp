package com.yz.restful.service;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.util.CharsetUtil;

import com.yz.restful.DAO.DeviceDAO;
import com.yz.restful.HandlerInterface.HandlerPOSTRequestInterface;

public class Handler_DEVICEUPDATE implements HandlerPOSTRequestInterface{
	@Override
	public boolean handlePOSTRequest(DefaultFullHttpRequest fullHttpRequest, StringBuilder responseContent) throws Exception {
		String jsonString=fullHttpRequest.content().toString(CharsetUtil.UTF_8);
        DeviceDAO deviceDAO=DeviceDAO.getInstance();
        if(deviceDAO.update(jsonString)>0){
        	responseContent.append("{\"status\":\"success\",\"cause\",\"ok\"}");
        	return true;
        }else{
        	throw new Exception("No device found!");
        }
		
	}

}
