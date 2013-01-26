package com.cactusretreat.cactuslauncher.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.eclipse.swt.widgets.Display;

import com.cactusretreat.cactuslauncher.GameUpdate;

import static org.apache.commons.io.FileUtils.copyURLToFile;

public class Download implements Runnable {

	private GameUpdate updater;
	private URL url;
	private File file;
	
	public Download(URL url, File file, GameUpdate updater) {
		this.updater = updater;
		this.url = url;
		this.file = file;
	}
	
	@Override
	public void run() {
		
		try {
			copyURLToFile(url, file);
			/*
			double size = url.openConnection().getContentLength();
			BufferedInputStream is = new BufferedInputStream(url.openStream());
			FileOutputStream fout = new FileOutputStream(file);
			try {
				byte [] data = new byte[1024];
				int count;
				double transferred = 0;
	    		while ((count = is.read(data, 0, 1024)) != -1) {
	    			fout.write(data, 0, count);
	    			transferred += data.length;
	    			updater.updateProgress((int) ((transferred/size)*100));
	    		}
				
			}
			finally {
				is.close();
				fout.close();
			}
			*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
