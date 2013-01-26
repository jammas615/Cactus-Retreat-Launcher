package com.cactusretreat.cactuslauncher;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.cactusretreat.cactuslauncher.gui.ModpackUpdater;

public class GameLauncher {

	private String username;
	private String sessionID;
	private String dataFolderPath;
	private String modpackName;
	private boolean forceUpdate;
	private String title;
	
	public GameLauncher(String [] args) {
		this.username = args[0];
		this.sessionID = args[1];
		this.dataFolderPath = args[2];
		this.modpackName = args[3];
		if (args[4].equals("true")) {
			this.forceUpdate = true;
		}
		else {
			this.forceUpdate = false;
		}
		this.title = args[5];
		
		run();	
	}
	
	private void run() {
		ModpackUpdater updater = new ModpackUpdater(dataFolderPath, modpackName, forceUpdate);
		updater.start();
		
		LauncherFrame launcher = new LauncherFrame(dataFolderPath, title);
		launcher.startGame(username, sessionID);	
	}
	
	public static void main(String [] args) {
		
		
		if (args.length == 6){
			System.out.println("Modpack updater started");
			new GameLauncher(args);
		}
		else {
			System.out.println("Invalid launch args");
			System.exit(0);
		}
	}

}
