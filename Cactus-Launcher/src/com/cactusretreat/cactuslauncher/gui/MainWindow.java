package com.cactusretreat.cactuslauncher.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.util.Base64;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cactusretreat.cactuslauncher.CactusLauncher;
import com.cactusretreat.cactuslauncher.ForumLogin;
import com.cactusretreat.cactuslauncher.LauncherStarter;
import com.cactusretreat.cactuslauncher.MCLogin;
import com.cactusretreat.cactuslauncher.Messages;
import com.cactusretreat.cactuslauncher.ServerStats;
import com.cactusretreat.cactuslauncher.UpdateNews;
import com.cactusretreat.cactuslauncher.exception.ServerDownException;

public class MainWindow {
	
	private static final String HEADER_TEXT = "Welcome to the Cactus Retreat Launcher!\n" +
			"To begin, select a modpack from the drop down menu to the right.";
	private static String NEWS;
	private static String NEWS_LINES;
	private static int USER_RANK;
	private Shell shell;
	private CactusLauncher launcher;
	private DialogForumLogin forumLogin;
	private DialogOptions dialogOptions;
	
	private GridLayout layout;
	private GridData data;
	private Composite windowComposite;
	private Composite header;
	private Composite rightCol;
	private Composite leftCol;
	
	private Composite leftHeaderComposite;
	private Composite leftHeaderButtonComposite;
	private Composite leftHeaderTextComposite;
	private Button websiteButtonLink;
	private Button forumsButtonLink;
	
	private Composite newsComposite;
	private Composite newsHeaderComposite;
	private ScrolledComposite newsTextContainer;
	private Composite newsTextComposite;
	private Composite newsTextInner;
	private Link newsText;
	
	private Composite loginForm;
	private Composite modpackSelectForm;
	private Composite modpackInfoPanel;
	private Composite modpackInfoPanelLeft;
	private Composite modpackInfoPanelRight;
	
	private Image headerImage;
	
	private Combo selectModpack;
	private Combo username;
	private Text password;
	private Button buttonOptions;
	private Button buttonLaunch;
	private Button savePassword;
	
	private Label labelSelectModpack;
	private Label labelUser;
	private Label labelPass;
	
	private Label infoModpackInstalled;
	private Label infoServerAddress;
	private Label infoPlayersOnline;
	
	private Font fontTitle;
	private Font fontText;
	private FontData fontTextData;
	private FontData fontTitleData;
	
	private HashMap<String, String[]> launcherSettings;
	private HashMap<String, String[]> profiles;
	private HashMap<String, String[]> modpacks; // Keys are the display names of modpacks, not index
	private boolean isForcingUpdate = false;
	
	public MainWindow(CactusLauncher launcher, Shell shell, int rankLevel) {
		this.launcher = launcher;
		this.shell = shell;
		this.USER_RANK = rankLevel;
		launcherSettings = launcher.getSettings();
		String [] str = UpdateNews.getNews();
		NEWS_LINES = str[0];
		NEWS = str[1];
		init();
		initUI();
	}
	
	private void init() {
		String [] login = launcher.getForumLogin();
		if (login == null) {
			forumLogin = new DialogForumLogin(this);
			forumLogin.start();
		}
		else {
			try {
				USER_RANK = doForumLogin(login[0], login[1], false);
				if (USER_RANK <= -1) {
					forumLogin = new DialogForumLogin(this);
					forumLogin.start();
				}
			} catch (ServerDownException e) {
				MessageBox msg = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
				msg.setText("Error!");
				msg.setMessage(Messages.CACTUS_SERVER_DOWN);
				msg.open();
			}
		}
	}
	
