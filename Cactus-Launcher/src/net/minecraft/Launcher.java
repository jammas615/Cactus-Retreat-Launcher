package net.minecraft;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Launcher extends Applet implements AppletStub {


	private static final long serialVersionUID = 5995041256173941476L;
	private Applet mcApplet;
	private URL mcDocumentBase;
	private final Map<String, String> customParameters;
	private boolean active = false;

	public Launcher() {
		this.customParameters = new HashMap<String, String>();
	    this.setLayout(new GridBagLayout());
	}
	
	public Launcher(Applet mcApplet) {
		this();
		this.mcApplet = mcApplet;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = gbc.BOTH;
		gbc.anchor = gbc.FIRST_LINE_START;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(0, 0, 0, 0);
		this.add(mcApplet, gbc);
	}
	
	@Override
	public boolean isActive() {
		return active;
	}
	
	public void addParameter(String param, String value) {
		this.customParameters.put(param, value);
	}
	
	@Override
	public String getParameter(String param) {
		if (customParameters.containsKey(param)) {
			return customParameters.get(param);
		}
		else {
			customParameters.put(param, null);
		}
		return null;
	}
	
	@Override
	public void appletResize(int width, int height) {
		mcApplet.resize(width, height);
	}

	@Override
	public void init() {
		if (mcApplet != null) {
			mcApplet.init();
		}
	}
	
	@Override
	public void start() {
		if (mcApplet != null) {
			mcApplet.start();
			active = true;
		}
	}
	
	@Override
	public void stop() {
		if (mcApplet != null) {
			mcApplet.stop();
		}
	}
	
	public void replace(Applet newApplet) {
		this.mcApplet = newApplet;
		
		newApplet.setStub(this);
		newApplet.setSize(getWidth(), getHeight());
		
		this.setLayout(new BorderLayout());
		this.add(newApplet, "Center");
		newApplet.init();
		active = true;
		newApplet.start();
		validate();
	}
	
	@Override
	public URL getCodeBase() {
		return mcApplet.getCodeBase();
	}
	
	@Override
	public URL getDocumentBase() {
		if (mcDocumentBase == null) {
			try {
				mcDocumentBase = new URL("http://minecraft.net/game");
			} catch (MalformedURLException e) {
			}
		}
		return mcDocumentBase;
	}
	
	 @Override
	  public void resize(int width, int height) {
	    mcApplet.resize(width, height);
	  }

	  @Override
	  public void resize(Dimension d) {
	    mcApplet.resize(d);
	  }

	  @Override
	  public void setVisible(boolean state) {
	    super.setVisible(state);
	    mcApplet.setVisible(state);
	  }
}
