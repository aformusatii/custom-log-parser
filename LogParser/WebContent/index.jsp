<%@page import="log.LogRow"%>
<%@page import="java.util.List"%>
<%@page import="log.LogHelper"%>
<%@page import="log.LogFile"%>
<%@page import="log.LogReader"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%! 
	public String createHiddenInput(HttpServletRequest request, String paramName) {
		String result = "";
		String value = request.getParameter(paramName);
		if (LogHelper.isNotBlank(value)) {
			result ="<input type=\"hidden\" name=\"" + paramName + "\" value=\"" + value + "\">";
		}
		return result;
	}
%>

<%
	String filePath = request.getParameter("file");
	if (LogHelper.isBlank(filePath)) {
		response.sendError(400);
		return;
	}

	LogFile logFile = LogReader.readFile(filePath);
	
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
	
	String searchText = request.getParameter("searchText");
	if (searchText == null) {
		searchText = "";
	}
	
	String searchType = request.getParameter("searchType");
	
	int numberOfPages = logFile.getNumberOfPages(pageSize);
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
		<form method="get">
			<%= createHiddenInput(request, "file") %>
			<%= createHiddenInput(request, "page") %>
			<div class="topMenu">
				
				<div class="pagination">
					<div class="pages"">
						<input class="page" type="submit" name="move" value="<">
						<% for (int i = 1; i <= numberOfPages; i++) { %>
							<input class="page" type="submit" name="setPage" value="<%= i %>">
						<% } %>
						<input class="page" type="submit" name="move" value=">">
					</div>
					<div class="conf">
						<label for="pageSize">Page size: </label>
						<input type="text" name="pageSize" id="pageSize" value="<%= pageSize %>">
					</div>
					<div class="clrfix"></div>	
				</div>
				<div class="search">
					<label for="search_text">Search:</label>
					<input type="radio" name="searchType" id="search_simple" value="simple" <%= (((searchType == null) || "simple".equals(searchType)) ? "checked=\"checked\"" : "") %>>
					<label for="search_simple">Simple</label>
					<input type="radio" name="searchType" id="search_regex" value="regex" <%= (("regex".equals(searchType)) ? "checked=\"checked\"" : "") %>>
					<label for="search_regex">Regex</label>&nbsp;
					<input type="text" name="searchText" id="search_text" value="<%= searchText %>">
					<input type="submit" value="Go">
				</div>
				<div class="clrfix"></div>
			</div>
			<div class="topMenuPlaceHolder"></div>
			<div class="list">	
				<table>
					<thead>
						<tr>	
							<th>Session</th>
							<th>Date</th>
							<th>Flow Path</th>
							<th>Level</th>
							<th>Thread</th>
						</tr>	
					</thead>	
					<tbody>
						<% for (LogRow row : rows) { row.parse(); %>
							<tr class="level_<%= row.getParameter("LEVEL").getValue() %>">		
								<td><%= row.getParameter("SESSION").getValue() %></td>			
								<td><%= row.getParameter("DATE").getValue() %></td>
								<td><%= row.getParameter("FLOW_PATH").getValue() %></td>
								<td><%= row.getParameter("LEVEL").getValue() %></td>
								<td><%= row.getParameter("THREAD").getValue() %></td>
							</tr>
						<% } %>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="5"></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</form>
	</body>
</html>