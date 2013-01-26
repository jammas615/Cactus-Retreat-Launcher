package com.cactusretreat.cactuslauncher.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzip implements Runnable {

	private File zipFile;
	private File dir;
	
	public Unzip(File zipFile, File unzipDir) {
		this.zipFile = zipFile;
		this.dir = unzipDir;
	}
	
	@Override
	public void run() {
		try {
			InputStream is = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				File file = new File(dir, entry.getName());
				if (file.getName().contains("aux")) {
					file = new File(dir, "randomshit.class");
				}
				if (entry.isDirectory()) {
					if (!file.exists())file.mkdirs();
				}
				else {
					file.getParentFile().mkdirs();
					FileUtils.copy(zis, zipFile.getTotalSpace(), file);
				}
			}
			zis.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}

}
