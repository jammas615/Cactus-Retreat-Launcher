package com.cactusretreat.cactuslauncher.gui;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.cactusretreat.cactuslauncher.CactusLauncher;
import com.cactusretreat.cactuslauncher.Messages;

public class DialogOptions {

	private Display display;
	private Shell parent;
	private Shell shell;
	private MainWindow window;

	private Composite ramComposite;
	
	private GridLayout layout;
	private GridData data;
	private RowLayout radioCompositeLayout;
	
	private Combo selectRam;
	private String[] ramList;
	private Button ram512m;
	private Button ram1G;
	private Button ram2G;
	private Button ram4G;
	private Button ram8G;
	private Button ram16G;
	
	private Button forceUpdate;
	private Button checkKeepLauncherOpen;
	private Button okay;
	
	private boolean isOpen;
	private boolean okPressed;
	
	private HashMap<String, String[]> settings;
	private FontData fontTextData;
	private Font fontText;
	private FontData fontTitleData;
	private Font fontTitle;
	
	public DialogOptions(Shell parent, MainWindow window) {
		this.parent = parent;
		this.window = window;
		this.settings = window.getSettings();
		init();
		shell.pack();
		addShellListener();
		shell.open();
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	private void init() {
		fontTextData = new FontData("fontText", 9, SWT.NONE);
		fontText = new Font(display, fontTextData);
		
		fontTitleData = new FontData("fontTitle", 16, SWT.NONE);
		fontTitle = new Font(display, fontTitleData);
		
		display = parent.getDisplay();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("Options");
		layout = new GridLayout(1, false);
		data = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
		shell.setLayout(layout);
		shell.setLayoutData(data);
		
		setupRamButtons();
		
		new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		data = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		data.widthHint = 90;
		forceUpdate = new Button(shell, SWT.PUSH | SWT.TOGGLE);
		
		if (window.isForcingUpdate()) {
			setForceUpdate();
		}
		else {
			forceUpdate.setText("Force update");
		}
		forceUpdate.addSelectionListener(new ButtonListener());
		forceUpdate.setLayoutData(data);
		
		new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		data = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		checkKeepLauncherOpen = new Button(shell, SWT.CHECK);
		checkKeepLauncherOpen.setText("Keep launcher open");
		if (settings.containsKey("keepLauncherOpen")) { 
			String keepOpen = settings.get("keepLauncherOpen")[0];
			if (keepOpen.equals("true")) {
				checkKeepLauncherOpen.setSelection(true);
			}
			else {
				checkKeepLauncherOpen.setSelection(false);
			}
		}
		
		new Label(shell, SWT.NONE);
		data = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		data.widthHint = 90;
		okay = new Button(shell, SWT.PUSH);
		okay.setAlignment(SWT.CENTER);
		okay.setText("OK");
		okay.addSelectionListener(new ButtonListener());
		okay.setFont(fontText);
		okay.setLayoutData(data);
	}
	
	private void setupRamButtons() {
		new Label(shell, SWT.NONE).setText("Minecraft memory allocation: ");		
		selectRam = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
		ramList = new String[7];
		
		ramList[0] = "512MB";
		selectRam.setData("512MB", "512");
		ramList[1] = "1GB";
		selectRam.setData("1GB", "1024");
		ramList[2] = "1.5GB";
		selectRam.setData("1.5GB", "1536");
		ramList[3] = "2GB";
		selectRam.setData("2GB", "2048");
		ramList[4] = "4GB";
		selectRam.setData("4GB", "4096");
		ramList[5] = "8GB";
		selectRam.setData("8GB", "8192");
		ramList[6] = "16GB";
		selectRam.setData("16GB", "16384");
		
		selectRam.setItems(ramList);
		selectRam.addSelectionListener(new ComboSelectionListener());
		String ram = settings.get("ram")[0];
		for (int i = 0; i < ramList.length; i++) {
			if (selectRam.getData(ramList[i]).equals(ram)) {
				selectRam.select(i);
			}
		}
	}
	
	private void setForceUpdate() {
		forceUpdate.setSelection(true);
		forceUpdate.setText("Forcing update");
		forceUpdate.setEnabled(false);
	}
	
	public void update() {
		while (!display.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	private void addShellListener() {
		shell.addShellListener(new ShellListener() {
			public void shellActivated(ShellEvent e) {}
			public void shellDeactivated(ShellEvent e) {}
			public void shellDeiconified(ShellEvent e) {}
			public void shellIconified(ShellEvent e) {}
			
			@Override
			public void shellClosed(ShellEvent e) {
				if (okPressed) {
					if (selectRam.getSelectionIndex() > -1) {
						settings.put("ram", new String [] {(String) selectRam.getData(ramList[selectRam.getSelectionIndex()])});
						settings.put("keepLauncherOpen", new String [] {String.valueOf(checkKeepLauncherOpen.getSelection())});
					}
					window.changeSettings(settings);
				}
			}
			
		});
	}
	
	private class ButtonListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource().equals(okay)) {
				okPressed = true;
				shell.close();
			}
			if (e.getSource().equals(forceUpdate)) {
				setForceUpdate();
				window.forceUpdate();
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {			
		}
		
	}
	
	private class ComboSelectionListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource().equals(selectRam)) {
				if (selectRam.getSelectionIndex() > -1 && selectRam.getSelectionIndex() > 2 && CactusLauncher.OS_ARCH == 32) {
					MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					msg.setText("Error!");
					msg.setMessage(Messages.RAM_SELECTION_INVALID);
					msg.open();
					selectRam.select(2);
				}
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {			
		}
		
	}
}
