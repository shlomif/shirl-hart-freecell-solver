package main;

import java.io.FileWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	
	private FileWriter log;
	
	private final Format formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private final String logfile   = "fc$log.txt";

	public Logger () {
		try { 
			log = new FileWriter(logfile, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void log (String line) {
		String info = String.format("%s INFO ", formatter.format(new Date()));
		try {
			System.out.print(info + line +"\r\n");
			log.write(       info + line +"\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close () {
		try {
			log.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
