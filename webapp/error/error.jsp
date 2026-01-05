<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>에러 페이지</title>
</head>
<body>

	<img alt="에러" src="images/404.png">
	<hr>
	<h3><%=exception%></h3>
	<a href="controller.jsp?command=MAINPAGE">메인페이지</a>

</body>
</html>