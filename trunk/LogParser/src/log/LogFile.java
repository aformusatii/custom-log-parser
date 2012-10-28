package log;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
		rows.add(row);
	}
	
	public List<LogRow> getRows() {
		return rows;
	}
	
	public List<LogRow> getRows(int currentPage, int pageSize) {
		int total = getNumberOfRows();
		int pages = (total / pageSize) + (((total % pageSize) > 0) ? 1 : 0);
		pages = (pages < 1) ? 1 : pages;
		currentPage = ((currentPage < 1) || (currentPage > pages)) ? pages : currentPage;
		int fromIndex = (currentPage * pageSize) - pageSize;
		int toIndex = (currentPage * pageSize);
		if (toIndex > total) {
			toIndex = total;
		}

		return rows.subList(fromIndex, toIndex);
	}
	
	public int getNumberOfPages(int pageSize) {
		int total = getNumberOfRows();
		int pages = (total / pageSize) + (((total % pageSize) > 0) ? 1 : 0);
		pages = (pages < 1) ? 1 : pages;
		return pages;
	}
	
	public int getNumberOfRows() {
		return rows.size();
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
