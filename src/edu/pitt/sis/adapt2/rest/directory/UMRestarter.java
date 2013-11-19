package edu.pitt.sis.adapt2.rest.directory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class for Servlet: UMRestarter
 * 
 */
public class UMRestarter extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	static final long serialVersionUID = -2L;

	private static final String um_restart_url = "um_restart_url";
	private String um_restart_url_param ;
	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public void init() throws ServletException
	{
		super.init();
		um_restart_url_param = getInitParameter(um_restart_url);
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
//        URLConnection uconn = (new URL(um_restart_url_param)).openConnection();
//        InputStream in  = uconn.getInputStream();
//        in.close();

		response.sendRedirect(um_restart_url_param);
		
//		String _context_path = request.getContextPath();
//		String result = DataRobot.getMessageHTML("User Info Updated",
//				"User Model is restarting. Please allow 1-2 min for that.<br/>Back to Directory <a href='"+_context_path+"/home'>home</a>.",
//				_context_path);
//		
//		PrintWriter out = response.getWriter();
//		out.println(result);
	}
}