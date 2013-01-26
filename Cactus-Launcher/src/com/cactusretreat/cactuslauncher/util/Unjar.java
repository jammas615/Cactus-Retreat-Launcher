package com.cactusretreat.cactuslauncher.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class Unjar implements Runnable {

	private File jarFile;
	private File unpackDir;
	
	public Unjar(File jarFile, File unpackDir) {
		this.jarFile = jarFile;
		this.unpackDir = unpackDir;
	}
	
	@Override
	public void run() {
		try {
			InputStream is = new FileInputStream(jarFile);
			JarInputStream jis = new JarInputStream(is);
			JarEntry entry;
			while ((entry = jis.getNextJarEntry()) != null) {
				File file = new File(unpackDir, entry.getName());
				if (file.getName().contains("aux")) {
					file = new File(unpackDir, "randomshit.class");
				}
				if (entry.isDirectory()) {
					if (!file.exists()) file.mkdirs();
				}
				else {
					file.getParentFile().mkdirs();
					FileUtils.copy(jis, jarFile.getTotalSpace(), file);
				}
			}
			jis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
