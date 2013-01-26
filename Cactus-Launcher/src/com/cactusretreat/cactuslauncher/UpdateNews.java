package com.cactusretreat.cactuslauncher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateNews {

	public static String[] getNews() {
		try {
			String news = new String();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new URL("http://update.cactusmc.com/cactuslauncher/news.txt").openStream()));
			String line;
			int count = 0;
			while ((line = br.readLine()) != null) {
				news += line + "\n";
				count++;
			}
			return new String [] {String.valueOf(count), news};
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
