package fr.ydelouis.yrelessadb.util;

import java.io.DataOutputStream;

public class Root
{
	private static final String SU = "su";
	private static final String COMMAND_EXIT = "exit\n";
	
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
}
