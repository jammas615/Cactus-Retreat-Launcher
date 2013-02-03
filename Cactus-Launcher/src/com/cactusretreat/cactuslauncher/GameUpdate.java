package com.cactusretreat.cactuslauncher;

import de.schlichtherle.io.ArchiveException;
import de.schlichtherle.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import static org.apache.commons.io.FileUtils.*;

import com.cactusretreat.cactuslauncher.config.Configuration;
import com.cactusretreat.cactuslauncher.diff.JBPatch;
import com.cactusretreat.cactuslauncher.gui.ModpackUpdater;
import com.cactusretreat.cactuslauncher.util.Download;
import com.cactusretreat.cactuslauncher.util.FileUtils;
import com.cactusretreat.cactuslauncher.util.Jar;
import com.cactusretreat.cactuslauncher.util.Unjar;
import com.cactusretreat.cactuslauncher.util.Unzip;

import de.schlichtherle.io.ArchiveDetector;

public class GameUpdate extends Thread {

	private ModpackUpdater updater;
	private static final String UPDATE_URL = "http://update.cactusmc.com/cactuslauncher/";
	private static final String MC_DL_URL = "http://s3.amazonaws.com/MinecraftDownload/";
	private static String MODPACK_URL;
	private static String DATA_FOLDER_PATH;
	private HashMap<String, String[]> modpackFoldersConfig;
	private HashMap<String, String[]> newModpackFoldersConfig;
	// #mcconfig should look like:
	// mcjar::<version>::<md5>
	// mcrequired::<version>::>md5>
	// If both versions match, no need to patch!
	private HashMap<String, String[]> mcConfig;
	
	private HashMap<String, File> modpackFolders;
	private HashMap<String, java.io.File> modpackZips;
	private HashMap<String, Boolean> existingFolders;
	private HashMap<String, Boolean> updateFolders;
	
	private File modpackConfigFile;
	private File newModpackConfigFile;
	private Configuration configModpack;
	private Configuration newConfigModpack;
	private File zipFileFolder;
	
	private File stockMcJar;
	private File patchedMcJar;
	private boolean stockMcJarExists;
	private boolean stockMcJarOutOfDate;
	private boolean patchRequired;
	private boolean jarmodsOutOfDate;
	private java.io.File nativesZip;
	private File nativesFolder;
	private String nativesString;
	private boolean updateNatives;
	private boolean forceUpdate;
	
	private File dataFolder;
	private java.io.File backupFolder;
	private java.io.File savesBackup;
	private java.io.File optionsBackup;
	
