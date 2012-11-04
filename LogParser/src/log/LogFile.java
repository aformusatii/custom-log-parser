package log;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**********************************************************
 *
 * Class name:	LogFile.java
 * Package: 	
 * Created:		14 May 2011
 * Author:		f24206
 * Description:	TODO Describe purpose of class
 *								
 *********************************************************/

public class LogFile {

	private String path;
	private long lastPosition;
	private String firstLine;
	private List<LogRow> rows = new LinkedList<LogRow>();
	private Date expiryDate;
	private int rowCount = 0;
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public long getLastPosition() {
		return lastPosition;
	}
	public void setLastPosition(long lastPosition) {
		this.lastPosition = lastPosition;
	}
	public String getFirstLine() {
		return firstLine;
	}
	public void setFirstLine(String firstLine) {
		this.firstLine = firstLine;
	}
	
	public void addRow(LogRow row) {
		row.setIndex(rowCount++);
		rows.add(row);
	}
	
	public List<LogRow> getRows(String searchText, String searchType) {
		if (LogHelper.isNotBlank(searchText)) {
			List<LogRow> result = new LinkedList<LogRow>();
			if ("regex".equals(searchType)) {
				Pattern p = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
				for (LogRow row : rows) {
					if (p.matcher(row.getDataWithoutNewLine()).matches()) {
						result.add(row);	
					}
				}				
			} else {
				for (LogRow row : rows) {
					if (row.getDataWithoutNewLine().toLowerCase().contains(searchText.toLowerCase())) {
						result.add(row);	
					}
				}
			}
			return result;
		} else {
			return rows;
		}
	}
	
	public List<LogRow> getRows() {
		return rows;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void updateExpiryDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, LogHelper.LOG_FILE_EXPIRY_TIMEOUT);		
		this.expiryDate = calendar.getTime();
	}

}
