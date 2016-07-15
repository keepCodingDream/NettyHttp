package com.yz.restful.HandlerInterface;

import io.netty.handler.codec.http.DefaultFullHttpRequest;

/**
 * <p>Title:HandlerPOSTRequestInterface</p>
 * <p>This interface is the super interface for all handler POST request<p>
 * @author 陆仁杰
 * @date 2015年11月16日
 */
public interface HandlerPOSTRequestInterface {
    public boolean handlePOSTRequest(DefaultFullHttpRequest fullHttpRequest, StringBuilder responseContent) throws Exception;
}
