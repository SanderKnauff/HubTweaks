package nl.imine.hubtweaks.util;

import nl.imine.hubtweaks.HubTweaksPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Util {

	public static boolean saveToFile(File out, String[] strings) {
		try (PrintWriter writer = new PrintWriter(out, "UTF-8");) {
			if (!out.exists()) {
				out.createNewFile();
			}
			for (String s : strings) {
				writer.println(s);
			}
			writer.close();
		} catch (FileNotFoundException fnfe) {
			HubTweaksPlugin.getInstance().getLogger().severe("FileNotFoundException: " + fnfe.getMessage());
			return false;
		} catch (UnsupportedEncodingException uee) {
			HubTweaksPlugin.getInstance().getLogger().severe("UnsupportedEncodingException: " + uee.getMessage());
		} catch (IOException ioe) {
			HubTweaksPlugin.getInstance().getLogger().severe("IOException: " + ioe.getMessage());
		}
		return true;
	}

	public static List<String> readFromFileSplitByLine(File in) {
		List<String> ret = new ArrayList<>();
		BufferedReader br = null;
		try {
			if (!in.exists()) {
				in.createNewFile();
			}
			br = new BufferedReader(new InputStreamReader(new FileInputStream(in), "UTF-8"));
			String line;
			while ((line = br.readLine()) != null) {
				ret.add(line);
			}
		} catch (FileNotFoundException fnfe) {
			HubTweaksPlugin.getInstance().getLogger().severe("FileNotFoundException: " + fnfe.getMessage());
		} catch (IOException ioe) {
			HubTweaksPlugin.getInstance().getLogger().severe("IOException: " + ioe.getMessage());
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ioe) {
				HubTweaksPlugin.getInstance().getLogger().severe("IOException: " + ioe.getMessage());
			}
		}
		return ret;
	}

	public static String readFromFile(File in) {
		String ret = "";
		for (String str : readFromFileSplitByLine(in)) {
			ret += str;
			ret += System.lineSeparator();
		}
		return ret;
	}
}
