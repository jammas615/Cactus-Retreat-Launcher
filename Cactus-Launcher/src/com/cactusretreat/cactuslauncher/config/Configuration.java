package com.cactusretreat.cactuslauncher.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Configuration {

	private String filename;
	private InputStream is;
	private File file;
	
	public Configuration(InputStream is) {
		this.is = is;
	}
	
	public Configuration(File file) {
		this.file = file;
	}
	
	public HashMap<String, String[]> getConfig() {
		HashMap<String, String[]> data = new HashMap<String, String[]>();
		/*
		try {
			//file = new File(filename);
			
			String str = null;
			
			while ((str = br.readLine()) != null) {
				String label = str.substring(0, str.indexOf(":"));
				String value = str.substring(str.indexOf(":"));
				//data.put(label, value);
			}
			br.close();
			return data;
		} catch(Exception e) {
			e.printStackTrace();
		}
		*/
		return null;
		
	}
	
	public HashMap<String, String[]> getNode(String node) {
		HashMap<String, String[]> data = new HashMap<String, String[]>();
		
		try {
			//file = new File(filename);
			BufferedReader br;
			if (file != null) {
				br = new BufferedReader(new FileReader(file));
			}
			else {
				br = new BufferedReader(new InputStreamReader(is));
			}
			//InputStreamReader isr = new InputStreamReader(is);
			//BufferedReader br = new BufferedReader(isr);
			String str = null;
			try {
				while ((str = br.readLine()) != null) {
					if (str.startsWith("#") && (str.substring(1).equals(node))) {
						while ((str = br.readLine()) != null) {
							if (!(str.startsWith("#"))) {
								String [] tokens = str.split("::");
								String [] values = new String [tokens.length-1];
								String id = tokens[0];
								for (int i = 0; i < tokens.length-1; i++) {
									values[i] = tokens[i+1];
								}
								data.put(id, values);
							}
							else {
								break;
							}
						}
					}
					
				}
			} finally {
				//isr.close();
				br.close();
			}
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void writeConfig(File file, String node, HashMap<String, String[]> config, boolean append) {
		try {
			file.delete();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
			bw.write("#" + node);
			for (String label : config.keySet()) {
				String [] values = config.get(label);
				bw.newLine();
				bw.write(label);
				for (int i = 0; i < values.length; i++) {
					bw.write("::" + values[i]);
				}
				
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
