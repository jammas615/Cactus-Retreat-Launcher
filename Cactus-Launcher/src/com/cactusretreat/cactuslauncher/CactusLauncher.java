package com.cactusretreat.cactuslauncher;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.cactusretreat.cactuslauncher.config.ConfigCactusLogin;
import com.cactusretreat.cactuslauncher.config.ConfigModpacks;
import com.cactusretreat.cactuslauncher.config.ConfigProfiles;
import com.cactusretreat.cactuslauncher.config.ConfigSettings;
import com.cactusretreat.cactuslauncher.exception.ServerDownException;
import com.cactusretreat.cactuslauncher.gui.DialogForumLogin;
import com.cactusretreat.cactuslauncher.gui.MainWindow;

public class CactusLauncher {
	
	static {
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			WINDOWS = true;
			APPDATA_PATH = System.getenv("APPDATA");
			
			if (System.getProperty("os.arch").toLowerCase().contains("64")) {
				OS_ARCH = 64;
			}
			else {
				OS_ARCH = 32;
			}
		}
		else {
			WINDOWS = false;
			APPDATA_PATH = System.getProperty("user.home");
		}
	}
	
	public static final int SHELL_TRIM = SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX;
	public static boolean WINDOWS;
	public static int OS_ARCH;
	public static String APPDATA_PATH;
	public static String DATA_FOLDER_PATH = APPDATA_PATH + File.separator + ".cactuslauncher";
	public static String LAUNCHER_VERSION_FILE = DATA_FOLDER_PATH + File.separator + "launcher-version";
	public static String MODPACKS_FILE = DATA_FOLDER_PATH + File.separator + "modpacks";
	public static String SETTINGS_FILE = DATA_FOLDER_PATH + File.separator + "settings";
	public static String PROFILES_FILE = DATA_FOLDER_PATH + File.separator + "profiles";
	public static String FORUM_LOGIN_FILE = DATA_FOLDER_PATH + File.separator + "forum";
	public static String NEWS_URL = "http://update.cactusmc.com/cactuslauncher/news.html";
	
	private Shell shell;
	private Display display;
	
	public static final int WIDTH = 850;
	public static final int HEIGHT = 500;
	
	private ConfigProfiles profiles;
	private ConfigSettings settings;
	private ConfigCactusLogin login;
	private ConfigModpacks modpacksConfig;
	private MainWindow mainWindow;
	private int rankLevel;
	
	
	
	public CactusLauncher() {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(new File(DATA_FOLDER_PATH, "output.txt")));
			PrintStream err = new PrintStream(new FileOutputStream(new File(DATA_FOLDER_PATH, "error.txt")));
			System.setErr(err);
			System.setOut(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
				
		init();
		start();
		run();
	}
	
	private void init() {
		modpacksConfig = new ConfigModpacks();
		modpacksConfig.load();
		
		settings = new ConfigSettings();
		settings.load();
		
		profiles = new ConfigProfiles();
		profiles.load();
		
		login = new ConfigCactusLogin();
		
		display = new Display();
		shell = new Shell(SHELL_TRIM);
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				close();
			}
		});	
	}
	
	public void start() {
		mainWindow = new MainWindow(this, shell, rankLevel);
		mainWindow.openWindow();
	}
	
	public void close() {
		if (!shell.isDisposed()) {
			shell.dispose();
		}
		display.dispose();
		System.exit(0);
	}
	
	private void run() {
		while (!display.isDisposed()) {
			
			
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
	public HashMap<String, String[]> getModpacks() {
		return modpacksConfig.getModpacks();
	}
	
	public HashMap<String, String[]> getSettings() {
		return settings.getSettings();
	}
	
	public HashMap<String, String[]> getProfiles() {
		return profiles.getProfiles();
		
	}
	
	public String [] getForumLogin() {
		return login.getForumLogin();
	}
	
	public void writeSettings(HashMap<String, String[]> newSettings) {
		if (this.settings != null) {
			this.settings.write(newSettings);
		}
	}
	
	public void writeProfiles(HashMap<String, String[]> newProfiles) {
		if (this.profiles != null) {
			this.profiles.write(newProfiles);
		}
	}
	
	public void writeForumLogin(String user, String pass) {
		if (this.profiles != null) {
			this.login.writeForumLogin(user, pass);
		}
	}
	
	public Display getDisplay() {
		return this.display;
	}
	
	public static void main(String [] args) {
		new CactusLauncher();
		
		System.exit(0);
	}
}
