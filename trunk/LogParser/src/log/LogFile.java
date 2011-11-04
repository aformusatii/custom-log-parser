package log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import ucar.unidata.io.RandomAccessFile;

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
	private List<RequestAndResponse> requestsAndResponses = new LinkedList<RequestAndResponse>();
	private int count = 0;
	private int delta = 0;
	
	public static void main(String[] args) {
		String val = "[14F25A18968C71152D6A4317DDC4B760 quoteAndBuy.quoteSummary]	DEBUG 21/04/11 11:15:49 sent	[tomcat-http--20]	Sent request [<SOAP-ENV:Envelope ";
		String start = "]	DEBUG ";
		String end = " sent	[tomcat-http";
		int beginIndex = val.indexOf(start);
		int endIndex = val.indexOf(end);
		System.out.println(val.substring(beginIndex + start.length(), endIndex));
	}
	
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
	public List<RequestAndResponse> getRequestsAndResponses() {
		return requestsAndResponses;
	}
	public String getFirstLine() {
		return firstLine;
	}
	public void setFirstLine(String firstLine) {
		this.firstLine = firstLine;
	}
	public void setRequestsAndResponses(
			List<RequestAndResponse> requestsAndResponses) {
		this.requestsAndResponses = requestsAndResponses;
	}
	public int getDelta() {
		return delta;
	}

	public void add(String data, boolean onlyRequest) throws Exception {
		RequestAndResponse request = new RequestAndResponse(count++, data, onlyRequest);
		requestsAndResponses.add(request);
		if (requestsAndResponses.size() > 5000) {
			((LinkedList) requestsAndResponses).removeFirst();
			delta++;
		}
	}
	
	public void add(Exception e) throws Exception {
		RequestAndResponse request = new RequestAndResponse(count++, getStackTrace(e));
		requestsAndResponses.add(request);
		if (requestsAndResponses.size() > 5000) {
			((LinkedList) requestsAndResponses).removeFirst();
			delta++;
		}
	}
	
	public static String getStackTrace(Exception e) {
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    e.printStackTrace(printWriter);
	    return result.toString();
    }	
	
	public void parse(String logFilePath) throws Exception {
		boolean requestFlag = false;
		boolean requestAndResponseFlag = false;
		String _currentLine;
		StringBuilder sb = new StringBuilder();
		RandomAccessFile _reader = new RandomAccessFile(logFilePath, "r");
		String firstLine = _reader.readLine();
		
		long lastPosition = (firstLine.equals(getFirstLine())) ? getLastPosition() : 0;
		_reader.seek(lastPosition);
		
		System.out.println("Start to read from [" + getLastPosition() + "] file [" + logFilePath + "]");
		
		while (true) {
			_currentLine = _reader.readLine();
			if (_currentLine != null) {
				
				// Detect requests and responses
				try {
					if (_currentLine.contains("]	Received response [")) {
						sb = new StringBuilder();
						requestAndResponseFlag = true;
					}
					
					if (requestAndResponseFlag && !"".equals(_currentLine)) {
						//_currentLine = _currentLine.trim();
						sb.append(_currentLine);
						sb.append("\n");
					}
					
					if (requestAndResponseFlag && _currentLine.contains("</SOAP-ENV:Body></SOAP-ENV:Envelope>]")) {
						add(sb.toString(), false);
						requestAndResponseFlag = false;
					}				
					
					// Detect single requests
					if (_currentLine.contains("]	Sent request [<")) {
						sb = new StringBuilder();			
						requestFlag = true;
					}
					
					if (requestFlag && !"".equals(_currentLine)) {
						//_currentLine = _currentLine.trim();
						sb.append(_currentLine);
						sb.append("\n");
					}
					
					if (requestFlag && _currentLine.contains("</SOAP-ENV:Body></SOAP-ENV:Envelope>]")) {
						add(sb.toString(), true);
						requestFlag = false;
					}
				} catch (Exception e) {
					add(e);
				}
			} else {
				break;
			}
		}
		
		setLastPosition(_reader.getFilePointer());
		setFirstLine(firstLine);		
	}

}
