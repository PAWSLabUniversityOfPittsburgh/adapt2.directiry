package edu.pitt.sis.adapt2.rest.directory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.pitt.sis.adapt2.rest.RestServlet;

/**
 * Servlet implementation class for Servlet: RestAllGroups
 */
public class AllGroups extends RestServlet implements Servlet
{
	static final long serialVersionUID = -2L;
		
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public AllGroups()
	{
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String _format = request.getParameter(DataRobot.REST_FORMAT);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(DataRobot.REST_FORMAT, _format);
		params.put(DataRobot.REST_CONTEXT_PATH, 
				"http://" + request.getServerName() + 
				((request.getLocalPort() != 80)?":"+ request.getLocalPort():"") + request.getContextPath());
		
		params = DataRobot.getGroupInfo(params, this.getSqlManager(), true /*multiple groups*/);
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
		// TODO adding new group
	}   	  	    
}