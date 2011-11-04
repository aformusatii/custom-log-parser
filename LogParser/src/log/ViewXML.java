package log;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		String logFilePath = request.getParameter("logFile");
		Integer index = Integer.valueOf(request.getParameter("index"));
		boolean showResponse = "true".equals(request.getParameter("showResponse"));
		boolean showText = "true".equals(request.getParameter("showText"));
		response.setContentType(showText ? "text/plain" : "text/xml");
		PrintWriter out = response.getWriter();
		LogFile logFile = ViewLog.getLogFile(logFilePath);
		
		RequestAndResponse requestAndResponse = logFile.getRequestsAndResponses().get(index - logFile.getDelta());
		String content = showResponse ? requestAndResponse.getResponse() : requestAndResponse.getRequest();
		out.write(content);
        out.flush();
        out.close();
	}

}
