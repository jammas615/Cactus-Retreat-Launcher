package com.cactusretreat.cactuslauncher.exception;

public class DownloadException extends Exception {

	private String fileName;
	
	public DownloadException(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFile() {
		return this.fileName;
	}
}
