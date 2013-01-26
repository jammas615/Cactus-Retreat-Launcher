package com.cactusretreat.cactuslauncher.gui;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cactusretreat.cactuslauncher.CactusLauncher;
import com.cactusretreat.cactuslauncher.ForumLogin;
import com.cactusretreat.cactuslauncher.Messages;
import com.cactusretreat.cactuslauncher.exception.ServerDownException;

public class DialogForumLogin {

	private static String HEAD_TEXT = "Please use your Cactus Retreat forums username and password to login to the Cactus Launcher.";
	public static final int SHELL_TRIM = SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX;
	private MainWindow mainWindow;
	private Display display;
	private Shell shell;
	
	private Composite wrapper;
	private Composite header;
	private Composite loginForm;
	private GridData data;
	
	private Label labelHeadText;
	private Label labelUsername;
	private Label labelPassword;
	private Text textUsername;
	private Text textPassword;
	private Button buttonOkay;
	private Button buttonCancel;
	private FontData fontTextData;
	private Font fontText;
	
	public DialogForumLogin(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		init();
	}
	
	public void close() {
		if (!shell.isDisposed()) {
			shell.dispose();
		}
	}
	
	public void start() {
		shell.open();
		run();
	}
	
	private void init() {
		shell = new Shell(SHELL_TRIM);
		shell.setSize(320, 220);
		shell.setText("Login to Cactus Retreat");
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		Image icon = new Image(shell.getDisplay(), this.getClass().getResourceAsStream("cactus.png"));
		shell.setImage(icon);
		
		GridLayout shellLayout = new GridLayout(1, false);
		shellLayout.marginWidth = 10;
		shellLayout.marginHeight = 10;
		wrapper = new Composite(shell, SWT.NONE);
		wrapper.setLayout(shellLayout);
		
		fontTextData = new FontData("fontText", 9, SWT.BOLD);
		fontText = new Font(shell.getDisplay(), fontTextData);
		
		GridData headData = new GridData(SWT.FILL, SWT.FILL, true, true);
		headData.widthHint = 300;
		header = new Composite(wrapper, SWT.NONE);
		header.setLayout(new GridLayout(1, false));
		header.setLayoutData(headData);
		labelHeadText = new Label(header, SWT.WRAP);
		labelHeadText.setText(HEAD_TEXT);
		labelHeadText.setLayoutData(headData);
		
		GridLayout layout = new GridLayout(4, false);
		loginForm = new Composite(wrapper, SWT.NONE);
		loginForm.setLayout(layout);
		loginForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		loginForm.setFont(fontText);
		
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 2;
		data.heightHint = 20;
		labelUsername = new Label(loginForm, SWT.NONE);
		labelUsername.setText("Username: ");
		labelUsername.setLayoutData(data);
		labelUsername.setFont(fontText);
		
		textUsername = new Text(loginForm, SWT.NONE);
		textUsername.setLayoutData(data);
		textUsername.setFont(fontText);
		
		labelPassword = new Label(loginForm, SWT.NONE);
		labelPassword.setText("Password: ");
		labelPassword.setLayoutData(data);
		labelPassword.setFont(fontText);
		
		textPassword = new Text(loginForm, SWT.PASSWORD);
		textPassword.setLayoutData(data);
		textPassword.setFont(fontText);
		
		new Label(loginForm, SWT.NONE).setLayoutData(data);
		new Label(loginForm, SWT.NONE).setLayoutData(data);
		new Label(loginForm, SWT.NONE).setLayoutData(data);

		
		data = new GridData(SWT.END, SWT.CENTER, true, false);
		data.widthHint = 90;
		data.horizontalSpan = 1;
		buttonOkay = new Button(loginForm, SWT.PUSH);
		buttonOkay.setText("Login");
		buttonOkay.setLayoutData(data);
		buttonOkay.addSelectionListener(new ButtonListener());
		
		buttonCancel = new Button(loginForm, SWT.PUSH);
		buttonCancel.setText("Cancel");
		buttonCancel.setLayoutData(data);
		buttonCancel.addSelectionListener(new ButtonListener());
	}
	
	private void run() {
		while(!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}
		shell.dispose();
	}
	
	private class ButtonListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource().equals(buttonOkay)) {
				if (!textUsername.getText().equals("") && !textPassword.getText().equals("")) {
					int response;
					try {
						response = mainWindow.doForumLogin(textUsername.getText(), textPassword.getText(), true);
						
						if (response > -1) {
							close();
						}
						else {
							MessageBox msg = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
							msg.setText("Error!");
							msg.setMessage(Messages.LOGIN_INCORRECT);
							msg.open();
						}
						
					} catch (ServerDownException e1) {
						MessageBox msg = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
						msg.setText("Error!");
						msg.setMessage(Messages.CACTUS_SERVER_DOWN);
						msg.open();
					}
					
				}
				else {
					MessageBox msg = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
					msg.setText("Error!");
					msg.setMessage(Messages.LOGIN_MISSING_REQUIRED);
					msg.open();
				}
					
			}
			
			if (e.getSource().equals(buttonCancel)) {
				MessageBox msg = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
				msg.setText("Error!");
				msg.setMessage(Messages.MUST_LOGIN_TO_FORUMS);
				msg.open();
				shell.close();
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {			
		}
		
	}
}
