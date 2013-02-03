package com.cactusretreat.cactuslauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CactusLauncherUpdater {

	static {
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			WINDOWS = true;
			APPDATA_PATH = System.getenv("APPDATA");
		}
		else {
			WINDOWS = false;
			APPDATA_PATH = System.getProperty("user.home");
		}
	}
	
	public static boolean WINDOWS;
	public static String UPDATE_SERVER_URL = "http://update.cactusmc.com/cactuslauncher/";
	public static String APPDATA_PATH;
	public static String DATA_FOLDER_PATH = APPDATA_PATH + File.separator + ".cactuslauncher";
	public static String LAUNCHER_VERSION_FILE = DATA_FOLDER_PATH + File.separator + "launcher-version";
	public static String MODPACKS_FILE = DATA_FOLDER_PATH + File.separator + "modpacks";
	public static String LAUNCHER_JAR_FILE = DATA_FOLDER_PATH + File.separator + "cactuslauncher.jar";
	
	private ArrayList list;
	
	public static int LAUNCHER_VERSION;
	
	public CactusLauncherUpdater() {
		init();
	}
	
	private void init() {
		LauncherUpdater updater = new LauncherUpdater();
		updater.start();
		startLauncher();
	}
	
	private void startLauncher() {
		System.out.println("Running");
		list = new ArrayList();
		list.add("java");
		list.add("-Xmx1024m");
		list.add("-cp");
		list.add(LAUNCHER_JAR_FILE);
		list.add("com.cactusretreat.cactuslauncher.CactusLauncher");
		
		ProcessBuilder proc = new ProcessBuilder(list);
		try {
			Process process = proc.start();
			System.out.println("Started");
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		new Thread() {
			@Override
			public void run() {
				
			}
		}.start();
		*/
		System.exit(0);
	}
	
	public static void main(String [] args) {
		new CactusLauncherUpdater();
	}
}
