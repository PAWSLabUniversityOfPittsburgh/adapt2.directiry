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
 * Servlet implementation class for Servlet: RestGroupEdit
 *
 */
 public class GroupEdit extends RestServlet implements javax.servlet.Servlet
 {
	static final long serialVersionUID = -2L;
		
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public GroupEdit()
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
		
		params = DataRobot.getGroupEditor(params, this.getSqlManager(), false /*multiple groups*/);
		String result = params.get(DataRobot.REST_RESULT);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(result);
	}  	  	  	    
}