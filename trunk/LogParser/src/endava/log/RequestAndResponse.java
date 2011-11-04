/**********************************************************
 *
 * Class name:	RequestAndResponse.java
 * Package: 	logs
 * Created:		14 May 2011
 * Author:		f24206
 * Description:	TODO Describe purpose of class
 *								
 *********************************************************/

package endava.log;


public class RequestAndResponse {

	private int index;
	private String data;
	private boolean onlyRequest;
	private String request;
	private String response;
	private String date;
	private String bsName;
	private boolean error;
	private String refData;
	
	public RequestAndResponse(int index, String data, boolean onlyRequest) throws Exception {
		this.data = data;
		this.onlyRequest = onlyRequest;
		this.index = index;
		try {
			parse();
		} catch (Exception e) {
			this.data = data;
			this.onlyRequest = false;
			this.index = index;
			this.request = data;
			this.response = LogFile.getStackTrace(e);
			this.error = true;			
		}
	}
	
	public RequestAndResponse(int index, String data) throws Exception {
		this.data = data;
		this.onlyRequest = true;
		this.index = index;
		this.request = data;
		this.error = true;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public boolean isOnlyRequest() {
		return onlyRequest;
	}

	public void setOnlyRequest(boolean onlyRequest) {
		this.onlyRequest = onlyRequest;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBsName() {
		return bsName;
	}

	public void setBsName(String bsName) {
		this.bsName = bsName;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getRefData() {
		return refData;
	}

	public void setRefData(String refData) {
		this.refData = refData;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	private void parse() throws Exception {
		//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		if (onlyRequest) {
			
			String start = "]	Sent request [";
			String end = "</SOAP-ENV:Body></SOAP-ENV:Envelope>]";
			int beginIndex = data.indexOf(start);
			int endIndex = data.lastIndexOf(end);
			request = data.substring(beginIndex + start.length(), endIndex + end.length() - 1);
			
			start = "]	DEBUG ";
			end = " sent	[tomcat-http";
			beginIndex = data.indexOf(start);
			endIndex = data.indexOf(end);
			date = data.substring(beginIndex + start.length(), endIndex);
			//date = sdf.parse(dateStr);
			
			error = false;
			start = "</SOAP-ENV:Header><SOAP-ENV:Body>";
			beginIndex = data.indexOf(start);
			endIndex = beginIndex + 100;			
			bsName = data.substring(beginIndex + start.length(), endIndex);
			
			start = ":";
			end = " xmlns:";
			beginIndex = bsName.indexOf(start);
			endIndex = bsName.indexOf(end);				
			bsName = bsName.substring(beginIndex + start.length(), endIndex);
			
			start = "</SOAP-ENV:Header><SOAP-ENV:Body>";
			beginIndex = data.indexOf(start);
			endIndex = beginIndex + 500;
			endIndex = (endIndex > data.length()) ? data.length() : endIndex;
			refData = data.substring(beginIndex + start.length(), endIndex) + "...";
		} else {
			
			String start = "]	Received response [";
			String end = "] for request [";
			int beginIndex = data.indexOf(start);
			int endIndex = data.indexOf(end);
			response = data.substring(beginIndex + start.length(), endIndex);
			
			start = "] for request [";
			end = "</SOAP-ENV:Body></SOAP-ENV:Envelope>]";
			beginIndex = data.indexOf(start);
			endIndex = data.lastIndexOf(end);
			request = data.substring(beginIndex + start.length(), endIndex + end.length() - 1);
			

			
			start = "]	DEBUG ";
			end = " received	[tomcat-http";
			beginIndex = data.indexOf(start);
			endIndex = data.indexOf(end);
			date = data.substring(beginIndex + start.length(), endIndex);
			//date = sdf.parse(dateStr);

			if (data.contains("<SOAP-ENV:Fault>")) {
				error = true;
				bsName = "SOAP Error";
			} else {
				error = false;
				start = "</SOAP-ENV:Header><SOAP-ENV:Body>";
				beginIndex = data.indexOf(start);
				endIndex = beginIndex + 100;			
				bsName = data.substring(beginIndex + start.length(), endIndex);
				
				start = ":";
				end = " xmlns:";
				beginIndex = bsName.indexOf(start);
				endIndex = bsName.indexOf(end);				
				bsName = bsName.substring(beginIndex + start.length(), endIndex);				
			}			
			
			start = "</SOAP-ENV:Header><SOAP-ENV:Body>";
			beginIndex = data.indexOf(start);
			endIndex = beginIndex + 500;
			endIndex = (endIndex > data.length()) ? data.length() : endIndex;
			refData = data.substring(beginIndex + start.length(), endIndex) + "...";			
		}
	}
	
	public String toString() {
		String str = request;
		if (response != null) {
			str += "\n" + response;
		}
		return str;
	}	

}
