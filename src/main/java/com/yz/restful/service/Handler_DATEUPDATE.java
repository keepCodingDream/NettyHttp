package com.yz.restful.service;

import java.util.Date;

import io.netty.handler.codec.http.DefaultFullHttpRequest;

import com.yz.restful.Common.CommonUtil;
import com.yz.restful.HandlerInterface.HandlerPOSTRequestInterface;

/**
 * <p>Title:Handler_DATEUPDATE</p>
 * <p>This class for handler the request of get current system time<p>
 * @author 陆仁杰
 * @date 2015年11月16日
 */
public class Handler_DATEUPDATE implements HandlerPOSTRequestInterface{

	@Override
	public boolean handlePOSTRequest(DefaultFullHttpRequest fullHttpRequest, StringBuilder responseContent) {
		responseContent.append("{\"currentTime\":"+"\""+CommonUtil.dateFormate(new Date())+"\"}");
		return true;
	}

}
