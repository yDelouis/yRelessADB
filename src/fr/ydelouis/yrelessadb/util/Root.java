package fr.ydelouis.yrelessadb.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class Root
{
	private static final String SU = "su";
	private static final String COMMAND_PROCESSRUNNING = "ps";
	private static final String COMMAND_EXIT = "exit\n";
	private static final String COMMAND_SETPROP = "setprop ";
	
	public static boolean hasRootPermission() {
		Process process = null;
		DataOutputStream os = null;
		boolean rooted = true;
		try {
			process = Runtime.getRuntime().exec(SU);
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(COMMAND_EXIT);
			os.flush();
			process.waitFor();
			if (process.exitValue() != 0) {
				rooted = false;
			}
		} catch (Exception e) {
			rooted = false;
		} finally {
			if (os != null) {
				try {
					os.close();
					process.destroy();
				} catch (Exception e) {}
			}
		}
		return rooted;
	}
	
	public static boolean setProp(String property, String value) {
		return runCommand(COMMAND_SETPROP + property + " " + value);
	}
	
	public static boolean runCommand(String command) {
		Process process = null;
		DataOutputStream os = null;
		boolean result;
		try {
			process = Runtime.getRuntime().exec(SU);
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes(COMMAND_EXIT);
			os.flush();
			process.waitFor();
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {}
		}
		return result;
	}
	
	public static boolean isProcessRunning(String processName) throws Exception {
		boolean running = false;
		Process process = null;
		process = Runtime.getRuntime().exec(COMMAND_PROCESSRUNNING);
		BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = null;
		while ((line = in.readLine()) != null) {
			if (line.contains(processName)) {
				running = true;
				break;
			}
		}
		in.close();
		process.waitFor();
		return running;
	}
}
