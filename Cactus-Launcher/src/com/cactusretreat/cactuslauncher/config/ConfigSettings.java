package com.cactusretreat.cactuslauncher.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.cactusretreat.cactuslauncher.CactusLauncher;
import com.cactusretreat.cactuslauncher.util.FileUtils;

public class ConfigSettings {

	private Configuration config;
	private HashMap<String, String[]> settings;
	private File settingsFile;
	
	public ConfigSettings() {
		settingsFile = new File(CactusLauncher.SETTINGS_FILE);
		settings = new HashMap<String, String[]>();
		
		try {
			if (!settingsFile.exists() || isEmpty()) {
				settingsFile.createNewFile();
				writeNew();
			}
			else {
				config = new Configuration(new FileInputStream(CactusLauncher.SETTINGS_FILE));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void load() {
		if (isEmpty()) {
			writeNew();
		}
		else if (settingsFile.exists()) {
			settings = config.getNode("options");
		}
	}
	
	private void writeNew() {
		try {
			config = new Configuration(new FileInputStream(CactusLauncher.SETTINGS_FILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		settingsFile.delete();
		settings.put("ram", new String [] {"1024"});
		settings.put("keepLauncherOpen", new String [] {"false"});
		write(settings);
	}
	
	public void write(HashMap<String, String[]> newSettings) {
		config.writeConfig(settingsFile, "options", newSettings, false);
	}
	
	public HashMap<String, String[]> getSettings() {
		return settings;
	}
	
	private boolean isEmpty() {
		return FileUtils.isEmpty(settingsFile);
	}
}
