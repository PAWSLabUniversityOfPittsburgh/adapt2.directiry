package edu.pitt.sis.adapt2.rest.directory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.pitt.sis.adapt2.rest.RestServlet;
import edu.pitt.sis.adapt2.rest.directory.DataRobot;

/**
 * Servlet implementation class for Servlet: RestUserAdd
 *
 */
 public class UserAdd extends RestServlet implements javax.servlet.Servlet 
 {
	static final long serialVersionUID = -2L;
   /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public UserAdd()
	{
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String _group_id = request.getParameter(DataRobot.REST_GROUP_ID);
		String _group_mnemonic = request.getParameter(DataRobot.REST_GROUP_MNEMONIC);

		Map<String, String> params = new HashMap<String, String>();
		params.put(DataRobot.REST_GROUP_ID, _group_id);
		params.put(DataRobot.REST_GROUP_MNEMONIC, _group_mnemonic);
		params.put(DataRobot.REST_CONTEXT_PATH, 
				"http://" + request.getServerName() + 
				((request.getLocalPort() != 80)?":"+ request.getLocalPort():"") + request.getContextPath());
		
		params = DataRobot.getGroupUserAdd(params, this.getSqlManager());
		String result = params.get(DataRobot.REST_RESULT);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(result);
	}  	  	  	    

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String _group_id = request.getParameter(DataRobot.REST_GROUP_ID);
		String _group_mnemonic = request.getParameter(DataRobot.REST_GROUP_MNEMONIC);
		String _user_login = request.getParameter(DataRobot.REST_USER_LOGIN);
		
		String _login = request.getParameter(DataRobot.REST_USER_F_LOGIN);
		String _pass = request.getParameter(DataRobot.REST_USER_F_PASS);
		String _name = request.getParameter(DataRobot.REST_USER_F_NAME);
		String _email = request.getParameter(DataRobot.REST_USER_F_EMAIL);
		String _org = request.getParameter(DataRobot.REST_USER_F_ORGANIZATION);
		String _country = request.getParameter(DataRobot.REST_USER_F_COUNTRY);
		String _city = request.getParameter(DataRobot.REST_USER_F_CITY);
		String _how = request.getParameter(DataRobot.REST_USER_F_HOW);

		String _bulk_add = request.getParameter(DataRobot.REST_USER_F_BULKADD);

//System.out.println("RestUserAdd:: _group_id = " + _group_id);		
//System.out.println("RestUserAdd:: _group_mnemonic = " + _group_mnemonic);		
//System.out.println("RestUserAdd:: _user_login = " + _user_login);		
//System.out.println("RestUserAdd:: _login = " + _login);		
//System.out.println("RestUserAdd:: _pass = " + _pass);		
//System.out.println("RestUserAdd:: _name = " + _name);		
//System.out.println("RestUserAdd:: _email = " + _email);		
//System.out.println("RestUserAdd:: _org = " + _org);		
//System.out.println("RestUserAdd:: _country = " + _country);		
//System.out.println("RestUserAdd:: _city = " + _city);		
//System.out.println("RestUserAdd:: _how = " + _how);		
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(DataRobot.REST_GROUP_ID, _group_id);
		params.put(DataRobot.REST_GROUP_MNEMONIC, _group_mnemonic);
		params.put(DataRobot.REST_USER_LOGIN, _user_login);
		params.put(DataRobot.REST_CONTEXT_PATH, 
				"http://" + request.getServerName() + 
				((request.getLocalPort() != 80)?":"+ request.getLocalPort():"") + request.getContextPath());

		params.put(DataRobot.REST_USER_F_LOGIN, _login);
		params.put(DataRobot.REST_USER_F_PASS, _pass);
		params.put(DataRobot.REST_USER_F_NAME, _name);
		params.put(DataRobot.REST_USER_F_EMAIL, _email);
		params.put(DataRobot.REST_USER_F_ORGANIZATION, _org);
		params.put(DataRobot.REST_USER_F_COUNTRY, _country);
		params.put(DataRobot.REST_USER_F_CITY, _city);
		params.put(DataRobot.REST_USER_F_HOW, _how);
		params.put(DataRobot.REST_USER_F_BULKADD, _bulk_add);
		
		params = DataRobot.setGroupUserAdd(params, this.getSqlManager());
		String result = params.get(DataRobot.REST_RESULT);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(result);
	}  	  	  	    

}