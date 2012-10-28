<%@page import="log.LogRow"%>
<%@page import="java.util.List"%>
<%@page import="log.LogHelper"%>
<%@page import="log.LogFile"%>
<%@page import="log.LogReader"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	LogHelper.loadProperties();
	
	String filePath = request.getParameter("file");
	if (LogHelper.isBlank(filePath)) {
		response.sendError(400);
		return;
	}

	LogFile logFile = LogReader.readFile(filePath);
	
	logFile.updateExpiryDate();
	
	String pageStr = request.getParameter("page");
	int currentPage = 1;
	if (LogHelper.isNotBlank(pageStr)) {
		currentPage = Integer.valueOf(pageStr);
	}
	
	String pageSizeStr = request.getParameter("pageSize");
	int pageSize = LogHelper.DEFAULT_PAGE_SIZE;
	if (LogHelper.isNotBlank(pageSizeStr)) {
		pageSize = Integer.valueOf(pageSizeStr);
	}
	
	List<LogRow> rows = logFile.getRows(currentPage, pageSize);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><%= filePath %></title>
		<link rel="stylesheet" type="text/css" media="all" href="style/style.css" />
		<script type="text/javascript" src="javascript/script.js"></script>
	</head>
	<body>
	<table>
		<tr class="header">	
			<th>Session</th>
			<th>Date</th>
			<th>Flow Path</th>
			<th>Level</th>
			<th>Thread</th>
		</tr>		
	<% for (LogRow row : rows) { row.parse(); %>
		<tr>		
			<td><%= row.getParameter("SESSION").getValue() %></td>			
			<td><%= row.getParameter("DATE").getValue() %></td>
			<td><%= row.getParameter("FLOW_PATH").getValue() %></td>
			<td><%= row.getParameter("LEVEL").getValue() %></td>
			<td><%= row.getParameter("THREAD").getValue() %></td>
		</tr>
	<% } %>
	</table>
	</body>
</html>