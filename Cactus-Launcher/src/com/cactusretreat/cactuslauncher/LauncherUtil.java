package com.cactusretreat.cactuslauncher;

import java.applet.Applet;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;

public class LauncherUtil {

	public static Class<?> appletClass;
	
	public static Applet getMCApplet(String modpackDir) throws Exception {
		File modpackDirFolder = new File(modpackDir);
		String binPath = modpackDir + File.separator + "bin";
		URL [] urls;
		urls = new URL [] {
			new File(binPath, "minecraft.jar").toURI().toURL(),
			new File(binPath, "jinput.jar").toURI().toURL(),
			new File(binPath, "lwjgl.jar").toURI().toURL(),
			new File(binPath, "lwjgl_util.jar").toURI().toURL()
		};
		
		
		URLClassLoader loader = new URLClassLoader(urls);
		setMinecraftDir(loader, modpackDirFolder);
		
		String nativeDir = new File(binPath, "natives").getPath();
		System.setProperty("org.lwjgl.librarypath", nativeDir);
		System.setProperty("net.java.games.input.librarypath", nativeDir);
		
		System.setProperty("minecraft.applet.TargetDirectory", modpackDirFolder.getAbsolutePath());
		System.setProperty("minecraft.applet.WrapperClass", "net.minecraft.Launcher");
		
		appletClass = loader.loadClass("net.minecraft.client.MinecraftApplet");
		
		return (Applet)appletClass.newInstance();
	}
	
	private static void setMinecraftDir(ClassLoader loader, File dir) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

		Class<?> mcClass = loader.loadClass("net.minecraft.client.Minecraft");
		Field[] fields = mcClass.getDeclaredFields();

		int fieldCount = 0;
		Field mcDirField = null;
		for (Field field : fields) {
			if (field.getType() == File.class) {
				int mods = field.getModifiers();
				if (Modifier.isStatic(mods) && Modifier.isPrivate(mods)) {
					mcDirField = field;
					fieldCount++;
				}
			}
		}
		
		if (fieldCount != 1) {
			System.out.println("Could not set mc directory");
			System.exit(0);
		}
		
		mcDirField.setAccessible(true);
		mcDirField.set(null, dir);
	}
}
