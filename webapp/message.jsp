<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>메세지 페이지</title>
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">

<link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
 <div class="wrapper">
 
<script>
	alert('${msg}');
	location.href='${location}';
</script>
</div>

</body>
</html>