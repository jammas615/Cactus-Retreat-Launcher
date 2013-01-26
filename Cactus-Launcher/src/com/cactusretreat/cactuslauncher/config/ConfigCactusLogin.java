package com.cactusretreat.cactuslauncher.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

import com.cactusretreat.cactuslauncher.CactusLauncher;
import com.cactusretreat.cactuslauncher.util.FileUtils;

public class ConfigCactusLogin {

	private File loginFile;
	private Configuration config;

	public ConfigCactusLogin() {
		loginFile = new File(CactusLauncher.FORUM_LOGIN_FILE);
		
		try {
			//is = new FileInputStream(profilesFile);
			if (loginFile.exists() && !isEmpty()) {
				config = new Configuration(loginFile);
			}
			else {
				System.out.println("No forum login exists!");
				loginFile.createNewFile();
				config = new Configuration(new FileInputStream(loginFile));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String[] getForumLogin() {
		HashMap<String, String[]> login = config.getNode("forums");
		
		if (login != null) {
			for (String key : login.keySet()) {
				byte [] decoded = Base64.decodeBase64(login.get(key)[0]);
				String pass = new String(decoded);
				return new String [] {key, pass};
			}
		}
		else {
			return null;
		}
		return null;
	}
	
	public void writeForumLogin(String user, String pass) {
		HashMap<String, String []> login = new HashMap<String, String []>();
		byte [] encoded = Base64.encodeBase64(pass.getBytes());
		login.put(user, new String [] {new String(encoded)});
		config.writeConfig(loginFile, "forums", login, true);
	}
	
	private boolean isEmpty() {
		return FileUtils.isEmpty(loginFile);
	}
}
