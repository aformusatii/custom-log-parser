package log;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ViewParam
 */
public class ViewParam extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewParam() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}
	
	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String logFilePath = request.getParameter("file");
		Integer index = Integer.valueOf(request.getParameter("index"));
		String paramName = request.getParameter("param");
		String contentType = request.getParameter("contentType");
		//response.setContentType(showText ? "text/plain" : "text/xml");
		contentType = LogHelper.isBlank(contentType) ? "text/plain" : contentType;  
		response.setContentType(contentType);
		
		PrintWriter out = response.getWriter();
		LogFile logFile = LogReader.getLogFile(logFilePath);
		
		if (logFile == null) {
			try {
				logFile = LogReader.readFile(logFilePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String content;
		int size = logFile.getRows().size();
		if (size > index) {
			LogRow row = logFile.getRows().get(index);
			logFile.updateExpiryDate();
		
			if ("DATA".equalsIgnoreCase(paramName)) {
				content = row.getData();
				
			} else {
				LogParameter param = row.getParameter(paramName);
				if (param != null) {
					content = param.getValue();
				} else {
					content = "No data for: Parameter [" + paramName + "] LogFile [" + logFilePath + "]";
				}
			}
		} else {
			content = "Can't find row with id = " + index + " please reload the list and open this row again.";
		}

		out.write(content.replaceAll("\\r", "").trim());
        out.flush();
        out.close();
	}

}
