package edu.pitt.sis.adapt2.rest.directory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.pitt.sis.adapt2.rest.RestServlet;
import edu.pitt.sis.adapt2.rest.directory.DataRobot;

public class GroupView extends RestServlet implements javax.servlet.Servlet
{
	static final long serialVersionUID = -2L;
		
   /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public GroupView()
	{
		super();
	}   	

		
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
//		String req_group_id = request.getParameter(RestDataRobot.REST_GROUP_ID);
//		String req_group_login = request.getParameter(RestDataRobot.REST_GROUP_MNEMONIC);
//		System.out.println("RestUserView.doGet:: req_group_id=" + req_group_id);		
//		System.out.println("RestUserView.doGet:: req_group_login=" + req_group_login);		
//		System.out.println("RestUserView.doGet:: query={" + request.getQueryString() + "}\n");

		String _group_id = request.getParameter(DataRobot.REST_GROUP_ID);
		String _group_mnemonic = request.getParameter(DataRobot.REST_GROUP_MNEMONIC);
		String _format = request.getParameter(DataRobot.REST_FORMAT);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(DataRobot.REST_GROUP_ID, _group_id);
		params.put(DataRobot.REST_GROUP_MNEMONIC, _group_mnemonic);
		params.put(DataRobot.REST_FORMAT, _format);
		params.put(DataRobot.REST_CONTEXT_PATH, 
				"http://" + request.getServerName() + 
				((request.getLocalPort() != 80)?":"+ request.getLocalPort():"") + request.getContextPath());
		
		params = DataRobot.getGroupInfo(params, this.getSqlManager(), false /*multiple groups*/);
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
		String _group_id = request.getParameter(DataRobot.REST_GROUP_ID);
		String _group_mnemonic = request.getParameter(DataRobot.REST_GROUP_MNEMONIC);

		String _mnemonic = request.getParameter(DataRobot.REST_GROUP_F_MNEMONIC);
		String _name = request.getParameter(DataRobot.REST_GROUP_F_NAME);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(DataRobot.REST_GROUP_ID, _group_id);
		params.put(DataRobot.REST_GROUP_MNEMONIC, _group_mnemonic);
		params.put(DataRobot.REST_CONTEXT_PATH, 
				"http://" + request.getServerName() + 
				((request.getLocalPort() != 80)?":"+ request.getLocalPort():"") + request.getContextPath());

		params.put(DataRobot.REST_GROUP_F_MNEMONIC, _mnemonic);
		params.put(DataRobot.REST_GROUP_F_NAME, _name);
		
		params = DataRobot.setGroupInfo(params, request, this.getSqlManager(), false /*multiple groups*/);
		String result = params.get(DataRobot.REST_RESULT);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(result);
	}   	  	    
}