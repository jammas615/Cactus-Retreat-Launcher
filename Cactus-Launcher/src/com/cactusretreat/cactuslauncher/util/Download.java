package com.cactusretreat.cactuslauncher.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import com.cactusretreat.cactuslauncher.GameUpdate;
import com.cactusretreat.cactuslauncher.exception.DownloadException;

public class Download {

	public static void copyURLToFile(URL url, File file, GameUpdate updater) throws DownloadException {
		try {	
			double size = url.openConnection().getContentLength();
			InputStream is = url.openConnection().getInputStream();
			FileOutputStream fout = new FileOutputStream(file);
			try {
				byte [] data = new byte [(int) size];
				int count;
				double transferred = 0;
	    		while ((count = is.read(data)) != -1) {
	    			fout.write(data, 0, count);
	    			transferred += count;
	    			updater.updateProgress((int) ((transferred/size)*100));
	    		}
				
			}
			finally {
				updater.updateProgress(0);
				is.close();
				fout.close();
			}
		} catch (Exception e) {
			throw new DownloadException(file.getName());
		}
	}

}