	private void initUI() {
		shell.setSize(CactusLauncher.WIDTH, CactusLauncher.HEIGHT);
		shell.setText("Cactus Retreat Launcher");
		shell.setLayout(new FillLayout());
		Image icon = new Image(shell.getDisplay(), this.getClass().getResourceAsStream("cactus.png"));
		shell.setImage(icon);
		
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		windowComposite = new Composite(shell, SWT.NONE);
		windowComposite.setLayout(layout);
		
		data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.horizontalSpan = 2;
		data.heightHint = 126;
		header = new Composite(windowComposite, SWT.CENTER);
		header.setLayoutData(data);
		headerImage = new Image(shell.getDisplay(), this.getClass().getResourceAsStream("banner.jpg"));
		header.setBackgroundImage(headerImage);
		
		fontTextData = new FontData("fontText", 9, SWT.BOLD);
		fontText = new Font(shell.getDisplay(), fontTextData);
		
		fontTitleData = new FontData("fontTitle", 16, SWT.NONE);
		fontTitle = new Font(shell.getDisplay(), fontTitleData);
		
		setupLeftCol();
		setupLoginForm();
		
		setupModpacksCombo();
		loadProfiles();
	}
	
	private void setupLoginForm() {
		data = new GridData(SWT.END, SWT.FILL, true, true);
		data.horizontalSpan = 1;
		rightCol = new Composite(windowComposite, SWT.NONE);
		data.widthHint = 300;
		data.verticalIndent = 5;
		rightCol.setLayoutData(data);
		
		GridLayout layout  = new GridLayout(4, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		rightCol.setLayout(layout);
		
		data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.horizontalSpan = 4;
		
		modpackSelectForm = new Composite(rightCol, SWT.NONE);
		modpackSelectForm.setLayout(layout);
		modpackSelectForm.setLayoutData(data);
	
		labelSelectModpack = new Label(modpackSelectForm, SWT.NONE);
		labelSelectModpack.setText("Select modpack: ");
		labelSelectModpack.setFont(fontText);
		data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.horizontalSpan = 4;
		data.widthHint = 150;
		selectModpack = new Combo(modpackSelectForm, SWT.DROP_DOWN | SWT.READ_ONLY);
		selectModpack.setLayoutData(data);
		selectModpack.addSelectionListener(new ModpackSelectionListener());
		
		setupInfoPanel();
		
		data = new GridData(SWT.END, SWT.END, false, false);
		data.horizontalSpan = 4;
		loginForm = new Composite(rightCol, SWT.NONE);
		loginForm.setLayout(layout);
		loginForm.setLayoutData(data);
		
		labelUser = new Label(loginForm, SWT.NONE);
		labelUser.setText("Username: ");
		labelUser.setFont(fontText);
		data = new GridData();
		data.verticalSpan = 1;
		data.widthHint = 90;
		username = new Combo(loginForm, SWT.DROP_DOWN);
		username.setLayoutData(data);
		username.setFont(fontText);
		username.addSelectionListener(new ProfileChangeListener());
		
		data = new GridData();
		data.horizontalSpan = 2;
		data.heightHint = 23;
		data.widthHint = 80;
		buttonOptions = new Button(loginForm, SWT.NONE);
		buttonOptions.setLayoutData(data);
		buttonOptions.setText("Options");
		buttonOptions.setFont(fontText);
		buttonOptions.addSelectionListener(new ButtonListener());
		
		labelPass = new Label(loginForm, SWT.NONE);
		labelPass.setText("Password: ");
		labelPass.setFont(fontText);
		data = new GridData();
		data.verticalSpan = 1;
		data.widthHint = 110;
		data.heightHint = 20;
		password = new Text(loginForm, SWT.PASSWORD);
		password.setLayoutData(data);
		password.setFont(fontText);
		
		data = new GridData();
		data.horizontalSpan = 2;
		data.heightHint = 23;
		data.widthHint = 80;
		buttonLaunch = new Button(loginForm, SWT.NONE);
		buttonLaunch.setLayoutData(data);
		buttonLaunch.setText("Launch");
		buttonLaunch.setFont(fontText);
		buttonLaunch.addSelectionListener(new ButtonListener());
		
		new Label(loginForm, SWT.NONE);
		data = new GridData();
		data.horizontalSpan = 1;
		savePassword = new Button(loginForm, SWT.CHECK);
		savePassword.setText("Save password");
	}
	
	private void setupInfoPanel() {
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 4;
		modpackInfoPanel = new Composite(rightCol, SWT.NONE);
		modpackInfoPanel.setLayout(layout);
		modpackInfoPanel.setLayoutData(data);
		modpackInfoPanel.setVisible(false);
		
		data = new GridData(SWT.FILL, SWT.FILL, false, true);
		modpackInfoPanelLeft = new Composite(modpackInfoPanel, SWT.NONE);
		modpackInfoPanelLeft.setLayout(new GridLayout(1, false));
		modpackInfoPanelLeft.setLayoutData(data);
		
		data = new GridData(SWT.END, SWT.FILL, true, true);
		Label labelInstalled = new Label(modpackInfoPanelLeft, SWT.NONE);
		labelInstalled.setText("Installed: ");
		labelInstalled.setFont(fontText);
		
		Label serverAddress = new Label(modpackInfoPanelLeft, SWT.NONE);
		serverAddress.setText("Server address: ");
		serverAddress.setFont(fontText);
		
		Label playersOnline = new Label(modpackInfoPanelLeft, SWT.NONE);
		playersOnline.setText("Players online: ");
		playersOnline.setFont(fontText);
		
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		modpackInfoPanelRight = new Composite(modpackInfoPanel, SWT.NONE);
		modpackInfoPanelRight.setLayout(new GridLayout(1, false));
		modpackInfoPanelRight.setLayoutData(data);
		
		infoModpackInstalled = new Label(modpackInfoPanelRight, SWT.NONE);
		infoServerAddress = new Label(modpackInfoPanelRight, SWT.NONE);
		infoPlayersOnline = new Label(modpackInfoPanelRight, SWT.NONE);
	}
	
	private void setupLeftCol() {
		GridLayout layout = new GridLayout(2, false); 
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.verticalIndent = 5;
		data.widthHint = 545;
		leftCol = new Composite(windowComposite, SWT.NONE);
		leftCol.setLayout(layout);
		leftCol.setLayoutData(data);
		
		
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 2;
		leftHeaderComposite = new Composite(leftCol, SWT.NONE);
		leftHeaderComposite.setLayout(layout);
		leftHeaderComposite.setLayoutData(data);
		
		data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.horizontalSpan = 1;
		leftHeaderTextComposite = new Composite(leftHeaderComposite, SWT.NONE);
		leftHeaderTextComposite.setLayout(new GridLayout(1, false));
		leftHeaderTextComposite.setLayoutData(data);
		
		data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.horizontalSpan = 1;
		leftHeaderButtonComposite = new Composite(leftHeaderComposite, SWT.NONE);
		leftHeaderButtonComposite.setLayout(new FillLayout(SWT.VERTICAL));
		leftHeaderButtonComposite.setLayoutData(data);
		
		Label headerLabel = new Label(leftHeaderTextComposite, SWT.NONE);
		headerLabel.setText("Cactus Retreat Launcher");
		headerLabel.setFont(fontTitle);	
		
		Text headerText = new Text(leftHeaderTextComposite, SWT.READ_ONLY | SWT.MULTI);
		headerText.setFont(fontText);
		headerText.setText(HEADER_TEXT);
		
		websiteButtonLink = new Button(leftHeaderButtonComposite, SWT.PUSH);
		websiteButtonLink.setText("Cactus Website");
		websiteButtonLink.setFont(fontText);
		websiteButtonLink.addSelectionListener(new LinkButtonListener());
		
		forumsButtonLink = new Button(leftHeaderButtonComposite, SWT.PUSH);
		forumsButtonLink.setText("Cactus Forums");
		forumsButtonLink.setFont(fontText);
		forumsButtonLink.addSelectionListener(new LinkButtonListener());
		
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.verticalIndent = 15;
		newsComposite = new Composite(leftCol, SWT.NONE);
		newsComposite.setLayoutData(data);
		newsComposite.setLayout(new GridLayout(1, false));
		
		newsHeaderComposite = new Composite(newsComposite, SWT.NONE);
		newsHeaderComposite.setLayout(new FillLayout());
		
		Label newsLabel = new Label(newsHeaderComposite, SWT.NONE);
		newsLabel.setText("News/Updates");
		newsLabel.setFont(fontTitle);
		
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		newsTextComposite = new Composite(newsComposite, SWT.NONE);
		newsTextComposite.setLayoutData(data);
		newsTextComposite.setLayout(new FillLayout());
		newsTextContainer = new ScrolledComposite(newsTextComposite, SWT.V_SCROLL | SWT.BORDER);
		
		RowLayout containerLayout = new RowLayout(SWT.HORIZONTAL);
		containerLayout.wrap = true;
		newsTextContainer.setLayout(containerLayout);
		
		newsText = new Link(newsTextContainer, SWT.WRAP | SWT.MULTI);
		newsText.setLayoutData(data);
		if (NEWS != null) {
			newsText.setText(NEWS);
		}
		else {
			newsText.setText("Could not get news");
		}
		newsText.addSelectionListener(new LinkButtonListener());
		Point size = newsText.computeSize(500, SWT.DEFAULT);
		newsText.setBounds(0, 0, size.x, size.y);
		
		newsTextContainer.setContent(newsText);
		newsTextContainer.setExpandVertical(true);
		newsTextContainer.setExpandHorizontal(true);
		newsTextContainer.setMinSize(size);
	}
	
	private void setupModpacksCombo() {
		this.modpacks = new HashMap<String, String[]>();
		Map<String, String []> modpacksMap = launcher.getModpacks();
		ArrayList<String> modpacks = new ArrayList<String>();
		
		for (String key : modpacksMap.keySet()) {
			int minRank = Integer.parseInt(modpacksMap.get(key)[3]);
			if (USER_RANK >= minRank || USER_RANK == 5) {
				modpacks.add(key);
				
			}
		}
		
		selectModpack.setItems(modpacks.toArray(new String[] {}));
		
		if (launcherSettings.containsKey("lastmodpack")) {
			try {
				selectModpack.select(Integer.parseInt(launcherSettings.get("lastmodpack")[0]));
				showServerStats();
			} catch (Exception e) {
				System.out.println("Could not set last modpack");
			}
		}
	}
	
	public void setRank(int rank) {
		this.USER_RANK = rank;
	}
	
	private void loadProfiles() {
		profiles = launcher.getProfiles();
		if (profiles == null) {
			profiles = new HashMap<String, String[]>();
		}
		String [] usernames = new String [profiles.size()];
		
		if (profiles != null) {
			int i = 0;
			for (String user : profiles.keySet()) {
				usernames[i] = user;
				i++;
			}
			username.setItems(usernames);
			
			if (launcherSettings.containsKey("lastprofile")) {
				String lastUser = launcherSettings.get("lastprofile")[0].trim();
				if (profiles.containsKey(lastUser)) {
					username.setText(lastUser);
					if (profiles.get(lastUser).length > 0) {
						byte [] decoded = Base64.decodeBase64(profiles.get(lastUser)[0]);
						String pass = new String(decoded);
						password.setText(pass);
						savePassword.setSelection(true);
					}
				}
			}
		}
	}
	
	private void saveProfile(String user, String pass) {
		if (!user.trim().equals("")) {
			if (savePassword.getSelection()) {
				profiles.put(user, new String [] {pass});
			}
			else {
				profiles.put(user, new String [] {""});
			}
			launcher.writeProfiles(profiles);
		}
	}
	
	private void writeLastModpackUser(String modpack, String user) {
		launcherSettings.put("lastmodpack", new String [] {modpack});
		launcherSettings.put("lastprofile", new String [] {user});
		launcher.writeSettings(launcherSettings);
	}
	
	private void showServerStats() {
		String selectedModpack = selectModpack.getText();
		if (selectedModpack != null) {
			if (new File(CactusLauncher.DATA_FOLDER_PATH, launcher.getModpacks().get(selectedModpack)[0]).exists()) {
				infoModpackInstalled.setText("Yes");
			}
			else {
				infoModpackInstalled.setText("No");
			}
			String address = launcher.getModpacks().get(selectedModpack)[1];
			String port = launcher.getModpacks().get(selectedModpack)[2];
			
			infoServerAddress.setText(address + ":" + port);
			try {
				infoPlayersOnline.setText(ServerStats.getServerPlayers(address, Integer.parseInt(port)));
			} catch (IOException e) {
				infoPlayersOnline.setText("Server down.");
			}
			modpackInfoPanelRight.layout();
			modpackInfoPanel.setVisible(true);
		}
		else {
			modpackInfoPanel.setVisible(false);
		}
	}
	
	public void openWindow() {
		shell.open();
	}
	
	public HashMap<String, String[]> getSettings() {
		return this.launcherSettings;
	}
	
	public void forceUpdate() {
		if (!isForcingUpdate) {
			isForcingUpdate = true;
			System.out.println("Forcing update!");
		}
	}
	
	public boolean isForcingUpdate() {
		return isForcingUpdate;
	}
	
	public void changeSettings(HashMap<String, String[]> newSettings) {
		this.launcherSettings = newSettings;
		launcher.writeSettings(this.launcherSettings);
	}
	
	private void openDialogOptions() {
		dialogOptions = new DialogOptions(shell, this);
	}
	
	public int doForumLogin(String user, String pass, boolean writeToDisk) throws ServerDownException{
			int response = ForumLogin.doLogin(user, pass);
			if (response > -1) {
				USER_RANK = response;
				if (writeToDisk) launcher.writeForumLogin(user, pass);
			}
			return response;
	}
	
	private void doMCLogin() {
		if (selectModpack.getSelectionIndex() == -1) {
			MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			msg.setText("Error!");
			msg.setMessage(Messages.MODPACK_NOT_SELECTED);
			msg.open();
		}
		else {
			if (username.getText() != "" && password.getText() != "") {
				try {
					MCLogin login = new MCLogin(username.getText(), password.getText());
					if (login.getResponse().equals("OK")) {
						System.out.println("Successfully logged into Minecraft");
						String username = login.getUsername();
						String sessionID = login.getSessionID();
						String modpackName = launcher.getModpacks().get((selectModpack.getText()))[0];
						String dataFolderPath = CactusLauncher.DATA_FOLDER_PATH + File.separator + modpackName;
						int ramAlloc = Integer.parseInt(launcherSettings.get("ram")[0]);
						saveProfile(this.username.getText(), password.getText());
						writeLastModpackUser(String.valueOf(selectModpack.getSelectionIndex()), username);
						System.out.println("Preparing to launch " + modpackName);
						LauncherStarter launcherStarter = new LauncherStarter(username, sessionID, dataFolderPath, modpackName, ramAlloc, isForcingUpdate, selectModpack.getText());
						launcherStarter.launch();
						if (launcherSettings.containsKey("keepLauncherOpen")) {
							if (!Boolean.parseBoolean(launcherSettings.get("keepLauncherOpen")[0])) {
								System.out.println("Closing launcher window");
								launcher.close();
							}
						}
						else {
							System.out.println("Closing launcher window");
							launcher.close();
						}
					}
					else {
						if (login.getResponse().equals("User not premimum")) {
							MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
							msg.setText("Error!");
							msg.setMessage(Messages.LOGIN_NOT_PREMIUM);
							msg.open();
						}
						if (login.getResponse().equals("Bad login")) {
							MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
							msg.setText("Error!");
							msg.setMessage(Messages.LOGIN_INCORRECT);
							msg.open();
						}
					}
				} catch (ServerDownException e) {
					MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					msg.setText("Error!");
					msg.setMessage(Messages.LOGIN_SERVER_DOWN);
					msg.open();
				}
			}
			else {
				MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
				msg.setText("Error!");
				msg.setMessage(Messages.LOGIN_INCOMPLETE);
				msg.open();
			}
		}
	}
	
	private class ButtonListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource().equals(buttonOptions)) {
				openDialogOptions();
			}
			if (e.getSource().equals(buttonLaunch)) {
				doMCLogin();
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {			
		}
		
	}
	
	private class ModpackSelectionListener implements SelectionListener {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource().equals(selectModpack)) {
				showServerStats();
			}
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {	
		}
	}

	private class LinkButtonListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource().equals(websiteButtonLink)) {
				Program.launch("http://www.cactusretreat.com/");
			}
			if (e.getSource().equals(forumsButtonLink)) {
				Program.launch("http://www.cactusretreat.com/forums/");
			}
			if (e.getSource().equals(newsText)) {
				Program.launch(e.text);
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {			
		}
		
	}

	private class ProfileChangeListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource().equals(username)) {
				String user = username.getText();
				if (profiles.containsKey(user)) {
					if (profiles.get(user).length > 0) {
						byte [] decoded = Base64.decodeBase64(profiles.get(user)[0]);
						String pass = new String(decoded);
						password.setText(pass);
						savePassword.setSelection(true);
					}
				}
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
	}
}
