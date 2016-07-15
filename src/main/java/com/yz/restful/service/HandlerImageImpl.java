package com.yz.restful.service;

import java.net.InetSocketAddress;
import org.apache.log4j.Logger;
import com.yz.restful.DFSConfig.ClientGlobal;
import com.yz.restful.DFSConfig.StorageClient;
import com.yz.restful.DFSConfig.StorageServer;
import com.yz.restful.DFSConfig.TrackerClient;
import com.yz.restful.DFSConfig.TrackerGroup;
import com.yz.restful.DFSConfig.TrackerServer;
import com.yz.restful.HandlerInterface.HandlerImageInterface;
import com.yz.restful.RESTFul_Netty.HttpPOSTDispatcher;

public class HandlerImageImpl implements HandlerImageInterface {
	private final static String CONF_PATH = "D:\\fdfs_client.conf";
	private static TrackerClient trackerClient;
	private static final Logger logger = Logger.getLogger(HttpPOSTDispatcher.class);
	private static InetSocketAddress[] tracker_servers = new InetSocketAddress[1];
	public static TrackerGroup g_tracker_group;
	static {
		try {
			ClientGlobal.init(CONF_PATH);
			trackerClient = new TrackerClient();
		} catch (Exception e) {
			logger.error(e);
			ClientGlobal.setG_connect_timeout(2 * 1000);
			ClientGlobal.setG_network_timeout(30 * 1000);
			ClientGlobal.setG_charset("UTF-8");
			ClientGlobal.setG_tracker_http_port(80);
			ClientGlobal.setG_anti_steal_token(false);
			ClientGlobal.setG_secret_key("FastDFS1234567890");
			tracker_servers[0] = new InetSocketAddress(ClientGlobal.current_ip, 22122);
			g_tracker_group = new TrackerGroup(tracker_servers);
			ClientGlobal.setG_tracker_group(g_tracker_group);
			trackerClient = new TrackerClient();
		}
	}

	@Override
	public String uploadImag(byte[] image, String type) {
		TrackerServer trackerServer = null;
		StorageServer storageServer = null;
		try {
			trackerServer = trackerClient.getConnection();
			storageServer = trackerClient.getStoreStorage(trackerServer);
			StorageClient client = new StorageClient(trackerServer, storageServer);

			String[] result = client.upload_file(image, 0, image.length, type, null);
			for (String s : result) {
				System.out.println(s);
			}
			return result[1];
		} catch (Exception e) {
			logger.error(e);
			return null;
		} finally {
			if (trackerServer != null) {
				try {
					trackerServer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (storageServer != null) {
				try {
					storageServer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
