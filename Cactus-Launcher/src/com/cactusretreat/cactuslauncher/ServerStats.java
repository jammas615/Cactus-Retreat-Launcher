package com.cactusretreat.cactuslauncher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import com.cactusretreat.cactuslauncher.exception.ServerDownException;

public class ServerStats {

	public static String getServerPlayers(String host, int port) throws IOException {
		byte [] input = new byte [255];
			Socket socket = new Socket(host, port);
			OutputStream out = socket.getOutputStream();
			InputStream in = socket.getInputStream();
			out.write(0xFE);
			//out.flush();
			int i = in.read(input);
			String inputString = new String (input, "UTF-8");
			String playerData = inputString.trim();
			String playersOnline = playerData.substring(playerData.length()-7, playerData.length()-6);
			String totalPlayers = playerData.substring(playerData.length()-3, playerData.length()-2) + playerData.substring(playerData.length()-1, playerData.length());
			out.close();
			in.close();
			return playersOnline + " / " + totalPlayers;
	}
}