	public GameUpdate(ModpackUpdater updater, String dataFolderPath, String modpackName, boolean forceUpdate) {
		this.updater = updater;
		DATA_FOLDER_PATH = dataFolderPath;
		MODPACK_URL = UPDATE_URL + modpackName;
		this.forceUpdate = forceUpdate;
		
		dataFolder = new File(DATA_FOLDER_PATH);
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		
		try {
			PrintStream out = new PrintStream(new FileOutputStream(new java.io.File(dataFolderPath, "output.txt")));
			PrintStream err = new PrintStream(new FileOutputStream(new java.io.File(dataFolderPath, "error.txt")));
			System.setErr(err);
			System.setOut(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		
		modpackFolders = new HashMap<String, File>();
		modpackZips = new HashMap<String, java.io.File>();
		existingFolders = new HashMap<String, Boolean>();
		updateFolders = new HashMap<String, Boolean>();
		
		zipFileFolder = new File(DATA_FOLDER_PATH, "zips");
		if (!zipFileFolder.exists()) {
			zipFileFolder.mkdirs();
		}
		stockMcJar = new File(zipFileFolder, "minecraft.jar");
		patchedMcJar = new File(DATA_FOLDER_PATH + File.separator + "bin", "minecraft.jar");
		nativesFolder = new File(DATA_FOLDER_PATH + File.separator + "bin", "natives");
		System.out.println("Updater initialised");
	}
	
	private void backup() {
		if (dataFolder.exists()) {
			if (forceUpdate) {
				try {
					updater.setStatusText("Backing up");
					backupFolder = new java.io.File(dataFolder.getParentFile(), "backup");
					if (!backupFolder.exists()) {
						backupFolder.mkdir();
					}
					savesBackup = new java.io.File(dataFolder, "saves");
					optionsBackup = new java.io.File(dataFolder, "options.txt");
					
					if (directoryContains(dataFolder, savesBackup)) {
						if (savesBackup.exists()) {
							System.out.println(new java.io.File(backupFolder, savesBackup.getParentFile().getName()+File.separator+savesBackup.getName()).getAbsolutePath());
							moveDirectory(savesBackup, new java.io.File(backupFolder, savesBackup.getParentFile().getName()+File.separator+savesBackup.getName()));
						}
					}
					
					if (directoryContains(dataFolder, optionsBackup)) {
						if (optionsBackup.exists()) {
							moveFile(optionsBackup, new java.io.File(backupFolder, optionsBackup.getParentFile().getName()+File.separator+optionsBackup.getName()));
						}
					}
					cleanDirectory(zipFileFolder);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void updateModpackConfig() {
		System.out.println("Updating modpack config");
		modpackConfigFile = new File(DATA_FOLDER_PATH, "modpack-config");
		newModpackConfigFile = new File(DATA_FOLDER_PATH, "modpack-config");
		try {
			
			
			if (modpackConfigFile.exists()) {
				configModpack = new Configuration(new FileInputStream(modpackConfigFile));
				modpackFoldersConfig = configModpack.getNode("folders");
			}
			
			updater.setStatusFine("Updating modpack config");
			Download.copyURLToFile(new URL(MODPACK_URL + "/modpack-config"), newModpackConfigFile, this);

			newConfigModpack = new Configuration(new FileInputStream(newModpackConfigFile));
			newModpackFoldersConfig = newConfigModpack.getNode("folders");
			for (String key : newModpackFoldersConfig.keySet()) {
				System.out.println(key);
			}
			newConfigModpack = new Configuration(new FileInputStream(newModpackConfigFile));
			mcConfig = newConfigModpack.getNode("mcconfig");
			System.out.println("Config loaded");
			
			for (String key : newModpackFoldersConfig.keySet()) {
				modpackFolders.put(key, new File(DATA_FOLDER_PATH, key));
				modpackZips.put(key, new java.io.File(zipFileFolder, newModpackFoldersConfig.get(key)[0]));
			}
			
			String os = System.getProperty("os.name");
			if (os.toLowerCase().contains("windows")) {
				nativesZip = new java.io.File(zipFileFolder, "natives-win.zip");
				nativesString = "natives-win";
			}
			else {
				nativesZip = new java.io.File(zipFileFolder, "natives-mac-lin.zip");
				nativesString = "natives-mac-lin";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void checkExistingFolders() {
		System.out.println("Checking existing");
		updater.setStatusFine("Checking folders");
		// First check which any required folders exist
		for (String key : modpackFolders.keySet()) {
			existingFolders.put(key, modpackFolders.get(key).exists());
		}
	}
	
	public void checkUpdates() {
		updater.setStatusFine("Checking files");
		try {
			// Iterate over each folder, if the folder exists check it's md5 to the server and decide whether to update, otherwise force update if it does not exist
			for (String key : modpackFolders.keySet()) {
				if (modpackZips.get(key).exists()) {
					if (modpackFoldersConfig.containsKey(key)) {
						updateFolders.put(key, !(modpackFoldersConfig.get(key)[1].equals(newModpackFoldersConfig.get(key)[1])));
					}
					else {
						updateFolders.put(key, true);
					}
				}
				else {
					updateFolders.put(key, true);
				}
			}
			
			if (nativesString != null) {
				if (!nativesFolder.exists()) {
					updateNatives = true;
				}
				else if (nativesZip.exists()) {
					updateNatives = (!DigestUtils.md5Hex(new URL(UPDATE_URL + nativesString + ".zip").openStream()).equals(DigestUtils.md5Hex(new FileInputStream(nativesZip))));
				}
				else {
					updateNatives = true;
				}
			}
			
			if (updateNatives) {
				System.out.println("Updating natives");
			}
			else {
				System.out.println("Not updating natives");
			}
			
			if (mcConfig.containsKey("mcjar")) {
				if (stockMcJar.exists()) {
					stockMcJarExists = true;
					if (DigestUtils.md5Hex(new FileInputStream(stockMcJar)).equals(mcConfig.get("mcjar")[1])) {
						stockMcJarOutOfDate = false;
						System.out.println("mc jar up to date");
					}
					else {
						stockMcJarOutOfDate = true;
						System.out.println("mc jar out of date");
					}
				}
				else {
					stockMcJarExists = false;
					stockMcJarOutOfDate = true;
					System.out.println("mc jar doesn't exist");
				}
				
			}
			
			if (mcConfig.containsKey("mcrequired")) {
				if (!mcConfig.get("mcrequired")[0].equals(mcConfig.get("mcjar")[0])) {
					System.out.println("patch required");
					patchRequired = true;
				}
				else {
					patchRequired = false;
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void applyUpdates() {
		// Create any folders that don't exist
		/*
		for (String key : modpackFolders.keySet()) {
			if (!existingFolders.get(key)) {
				modpackFolders.get(key).mkdir();
			}
		}
		*/
		
		try {
			//Download zips for install
			for (String key : modpackFolders.keySet()) {
				if (updateFolders.get(key)) {
					
					URL url = new URL(MODPACK_URL + "/"+newModpackFoldersConfig.get(key)[0]);
					
					System.out.println("Downloading " + modpackZips.get(key).getName());
					updater.setStatusFine("Downloading " + modpackZips.get(key).getName());
					Download.copyURLToFile(url, modpackZips.get(key), this);
					//new Download(url, modpackZips.get(key), this).run();
				}
			}
			
			if (updateNatives) {
				System.out.println("Downloading natives");
				updater.setStatusFine("Downloading natives");
				
				Download.copyURLToFile(new URL(UPDATE_URL + nativesString + ".zip"), nativesZip, this);
				//new Download(new URL(UPDATE_URL + nativesString + ".zip"), modpackZips.get(nativesString), this).run();
				nativesZip = null;
				//new Unzip(modpackZips.get(nativesString), new File(DATA_FOLDER_PATH + File.separator + "bin" + File.separator + "natives")).run();
			}
			
			// Unpack zips and install new files
			for (String key : modpackFolders.keySet()) {
				if (updateFolders.get(key)) {
					//updater.setStatusFine("Installing " + key);
					if (key.equals("jarmods")) {
						jarmodsOutOfDate = true;
					} 
					else {
						System.out.println("Unpacking " + key);
						updater.setStatusFine("Unpacking " + key);
						File zip = new File(modpackZips.get(key).getAbsolutePath());
						if (key.equals("root")) {
							zip.archiveCopyAllTo(new File(DATA_FOLDER_PATH));
							//new Unzip(modpackZips.get(key), new File(DATA_FOLDER_PATH)).run();
						}
						else {
							deleteDirectory(modpackFolders.get(key));
							zip.archiveCopyAllTo(modpackFolders.get(key));
							//new Unzip(modpackZips.get(key), modpackFolders.get(key)).run();
						}
						File.umount(zip);
						zip = null;
					}
				}
			}
			
			if (updateNatives) {
				File natives = new File(zipFileFolder, nativesString+".zip");
				System.out.println("Unpacking natives");
				updater.setStatusFine("Unpacking natives");
				nativesFolder.mkdir();
				if (natives.archiveCopyAllTo(nativesFolder)) {
					System.out.println("Natives installed");
				}
				else {
					System.out.println("Natives install failed");
				}
			}
			
			URL mcURL = new URL(MC_DL_URL + "minecraft.jar");
			if (stockMcJarExists) {
				updater.setStatusFine("Checking Minecraft version");
				if (DigestUtils.md5Hex(new FileInputStream(stockMcJar)).equals(mcConfig.get("mcjar")[1])) {
					stockMcJarOutOfDate = false;
				}
				else {
					stockMcJarOutOfDate = true;
				}
			}
			else {
				//new Download(mcURL, stockMcJar, this).run();
				stockMcJarOutOfDate = true;
			}
			
			if (stockMcJarOutOfDate || !stockMcJarExists) {
				System.out.println("Downloading stock mc jar");
				updater.setStatusFine("Downloading minecraft.jar");
				Download.copyURLToFile(mcURL, stockMcJar, this);
			}
			
			if (patchRequired && stockMcJarOutOfDate) {
				patchMcJar();
			}
			else if (!patchRequired && !patchedMcJar.exists()) {
				System.out.println("Copying stock mc jar");
				stockMcJar.copyAllTo(patchedMcJar);
			}
			/*
			if ((stockMcJarOutOfDate || !stockMcJarExists)) {
				//System.out.println("mc jar out of date - jar doesn't exist");
				//copyURLToFile(mcURL, stockMcJar);
				//new Download(mcURL, stockMcJar, this).run();
				stockMcJar.copyTo(patchedMcJar);
				//FileUtils.copy(stockMcJar, new FileOutputStream(patchedMcJar));
			}
			*/
			/*
			if ((stockMcJarExists && !patchedMcJar.exists())) {
				stockMcJar.copyTo(patchedMcJar);
				//FileUtils.copy(stockMcJar, new FileOutputStream(patchedMcJar));
				if (patchRequired) {
					patchMcJar();
				}
				installJarmods();
			}
			
			if (!stockMcJarExists && patchRequired) {
				patchMcJar();
			}
			*/
			
			if (jarmodsOutOfDate) {
				//FileUtils.copy(stockMcJar, new FileOutputStream(patchedMcJar));
				
				if (patchRequired) {
					patchMcJar();
				}
				
				installJarmods();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void patchMcJar() {
		System.out.println("Patching mc jar");
		updater.setStatusFine("Patching minecraft.jar");
		java.io.File mcPatchFile = new java.io.File(modpackFolders.get("bin"), "mc.patch");
		try {
			File.umount(stockMcJar);
			File.umount(patchedMcJar);
			
			java.io.File stockMcJar = new java.io.File(zipFileFolder, "minecraft.jar");
			java.io.File patchedMcJar = new java.io.File(DATA_FOLDER_PATH + File.separator + "bin", "minecraft.jar");
			
			URL patchFileURL = new URL("http://mirror.technicpack.net/Technic/Patches/Minecraft/minecraft_" + mcConfig.get("mcjar")[0] + "-" + mcConfig.get("mcrequired")[0] + ".patch");
			Download.copyURLToFile(patchFileURL, mcPatchFile, this);
			
			JBPatch.bspatch(stockMcJar, patchedMcJar, mcPatchFile);
			
			patchedMcJar = null;
			stockMcJar = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void installJarmods() {
		try {
			if (patchedMcJar.exists())	{
				
				if (modpackZips.get("jarmods").exists()) {
					System.out.println("Installing jarmods");
					updater.setStatusFine("Installing jarmods");
					
					for (java.io.File file : patchedMcJar.listFiles()) {
						if (file.isDirectory()) {
							if (file.getName().equals("META-INF")) {
								try {
									org.apache.commons.io.FileUtils.deleteDirectory(file);
									System.out.println("META-INF Deleted");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
					File jarmods = new File(modpackZips.get("jarmods"));
					if (jarmods.archiveCopyAllTo(patchedMcJar)) {
						System.out.println("Jarmods successfully installed");
					}
					else {
						System.out.println("Install of jarmods(forge) failed");
					}
					File.umount(true);
					updateFolders.put("jarmods", false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		System.out.println("Updater started");
		backup();
		updater.setStatusText("Checking for updates");
		updateModpackConfig();
		checkExistingFolders();
		checkUpdates();
		updater.setStatusText("Updating");
		applyUpdates();
		System.out.println("Updating done!");
		updater.setStatusFine("");
		updater.setStatusText("Finished!");
		
		if (forceUpdate) {
			try {
				for (java.io.File rootFile : backupFolder.listFiles()) {
					if (rootFile.getName().equals(this.dataFolder.getName()) && rootFile.isDirectory()) {
						for (java.io.File file : rootFile.listFiles()) {
							moveToDirectory(file, dataFolder, true);
						}
					}
					deleteDirectory(rootFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			File.umount();
		} catch (ArchiveException e) {
			e.printStackTrace();
		}
		
		updater.setFinished();
	}

	public void updateProgress(int i) {
		updater.setProgress(i);
	}
}
