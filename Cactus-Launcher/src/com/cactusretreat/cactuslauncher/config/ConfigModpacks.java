package com.cactusretreat.cactuslauncher.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import com.cactusretreat.cactuslauncher.CactusLauncher;

public class ConfigModpacks {

	private Configuration config;
	private HashMap<String, String[]> modpacks;
	
	public ConfigModpacks() {
		try {
			config = new Configuration(new FileInputStream(CactusLauncher.MODPACKS_FILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void load() {
		modpacks = config.getNode("modpacks");		
	}
	
	public HashMap<String, String[]> getModpacks() {
		return modpacks;
	}
}
