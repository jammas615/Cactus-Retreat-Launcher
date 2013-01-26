package com.cactusretreat.cactuslauncher;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFrame;

import net.minecraft.Launcher;

public class LauncherFrame extends JFrame implements WindowListener {

	private Applet containerApplet;
	private String modpackDir;
	private String title;
	private Launcher mc;
	
	public LauncherFrame(String dataFolderPath, String title) {
		this.modpackDir = dataFolderPath;
		this.title = title;
		File favicon = new File(modpackDir, "favicon.png");
		if (favicon.exists()) {
			Image icon = Toolkit.getDefaultToolkit().getImage(favicon.getAbsolutePath());
			this.setIconImage(icon);
		}
		this.setTitle(title);
		this.toFront();
		super.setVisible(true);
		setSize(new Dimension(871, 519));
		setResizable(true);
		addWindowListener(this);
	}
	
	public int startGame(String user, String sessionID) {
		Applet mcApplet = null;
		
		try {
			mcApplet = LauncherUtil.getMCApplet(modpackDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mc = new Launcher(mcApplet);

		mc.addParameter("username", user);
		mc.addParameter("sessionid", sessionID);
		
		mcApplet.setStub(mc);
		this.add(mc);
		validate();
		

		try {
			mc.init();
			mc.setSize(getWidth(), getHeight());
			mc.start();
			
		} catch (Throwable t) {
			t.printStackTrace();
			return 0;
		}
		
		this.setVisible(true);
		return 1;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		System.out.println("Window closing called");
		LauncherFrame.this.mc.stop();
		LauncherFrame.this.mc.destroy();
		
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {		
	}
}
