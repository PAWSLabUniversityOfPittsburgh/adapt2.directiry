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
 * Servlet implementation class for Servlet: RestUserEdit
 *
 */
 public class UserEdit extends RestServlet implements javax.servlet.Servlet
 {
	static final long serialVersionUID = -2L;
		
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public UserEdit()
	{
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
//		String req_user_id = request.getParameter(RestUserDataRobot.REST_USER_ID);
//		String req_user_login = request.getParameter(RestUserDataRobot.REST_USER_LOGIN);
//		System.out.println("RestUserEdit.doGet:: req_user_id=" + req_user_id);		
//		System.out.println("RestUserEdit.doGet:: req_user_login=" + req_user_login);		
		
		String _user_id = request.getParameter(DataRobot.REST_USER_ID);
		String _user_login = request.getParameter(DataRobot.REST_USER_LOGIN);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(DataRobot.REST_USER_ID, _user_id);
		params.put(DataRobot.REST_USER_LOGIN, _user_login);
		params.put(DataRobot.REST_CONTEXT_PATH, 
				"http://" + request.getServerName() + 
				((request.getLocalPort() != 80)?":"+ request.getLocalPort():"") +request.getContextPath());
		
		params = DataRobot.getUserEditor(params, this.getSqlManager(), false /*multiple users*/);
		String result = params.get(DataRobot.REST_RESULT);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(result);
	}  	  	  	    
}