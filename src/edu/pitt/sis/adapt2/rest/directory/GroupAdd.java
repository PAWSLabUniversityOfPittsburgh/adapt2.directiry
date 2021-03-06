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
 public class GroupAdd extends RestServlet implements javax.servlet.Servlet 
 {
	static final long serialVersionUID = -2L;
   /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public GroupAdd()
	{
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put(DataRobot.REST_CONTEXT_PATH, 
				"http://" + request.getServerName() + 
				((request.getLocalPort() != 80)?":"+ request.getLocalPort():"") + request.getContextPath());
		
		params = DataRobot.getGroupAdd(params, this.getSqlManager());
		String result = params.get(DataRobot.REST_RESULT);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(result);
	}  	  	  	    

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String _group_mnemonic = request.getParameter(DataRobot.REST_GROUP_F_MNEMONIC);
		String _group_name = request.getParameter(DataRobot.REST_GROUP_F_NAME);

//System.out.println("RestGroupAdd:: _group_mnemonic = " + _group_mnemonic);		
//System.out.println("RestGroupAdd:: _group_name = " + _group_name);		
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(DataRobot.REST_GROUP_F_MNEMONIC, _group_mnemonic);
		params.put(DataRobot.REST_GROUP_F_NAME, _group_name);
		params.put(DataRobot.REST_CONTEXT_PATH, 
				"http://" + request.getServerName() + 
				((request.getLocalPort() != 80)?":"+ request.getLocalPort():"") + request.getContextPath());
		
		params = DataRobot.setGroupAdd(params, this.getSqlManager());
		String result = params.get(DataRobot.REST_RESULT);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(result);
	}  	  	  	    

}