package com.cactusretreat.cactuslauncher.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cactusretreat.cactuslauncher.GameUpdate;

public class FileUtils {

	//private static GameUpdate updater;
	
	public static void copy(InputStream in, double inputSize, OutputStream out) throws IOException {
		//double copied = 0;
		byte[] buffer = new byte[8192];
		while (true) {
			int readCount = in.read(buffer);
			if (readCount < 0) {
				break;
			}
			out.write(buffer, 0, readCount);
			//copied += buffer.length;
			//updater.get().updateProgress((int)(copied/inputSize)*100);
		}
	}
	
	public static void copy(File file, OutputStream out) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			copy(in, file.getTotalSpace(), out);
		} finally {
			in.close();
		}
	}
	
	public static void copy(InputStream in, double inputSize, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			copy(in, inputSize, out);
		} finally {
			out.close();
		}
	}
	
	public static boolean isEmpty(File file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			if (br.readLine() == null) {
				return true;
			}
			else {
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
