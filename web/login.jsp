<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" 
	language="java" 
	errorPage="" %>	
	
<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01//EN' 'http://www.w3.org/TR/html4/strict.dtd'> 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ADAPT&sup2; User &amp; Group Directory - Login</title>
<link rel='StyleSheet' href='<%=request.getContextPath()%>/assets/rest.css' type='text/css' />
</head>
<body>
<form action="j_security_check" method="POST">
	<table cellpadding='2px' cellspacing='0px' class='dkcyan_table' width='320px' align="center">
		<caption class='dkcyan_table_caption'>ADAPT&sup2; User &amp; Group Directory - Login</caption>
		<tr><td colspan="2">To access Directory enter your credentials</td></tr>
		<tr> 
			<td width="50">Login</td>
			<td width="150"><input id="j_username" name="j_username" type="text" value="" size="25" maxlength="15"></td>
		</tr>
		<tr> 
			<td width="50">Password</td>
			<td width="150"><input id="j_password" name="j_password" type="password" value="" size="25" maxlength="15"></td>
		</tr>
		<tr><td colspan="2" style="color:red;"><%=(request.getParameter("relogin")!=null)?"Incorrect login/password or insufficient priviledges":"&nbsp;"%></td></tr>
		<tr> 
			<td><input type="reset" value="Reset"></td><td align="right"><input type="Submit" value="Login" ></td>
		</tr>
	</table>
</form>
<script language='javascript'>
	document.getElementById("j_username").focus();
</script>
</body>
</html>