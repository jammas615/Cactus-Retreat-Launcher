package com.cactusretreat.cactuslauncher.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Deque;
import java.util.LinkedList;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

public class Jar implements Runnable {

	private File dirToJar;
	private File outputJar;
	
	public Jar(File dirToJar, File outputJar) {
		this.dirToJar = dirToJar;
		this.outputJar = outputJar;
	}
	
	@Override
	public void run() {
		try {
			URI base = dirToJar.toURI();
			Deque<File> queue = new LinkedList<File>();
			queue.push(dirToJar);
			
			OutputStream os = new FileOutputStream(outputJar);
			JarOutputStream  jos = new JarOutputStream(os);
			
			try {
				while (!queue.isEmpty()) {
					dirToJar = queue.pop();
					for (File file : dirToJar.listFiles()) {
						String name = base.relativize(file.toURI()).getPath();
						if (name.startsWith("_")) {
							name = name.substring(1);
						}
						if (name.contains("randomshit.class")) {
							name = "aux.class";
						}
						if (file.isDirectory()) {
							queue.push(file);
							if (!name.endsWith("/")) name = name + "/";
							jos.putNextEntry(new ZipEntry(name));
						}
						else {
							jos.putNextEntry(new ZipEntry(name));
							FileUtils.copy(file, jos);
							jos.closeEntry();
						}
					}
				}
			}
			finally {
				jos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
