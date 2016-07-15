package com.yz.restful.RESTFul_Netty;

import java.io.IOException;

import com.yz.restful.DFSConfig.Base64;

public class TestRefect {
	public static void main(String[] args) {
	    Base64 base64=new Base64();
	    try {
			base64.encode("abc".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
