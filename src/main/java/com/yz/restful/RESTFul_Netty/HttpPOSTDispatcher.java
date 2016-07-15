package com.yz.restful.RESTFul_Netty;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.util.CharsetUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.log4j.Logger;
import com.google.gson.Gson;
import com.yz.restful.Common.CommonConstants;
import com.yz.restful.Common.CommonUtil;
import com.yz.restful.DTO.EventTypeMessageToMQ;
import com.yz.restful.HandlerInterface.HandlerPOSTRequestInterface;
import com.yz.restful.HandlerInterface.MessageHandlerInterface;
import com.yz.restful.MQ.SendMessageFactory;

public class HttpPOSTDispatcher {
	private static final Logger logger = Logger.getLogger(HttpPOSTDispatcher.class);
	private static final String packageName = "com.yz.restful.service";
	private static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private static List<Class<HandlerPOSTRequestInterface>> classes = new ArrayList<Class<HandlerPOSTRequestInterface>>();
	private static Enumeration<URL> resources;
	private static SendMessageFactory sendMessageFactory = new SendMessageFactory();
	private static Class<HandlerPOSTRequestInterface> superClass = HandlerPOSTRequestInterface.class;
	private Gson gson=new Gson();
	static {
		String path = packageName.replace(".", "/");
		try {
			resources = classLoader.getResources(path);
		} catch (IOException e) {
			logger.error(e);
		}
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			if ("jar".equals(resource.getProtocol())) {
				classes = getClasssFromJarFile(resource.getFile().split("!")[0].substring(6), path);
			} else {
				dirs.add(new File(resource.getFile()));
			}

		}
		for (File directory : dirs) {
			try {
				classes.addAll(findClass(directory, packageName));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

	public void dispatcherPOSTRequest(DefaultFullHttpRequest fullHttpRequest, StringBuilder responseContent) throws Exception {
		String uri = fullHttpRequest.getUri().substring(1);
		uri = CommonConstants.HANDLER + uri.toUpperCase();
		Class<HandlerPOSTRequestInterface> classItem = findClassByName(uri);
		HandlerPOSTRequestInterface handler = null;
		if (classItem == null) {// The event message,will send to MQ use the uri
								// as exchange name
			String jsonString = fullHttpRequest.content().toString(CharsetUtil.UTF_8);
			uri = uri.split("_")[1];
			EventTypeMessageToMQ device=gson.fromJson(jsonString, EventTypeMessageToMQ.class);
			if(!CommonUtil.judgeIllegalMessage(device.getDeviceId())){
				responseContent.append("{\"status\":\"fail\",\"cause\",\"You must regist your device first!\"}");
				logger.error("Unregist device :"+device.getDeviceId()+" try to upload events");
				return;
			}
			MessageHandlerInterface messSend = sendMessageFactory.getSendInstance(uri);
			if (messSend.sendMessage(jsonString, uri)) {
				responseContent.append("{\"status\":\"success\",\"cause\",\"ok\"}");
				return;
			} else {
				responseContent.append("{\"status\":\"fail\",\"cause\",\"Illeagal http request!\"}");
				throw new Exception();
			}
		}
		try {
			handler = classItem.newInstance();
			handler.handlePOSTRequest(fullHttpRequest, responseContent);
			return;
		} catch (Exception e) {
			responseContent.append("{\"status\":\"fail\",\"cause\",\"" + e.getMessage() + "\"}");
			throw e;
		}

	}

	@SuppressWarnings("unchecked")
	private static List<Class<HandlerPOSTRequestInterface>> findClass(File directory, String packageName) throws ClassNotFoundException {
		List<Class<HandlerPOSTRequestInterface>> classes = new ArrayList<Class<HandlerPOSTRequestInterface>>();
		System.out.println(directory.getAbsolutePath());
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClass(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				Class<?> item = Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6));
				if (superClass.isAssignableFrom(item)) {
					classes.add((Class<HandlerPOSTRequestInterface>) item);
				}
			}
		}
		return classes;
	}

	private Class<HandlerPOSTRequestInterface> findClassByName(String className) {
		for (Class<HandlerPOSTRequestInterface> classItem : classes) {
			if (classItem.getName().endsWith(className)) {
				return classItem;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Class<HandlerPOSTRequestInterface>> getClasssFromJarFile(String jarPaht, String filePaht) {
		List<Class<HandlerPOSTRequestInterface>> clazzs = new ArrayList<Class<HandlerPOSTRequestInterface>>();

		JarFile jarFile = null;
		if("usr".equalsIgnoreCase(jarPaht.split("/")[0])){
			jarPaht="/"+jarPaht;
		}
		logger.info(jarPaht);
		try {
			jarFile = new JarFile(jarPaht);
		} catch (IOException e1) {
			logger.error(e1);
		}
		List<JarEntry> jarEntryList = new ArrayList<JarEntry>();
		Enumeration<JarEntry> ee = jarFile.entries();
		while (ee.hasMoreElements()) {
			JarEntry entry = (JarEntry) ee.nextElement();
			if (entry.getName().startsWith(filePaht) && entry.getName().endsWith(".class")) {
				jarEntryList.add(entry);
			}
		}
		for (JarEntry entry : jarEntryList) {
			String className = entry.getName().replace('/', '.');
			className = className.substring(0, className.length() - 6);
			try {
				Class<?> loadClass = Thread.currentThread().getContextClassLoader().loadClass(className);
				if (superClass.isAssignableFrom(loadClass)) {
					logger.info(loadClass);
					clazzs.add((Class<HandlerPOSTRequestInterface>) loadClass);
				}
			} catch (ClassNotFoundException e) {
				logger.error(e);
			}
		}
		try {
			jarFile.close();
		} catch (IOException e) {
		}
		return clazzs;
	}
}
