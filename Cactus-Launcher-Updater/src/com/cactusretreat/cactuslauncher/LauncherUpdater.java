package com.cactusretreat.cactuslauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import static org.apache.commons.io.FileUtils.copyURLToFile;

public class LauncherUpdater {

	private String bits;
	private Shell shell;
	private Display display;
	private GridLayout layout;
	private GridData data;
	private Composite windowComposite;
	private Label statusText;
	private Label statusFine;
	private ProgressBar statusBar;
	
	private boolean updaterFinished = false;
	
	private boolean dataFolderExists = true;
	private boolean modpacksFileExists = true;
	private boolean versionFileExists = true;
	private boolean launcherJarExists = true;
	private boolean launcherOutOfDate = true;
	
	private int installedLauncherVersion;
	private int currentLauncherVersion;
	
	private File dataFolder;
	private File modpacksFile;
	private File versionFile;
	private File launcherJar;
	
	public LauncherUpdater() {
		init();
	}
	
	private void init() {
		if (CactusLauncherUpdater.WINDOWS) {
			String jvmBitString = System.getProperty("os.arch");
			System.out.println(bits);
			if (jvmBitString.matches("amd64")) {
				bits = "64";
			}
			else {
				bits = "32";
			}
		}
			
		display = new Display();
		shell = new Shell(display);
		setupWindow();
	}
	
	private void setupWindow() {
		shell.setSize(300, 150);
		shell.setText("Cactus Launcher Updater");
		shell.setLayout(new FillLayout());
		Image icon = new Image(shell.getDisplay(), this.getClass().getResourceAsStream("cactus.png"));
		shell.setImage(icon);
		
		
		layout = new GridLayout(1, false);
		windowComposite = new Composite(shell, SWT.NONE);
		//windowComposite.setSize(300, 150);
		windowComposite.setLayout(layout);
		windowComposite.setLayoutData(data);
		
		
		data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.horizontalIndent = 2;
		statusText = new Label(windowComposite, SWT.BOLD);
		statusText.setLayoutData(data);
		FontData[] fd = statusText.getFont().getFontData();
		fd[0].setHeight(15);
		statusText.setFont(new Font(display, fd));
		
		data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		statusFine = new Label(windowComposite, SWT.NONE);
		statusFine.setLayoutData(data);
		
		statusBar = new ProgressBar(windowComposite, SWT.SMOOTH);
		data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.heightHint = 40;
		statusBar.setLayoutData(data);
	}
	
	public void start() {
		shell.open();
		
		while (!display.isDisposed()) {
			
			if (updaterFinished) {
				display.dispose();
				break;
			}
			
			if(!display.readAndDispatch()) {
				display.sleep();
			}
			
			statusText.setText("Checking files");
			checkExistingFiles();
			statusBar.setSelection(33);
			statusText.setText("Checking for updates");
			checkUpdates();
			statusBar.setSelection(66);
			statusText.setText("Updating");
			downloadFiles();
		}
		shell.dispose();
		display.dispose();
	}
	
	private void checkExistingFiles() {
		dataFolder = new File(CactusLauncherUpdater.DATA_FOLDER_PATH);
		modpacksFile = new File(CactusLauncherUpdater.MODPACKS_FILE);
		versionFile = new File(CactusLauncherUpdater.LAUNCHER_VERSION_FILE);
		launcherJar = new File(CactusLauncherUpdater.LAUNCHER_JAR_FILE);
		
		if (!dataFolder.exists()) {
			dataFolderExists = false;
		}
		
		if (!modpacksFile.exists()) {
			modpacksFileExists = false;
		}
		
		if (!launcherJar.exists()) {
			launcherJarExists = false;
		}
	}
	
	private void checkUpdates() {
		if (!launcherJar.exists()) {
			launcherJarExists = false;
			return;
		}
		

		String currentHex;
		try {
			currentHex = DigestUtils.md5Hex(new FileInputStream(launcherJar));
			String newHex =null;
			if (CactusLauncherUpdater.WINDOWS) {
				newHex = DigestUtils.md5Hex(new URL(CactusLauncherUpdater.UPDATE_SERVER_URL + bits + "/" + "cactuslauncher.jar").openStream());
			}
			else {
				newHex = DigestUtils.md5Hex(new URL(CactusLauncherUpdater.UPDATE_SERVER_URL + "mac" + "/" + "cactuslauncher.jar").openStream());
			}
				
			
			if (currentHex.equals(newHex)) {
				launcherOutOfDate = false;
			}
			else {
				launcherOutOfDate = true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void downloadFiles(){
		if (!dataFolderExists) {
			dataFolder.mkdir();
		}
		
		URL modpacksFileURL;
		try {
			modpacksFileURL = new URL(CactusLauncherUpdater.UPDATE_SERVER_URL + "modpacks");
			URL launcherJarURL;
			if (CactusLauncherUpdater.WINDOWS) {
				launcherJarURL = new URL(CactusLauncherUpdater.UPDATE_SERVER_URL + bits + "/" + "cactuslauncher.jar");
			}
			else {
				launcherJarURL = new URL(CactusLauncherUpdater.UPDATE_SERVER_URL + "mac" + "/" + "cactuslauncher.jar");
			}
			
			statusFine.setText("Updating modpacks info");
			copyURLToFile(modpacksFileURL, modpacksFile);
				
			if (launcherOutOfDate || (!launcherJarExists)) {		
				statusFine.setText("Downloading launcher");
				copyURLToFile(launcherJarURL, launcherJar);
			}
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		updaterFinished = true;
	}
	
	public boolean isFinished() {
		return updaterFinished;
	}
}
