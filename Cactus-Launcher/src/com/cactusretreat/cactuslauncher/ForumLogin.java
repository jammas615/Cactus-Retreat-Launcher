package com.cactusretreat.cactuslauncher;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.cactusretreat.cactuslauncher.exception.ServerDownException;

public class ForumLogin {
	
	public static int doLogin(String user, String pass) throws ServerDownException {
		try {
			URL url = new URL("http://update.cactusmc.com/login.php");
			String urlParams = "username=" + user.trim() + "&password=" + pass.trim();
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
			
			DataOutputStream dos = new DataOutputStream(con.getOutputStream());
			dos.writeBytes(urlParams);
			dos.flush();
			dos.close();

			if (con.getResponseCode() == 503) {
				throw new ServerDownException();
			}
			
			InputStream is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			line = br.readLine();
			br.close();
			
			if (con != null) {
				con.disconnect();
			}
			
			String [] values = line.trim().split(":");
			if (values[0].equals("Bad login")) {
				return -1;
			}
			else {
				return Integer.parseInt(values[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -2;
	}
}
