package com.yz.restful.RESTFul_Netty;

import java.util.List;

import org.apache.log4j.Logger;

import com.yz.restful.Common.CommonUtil;
import com.yz.restful.DAO.DeviceDAO;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;


public class HttpServer {
	private int port;
	private static final Logger logger = Logger.getLogger(HttpServer.class);
	public HttpServer(int port) {
		this.port = port;
	}

	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(50);
		EventLoopGroup workerGroup = new NioEventLoopGroup(50);
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
								@Override
								public void initChannel(SocketChannel ch) throws Exception {
									ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());
									ch.pipeline().addLast("http-encoder",new HttpResponseEncoder());
									ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(655360));
									ch.pipeline().addLast(new HttpServerHandler());
								}
							});
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		DeviceDAO deviceDAO=DeviceDAO.getInstance();
		CommonUtil.setDeviceIds(deviceDAO.getAllDevices());
		List<String> deviceIds=CommonUtil.getDeviceIds();
		logger.info("===========The deviceIds has registed  start========");
		for(String item:deviceIds){
			logger.info(item);
		}
		logger.info("===========The deviceIds has registed  end========");
		int port=9000;
		new HttpServer(port).run();
		
	}

}
