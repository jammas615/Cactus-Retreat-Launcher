package com.cactusretreat.cactuslauncher.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

import com.cactusretreat.cactuslauncher.CactusLauncher;
import com.cactusretreat.cactuslauncher.util.FileUtils;


public class ConfigProfiles {

	private Configuration config;
	private InputStream is;
	private HashMap<String, String[]> profiles;
	private File profilesFile;
	
	public ConfigProfiles() {
		profilesFile = new File(CactusLauncher.PROFILES_FILE);
		profiles = new HashMap<String, String[]>();
		
		try {
			//is = new FileInputStream(profilesFile);
			if (profilesFile.exists() && !isEmpty()) {
				config = new Configuration(profilesFile);
			}
			else {
				System.out.println("No profiles exist!");
				profilesFile.createNewFile();
				config = new Configuration(new FileInputStream(profilesFile));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void load() {
		if (profilesFile.exists() && !isEmpty()) {
			profiles = config.getNode("profiles");
		}
	}
	
	public void write(HashMap<String, String[]> newProfiles) {
		for (String user : newProfiles.keySet()) {
			if (!user.equals("")) {
				String pass = newProfiles.get(user)[0];
				byte [] encoded = Base64.encodeBase64(pass.getBytes());
				newProfiles.put(user, new String [] {new String(encoded)});
			}
		}
		config.writeConfig(profilesFile, "profiles", newProfiles, false);
	}
	
	
	
	public HashMap<String, String[]> getProfiles() {
		return profiles;
	}
	
	private boolean isEmpty() {
		return FileUtils.isEmpty(profilesFile);
	}
}
