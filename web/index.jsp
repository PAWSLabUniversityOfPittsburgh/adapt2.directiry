<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01//EN' 'http://www.w3.org/TR/html4/strict.dtd'> 
<html>
<head>
<title>ADAPT&sup2; User &amp; Group Directory</title>
<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>
<link rel='StyleSheet' href='assets/rest.css' type='text/css' />
<%!
	String user_login;
	boolean isLoggedIn;
	String logout_url;
	String login_url;
	boolean invalidated;
%>
<%
	invalidated = false;
	if(request.getParameter("logout") != null)
	{
		session.invalidate();
		invalidated = true;
	}
	
	user_login = request.getRemoteUser();
	isLoggedIn = !invalidated && (user_login!=null) && (user_login.length()>0);
	logout_url = "<a href='" + request.getContextPath() + "/index.jsp?logout=1'>[logout]</a>";
	login_url = "<a href='" + request.getContextPath() + "/home'>[login]</a>";
%>
</head>
<body>
<table cellpadding='2px' cellspacing='0px' class='dkcyan_table' width='500px'>
	<caption class='dkcyan_table_caption'>ADAPT&sup2; User &amp; Group Directory</caption>
	<tr>
		<td class="dkcyan_table_header">Home<div style='text-align:right;display:block;'><%=(isLoggedIn)?user_login+"&nbsp;"+logout_url:login_url%></div></td>
	</tr>
	<tr>
		<td>
			<div>Users</div>
			<div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='<%=request.getContextPath()%>/users'>View all users&nbsp;<img border='0' src='<%=request.getContextPath()%>/assets/view_enabled.gif' /></a></div>
			<div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='<%=request.getContextPath()%>/users/new'>Add new user&nbsp;<img border='0' src='<%=request.getContextPath()%>/assets/add2_enable.gif' /></a></div>
		</td>
	</tr>
	<tr>
		<td>
			<div>Groups</div>
			<div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='<%=request.getContextPath()%>/groups'>View all groups&nbsp;<img border='0' src='<%=request.getContextPath()%>/assets/view_enabled.gif' /></a></div>
			<div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='<%=request.getContextPath()%>/groups/new'>Add new group&nbsp;<img border='0' src='<%=request.getContextPath()%>/assets/add2_enable.gif' /></a></div>
		</td>
	</tr>
	<tr>
		<td>
			<div>User Model (CUMULATE)</div>
			<div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a target="_blank" href='<%=request.getContextPath()%>/um/restart'>Restart</a> (allow up to 1 min)</div>
		</td>
	</tr>
	<tr>
	  <td>&nbsp;</td>
    </tr>
	<tr>
	  <td class='dkcyan_table_footer'>Michael V. Yudelson &copy; 2007</td>
    </tr>
</table>

</body>
</html>
