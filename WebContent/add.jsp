<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();

String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  <meta charset="utf-8" />
    <base href="<%=basePath%>">
    
    <title>添加数据</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" src="${basePath}static/js/jquery-1.8.3.js"></script>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
  <input type="hidden" value="<%=basePath%>" id="basePath">
    <form <%-- action="<%=request.getContextPath() %>/addInfo.do" method="post" --%>>

    	用户名：<input type="text" name="tname" id="tname">
		密码：<input type="password" name="tpwd" id="password">
    <input type="button" value="提交数据" id="goGetAll">

      </form>
      
      <script type="text/javascript">
      		$("#goGetAll").click(function(){
      			var param = {};
      			var basePath = $("#basePath").val();
				param["tname"] = $("#tname").val();
				param["password"] = $("#password").val();
				alert(basePath);
				$.ajax({
					type : "POST",
					cache : false,
					async : false,// 设置异步为false,重要！
					dataType : "json",
					url : basePath + "addInfo",
					data : param,
					success : function(data) {
						if (data.state == 0) {
							alert("添加成功");
							window.location.href=basePath+"getAll";
						}
					}
				})
      		})
      </script>
  </body>
</html>
