package edu.pitt.sis.adapt2.rest;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import edu.pitt.sis.paws.core.utils.SQLManager;

/**
 * Servlet implementation class for Servlet: RestServlet
 * 
 */
public abstract class RestServlet extends HttpServlet implements Servlet
{
	static final long serialVersionUID = -2L;
	private SQLManager sqlManager;
	public static final String db_context = "java:comp/env/jdbc/main";;
	public static final String db_context_um = "java:comp/env/jdbc/cbum";;
	
	/**
	 * @return
	 */
	public SQLManager getSqlManager() { return sqlManager; }

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public RestServlet()
	{
		super();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Servlet#destroy()
	 */
	public void destroy()
	{
		sqlManager = null;
		super.destroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException
	{
		super.init();
		
//		try{
//		Context initCtx = new InitialContext();
//		Object obj =  initCtx.lookup("jdbc/portal");
//		DataSource ds = (DataSource)obj;
//		}
//		catch(Exception e) {e.printStackTrace(System.out);}
		sqlManager = new SQLManager(db_context);
	}
}