package com.yz.restful.Common;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Title:CommonUtil
 * </p>
 * <p>
 * 提供一些重复使用的方法
 * <p>
 * 
 * @author 陆仁杰
 * @date 2015年8月14日
 */
public class CommonUtil {

	private static volatile List<String> deviceIds=new ArrayList<>(); 
	/**
	 * @param string
	 * @return If not empty String return false,else return true 
	 * 2015年8月26日
	 */
	public static boolean isEmptyString(String string) {
		if (string == null || "".equals(string.trim())) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @param dateString
	 * @return Transfor the String to Date 
	 * 2015年11月5日
	 */
	public static Date dateFormate(String dateString){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			Date date = sdf.parse(dateString);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * @param number
	 * @return
	 * 2015年11月5日
	 */
	public static float formatNumber(float number){
		DecimalFormat decimalFormat=new DecimalFormat(".00");
		return Float.parseFloat(decimalFormat.format(number));
	}
	
	public static String dateFormate(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			String dateString = sdf.format(date);
			return dateString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String dateFormateToString(String dateString){
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d hh:mm:ss z yyyy", java.util.Locale.US);
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			Date date = sdf.parse(dateString);
			return sdf2.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static long formateDateToLong(String date){
		 Date respDate=CommonUtil.dateFormate(date);
		 return respDate.getTime()/1000;
	}
	public synchronized static List<String> getDeviceIds() {
		return deviceIds;
	}
	public synchronized static  void setDeviceIds(List<String> deviceIds) {
		CommonUtil.deviceIds = deviceIds;
	}
	public synchronized static  void addDeviceid(String deviceId){
		deviceIds.add(deviceId);
	}
	/**
	 * @param deviceId
	 * @return  If the event type message is not correct will return false,otherwise return true
	 * 2015年11月26日
	 */
	public static boolean judgeIllegalMessage(String deviceId){
		if(isEmptyString(deviceId)){
			return false;
		}
		if(deviceIds.contains(deviceId)){
			return true;
		}else {
			return false;
		}
	}
}
