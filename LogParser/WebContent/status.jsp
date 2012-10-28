<%@page import="log.LogFile"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Set"%>
<%@page import="log.LogReader"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
Set<Map.Entry<String, LogFile>> entries = LogReader.getFiles().entrySet();
   
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Status</title>
		<link rel="stylesheet" type="text/css" media="all" href="style/style.css" />
		<script type="text/javascript" src="javascript/script.js"></script>		
	</head>
	<body>
		<table>
			<tr>
				<th>File</th>
				<th>Number of Rows</th>
				<th>Expiry date</th>
			</tr>
			<% for (Map.Entry<String, LogFile> entry : entries) { %>
				<tr>
					<td><%= entry.getKey() %></td>
					<td><%= entry.getValue().getRows().size() %></td>
					<td><%= entry.getValue().getExpiryDate().toString() %></td>
				</tr>
			<% } %>
		</table>
	</body>
</html>