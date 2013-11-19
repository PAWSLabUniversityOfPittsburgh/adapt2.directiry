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
 * Servlet implementation class for Servlet: RestGroupUserRemove
 *
 */
 public class GroupUserRemove extends RestServlet implements javax.servlet.Servlet
 {
	static final long serialVersionUID = -2L;
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public GroupUserRemove()
	{
		super();
	}   	 	

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		String _user_login = request.getParameter(DataRobot.REST_USER_LOGIN);
		String _group_mnemonic = request.getParameter(DataRobot.REST_GROUP_MNEMONIC);
		String _user_id = request.getParameter(DataRobot.REST_USER_ID);
		String _group_id = request.getParameter(DataRobot.REST_GROUP_ID);
		String _method = request.getParameter(DataRobot.REST_METHOD);
			
//		System.out.println("RestGroupUserRemove.doPut:: _user_id=" + _user_id);		
//		System.out.println("RestGroupUserRemove.doPut:: _user_login=" + _user_login);		
//		System.out.println("RestGroupUserRemove.doPut:: _group_id=" + _group_id);		
//		System.out.println("RestGroupUserRemove.doPut:: _group_mnemonic=" + _group_mnemonic);		
//		System.out.println("RestGroupUserRemove.doPut:: _method=" + _method);		
//		System.out.println("");		
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(DataRobot.REST_USER_LOGIN, _user_login);
		params.put(DataRobot.REST_GROUP_MNEMONIC, _group_mnemonic);
		params.put(DataRobot.REST_USER_ID, _user_id);
		params.put(DataRobot.REST_GROUP_ID, _group_id);
		params.put(DataRobot.REST_METHOD, _method);
		params.put(DataRobot.REST_CONTEXT_PATH, 
				"http://" + request.getServerName() + 
				((request.getLocalPort() != 80)?":"+ request.getLocalPort():"") + request.getContextPath());
		
		params = DataRobot.removeGroupUser(params, this.getSqlManager());
		String result = params.get(DataRobot.REST_RESULT);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(result);
		
		
	}   	  	    
}