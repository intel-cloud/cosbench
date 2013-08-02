<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="resources/cosbench.css" />
  <title>COSBench Controller</title>
</head>
<body>
<#include "header.ftl">
<div id="main">
<div class="top"><br /></div>
<div class="top"><br /></div>
<div class="top"><br /></div>
<center>
<#if hidden?? >
<div id="hiddenForm" style="${hidden}">
<#else>
<div>
</#if>
	<form action="j_security_check" method="post">
	<table>
	<tr>
   	<td align="center" >
   		<table border="0">
   			<tr>
   				<td><b>Name: </b></td>
   				<td>
      				<input id="username" type="text" size="15" name="j_username" value="${username}"> 
   				</td>
   			</tr>
   			<tr></tr>
   			<tr>
   				<td><b>Password: </b></td>
   				<td> 
      				<input id="password" type="password" size="15" name="j_password" value="${password}">
   				</td>
   				<td></td>
   				<td align="right"> 
   					<input type="submit" value="Submit" id="formButton">
   					    <script language="javascript">
   					    if(document.getElementById("username").value!="" && document.getElementById("password").value!="")
        					document.getElementById("formButton").click();
    					</script>
   				</td>
   			</tr>
		</table>
	</td>
	</tr>
	</table>
	</form>
</center>
<div class="bottom"><br /></div>
</div> <#-- end of main -->
</body>
</html>