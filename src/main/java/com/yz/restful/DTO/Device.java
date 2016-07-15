package com.yz.restful.DTO;

import java.util.ArrayList;
import java.util.List;

public class Device {
	private String physicalId;
	private List<Ipc> ipcs = new ArrayList<>();
	private String deviceIp;
	private String location;
	private String deviceName;
	private String checkPointName;
	private String gateway;
	private String DNS;
	private String configURL;
	private String mask;
	private String ability;
	private String isAuthorization;
	private String longitude;
	private String latitude;
	public String getPhysicalId() {
		return physicalId;
	}

	public void setPhysicalId(String physicalId) {
		this.physicalId = physicalId;
	}

	public List<Ipc> getIpcs() {
		return ipcs;
	}

	public void setIpcs(List<Ipc> ipcs) {
		this.ipcs = ipcs;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getCheckPointName() {
		return checkPointName;
	}

	public void setCheckPointName(String checkPointName) {
		this.checkPointName = checkPointName;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getDNS() {
		return DNS;
	}

	public void setDNS(String dNS) {
		DNS = dNS;
	}

	public String getConfigURL() {
		return configURL;
	}

	public void setConfigURL(String configURL) {
		this.configURL = configURL;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getAbility() {
		return ability;
	}

	public void setAbility(String ability) {
		this.ability = ability;
	}

	public String getIsAuthorization() {
		return isAuthorization;
	}

	public void setIsAuthorization(String isAuthorization) {
		this.isAuthorization = isAuthorization;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

}
