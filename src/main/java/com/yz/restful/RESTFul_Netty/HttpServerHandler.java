package com.yz.restful.RESTFul_Netty;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.yz.restful.Common.CommonConstants;
import com.yz.restful.DFSConfig.ClientGlobal;
import com.yz.restful.HandlerInterface.HandlerImageInterface;
import com.yz.restful.service.HandlerImageImpl;

/**
 * <p>
 * Title:HttpServerHandler
 * </p>
 * <p>
 * Handler the message received send them to MQ or DB
 * <p>
 * 
 * @author 陆仁杰
 * @date 2015年9月8日
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
	private static final Logger logger = Logger.getLogger(HttpServerHandler.class);
	private static HttpPOSTDispatcher httpPOSTDispatcher = new HttpPOSTDispatcher();
	private DefaultFullHttpRequest fullHttpRequest;
	private final StringBuilder responseContent = new StringBuilder();
	private HttpPostRequestDecoder decoder;
	private int flag = 0;// Flag the request is image
	private String imageUrl = "";

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (decoder != null) {
			decoder.cleanFiles();
		}
	}

	/*
	 * POST request has fileUpload\deviceRegiest\common events
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		/**
		 * Including GET、POST、PUT three case. When method is get do noting
		 * without return its request. When method is post, handle it with the
		 * HttpPOSTDispatcher. When method is put, will write it to the
		 * DFS(image).
		 */
		fullHttpRequest = (DefaultFullHttpRequest) msg;
		responseContent.setLength(0);
		if ("".equals(ClientGlobal.current_ip)) {
			List<Entry<String, String>> host = fullHttpRequest.headers().entries();
			for (Entry<String, String> attr : host) {
				if ("HOST".equalsIgnoreCase(attr.getKey())) {
					ClientGlobal.current_ip = attr.getValue().split(":")[0];
					logger.info("Default DFS IP IS: " + ClientGlobal.current_ip);
					break;
				}
			}
		}
		if (fullHttpRequest.getMethod().equals(HttpMethod.GET)) {
			QueryStringDecoder decoderQuery = new QueryStringDecoder(fullHttpRequest.getUri());
			Map<String, List<String>> uriAttributes = decoderQuery.parameters();
			for (Entry<String, List<String>> attr : uriAttributes.entrySet()) {
				for (String attrVal : attr.getValue()) {
					responseContent.append("URI: " + attr.getKey() + '=' + attrVal + "\r\n");
				}
			}
			writeResponse(ctx.channel(), HttpResponseStatus.OK, ctx);
			return;
		} else if (fullHttpRequest.getMethod().equals(HttpMethod.POST)) {
			try {
				httpPOSTDispatcher.dispatcherPOSTRequest(fullHttpRequest, responseContent);
			} catch (Exception e) {
				writeResponse(ctx.channel(), HttpResponseStatus.BAD_REQUEST, ctx);
				logger.error("The request uri is:" + fullHttpRequest.getUri() +" The error cause is: "+ e);
				return;
			}
			writeResponse(ctx.channel(), HttpResponseStatus.OK, ctx);
			return;
		} else if (fullHttpRequest.getMethod().equals(HttpMethod.PUT)) {
			flag = 1;
			String uri = fullHttpRequest.getUri();
			System.out.println(uri);
			uri = uri.substring(1);
			if (uri.contains(CommonConstants.FILEUPLOAD)) {
				ByteBuf buf = fullHttpRequest.content();
				byte[] req = new byte[buf.readableBytes()];
				buf.readBytes(req);
				HandlerImageInterface imageHandler = new HandlerImageImpl();
				try {
					imageUrl = imageHandler.uploadImag(req, "jpg");
					responseContent.append("{\"url\":" + "\"" + imageUrl + "\"}");
					writeResponse(ctx.channel(), HttpResponseStatus.OK, ctx);
				} catch (Exception e) {
					writeResponse(ctx.channel(), HttpResponseStatus.BAD_REQUEST, ctx);
					logger.error(e);
				}
			}
			return;
		}
		return;
	}

	/**
	 * 
	 * Write the response message to the caller
	 * 
	 * @param channel
	 */
	private void writeResponse(Channel channel, HttpResponseStatus status, ChannelHandlerContext ctx) {
		ByteBuf buf = copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
		responseContent.setLength(0);
		boolean close = fullHttpRequest.headers().contains(CONNECTION, HttpHeaders.Values.CLOSE, true) || fullHttpRequest.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
				&& !fullHttpRequest.headers().contains(CONNECTION, HttpHeaders.Values.KEEP_ALIVE, true);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);
		if (flag == 1) {
			flag = 0;
			response.headers().set("url", imageUrl);
		}
		response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
		if (!close) {
			response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
		}
		if (HttpHeaders.isKeepAlive(fullHttpRequest)) {
			// response.headers().set(CONNECTION, Values.KEEP_ALIVE);
		}
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		try {
			buf.release();
		} catch (Exception e) {
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.channel().close();
	}
}
