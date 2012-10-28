package log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class LogReader {
	
	private static Map<String, LogFile> files = new HashMap<String, LogFile>();
	
	static {
		(new LogReader()).new LogFileMemoryManager();
	}
	
	public synchronized static LogFile readFile(String filePath) throws Exception {
		LogFile file;
		if (files.containsKey(filePath)) {
			file = files.get(filePath);
		} else {
			file = new LogFile();
			file.setPath(filePath);
			files.put(filePath, file);
		}
		readFile(file);
		return file;
	}
	
	public synchronized static void readFile(LogFile file) throws Exception {
		long startTime = System.currentTimeMillis();
		RandomAccessFile _reader = new RandomAccessFile(file.getPath(), "r");
		String firstLine = "";
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			firstLine = _reader.readLine();
			
			long lastPosition = (firstLine.equals(file.getFirstLine())) ? file.getLastPosition() : 0;
			_reader.seek(lastPosition);
			
			byte[] buffer = new byte[8024];
			int nRead;
			while ((nRead=_reader.read(buffer)) != -1) {
				data.write(buffer, 0, nRead);
			}
			
			file.setLastPosition(_reader.getFilePointer());
		} finally {
			_reader.close();
		}
		file.setFirstLine(firstLine);
		System.out.println("End to read from [" + file.getLastPosition() 
				+ "] file [" + file.getPath() 
				+ "] in [" + (System.currentTimeMillis() - startTime) + "] Start parsing.");
		
		startTime = System.currentTimeMillis();
		
		ByteArrayInputStream is = new ByteArrayInputStream(data.toByteArray());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String strLine;
		LogRow row = new LogRow();

	    while ((strLine = br.readLine()) != null)   {
	    	if (strLine.length() > 100) {
		    	Matcher m = LogHelper.ROW_PATTERN.matcher(strLine);
		    	if (m.matches()) {
		    		row = new LogRow();
		    		file.addRow(row);
		    	}
	    	}

	    	row.appendValue(strLine + LogHelper.NEW_LINE);
	    }

		System.out.println("End parsing from [" + file.getPath() 
				+ "] in [" + (System.currentTimeMillis() - startTime) + "].");		
	}
	
	public static LogFile getLogFile(String filePath) {
		return files.get(filePath);
	}

	public static Map<String, LogFile> getFiles() {
		return files;
	}
	
	class LogFileMemoryManager implements Runnable {

		public LogFileMemoryManager() {
			Thread thread = new Thread(this);
			thread.start();
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					Date now = new Date();
					List<String> keysToRemove = new ArrayList<String>();
					for (Map.Entry<String, LogFile> entry : files.entrySet()) {
						Date expiryDate = entry.getValue().getExpiryDate();
						if (expiryDate.before(now)) {
							keysToRemove.add(entry.getKey());
						}
					}
					
					for (String key : keysToRemove) {
						LogFile f = files.remove(key);
						System.out.println("Removed [" + f.getPath() + "] from memory.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					Thread.sleep(1000 * 60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}