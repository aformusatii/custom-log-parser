package log;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import log.LogHelper.Parameter;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Servlet implementation class ViewXML
 */
public class ViewXML extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewXML() {
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
		response.setContentType(contentType);
		
		PrintWriter out = response.getWriter();
		LogFile logFile = LogReader.getLogFile(logFilePath);
		
		LogRow row = logFile.getRows().get(index);

		String content;		
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

		out.write(content);
        out.flush();
        out.close();
	}

}
