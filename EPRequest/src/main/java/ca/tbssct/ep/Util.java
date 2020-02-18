package ca.tbssct.ep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Util {

	public static String fileToString(String path) throws Exception {
		String content = "";
		try {
			content = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String ExecuteCommand(String workingDirectory, String command) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.directory(new File(workingDirectory));
		processBuilder.command("bash", "-c", command);
		StringBuilder output = new StringBuilder();
		try {
			Process process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

			int exitVal = process.waitFor();
			if (exitVal == 0) {
				return output.toString();
			} else {
				try (final BufferedReader b = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
					String line2;
					if ((line2 = b.readLine()) != null)
						return output.append(line2 + "\n").toString();
				} catch (final IOException e) {
					return e.getMessage();
				}
			}

		} catch (IOException e) {
			return e.getMessage();
		} catch (InterruptedException e) {
			return e.getMessage();
		}
		return "";
	}

	public static void writeFile(String path, String content) throws Exception {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"))) {
			writer.write(content);
		}
	}

	public static String GetVerificationURL() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File("/home/config/eprequest.properties")));
		} catch (Exception e) {

		}
		return prop.getProperty("server");
	}
	
	public static String GetPublicIp() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File("/home/config/eprequest.properties")));
		} catch (Exception e) {

		}
		return prop.getProperty("publicIP");
	}
	
	public static String GetValuesTemplate() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File("/home/config/eprequest.properties")));
		} catch (Exception e) {

		}
		return prop.getProperty("helmValuesTemplate");
	}
	
	public static String GetHost() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File("/home/config/eprequest.properties")));
		} catch (Exception e) {

		}
		return prop.getProperty("host");
	}


}
