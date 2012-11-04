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

	public String createHiddenInput(String paramName, String value) {
		String result = "";
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
	
	String reloadLastPage = request.getParameter("reloadLastPage");
	
	String setPageStr = request.getParameter("setPage");
	String pageStr = LogHelper.isBlank(setPageStr) ? request.getParameter("page") : setPageStr;
	int currentPage = 1;
	if (LogHelper.isNotBlank(pageStr)) {
		currentPage = Integer.valueOf(pageStr);
	}

	String movePage = request.getParameter("movePage");
	if ("<".equals(movePage)) {
		currentPage -= 1;
	} else if (">".equals(movePage)) {
		currentPage += 1;
	}
	
	String pageSizeStr = request.getParameter("pageSize");
	int pageSize = LogHelper.DEFAULT_PAGE_SIZE;
	if (LogHelper.isNotBlank(pageSizeStr)) {
		pageSize = Integer.valueOf(pageSizeStr);
	}
	
	pageSize = (pageSize < 10) ? 10 : pageSize;
	
	String searchText = request.getParameter("searchText");
	String searchType = request.getParameter("searchType");
	if (searchText == null) {
		searchText = "";
	}

	List<LogRow> allRows = logFile.getRows(searchText,searchType);
	int total = allRows.size();
	int numberOfPages = LogHelper.calculateNumberOfPages(total, pageSize);
	
	currentPage = LogHelper.isNotBlank(reloadLastPage) ? numberOfPages : currentPage;
	currentPage = (currentPage < 1) ? 1 : currentPage;
	currentPage = (currentPage > numberOfPages) ? numberOfPages : currentPage;
	int fromIndex = (currentPage * pageSize) - pageSize;
	int toIndex = (currentPage * pageSize);
	if (toIndex > total) {
		toIndex = total;
	}

	List<LogRow> rows = allRows.subList(fromIndex, toIndex);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><%= filePath %></title>
		<link rel="stylesheet" type="text/css" media="all" href="style/style.css" />
		<script type="text/javascript" src="javascript/jquery-1.8.2.min.js"></script>
		<script type="text/javascript" src="javascript/script.js"></script>
	</head>
	<body>
		<form method="get" action="index.jsp">
			<%= createHiddenInput(request, "file") %>
			<%= createHiddenInput("page", String.valueOf(currentPage)) %>
			<div class="topMenu">
				
				<div class="pagination">
					<div class="pages"">
						<input class="page" type="submit" name="movePage" value="<">
						<%
							int firstPage = currentPage - 5;
							firstPage = (firstPage < 1) ? 1 : firstPage; 
							int lastPage = firstPage + 10;
							lastPage = (lastPage > numberOfPages) ? numberOfPages : lastPage;
							firstPage = lastPage - 10;
							firstPage = (firstPage < 1) ? 1 : firstPage;
						%>
						<% if (firstPage != 1) { %>
						 	<input class="page" type="submit" name="setPage" value="<%= 1 %>"> <span class="text">...</span>
						<% } %>
						<% for (int i = firstPage; i <= lastPage; i++) { %>
							<input class="page <%= ((i == currentPage) ? "selected" : "") %>" type="submit" name="setPage" value="<%= i %>">
						<% } %>
						<% if (lastPage != numberOfPages) { %>
							<span class="text">...</span> <input class="page" type="submit" name="setPage" value="<%= numberOfPages %>"> 
						<% } %>
						<input class="page" type="submit" name="movePage" value=">">
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
							
							<th>Original</th>
							<th>Request</th>
							<th>Response</th>
							<th>Preview</th>
						</tr>	
					</thead>	
					<tbody>
						<% for (LogRow row : rows) { row.parse(); %>
							<tr class="level_<%= row.getParameterValue("LEVEL") %> type_<%= row.getType() %>">		
								<td><%= row.getParameterValue("SESSION") %></td>			
								<td><%= row.getParameterValue("DATE") %></td>
								<td><%= row.getParameterValue("FLOW_PATH") %></td>
								<td><%= row.getParameterValue("LEVEL") %></td>
								<td align="center">
									<a href="ViewParam?file=<%= filePath %>&index=<%= row.getIndex() %>&param=DATA" target="log_<%= row.getIndex() %>">View</a>									
								</td>
								<% if ("REQ".equals(row.getType())) { %>
									<td class="link" align="center">
										<a href="ViewParam?file=<%= filePath %>&index=<%= row.getIndex() %>&param=REQUEST_1&contentType=text/xml" target="req_log_<%= row.getIndex() %>">Request</a>
									</td>
									<td>&nbsp;</td>
									<td><%= row.getParameterValue("PREV_1") %></td>
								<% } else if ("REQRESP".equals(row.getType())) { %>
									<td class="link" align="center"><a href="ViewParam?file=<%= filePath %>&index=<%= row.getIndex() %>&param=REQUEST_2&contentType=text/xml" target="req_log_<%= row.getIndex() %>">Request</a></td>
									<td class="link" align="center">
										<a href="ViewParam?file=<%= filePath %>&index=<%= row.getIndex() %>&param=RESPONSE_2&contentType=text/xml" target="resp_log_<%= row.getIndex() %>">Response</a>
									</td>
									<td><%= row.getParameterValue("PREV_2") %></td>
								<% } else { %>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td><%= row.getParameterValue("OTHER") %></td>
								<% } %>								
							</tr>
						<% } %>
					</tbody>
					<tfoot>
						<tr>
							<td id="bottom" colspan="8" align="center">
								<input type="submit" name="reloadLastPage" value="Reload last page">
							</td>
						</tr>
					</tfoot>
				</table>
				<%	if (LogHelper.isNotBlank(reloadLastPage)) { %>
					<script type="text/javascript">afterReload();</script>
				<% } %>				
			</div>
		</form>
	</body>
</html>