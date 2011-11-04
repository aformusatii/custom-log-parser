package log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Servlet implementation class ViewLog
 */
public class ViewLog extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Map<String, LogFile> logs = new HashMap<String, LogFile>();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewLog() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			process(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			process(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String logFilePath = request.getParameter("logFile");
		String search = request.getParameter("search");
		boolean isSearch = (search != null) && !"".equals(search);
		String pageStr = request.getParameter("page");
		String pageSizeStr = request.getParameter("pageSize");
		int page = 0;
		int pageSize = 100;
		int pages = 0;
		if (pageStr != null) {
			try {
				page = Integer.valueOf(pageStr);
			} catch (Exception e) {e.printStackTrace();}
		}
		if (pageSizeStr != null) {
			try {
				pageSize = Integer.valueOf(pageSizeStr);
			} catch (Exception e) {e.printStackTrace();}
		}		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println(" <HEAD>");
        out.println(" <TITLE>Log for: " + logFilePath + "</TITLE>");
        out.println(" <script type=\"text/javascript\" src=\"javascript/script.js\"></script>");
        out.println(" <link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"style/style.css\" />");
        out.println(" </HEAD>");
        out.println(" <BODY>");

        if (logFilePath == null || "".equals(logFilePath.trim())) {
        	out.println("Can not find file: [" + logFilePath + "]");
        } else {
			File f = new File(logFilePath);
			if (f.exists()) {
				LogFile logFile = getLogFile(logFilePath);
				logFile.parse(logFilePath);
				List<RequestAndResponse> requestsAndResponses;
				
				if (isSearch) {
					requestsAndResponses = new LinkedList<RequestAndResponse>();
					for (RequestAndResponse requestAndResponse : logFile.getRequestsAndResponses()) {
						if (requestAndResponse.getData().contains(search)) {
							requestsAndResponses.add(requestAndResponse);
						}			
					}					
				} else {
					requestsAndResponses = logFile.getRequestsAndResponses();
				}
				
				int total = requestsAndResponses.size();
				pages = (total / pageSize) + (((total % pageSize) > 0) ? 1 : 0);
				pages = (pages < 1) ? 1 : pages;
				page = ((page < 1) || (page > pages)) ? pages : page;
				int fromIndex = (page * pageSize) - pageSize;
				int toIndex = (page * pageSize);
				if (toIndex > total) {
					toIndex = total;
				}
				
				List<RequestAndResponse> subRequestsAndResponses = requestsAndResponses.subList(fromIndex, toIndex);
				out.println("<div class=\"info\">");
				out.println("<a href=\"ViewFile?logFile=" + URLEncoder.encode(logFilePath, "UTF-8") + getRandomParameter() + "\">" + logFilePath + "</a>");
				out.println("<div>");
				out.println("<table  border='0' cellspacing='0' cellpadding='0'>");
				for (RequestAndResponse requestAndResponse : subRequestsAndResponses) {
					int count = requestAndResponse.getIndex();
					if (requestAndResponse.isOnlyRequest()) {
						out.println("<tr class='request'>");
						out.println("<td nowrap='nowrap'>" + (count) + "</td>");
						out.println("<td nowrap='nowrap'>" + (requestAndResponse.getDate()) + "</td>");
						out.println("<td nowrap='nowrap'>" + (StringEscapeUtils.escapeHtml(requestAndResponse.getBsName())) + "</td>");
						out.println("<td nowrap='nowrap'>&nbsp;</td>");
						out.println("<td nowrap='nowrap'>&nbsp;</td>");
						out.println("<td nowrap='nowrap'>" + (buildLink(logFilePath, count, false, true, null)) + "</td>");
						out.println("<td nowrap='nowrap'>");
						out.println(buildLink(logFilePath, count, false, false, requestAndResponse.getRefData()));
						out.println("</td>");
						out.println("</tr>");
					} else {
						out.println("<tr class='response " + (requestAndResponse.isError() ? "error" : "") + "'>");
						out.println("<td nowrap='nowrap'>" + (count) + "</td>");
						out.println("<td nowrap='nowrap'>" + (requestAndResponse.getDate()) + "</td>");
						out.println("<td nowrap='nowrap'>" + (StringEscapeUtils.escapeHtml(requestAndResponse.getBsName())) + "</td>");
						out.println("<td nowrap='nowrap'>" + (buildLink(logFilePath, count, false, true, null)) + "</td>");
						out.println("<td nowrap='nowrap'>" + (buildLink(logFilePath, count, false, false, "Request")) + "</td>");
						out.println("<td nowrap='nowrap'>" + (buildLink(logFilePath, count, true, true, null)) + "</td>");
						out.println("<td nowrap='nowrap'>");
						out.println(buildLink(logFilePath, count, true, false, requestAndResponse.getRefData()));
						out.println("</td>");
						out.println("</tr>");
					}
				}
				out.println("</table>");
			} else {
				out.println("Can not find file: [" + logFilePath + "]");	
			}
        }
		
        String action = request.getRequestURI();
        
        out.println("<div class=\"search\">");
        out.println("<form action=\"" + action + "#search_\" method=\"GET\">");
        out.println("<a name=\"search_\"></a>");
        out.println("<input type=\"hidden\" name=\"logFile\" value=\"" + logFilePath + "\">");
        out.println("<input type=\"hidden\" name=\"r\" value=\"" + System.currentTimeMillis() + "\">");
        out.println("<input type=\"hidden\" name=\"pageSize\" value=\"" + pageSize + "\">");
        out.println("<input type=\"text\" class=\"text_search\" name=\"search\" value=\"" + ((search == null) ? "" : search) + "\">&nbsp;<input type=\"submit\" class=\"submit_search\"value=\"Search/Reload last page\">");
        
        String href = action + "?logFile=" + logFilePath + (isSearch ? ("&search=" + search) : "");
        
        if (pages > 1) {
        	
        	if (page > 1) {
	        	out.println("&nbsp;");
	        	out.println("<a href=\"" + href + "&page=" + (page - 1) + "&pageSize=" + pageSize + getRandomParameter() + "#search_\">");
	        	out.print("<<");
	        	out.print("</a>");
        	}
        	
	        for (int i = 1; i <= pages; i++) {
	        	out.println("&nbsp;");
	        	if (i != page) {
		        	out.println("<a href=\"" + href + "&page=" + i + "&pageSize=" + pageSize + getRandomParameter() + "#search_\">");
		        	out.print(i);
		        	out.print("</a>");
	        	} else {
	        		out.print(i);
	        	}
	        }
	        
        	if (page < pages) {
	        	out.println("&nbsp;");
	        	out.println("<a href=\"" + href + "&page=" + (page + 1) + "&pageSize=" + pageSize + getRandomParameter() + "#search_\">");
	        	out.print(">>");
	        	out.print("</a>");
        	}
        }
        
        if ((pageSize > 999) || (pages > 1)) {
        	out.println("&nbsp;");
	        out.println("&nbsp;");
	        String link = (pageSize != 50) ? ("href=\"" + href + "&pageSize=50" + getRandomParameter() + "#search_\"") : "";
	        out.println("<a " + link + ">");
        	out.print("[50/p]");
        	out.print("</a>");        	
        	
	        out.println("&nbsp;");
	        link = (pageSize != 100) ? ("href=\"" + href + "&pageSize=100" + getRandomParameter() + "#search_\"") : "";
	        out.println("<a " + link + ">");
        	out.print("[100/p]");
        	out.print("</a>");
        	
	        out.println("&nbsp;");
	        link = (pageSize != 200) ? ("href=\"" + href + "&pageSize=200" + getRandomParameter() + "#search_\"") : "";
	        out.println("<a " + link + ">");
        	out.print("[200/p]");
        	out.print("</a>");
        	
        	if (pages > 1) {
		        out.println("&nbsp;");
	        	out.println("<a href=\"" + href + "&pageSize=1000" + getRandomParameter() + "#search_\">");
	        	out.print("All");
	        	out.print("</a>");
        	}
        }
        
        out.println("</form>");
        out.println("<div>");
        
        out.println(" </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();		
	}
	
	private String getRandomParameter() {
		return "&r=" + System.currentTimeMillis();
	}
	
	private String buildLink(String logFilePath, Integer index, boolean showResponse, boolean showText, String html) throws UnsupportedEncodingException {
		String url = "ViewXML?logFile=" + URLEncoder.encode(logFilePath, "UTF-8") 
			+ "&index=" + index 
			+ "&showResponse=" + showResponse 
			+ "&showText=" + showText
			+ getRandomParameter();
		String text = showText ? "<img src=\"images/icon-note-pad.png\" border=\"0\" style=\"border: 0;\">" : 
			("<img src=\"images/xml.png\" border=\"0\" style=\"border: 0;\">&nbsp;" + StringEscapeUtils.escapeHtml(html));
		return "<a onclick=\"return newPopup('" + url + "');\" href='" + url + "' target='_blank'>" + text + "</a>";
	}
	
	public static LogFile getLogFile(String logFilePath) {
		LogFile logFile = logs.get(logFilePath);
		if (logFile == null) {
			logFile = new LogFile();
			logs.put(logFilePath, logFile);
		}
		return logFile;
	}

}
