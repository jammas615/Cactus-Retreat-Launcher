package com.cactusretreat.cactuslauncher;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class MCLauncher extends Applet implements AppletStub {

	public Map<String, String> customParameters;
	private Applet mcApplet;
	private String dataFolderPath;
	
	public MCLauncher(String modpackPath) throws HeadlessException{
		super();
		this.dataFolderPath = modpackPath;
		customParameters = new HashMap<String, String>();
	}
	
	public boolean load(String user, String sessionID) {
		customParameters.put("username", user);
		customParameters.put("sessionid", sessionID);
		
		String binPath = dataFolderPath + File.separator + "bin";
		URL [] urls;
		try {
			urls = new URL [] {
				new File(binPath, "minecraft.jar").toURI().toURL(),
				new File(binPath, "jinput.jar").toURI().toURL(),
				new File(binPath, "lwjgl.jar").toURI().toURL(),
				new File(binPath, "lwjgl_util.jar").toURI().toURL()
			};

			URLClassLoader loader = new URLClassLoader(urls);
			setMinecraftDir(loader, new File(dataFolderPath));
			setMCApplet((Applet)loader.loadClass("net.minecraft.client.MinecraftApplet").newInstance());
			
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void loadNatives(String nativeDir) {
		nativeDir = new File(nativeDir).getAbsolutePath();
		
		System.out.println("Loading natives from " + nativeDir);
		System.setProperty("org.lwjgl.librarypath", nativeDir);
		System.setProperty("net.java.games.input.librarypath", nativeDir);
	}
	
	private void setMinecraftDir(ClassLoader loader, File dir) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
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
	
	@Override
	public void appletResize(int width, int height) {
		resize(width, height);
	}
	
	@Override
	public boolean isActive() {
		return true;
	}
	
	@Override
	public void init() {
		mcApplet.init();
	}
	
	@Override
	public void start() {
		mcApplet.start();
	}
	
	public void stop() {
		if (mcApplet != null) {
			mcApplet.stop();
		}
	}
	
	public void replace(Applet applet) {
		setMCApplet(applet);
		mcApplet.setStub(this);
		mcApplet.setSize(getWidth(), getHeight());
		mcApplet.init();
		mcApplet.validate();
	}
	
	@Override
	public URL getCodeBase() {
		return mcApplet.getCodeBase();
	}
	
	@Override
	public URL getDocumentBase() {
		try {
			return new URL("http://www.minecraft.net/game/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String getParameter(String paramName) {
		if (customParameters.containsKey(paramName)) {
			return customParameters.get(paramName);
		}
		return null;
	}
	
	private void setMCApplet(Applet applet) {
		mcApplet = applet;
		mcApplet.setStub(this);

		mcApplet.setSize(getWidth(), getHeight());

		add(applet, BorderLayout.CENTER);
	}
}
