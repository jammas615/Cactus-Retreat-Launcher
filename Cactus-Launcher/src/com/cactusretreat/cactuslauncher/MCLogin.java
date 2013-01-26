package com.cactusretreat.cactuslauncher;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import com.cactusretreat.cactuslauncher.exception.ServerDownException;

public class MCLogin {

	private String response;
	private String username;
	private String pass;
	private String dlTicket;
	private String sessionID;
	private HttpURLConnection con;
	private URL url;
	
	public MCLogin(String user, String pass) throws ServerDownException {
		login(user, pass);
	}
	
	private String login(String user, String pass) throws ServerDownException {
		try {
			url = new URL("http://login.minecraft.net/");
			String urlParams = "user=" + user + "&password=" + pass + "&version=13";
			
			con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
			 
			DataOutputStream os = new DataOutputStream(con.getOutputStream());
			os.writeBytes(urlParams);
			os.flush();
			os.close();
			
			if (con.getResponseCode() == 503) {
				throw new ServerDownException();
			}
			
			InputStream is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer bufferResponse = new StringBuffer();
			while((line = br.readLine()) != null) {
				bufferResponse.append(line);
				bufferResponse.append('\r');
			}
			br.close();
			
			if (con != null) {
				con.disconnect();
			}
			String [] values = bufferResponse.toString().trim().split(":");
			if (values[0].equals("Bad request") || values[0].equals("Bad login") || values[0].equals("User not premium")) {
				this.response = "Bad login";
				return response;
			}
			else {	
				this.dlTicket = values[1];
				this.username = values[2];
				this.sessionID = values[3];
				this.pass = pass;
				this.response = "OK";
				
				return response;
			}
						
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getResponse() {
		return response;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getSessionID() {
		return sessionID;
	}
	
	public String getPass() {
		return pass;
	}
	
	public String getDlTicket() {
		return dlTicket;
	}
}
