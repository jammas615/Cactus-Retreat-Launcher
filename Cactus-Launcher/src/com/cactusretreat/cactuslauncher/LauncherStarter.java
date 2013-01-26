package com.cactusretreat.cactuslauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LauncherStarter {

	private ArrayList list;
	
	@SuppressWarnings("unchecked")
	public LauncherStarter(String username, String sessionID, String dataFolderPath, String modpackName, int ramAlloc, boolean forceUpdate, String title) {
		System.out.println("Setting launcher params");
		list = new ArrayList();
		list.add("java");
		list.add("-Xmx" + ramAlloc + "m");
		list.add("-cp");
		list.add(System.getProperty("java.class.path"));
		list.add("com.cactusretreat.cactuslauncher.GameLauncher");
		list.add(username);
		list.add(sessionID);
		list.add((dataFolderPath));
		list.add(modpackName);
		if (forceUpdate) {
			list.add("true");
		}
		else {
			list.add("false");
		}
		list.add(title);
		System.out.println("Launcher params set");
	}
	
	public void launch() {
		System.out.println("Launching!");
		new Thread() {
			@Override
			public void run() {
				ProcessBuilder proc = new ProcessBuilder(list);
				try {
					Process process = proc.start();
					System.out.println("Launched!");
					
					BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line;
					while ((line = br.readLine()) != null) {
						System.out.println(line);
					}
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Launch was not successful");
					System.out.println("Message: " + e.getMessage());
					System.out.println("Cause: " + e.getCause());
				}
			}
		}.start();
		
	}
}
