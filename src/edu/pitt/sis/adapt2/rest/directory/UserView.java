package edu.pitt.sis.adapt2.rest.directory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.pitt.sis.adapt2.rest.RestServlet;
import edu.pitt.sis.adapt2.rest.directory.DataRobot;

/**
 * Servlet implementation class for Servlet: RestUser
 *
 */
public class UserView extends RestServlet implements javax.servlet.Servlet
{
	static final long serialVersionUID = -2L;
		
   /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public UserView()
	{
		super();
	}   	

		
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
//		String req_user_id = request.getParameter("id");
//		String req_user_login = request.getParameter("login");
//		System.out.println("RestUserView.doGet:: req_user_id=" + req_user_id);		
//		System.out.println("RestUserView.doGet:: req_user_login=" + req_user_login);		
//		System.out.println("RestUserView.doGet:: query={" + request.getQueryString() + "}\n");
		
		String _user_id = request.getParameter(DataRobot.REST_USER_ID);
		String _user_login = request.getParameter(DataRobot.REST_USER_LOGIN);
		String _format = request.getParameter(DataRobot.REST_FORMAT);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(DataRobot.REST_USER_ID, _user_id);
		params.put(DataRobot.REST_USER_LOGIN, _user_login);
		params.put(DataRobot.REST_FORMAT, _format);
		params.put(DataRobot.REST_CONTEXT_PATH, 
				"http://" + request.getServerName() + 
				((request.getLocalPort() != 80)?":"+ request.getLocalPort():"") + request.getContextPath());
		
//		System.out.println("rURL " + request.getRequestURL());	
//		System.out.println("rURI " + request.getRequestURI());	

		params = DataRobot.getUserInfo(params, this.getSqlManager(), false /*multiple users*/);
		String result = params.get(DataRobot.REST_RESULT);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(result);
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String _user_id = request.getParameter(DataRobot.REST_USER_ID);
		String _user_login = request.getParameter(DataRobot.REST_USER_LOGIN);

		String _login = request.getParameter(DataRobot.REST_USER_F_LOGIN);
		String _name = request.getParameter(DataRobot.REST_USER_F_NAME);
		String _email = request.getParameter(DataRobot.REST_USER_F_EMAIL);
		String _org = request.getParameter(DataRobot.REST_USER_F_ORGANIZATION);
		String _country = request.getParameter(DataRobot.REST_USER_F_COUNTRY);
		String _city = request.getParameter(DataRobot.REST_USER_F_CITY);
		String _how = request.getParameter(DataRobot.REST_USER_F_HOW);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(DataRobot.REST_USER_ID, _user_id);
		params.put(DataRobot.REST_USER_LOGIN, _user_login);
		params.put(DataRobot.REST_CONTEXT_PATH, 
				"http://" + request.getServerName() + 
				((request.getLocalPort() != 80)?":"+ request.getLocalPort():"") + request.getContextPath());

		params.put(DataRobot.REST_USER_F_LOGIN, _login);
		params.put(DataRobot.REST_USER_F_NAME, _name);
		params.put(DataRobot.REST_USER_F_EMAIL, _email);
		params.put(DataRobot.REST_USER_F_ORGANIZATION, _org);
		params.put(DataRobot.REST_USER_F_COUNTRY, _country);
		params.put(DataRobot.REST_USER_F_CITY, _city);
		params.put(DataRobot.REST_USER_F_HOW, _how);
		
		params = DataRobot.setUserInfo(params, this.getSqlManager(), false /*multiple users*/);
		String result = params.get(DataRobot.REST_RESULT);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(result);
	}   	  	    
}