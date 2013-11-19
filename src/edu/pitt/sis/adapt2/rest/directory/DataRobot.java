package edu.pitt.sis.adapt2.rest.directory;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import edu.pitt.sis.adapt2.rest.RestServlet;
import edu.pitt.sis.paws.core.utils.Digest;
import edu.pitt.sis.paws.core.utils.SQLManager;

public class DataRobot
{
	// Constants
	//		User
	//			Identifying and configuring parameters
	public static final String REST_USER_ID = "user_id";
	public static final String REST_USER_LOGIN = "user_login";
	
	//			Field name parameters
	public static final String REST_USER_F_LOGIN = "login";
	public static final String REST_USER_F_PASS = "pass";
	public static final String REST_USER_F_NAME = "name";
	public static final String REST_USER_F_EMAIL = "email";
	public static final String REST_USER_F_ORGANIZATION = "organization";
	public static final String REST_USER_F_CITY = "city";
	public static final String REST_USER_F_COUNTRY = "country";
	public static final String REST_USER_F_HOW = "how";
	public static final String REST_USER_F_BULKADD = "bulk_add";

	//		Group
	//			Identifying and configuring parameters
	public static final String REST_GROUP_ID = "group_id";
	public static final String REST_GROUP_MNEMONIC = "group_mnemonic";
	
	//			Field name parameters
	public static final String REST_GROUP_F_MNEMONIC = "mnemonic";
	public static final String REST_GROUP_F_NAME = "name";

	//		Group-User
	public static final String REST_GROUP_USER_NOT_SELECTED = "-- not selected --";
	
	//		Portal
	public static final String REST_NODE_ID = "node_id";
	
	
	//		Common constants
	public static final String REST_FORMAT = "_format";
	public static final String REST_FORMAT_HTML = "html";
	public static final String REST_FORMAT_RDF = "rdf";
	public static final String REST_CONTEXT_PATH = "context_path";
	public static final String REST_STATUS = "Status";
	public static final String REST_STATUS_OK = "OK";
	public static final String REST_STATUS_ERROR = "Error";
	public static final String REST_METHOD = "_method";
	public static final String REST_METHOD_DELETE = "delete";
	
	public static final String REST_RESULT = "Result";

	public static Map<String, String> getUserInfo(Map<String, String> _parameters, SQLManager _sqlm, boolean multiple_users)
	{
		String user_id = _parameters.get(REST_USER_ID);
		String user_login = _parameters.get(REST_USER_LOGIN);
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		if(multiple_users)
		{// get user info by ID
			String qry = "SELECT * FROM ent_user WHERE UserID NOT IN(1,2) AND IsGroup=0;";
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			try
			{// retrieve user by login from db
				conn = _sqlm.getConnection();
				stmt = conn.prepareStatement(qry);
				rs = stmt.executeQuery();
				
				_parameters = formatUserInfo(rs, _parameters, true /*multiple_users*/, conn);
				
				rs.close();
				stmt.close();
				
			}// end of -- retrieve user by login from db
			catch(SQLException sqle)
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while retrieving user info.", _context_path));
				sqle.printStackTrace(System.out);
			}
			finally { SQLManager.recycleObjects(conn, stmt, rs); }
		}// end of -- get user info by ID
		else if((user_login != null && user_login.length() != 0) || (user_id != null && user_id.length() != 0))
		{// get user info by Login or ID
			String qry = (user_login != null && user_login.length() != 0)?"SELECT * FROM ent_user WHERE Login='" + user_login + "' AND IsGroup=0;"
					:"SELECT * FROM ent_user WHERE UserID=" + user_id + " AND IsGroup=0;";
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			try
			{// retrieve user by login from db
				conn = _sqlm.getConnection();
				stmt = conn.prepareStatement(qry);
				rs = stmt.executeQuery();
				
				_parameters = formatUserInfo(rs, _parameters, false /*multiple_users*/, conn);

				rs.close();
				stmt.close();
				
			}// end of -- retrieve user by login from db
			catch(SQLException sqle)
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while retrieving user info.", _context_path));
				sqle.printStackTrace(System.out);
			}
			finally { SQLManager.recycleObjects(conn, stmt, rs); }
		}// end of -- get user info by Login or ID
		else
		{// no parameters
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, getErrorMessageHTML("User ID or login specified incorrectly", _context_path));
		}// end of -- no parameters
		
		return _parameters;
	}
	
	public static Map<String, String> setUserInfo(Map<String, String> _parameters, SQLManager _sqlm, boolean multiple_users)
	{
		String user_id = _parameters.get(REST_USER_ID);
		String user_login = _parameters.get(REST_USER_LOGIN);
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		if(multiple_users)
		{// get user info by ID
//			String qry = "SELECT * FROM ent_user WHERE UserID NOT IN(1,2) AND IsGroup=0;";
//			Connection conn = null;
//			PreparedStatement stmt = null;
//			ResultSet rs = null;
//			
//			try
//			{// retrieve user by login from db
//				conn = _sqlm.getConnection();
//				stmt = conn.prepareStatement(qry);
//				rs = SQLManager.executeStatement(stmt);
//				
//				_parameters = formatUserInfo(rs, _parameters, true /*multiple_users*/);
//				
//				rs.close();
//				stmt.close();
//				
//			}// end of -- retrieve user by login from db
//			catch(SQLException sqle)
//			{
//				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
//				_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while retrieving user info.", _context_path));
//				sqle.printStackTrace(System.out);
//			}
//			finally {_sqlm.freeConnection(conn);}
		}// end of -- get user info by ID
		else if((user_login != null && user_login.length() != 0) || (user_id != null && user_id.length() != 0))
		{// set user info by Login or ID
			String user_login_new = _parameters.get(REST_USER_F_LOGIN);
			String qry = "UPDATE ent_user SET Login='" + _parameters.get(REST_USER_F_LOGIN) + "',"+
					" Name='" + clrStr(_parameters.get(REST_USER_F_NAME)) + "',"+
					" EMail='" + clrStr(_parameters.get(REST_USER_F_EMAIL)) + "',"+
					" Organization='" + clrStr(_parameters.get(REST_USER_F_ORGANIZATION)) + "',"+
					" City='" + clrStr(_parameters.get(REST_USER_F_CITY)) + "',"+
					" Country='" + clrStr(_parameters.get(REST_USER_F_COUNTRY)) + "',"+
					" How='" + clrStr(_parameters.get(REST_USER_F_HOW)) + "'";
			if(user_login != null && user_login.length() != 0)
			{// by login
				qry += " WHERE Login='" + _parameters.get(REST_USER_LOGIN) + "';";
			}
			else
			{// by id
				qry += " WHERE UserID='" + _parameters.get(REST_USER_ID) + "';";
			}

			Connection conn = null;
			PreparedStatement stmt = null;
			try
			{// set user by login from db
				conn = _sqlm.getConnection();
//System.out.println("qry="+qry);
				stmt = conn.prepareStatement(qry);
				stmt.executeUpdate();
//				SQLManager.executeUpdate(conn, SQLManager.stringUnbreak(qry));
				
				String result = getMessageHTML("User Info Updated",
						"<a href='"+_context_path+"/user/" + 
						((user_login != null && user_login.length() != 0)?user_login_new:"/id/"+user_id) +
						"'>Back</a> to user record",
						_context_path);
				_parameters.put(REST_STATUS, REST_STATUS_OK);
				_parameters.put(REST_RESULT, result);
				

			}// end of -- set user by login from db
			catch(SQLException sqle)
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while updating user info.", _context_path));
				sqle.printStackTrace(System.out);
			}
			finally { SQLManager.recycleObjects(conn, stmt, null); }
		}// end of -- set user info by Login or ID
		else
		{// no parameters
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, getErrorMessageHTML("User ID or login specified incorrectly", _context_path));
		}// end of -- no parameters
		
		return _parameters;
	}
	
	private static Map<String, String> formatUserInfo(ResultSet _rs, Map<String, String> _parameters, boolean multiple_users,
			Connection _conn) throws SQLException
	{
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		String _format = _parameters.get(REST_FORMAT);
		
		
		if(!multiple_users)
		{// single user
			if(_rs.next())
			{// user exists
				String login = _rs.getString("Login");
				String name = _rs.getString("Name");
				String email = _rs.getString("Email");
				String org = _rs.getString("Organization");
				String city = _rs.getString("City");
				String country = _rs.getString("Country");
				String how = _rs.getString("How");
				
				String result = "";
				
				if(_format == null || _format.length() == 0 || _format.equals(REST_FORMAT_HTML))
				{// format is html
					// retrieve groups
					String groups = "<div name='openerControl'>Groups</div>\n<div name='opener'>";
					String gqry = 
						"SELECT g.Login, g.Name "+
						"FROM ent_user u RIGHT JOIN rel_user_user uu ON(uu.ChildUserID=u.UserID) "+
						"RIGHT JOIN ent_user g ON(uu.ParentUserID=g.UserID) "+
						"WHERE u.Login='" + login + "';";
					PreparedStatement stmt = _conn.prepareStatement(gqry);
					ResultSet grs = stmt.executeQuery();
					while(grs.next())
					{
						String gname = grs.getString("Name");
						String gmnemonic = grs.getString("Login");
						groups += "<div>&nbsp;&nbsp;<a href='" + _context_path + "/group/" + gmnemonic + "'>" + gname + "</a></div>";
					}
					groups += "</div>";
					grs.close();
					stmt.close();
					// end of -- retrieve groups
//					String view_html = "<a href='" + _context_path + "/user/" + login + "'><img src='" + _context_path + "/assets/view_enabled.gif' title='View' alt='View' border='0'></a>";
					String rdf_html = "<a href='" + _context_path + "/rdf/user/" + login + "' title='View in RDF Format'>RDF</a>";
					String home_html = "<a href='" + _context_path + "/' title='Home'>Home</a>";
					String all_users_html = "&nbsp;&raquo;&nbsp;<a href='" + _context_path + "/users' title='List of all Users'>All Users</a>";
					String edit_html = "<a href='" + _context_path + "/user/" + login + "/edit' title='Edit User Info'>Edit</a>";
//					
//					String view_html = "<a href='" + _context_path + "/user/" + login + "'><img src='" + _context_path + "/assets/view_enabled.gif' title='View' alt='View' border='0'></a>";
//					String rdf_html = "<a href='" + _context_path + "/rdf/user/" + login + "'><img src='" + _context_path + "/assets/rdf.gif' title='RDF' alt='RDF' border='0'></a>";
//					String home_html = "<a href='" + _context_path + "/'><img src='" + _context_path + "/assets/home.gif' title='Home' alt='Home' border='0'></a>";
//					String edit_html = "<a href='" + _context_path + "/user/" + login + "/edit'><img src='" + _context_path + "/assets/edit2_enable.gif' title='Edit' alt='Edit' border='0'></a>";
//					
					String logout_html = "<a href='" + _context_path + "/index.jsp?logout=1' title='Lougout from the system'>[logout]</a>";
					result = 
						getPageHeaderHTML("ADAPT&sup2; User &amp; Group Directory - User Info", _context_path) +
						"<table cellpadding='2px' cellspacing='0px' class='blue_table' width='500px'>\n"+
						"	<tr><td colspan='2'class='dkcyan_table_caption'>" + ((name!=null && name.length()>0)?name:"{unspecified}") + "</td></tr>\n"+
						"	<tr>\n" +
						"	  <td class='blue_table_header' colspan=2'2>" + home_html + all_users_html +
							"<div style='text-align:right;display:block;'>" + rdf_html + "&nbsp;&nbsp;" + edit_html + "&nbsp;&nbsp;" + logout_html + "</div></td>\n" +
						"  	</tr>\n" +
//						"	<tr>\n"+
//						"	  <td class='blue_table_header'>" + home_html + "</td>\n"+
//						"	  <td class='blue_table_header' align='right'>" + edit_html + "&nbsp;&nbsp;&nbsp;&nbsp;" + rdf_html + "&nbsp;&nbsp;" + view_html + logout_url + "</td>\n"+
//						"  	</tr>\n"+
						"	<tr>\n"+
						"		<td>Login</td>\n"+
						"		<td width='100%'>" + login + "</td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td>Email</td>\n"+
						"		<td>" + email.replaceAll("@", "(at)").replaceAll("\\.", "(dot)") + "</td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td>Organization</td>\n"+
						"		<td>" + org + "</td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td>City</td>\n"+
						"		<td>" + city + "</td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td>Country</td>\n"+
						"		<td>" + country + "</td>\n"+
						"	</tr>\n"+
						"	<tr valign='top'>\n"+
						"		<td>Notes</td>\n"+
						"		<td>" + how + "</td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td valign='top' class='blue_table_footer'>Member of</td>\n"+
						"		<td class='blue_table_footer'>" + groups + "</td>\n"+
						"	</tr>\n"+
						"</table>\n"+
						"</body></html>";
	
					_parameters.put(REST_STATUS, REST_STATUS_OK);
					_parameters.put(REST_RESULT, result);
				}// end of -- format is html
				else if(_format != null && _format.length() != 0 && _format.equals(REST_FORMAT_RDF))
				{//format is rdf
					result = 
						getPageHeaderRDF() + "\n" + 
						"	<foaf:Person rdf:about='" + _context_path + "/rdf/users#" + login + "'>\n" + 
						"		<foaf:name>" + name + "</foaf:name>\n" + 
						"		<foaf:holdsAccount>\n" + 
						"			<foaf:OnlineAccount>\n" + 
						"				<foaf:accountServiceHomepage rdf:resource='" + _context_path + "/login.jsp'/>\n" + 
						"				<foaf:accountName>" + login + "</foaf:accountName>\n" + 
						"			</foaf:OnlineAccount>\n" + 
						"		</foaf:holdsAccount>\n" + 
						"		<foaf:mbox_sha1sum>" + Digest.SHA1(email) + "</foaf:mbox_sha1sum>\n" + 
						"		<vCard:note>" + how + "</vCard:note>\n" + 
						"		<rdfs:isDefinedBy rdf:resource='" + _context_path + "/rdf/users'/>\n" + 
						"	</foaf:Person>\n" + 
						"</rdf:RDF>";
					
					_parameters.put(REST_STATUS, REST_STATUS_OK);
					_parameters.put(REST_RESULT, result);
				}// end of -- format is rdf
				else
				{
					_parameters.put(REST_STATUS, REST_STATUS_ERROR);
					_parameters.put(REST_RESULT, getErrorMessageHTML("Specified format of data is not supported", _context_path));
				}
				
			}// end of -- user exists
			else
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("Specified user has not been found", _context_path));
			}
		}// end of -- single user
		else
		{// multiple users
			if(_format != null && _format.length() == 0 && (!_format.equals(REST_FORMAT_HTML)) && (!_format.equals(REST_FORMAT_RDF)) )
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("Specified format of data is not supported", _context_path));
			}
			else
			{// format ok
				String result = "";
				// HEADER
				if(_format == null || _format.length() == 0 || _format.equals(REST_FORMAT_HTML))
				{
					String home_html = "<a href='" + _context_path + "/' title='Home'>Home</a>";
					String rdf_html = "<a href='" + _context_path + "/rdf/users' title='View in RDF Format'>RDF</a>";
					String add_html = "<a href='" + _context_path + "/group/world/users/new' title='Add New User'>Add</a>";
					String logout_html = "<a href='" + _context_path + "/index.jsp?logout=1' title='Lougout from the system'>[logout]</a>";
//					String home_html = "<a href='" + _context_path + "/'><img src='" + _context_path + "/assets/home.gif' title='Home' alt='Home' border='0'></a>";
//					String rdf_html = "<a href='" + _context_path + "/rdf/users'><img src='" + _context_path + "/assets/rdf.gif' title='RDF' alt='RDF' border='0'></a>";
//					String add_html = "<a href='" + _context_path + "/group/world/users/new'><img src='" + _context_path + "/assets/add2_enable.gif' title='Add New User' alt='Add' border='0'></a>";
//					String logout_url = "&nbsp;&nbsp;<a href='" + _context_path + "/index.jsp?logout=1' title='Lougout from the system'>[logout]</a>";
					result = getPageHeaderHTML("ADAPT&sup2; User &amp; Group Directory - List of All Users", _context_path) +
							"<table cellpadding='2px' cellspacing='0px' class='blue_table' width='500px'>\n"+
							"	<tr><td colspan='2'class='dkcyan_table_caption'>List of All Users</td></tr>\n"+
							"	<tr>\n" +
							"	  <td class='blue_table_header' colspan=2'2>" + home_html + 
								"<div style='text-align:right;display:block;'>" + rdf_html + "&nbsp;&nbsp;" + add_html + "&nbsp;&nbsp;" + logout_html + "</div></td>\n" +
							"  	</tr>\n";
				}
				else if(_format != null && _format.length() != 0 && _format.equals(REST_FORMAT_RDF))
					result = getPageHeaderRDF();
				
				int user_count = 0;
				
				while(_rs.next())
				{// for all users
					user_count ++;
					
					String login = _rs.getString("Login");
					String name = _rs.getString("Name");
					String email = _rs.getString("Email");
//					String org = _rs.getString("Organization");
//					String city = _rs.getString("City");
//					String country = _rs.getString("Country");
					String how = _rs.getString("How");
					
					if(_format == null || _format.length() == 0 || _format.equals(REST_FORMAT_HTML))
					{// format is html
//						// retrieve groups
//						String groups = "<div name='openerControl'>Groups</div>\n<div name='opener'>";
//						String gqry = 
//							"SELECT g.Login, g.Name "+
//							"FROM ent_user u RIGHT JOIN rel_user_user uu ON(uu.ChildUserID=u.UserID) "+
//							"RIGHT JOIN ent_user g ON(uu.ParentUserID=g.UserID) "+
//							"WHERE u.Login='" + login + "';";
//						PreparedStatement stmt = _conn.prepareStatement(gqry);
//						ResultSet grs = SQLManager.executeStatement(stmt);
//						while(grs.next())
//						{
//							String gname = grs.getString("Name");
//							String gmnemonic = grs.getString("Login");
//							groups += "<div>&nbsp;&nbsp;<a href='" + _context_path + "/group/" + gmnemonic + "'>" + gname + "</a></div>";
//						}
//						groups += "</div>";
//
//						grs.close();
//						stmt.close();
//						// end of -- retrieve groups
//						String home_html = "<a href='" + _context_path + "/'><img src='" + _context_path + "/assets/home.gif' title='Home' alt='Home' border='0'></a>";
//						String view_html = "<a href='" + _context_path + "/user/" + login + "'><img src='" + _context_path + "/assets/view_enabled.gif' title='View' alt='View' border='0'></a>";
//						String rdf_html = "<a href='" + _context_path + "/rdf/user/" + login + "'><img src='" + _context_path + "/assets/rdf.gif' title='RDF' alt='RDF' border='0'></a>";
//						String edit_html = "<a href='" + _context_path + "/user/" + login + "/edit'><img src='" + _context_path + "/assets/edit2_enable.gif' title='Edit' alt='Edit' border='0'></a>";
						
						result += 
							"<tr>\n" +
							"	<td valign='top'>&nbsp;&nbsp;&bull;</td>\n" +
							"	<td><a href='" + _context_path + "/user/" + login + "'>" + name + "&nbsp;(" + login + ")</a></td>\n" +
							"</tr>\n";

//							String logout_url = "&nbsp;&nbsp;<a href='" + _context_path + "/index.jsp?logout=1' title='Lougout from the system'>[logout]</a>";
//							"<table cellpadding='2px' cellspacing='0px' class='blue_table' width='600px'>\n"+
//							"	<tr><td colspan='2'class='dkcyan_table_caption'>" + ((name!=null && name.length()>0)?name:"{unspecified}") + "</td></tr>\n"+
//							"	<tr>\n"+
//							"	  <td class='blue_table_header'>" + home_html + "</td>\n"+
//							"	  <td class='blue_table_header' align='right'>" + edit_html + "&nbsp;&nbsp;&nbsp;&nbsp;" + rdf_html + "&nbsp;&nbsp;" + view_html + logout_url +"</td>\n"+
//							"  	</tr>\n"+
//							"	<tr>\n"+
//							"		<td>Login</td>\n"+
//							"		<td width='100%'>" + login + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td>Email</td>\n"+
//							"		<td>" + email.replaceAll("@", "(at)").replaceAll("\\.", "(dot)") + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td>Organization</td>\n"+
//							"		<td>" + org + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td>City</td>\n"+
//							"		<td>" + city + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td>Country</td>\n"+
//							"		<td>" + country + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr valign='top'>\n"+
//							"		<td>Notes</td>\n"+
//							"		<td>" + how + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td valign='top' class='blue_table_footer'>Member of</td>\n"+
//							"		<td class='blue_table_footer'>" + groups + "</td>\n"+
//							"	</tr>\n"+
//							"</table>\n<p/>\n";
		
					}// end of -- format is html
					else if(_format != null && _format.length() != 0 && _format.equals(REST_FORMAT_RDF))
					{//format is rdf
						result += 
							"	<foaf:Person rdf:about='" + _context_path + "/rdf/users#" + login + "'>\n" + 
							"		<foaf:name>" + name + "</foaf:name>\n" + 
							"		<foaf:holdsAccount>\n" + 
							"			<foaf:OnlineAccount>\n" + 
							"				<foaf:accountServiceHomepage rdf:resource='" + _context_path + "/login.jsp'/>\n" + 
							"				<foaf:accountName>" + login + "</foaf:accountName>\n" + 
							"			</foaf:OnlineAccount>\n" + 
							"		</foaf:holdsAccount>\n" + 
							((email != null && email.length() >0 )?"		<foaf:mbox_sha1sum>" + Digest.SHA1(email) + "</foaf:mbox_sha1sum>\n":"") + 
							"		<vCard:note>" + how + "</vCard:note>\n" + 
							"		<rdfs:isDefinedBy rdf:resource='" + _context_path + "/rdf/users'/>\n" + 
							"	</foaf:Person>\n";
					}// end of -- format is rdf
				}// end of -- for all users
				
				
				// FOOTER
				if(_format == null || _format.length() == 0 || _format.equals(REST_FORMAT_HTML))
					result +=
						"	<tr>\n"+
						"		<td class='blue_table_footer'>&nbsp;</td>\n"+
						"		<td class='blue_table_footer'>" + user_count + " user(s)</td>\n"+
						"	</tr>\n"+
						"</table>\n" +
						"</body></html>";
				else if(_format != null && _format.length() != 0 && _format.equals(REST_FORMAT_RDF))
					result += "</rdf:RDF>";
					
				_parameters.put(REST_STATUS, REST_STATUS_OK);
				_parameters.put(REST_RESULT, result);
			}// end of -- format ok
		}// end of -- multiple users
		return _parameters;
	}

	public static Map<String, String> getUserEditor(Map<String, String> _parameters, SQLManager _sqlm, boolean multiple_users)
	{
		String user_id = _parameters.get(REST_USER_ID);
		String user_login = _parameters.get(REST_USER_LOGIN);
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		if(multiple_users)
		{// get user info by ID
//			String qry = "SELECT * FROM ent_user WHERE UserID NOT IN(1,2) AND IsGroup=0;";
//			Connection conn = null;
//			PreparedStatement stmt = null;
//			ResultSet rs = null;
//			
//			try
//			{// retrieve user by login from db
//				conn = _sqlm.getConnection();
//				stmt = conn.prepareStatement(qry);
//				rs = SQLManager.executeStatement(stmt);
//				
//				_parameters = formatUserEdit(rs, _parameters, true /*multiple_users*/);
//				
//			}// end of -- retrieve user by login from db
//			catch(SQLException sqle)
//			{
//				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
//				_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while retrieving user info.", _context_path));
//				sqle.printStackTrace(System.out);
//			}
//			finally {_sqlm.freeConnection(conn);}
		}// end of -- get user info by ID
		else if((user_login != null && user_login.length() != 0) || (user_id != null && user_id.length() != 0))
		{// get user info by Login or ID
			String qry = (user_login != null && user_login.length() != 0)?"SELECT * FROM ent_user WHERE login='" + user_login + "' AND IsGroup=0;"
					:"SELECT * FROM ent_user WHERE UserID='" + user_id + "' AND IsGroup=0;";
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			try
			{// retrieve user by login from db
				conn = _sqlm.getConnection();
				stmt = conn.prepareStatement(qry);
				rs = stmt.executeQuery();
				
				_parameters = formatUserEditor(rs, _parameters, false /*multiple_users*/);
				
				rs.close();
				stmt.close();
			}// end of -- retrieve user by login from db
			catch(SQLException sqle)
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while retrieving user info.", _context_path));
				sqle.printStackTrace(System.out);
			}
			finally { SQLManager.recycleObjects(conn, stmt, rs); }
		}// end of -- get user info by Login or ID
		else
		{// no parameters
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, getErrorMessageHTML("User ID or login specified incorrectly", _context_path));
		}// end of -- no parameters
		
		return _parameters;
	}


	private static Map<String, String> formatUserEditor(ResultSet _rs, Map<String, String> _parameters, boolean multiple_users) throws SQLException
	{
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		String _format = _parameters.get(REST_FORMAT);
		
		
		if(!multiple_users)
		{// single user
			if(_rs.next())
			{// user exists
				String login = _rs.getString("Login");
				String name = _rs.getString("Name");
				String email = _rs.getString("Email");
				String org = _rs.getString("Organization");
				String city = _rs.getString("City");
				String country = _rs.getString("Country");
				String how = _rs.getString("How");
				
				String result = "";
				
				if(_format == null || _format.length() == 0 || _format.equals(REST_FORMAT_HTML))
				{// format is html
					String home_html = "<a href='" + _context_path + "/' title='Home'>Home</a>";
					String all_users_html = "&nbsp;&raquo;&nbsp;<a href='" + _context_path + "/users' title='List of all Users'>All Users</a>";
					String view_user_html = "&nbsp;&raquo;&nbsp;<a href='" + _context_path + "/user/" + login + "' title='Back to User Info'>User</a>";
					String logout_html = "<a href='" + _context_path + "/index.jsp?logout=1' title='Lougout from the system'>[logout]</a>";

					result = 
						getPageHeaderHTML("ADAPT&sup2; User &amp; Group Directory - User Info Editor", _context_path) +
						"<form name='edit' id='edit' form method='post' action='" + _context_path + "/user/" + login + "'>\n"+
						"<input name='" + REST_USER_LOGIN + "' type='hidden' value='" + login + "'/>"+
						"<table cellpadding='2px' cellspacing='0px' class='blue_table' width='500px'>\n"+
						"	<tr><td colspan='2'class='dkcyan_table_caption'>" + name + " - Editing</td></tr>\n"+
						"	<tr>\n" +
						"	  <td class='blue_table_header' colspan=2'2>" + home_html + all_users_html + view_user_html +
							"<div style='text-align:right;display:block;'>" + logout_html + "</div></td>\n" +
						"  	</tr>\n" +
						"	<tr>\n"+
						"		<td>Name</td>\n"+
						"		<td width='100%'><input name='" + REST_USER_F_NAME + "' type='text' maxlength='60' size='45' value='" + name + "'/></td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td>Login</td>\n"+
						"		<td><input id='" + REST_USER_F_LOGIN + "' name='" + REST_USER_F_LOGIN + "' type='text' maxlength='15' size='21' value='" + login + "'/>&nbsp;<font color='#FF0000'>*</font></td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td>Email</td>\n"+
						"		<td><input name='" + REST_USER_F_EMAIL + "' type='text' maxlength='50' size='45' value='" + email + "'/></td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td>Organization</td>\n"+
						"		<td><input name='" + REST_USER_F_ORGANIZATION + "' type='text' maxlength='100' size='45' value='" + org + "'/></td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td>City</td>\n"+
						"		<td><input name='" + REST_USER_F_CITY + "' type='text' maxlength='30' size='45' value='" + city + "'/></td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td>Country</td>\n"+
						"		<td><input name='" + REST_USER_F_COUNTRY + "'type='text' maxlength='50' size='45' value='" + country + "'/></td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td valign='top'>Notes</td>\n"+
						"		<td><textarea name='" + REST_USER_F_HOW + "' cols='44' rows='5'>" + how + "</textarea></td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td></td>\n"+
						"		<td><font color='#FF0000'>*&nbsp;changing user login might have un-anticipated side effects</font></td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td class='blue_table_footer'><a href='" + _context_path + "/user/" + login+ "'><input type='button' value='Cancel'/></a></td>\n"+
						"		<td class='blue_table_footer' align='right'><a href='javascript:mySubmit();'><input type='button' value='Submit'/></a></td>\n"+
						"	</tr>\n"+
						"</table>\n" + 
						"<script type='text/javascript'>\n" + 
						"function mySubmit()\n" + 
						"{\n" + 
						"	var do_submit = 1;\n" + 
						"	var msg = '';\n" + 
						"	if(document.getElementById('" + REST_USER_F_LOGIN+  "').value.length == 0)\n" + 
						"	{\n" + 
						"		msg += 'User login can not be empty. ';\n" + 
						"		do_submit = 0;\n" + 
						"	}\n" + 
						"	if(do_submit==1)\n" + 
						"		document.edit.submit();\n" + 
						"	else\n" + 
						"		alert(msg);\n" + 
						"}\n" + 				
						"</script>\n" + 
						"</form>\n"+
						"</body></html>";
	
					_parameters.put(REST_STATUS, REST_STATUS_OK);
					_parameters.put(REST_RESULT, result);
				}// end of -- format is html
				else
				{
					_parameters.put(REST_STATUS, REST_STATUS_ERROR);
					_parameters.put(REST_RESULT, getErrorMessageHTML("Specified format of data is not supported", _context_path));
				}
				
			}// end of -- user exists
			else
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("Specified user has not been found", _context_path));
			}
		}// end of -- single user
		else
		{// multiple users
//			if(_format != null && _format.length() == 0 && (!_format.equals(REST_FORMAT_HTML)))
//			{
//				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
//				_parameters.put(REST_RESULT, getErrorMessageHTML("Specified format of data is not supported", _context_path));
//			}
//			else
//			{// format ok
//				String result = getPageHeaderHTML("Knowledge Tree - User Info", _context_path);
//				while(_rs.next())
//				{// for all users
//					String login = _rs.getString("Login");
//					String name = _rs.getString("Name");
//					String email = _rs.getString("Email");
//					String org = _rs.getString("Organization");
//					String city = _rs.getString("City");
//					String country = _rs.getString("Country");
//					String how = _rs.getString("How");
//					
//					if(_format == null || _format.length() == 0 || _format.equals(REST_FORMAT_HTML))
//					{// format is html
//						result += 
//							"<table cellpadding='2px' cellspacing='0px' class='blue_table' width='600px'>\n"+
//							"	<caption class='blue_name'>" + name + "</td></tr>\n"+
//							"	<tr>\n"+
//							"		<td>Login</td>\n"+
//							"		<td width='100%'>" + login + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td>Email</td>\n"+
//							"		<td>" + email.replaceAll("@", "(at)").replaceAll("\\.", "(dot)") + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td>Organization</td>\n"+
//							"		<td>" + org + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td>City</td>\n"+
//							"		<td>" + city + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td>Country</td>\n"+
//							"		<td>" + country + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td></td>\n"+
//							"		<td>" + how + "</td>\n"+
//							"	</tr>\n"+
//							"</table>\n<p/>\n";
//		
//					}// end of -- format is html
//				}// end of -- for all users
//				result += "</body></html>";
//				_parameters.put(REST_STATUS, REST_STATUS_OK);
//				_parameters.put(REST_RESULT, result);
//			}// end of -- format ok
		}// end of -- multiple users
		return _parameters;
	}


	public static Map<String, String> getGroupInfo(Map<String, String> _parameters, SQLManager _sqlm, boolean multiple_groups)
	{
		String group_id = _parameters.get(REST_GROUP_ID);
		String group_mnemonic = _parameters.get(REST_GROUP_MNEMONIC);
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		
		if(multiple_groups)
		{// get group info by ID
			String qry = 
				"SELECT u.UserID, u.Login, u.Name, COUNT(uu.ChildUserID) AS MemberCount " +
				"FROM rel_user_user uu RIGHT JOIN ent_user u ON(uu.ParentUserID=u.UserID) "+
				"WHERE u.UserID>2 AND IsGroup=1 " +
				"GROUP BY u.UserID;";
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			try
			{// retrieve group by login from db
				conn = _sqlm.getConnection();
				stmt = conn.prepareStatement(qry);
				rs = stmt.executeQuery();
				
				_parameters = formatGroupInfo(rs, _parameters, true /*multiple_groups*/, conn);
				
				rs.close();
				stmt.close();
				
			}// end of -- retrieve group by login from db
			catch(SQLException sqle)
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while retrieving group info.", _context_path));
				sqle.printStackTrace(System.out);
			}
			finally { SQLManager.recycleObjects(conn, stmt, rs); }
		}// end of -- get group info by ID
		else if((group_mnemonic != null && group_mnemonic.length() != 0) || (group_id != null && group_id.length() != 0))
		{// get group info by Login or ID
			String qry = 
				"SELECT u.UserID, u.Login, u.Name, COUNT(uu.ChildUserID) AS MemberCount " +
				"FROM rel_user_user uu RIGHT JOIN ent_user u ON(uu.ParentUserID=u.UserID) "+
				"WHERE " + ((group_mnemonic != null && group_mnemonic.length() != 0)?"Login='" + group_mnemonic + "'":"UserID=" + group_id) + " AND u.UserID>2  AND IsGroup=1 GROUP BY u.UserID;";
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			try
			{// retrieve group by login from db
				conn = _sqlm.getConnection();
				stmt = conn.prepareStatement(qry);
				rs = stmt.executeQuery();
				
				_parameters = formatGroupInfo(rs, _parameters, false /*multiple_groups*/, conn);

				rs.close();
				stmt.close();
				
			}// end of -- retrieve group by login from db
			catch(SQLException sqle)
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while retrieving group info.", _context_path));
				sqle.printStackTrace(System.out);
			}
			finally { SQLManager.recycleObjects(conn, stmt, rs); }
		}// end of -- get group info by Login or ID
		else
		{// no parameters
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, getErrorMessageHTML("Group ID or mnemonic specified incorrectly", _context_path));
		}// end of -- no parameters
		
		return _parameters;
	}
	

	public static Map<String, String> setGroupInfo(Map<String, String> _parameters, HttpServletRequest request, SQLManager _sqlm, boolean multiple_groups)
	{
		String group_id = _parameters.get(REST_GROUP_ID);
		String group_mnemonic = _parameters.get(REST_GROUP_MNEMONIC);
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		if(multiple_groups)
		{// get group info by ID
//			String qry = "SELECT * FROM ent_user WHERE UserID NOT IN(1,2) AND IsGroup=0;";
//			Connection conn = null;
//			PreparedStatement stmt = null;
//			ResultSet rs = null;
//			
//			try
//			{// retrieve group by mnemonic from db
//				conn = _sqlm.getConnection();
//				stmt = conn.prepareStatement(qry);
//				rs = SQLManager.executeStatement(stmt);
//				
//				_parameters = formatUserInfo(rs, _parameters, true /*multiple_users*/);
//				
//				rs.close();
//				stmt.close();
//				
//			}// end of -- retrieve group by mnemonic from db
//			catch(SQLException sqle)
//			{
//				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
//				_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while retrieving user info.", _context_path));
//				sqle.printStackTrace(System.out);
//			}
//			finally {_sqlm.freeConnection(conn);}
		}// end of -- get group info by ID
		else if((group_mnemonic != null && group_mnemonic.length() != 0) || (group_id != null && group_id.length() != 0))
		{// get group info by mnemonic or ID
			
			String gmnemonic_new = _parameters.get(REST_GROUP_F_MNEMONIC);
			String qry = "UPDATE ent_user SET Login='" + _parameters.get(REST_GROUP_F_MNEMONIC) + "',"+
					" Name='" + clrStr(_parameters.get(REST_GROUP_F_NAME)) + "'";
			if(group_mnemonic != null && group_mnemonic.length() != 0)
			{// by mnemonic
				qry += " WHERE Login='" + _parameters.get(REST_GROUP_MNEMONIC) + "';";
			}
			else
			{// by id
				qry += " WHERE UserID='" + _parameters.get(REST_GROUP_ID) + "';";
			}

			Connection conn = null;
			PreparedStatement stmt = null;
			
			try
			{// retrieve group by mnemonic from db
				conn = _sqlm.getConnection();
				stmt = conn.prepareStatement(SQLManager.stringUnbreak(qry));
				stmt.executeUpdate();
				stmt.close();
				stmt = null;
//System.out.println("qry="+qry);				
//				SQLManager.executeUpdate(conn, SQLManager.stringUnbreak(qry));
				
				// UM2
				qry = qry.replaceAll("ent_user", "um2.ent_user");
				stmt = conn.prepareStatement(SQLManager.stringUnbreak(qry));
				stmt.executeUpdate();
//				SQLManager.executeUpdate(conn, SQLManager.stringUnbreak(qry));
				// end of -- UM2
				
				String result = getMessageHTML("Group Info Updated","<a href='"+_context_path+"/group/" + 
						((gmnemonic_new != null && group_mnemonic.length() != 0)?gmnemonic_new:"/id/"+group_id) +
						"'>Back</a> to group record",_context_path);
				_parameters.put(REST_STATUS, REST_STATUS_OK);
				_parameters.put(REST_RESULT, result);
			}// end of -- retrieve group by mnemonic from db
			catch(SQLException sqle)
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while updating group info.", _context_path));
				sqle.printStackTrace(System.out);
			}
			finally { SQLManager.recycleObjects(conn, stmt, null); }
			
			// UPDATE App Subscription
			String um_query_find = "SELECT * FROM ent_user WHERE Login='" + gmnemonic_new + "';";
			String um_query_sel = "SELECT * FROM ent_app WHERE Title NOT LIKE '%free spot%';";
			String um_query_del = "DELETE FROM rel_app_user WHERE UserID IN " +
					"(SELECT UserID FROM ent_user WHERE Login='" + gmnemonic_new + "');";
			String um_query_ins = "";
//System.out.println(um_query);
			SQLManager um_sqlm = null;
			Connection um_conn = null;
			try
			{// application registration for group in DB
				um_sqlm = new SQLManager(RestServlet.db_context_um);
				um_conn = um_sqlm.getConnection();
				
				// Get Group ID
				int um_group_id = 0;
				PreparedStatement um_stmt = um_conn.prepareStatement(um_query_find);
//System.out.println("um_query_find: " + um_query_find);				
				ResultSet um_rs = um_stmt.executeQuery();
				if(um_rs.next())
				{
					um_group_id = um_rs.getInt("UserID");
				}
				um_rs.close();
				um_stmt.close();
				
				// Form Insertion
				um_stmt = um_conn.prepareStatement(um_query_sel);
				um_rs = um_stmt.executeQuery();
//System.out.println("um_query_sel: " + um_query_sel);				
//System.out.println("params: ");
//Enumeration enum1 = request.getParameterNames();
//for (; enum1.hasMoreElements(); )
//	System.out.println("\t" + enum1.nextElement());
				while(um_rs.next())
				{
					int um_app_id = um_rs.getInt("AppID");
					String um_app_login = um_rs.getString("Title");
					String param = request.getParameter(um_app_login);
//System.out.println("param " + um_app_login + "=" + param);
					if("1".equals(param))
						um_query_ins += ((um_query_ins.length()==0)?"INSERT INTO rel_app_user (AppID,UserID) VALUES":",") +
							"(" + um_app_id + "," + um_group_id + ")";
				}
				um_rs.close();
				um_stmt.close();
				
				// First delete
//System.out.println("um_query_del: " + um_query_del);
				um_stmt = um_conn.prepareStatement(um_query_del);
				um_stmt.executeUpdate();
//				SQLManager.executeUpdate(um_conn, um_query_del);
				
//System.out.println("um_query_ins: " + um_query_ins);				
				// Then add
				if(um_query_ins.length()>0)
				{
					um_stmt = um_conn.prepareStatement(um_query_ins + ";");
					um_stmt.executeUpdate();
//					SQLManager.executeUpdate(um_conn, um_query_ins + ";");
				}
			}
			catch(SQLException sqle)
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while registering applications for the group.", _context_path));
				sqle.printStackTrace(System.out);
			}
			finally
			{
				SQLManager.recycleObjects(um_conn, null, null); 
//				um_sqlm.freeConnection(um_conn); 
				um_sqlm = null;
			}
		}// end of -- application registration for group in DB
		else
		{// no parameters
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, getErrorMessageHTML("Group ID or mnemonic specified incorrectly", _context_path));
		}// end of -- no parameters
		
		return _parameters;
	}
	
	
	private static Map<String, String> formatGroupInfo(ResultSet _rs, Map<String, String> _parameters, boolean multiple_groups,
			Connection _conn) throws SQLException
	{
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		String _format = _parameters.get(REST_FORMAT);
		
		
		if(!multiple_groups)
		{// single group
			if(_rs.next())
			{// group exists
				String mnemonic = _rs.getString("Login");
				String name = _rs.getString("Name");
				int member_count = _rs.getInt("MemberCount");
				
				String result = "";
				
				if(_format == null || _format.length() == 0 || _format.equals(REST_FORMAT_HTML))
				{// format is html
					// retrieve user-app registrations
					String app_name_list = "";
					String um_query = "SELECT a.* FROM ent_user u JOIN rel_app_user au ON (u.UserID=au.UserID) " +
							"JOIN ent_app a ON(a.AppID=au.AppID) WHERE u.Login='" + mnemonic + "' " +
							"AND a.Title NOT LIKE '%free spot%';";
//	System.out.println(um_query);
					SQLManager um_sqlm = new SQLManager(RestServlet.db_context_um);
					Connection um_conn = um_sqlm.getConnection();
					PreparedStatement um_stmt = um_conn.prepareStatement(um_query);
					ResultSet um_rs = um_stmt.executeQuery();
					while(um_rs.next())
					{
						String app_name = um_rs.getString("Description");
						app_name_list += ((app_name_list.length() >0 )?", ":"") + app_name;
					}
					um_rs.close();
					um_stmt.close();
					SQLManager.recycleObjects(um_conn, um_stmt, um_rs); 
//					um_sqlm.freeConnection(um_conn);
					um_sqlm = null;
					
					// retrieve users
					String users = "<div name='openerControl'>Members</div>\n<div name='opener'>";
					String uqry = 
						"SELECT g.Login, g.Name "+
						"FROM ent_user u RIGHT JOIN rel_user_user uu ON(uu.ParentUserID=u.UserID) "+
						"RIGHT JOIN ent_user g ON(uu.ChildUserID=g.UserID) "+
						"WHERE u.Login='" + mnemonic + "';";
					PreparedStatement stmt = _conn.prepareStatement(uqry);
					ResultSet urs = stmt.executeQuery();
					while(urs.next())
					{
						String user_name = urs.getString("Name");
						String user_login = urs.getString("Login");
						users += "<div>&nbsp;&nbsp;<a href='" + _context_path + "/user/" + user_login + "'>" + ((user_name!=null && user_name.length()>0)?user_name:"{unspecified}") + "&nbsp;(" + user_login + ")</a>" +
								"  <form style='display:inline;' method='post' action='" + _context_path + "/group/" + mnemonic+ "/users/" + user_login + "?_method=delete'>" + //"<input name='_method' type='hidden' value='delete'/>" + 
//								"<a href='" + _context_path + "/group/" + mnemonic+ "/users/" + user_login + "?_method=delete' alt='x' title='Remove user from goup'><img border='0' src='" + _context_path + "/assets/remove.gif'/></a>" +
								"<input alt='x' title='Remove this user from the group' type='image' src='" + _context_path + "/assets/remove.gif'/>" +
								"</form>" + 
								"</div>";
					}
					users += "</div>";
					urs.close();
					stmt.close();
					// end of -- retrieve users
					String home_html = "<a href='" + _context_path + "/' title='Home'>Home</a>";
					String rdf_html = "<a href='" + _context_path + "/rdf/group/" + mnemonic + "' title='View in RDF Format'>RDF</a>";
					String add_html = "<a href='" + _context_path + "/group/" + mnemonic + "/users/new' title='Add New User to the Group'>Add</a>";
					String logout_html = "<a href='" + _context_path + "/index.jsp?logout=1' title='Lougout from the system'>[logout]</a>";
					String edit_html = "<a href='" + _context_path + "/group/" + mnemonic + "/edit' title='Edit Group Info' >Edit</a>";
					String all_groupss_html = "&nbsp;&raquo;&nbsp;<a href='" + _context_path + "/groups' title='List of all Groups'>All Groups</a>";

					result = 
						getPageHeaderHTML("ADAPT&sup2; User &amp; Group Directory - Group Info", _context_path) +
						"<table cellpadding='2px' cellspacing='0px' class='violet_table' width='500px'>\n"+
						"	<caption class='violet_table_caption'>" + name + "</td></tr>\n"+
						"	<tr>\n" +
						"	  <td class='violet_table_header' colspan=2'2>" + home_html + all_groupss_html +
							"<div style='text-align:right;display:block;'>" + rdf_html + "&nbsp;&nbsp;" + add_html + "&nbsp;&nbsp;" + edit_html + "&nbsp;&nbsp;" + logout_html + "</div></td>\n" +
						"  	</tr>\n" +
						"		<td>Mnemonic</td>\n"+
						"		<td width='100%'>" + mnemonic + "</td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td>Name</td>\n"+
						"		<td>" + name + "</td>\n"+
						"	</tr>\n"+
						"	<tr valign='top'>\n"+
						"		<td>Registered&nbsp;apps</td>\n"+
						"		<td>" + app_name_list + "</td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td valign='top' class='blue_table_footer'>Members (" + member_count + ")</td>\n"+
						"		<td class='blue_table_footer'>" + users + "</td>\n"+
						"	</tr>\n"+
						"</table>\n"+
						"</body></html>";
	
					_parameters.put(REST_STATUS, REST_STATUS_OK);
					_parameters.put(REST_RESULT, result);
				}// end of -- format is html
				else if(_format != null && _format.length() != 0 && _format.equals(REST_FORMAT_RDF))
				{//format is rdf
					// retrieve users
					String users = "";
					String uqry = 
						"SELECT g.Login "+
						"FROM ent_user u RIGHT JOIN rel_user_user uu ON(uu.ParentUserID=u.UserID) "+
						"RIGHT JOIN ent_user g ON(uu.ChildUserID=g.UserID) "+
						"WHERE u.Login='" + mnemonic + "';";
					PreparedStatement stmt = _conn.prepareStatement(uqry);
					ResultSet urs = stmt.executeQuery();
					while(urs.next())
					{
						String user_login = urs.getString("Login");
						users += "		<foaf:member rdf:resource='" + _context_path + "/rdf/users#" + user_login + "'/>\n";
					}
					urs.close();
					stmt.close();
					// end of -- retrieve users

					result = 
						getPageHeaderRDF() +
						"	<foaf:Group rdf:about='" + _context_path + "/rdf/groups#" + mnemonic + "'>\n" +
						"		<foaf:name>" + name + "</foaf:name>\n" +
						users +
						"		<rdfs:isDefinedBy rdf:resource='" + _context_path + "/rdf/groups'/>\n" +
						"	</foaf:Group>\n" +
						"</rdf:RDF>";

					_parameters.put(REST_STATUS, REST_STATUS_OK);
					_parameters.put(REST_RESULT, result);
				}//end of -- format is rdf
				else
				{
					_parameters.put(REST_STATUS, REST_STATUS_ERROR);
					_parameters.put(REST_RESULT, getErrorMessageHTML("Specified format of data is not supported", _context_path));
				}
				
			}// end of -- group exists
			else
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("Specified group has not been found", _context_path));
			}
		}// end of -- single group
		else
		{// multiple groups
			if(_format != null && _format.length() == 0 && (!_format.equals(REST_FORMAT_HTML)) && (!_format.equals(REST_FORMAT_RDF)))
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("Specified format of data is not supported", _context_path));
			}
			else
			{// format ok
				String result = "";
				// HEADER
				if(_format == null || _format.length() == 0 || _format.equals(REST_FORMAT_HTML))
				{
//					result = getPageHeaderHTML("Knowledge Tree - Group Info", _context_path);
					String home_html = "<a href='" + _context_path + "/' title='Home'>Home</a>";
					String rdf_html = "<a href='" + _context_path + "/rdf/groups' title='View in RDF Format'>RDF</a>";
					String add_html = "<a href='" + _context_path + "/groups/new' title='Add New Group'>Add</a>";
					String logout_html = "<a href='" + _context_path + "/index.jsp?logout=1' title='Lougout from the system'>[logout]</a>";

//					String home_html = "<a href='" + _context_path + "/'><img src='" + _context_path + "/assets/home.gif' title='Home' alt='Home' border='0'></a>";
//					String rdf_html = "<a href='" + _context_path + "/rdf/groups'><img src='" + _context_path + "/assets/rdf.gif' title='RDF' alt='RDF' border='0'></a>";
//					String add_html = "<a href='" + _context_path + "/groups/new'><img src='" + _context_path + "/assets/add2_enable.gif' title='Add New Group' alt='Add' border='0'></a>";
//					String logout_url = "&nbsp;&nbsp;<a href='" + _context_path + "/index.jsp?logout=1' title='Lougout from the system'>[logout]</a>";

					result = getPageHeaderHTML("ADAPT&sup2; User &amp; Group Directory - List of All Groups", _context_path) +
					"<table cellpadding='2px' cellspacing='0px' class='violet_table' width='500px'>\n"+
					"	<caption class='violet_table_caption'>List of All Groups</td></tr>\n"+ 
					"	<tr>\n" +
					"	  <td class='violet_table_header' colspan=2'2>" + home_html + 
						"<div style='text-align:right;display:block;'>" + rdf_html + "&nbsp;&nbsp;" + add_html + "&nbsp;&nbsp;" + logout_html + "</div></td>\n" +
					"  	</tr>\n";
				}
				else if(_format != null && _format.length() != 0 && _format.equals(REST_FORMAT_RDF))
					result = getPageHeaderRDF();
				
				int group_count = 0;
				
				while(_rs.next())
				{// for all groups
					group_count++;
					
					String mnemonic = _rs.getString("Login");
					String name = _rs.getString("Name");
//					int member_count = _rs.getInt("MemberCount");
					
					if(_format == null || _format.length() == 0 || _format.equals(REST_FORMAT_HTML))
					{// format is html
//						// retrieve users
//						String users = "<div name='openerControl'>Members</div>\n<div name='opener'>";
//						String uqry = 
//							"SELECT g.Login, g.Name "+
//							"FROM ent_user u RIGHT JOIN rel_user_user uu ON(uu.ParentUserID=u.UserID) "+
//							"RIGHT JOIN ent_user g ON(uu.ChildUserID=g.UserID) "+
//							"WHERE u.Login='" + mnemonic + "';";
//						PreparedStatement stmt = _conn.prepareStatement(uqry);
//						ResultSet urs = SQLManager.executeStatement(stmt);
//						while(urs.next())
//						{
//							String user_name = urs.getString("Name");
//							String user_login = urs.getString("Login");
//							users += "<div>&nbsp;&nbsp;<a href='" + _context_path + "/user/" + user_login + "'>" + ((user_name!=null && user_name.length()>0)?user_name:"{unspecified}") + "</a></div>";
//						}
//						users += "</div>";
//						urs.close();
//						stmt.close();
//						// end of -- retrieve users
//						String home_html = "<a href='" + _context_path + "/'><img src='" + _context_path + "/assets/home.gif' title='Home' alt='Home' border='0'></a>";
//						String view_html = "<a href='" + _context_path + "/group/" + mnemonic + "'><img src='" + _context_path + "/assets/view_enabled.gif' title='View' alt='View' border='0'></a>";
//						String rdf_html = "<a href='" + _context_path + "/rdf/group/" + mnemonic + "'><img src='" + _context_path + "/assets/rdf.gif' title='RDF' alt='RDF' border='0'></a>";
//						String edit_html = "<a href='" + _context_path + "/group/" + mnemonic + "/edit'><img src='" + _context_path + "/assets/edit2_enable.gif' title='Edit' alt='Edit' border='0'></a>";
//						String add_html = "<a href='" + _context_path + "/group/" + mnemonic + "/users/new'><img src='" + _context_path + "/assets/add2_enable.gif' title='Add New User' alt='Add' border='0'></a>";

						result +=
							"<tr>\n" +
							"	<td valign='top'>&nbsp;&nbsp;&bull;</td>\n" +
							"	<td><a href='" + _context_path + "/group/" + mnemonic + "'>" + name + "&nbsp;(" + mnemonic + ")</a></td>\n" +
							"</tr>\n";
							
//							String logout_url = "&nbsp;&nbsp;<a href='" + _context_path + "/index.jsp?logout=1' title='Lougout from the system'>[logout]</a>";
//							"<table cellpadding='2px' cellspacing='0px' class='violet_table' width='600px'>\n"+
//							"	<caption class='violet_table_caption'>" + name + "</td></tr>\n"+
//							"	<tr>\n"+
//							"	  <td class='blue_table_header'>" + home_html + "</td>\n"+
//							"	  <td class='violet_table_header' align='right'>" + add_html + "&nbsp;&nbsp;" +edit_html + "&nbsp;&nbsp;&nbsp;&nbsp;" + rdf_html + "&nbsp;&nbsp;" + view_html + logout_url +"</tr>\n"+
//							"	<tr>\n"+
//							"		<td>Mnemonic</td>\n"+
//							"		<td width='100%'>" + mnemonic + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td>No.&nbsp;of&nbsp;members</td>\n"+
//							"		<td>" + member_count + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td valign='top' class='blue_table_footer'>Members</td>\n"+
//							"		<td class='blue_table_footer'>" + users + "</td>\n"+
//							"	</tr>\n"+
//							"</table>\n<p/>\n";
		
					}// end of -- format is html
					else if(_format != null && _format.length() != 0 && _format.equals(REST_FORMAT_RDF))
					{//format is rdf
						// retrieve users
						String users = "";
						String uqry = 
							"SELECT g.Login "+
							"FROM ent_user u RIGHT JOIN rel_user_user uu ON(uu.ParentUserID=u.UserID) "+
							"RIGHT JOIN ent_user g ON(uu.ChildUserID=g.UserID) "+
							"WHERE u.Login='" + mnemonic + "';";
						PreparedStatement stmt = _conn.prepareStatement(uqry);
						ResultSet urs = stmt.executeQuery();
						while(urs.next())
						{
							String user_login = urs.getString("Login");
							users += "		<foaf:member rdf:resource='" + _context_path + "/rdf/users#" + user_login + "'/>\n";
						}
						urs.close();
						stmt.close();
						// end of -- retrieve users

						result += 
							"	<foaf:Group rdf:about='" + _context_path + "/rdf/groups#" + mnemonic + "'>\n" +
							"		<foaf:name>" + name + "</foaf:name>\n" +
							users +
							"		<rdfs:isDefinedBy rdf:resource='" + _context_path + "/rdf/groups'/>\n" +
							"	</foaf:Group>\n";
					}//end of -- format is rdf
					
				}// end of -- for all groups

				// FOOTER
				if(_format == null || _format.length() == 0 || _format.equals(REST_FORMAT_HTML))
					result +=
						"	<tr>\n"+
						"		<td class='violet_table_footer'>&nbsp;</td>\n"+
						"		<td class='violet_table_footer'>" + group_count + " group(s)</td>\n"+
						"	</tr>\n"+
						"</table>\n" +
						"</body></html>";
				else if(_format != null && _format.length() != 0 && _format.equals(REST_FORMAT_RDF))
					result += "</rdf:RDF>";
				
				
				_parameters.put(REST_STATUS, REST_STATUS_OK);
				_parameters.put(REST_RESULT, result);
			}// end of -- format ok
		}// end of -- multiple groups
		return _parameters;
	}
	

	public static Map<String, String> getGroupEditor(Map<String, String> _parameters, SQLManager _sqlm, boolean multiple_groups)
	{
		String group_id = _parameters.get(REST_GROUP_ID);
		String group_mnemonic = _parameters.get(REST_GROUP_MNEMONIC);
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		if(multiple_groups)
		{// get user info by ID
//			String qry = "SELECT * FROM ent_user WHERE UserID NOT IN(1,2) AND IsGroup=0;";
//			Connection conn = null;
//			PreparedStatement stmt = null;
//			ResultSet rs = null;
//			
//			try
//			{// retrieve user by login from db
//				conn = _sqlm.getConnection();
//				stmt = conn.prepareStatement(qry);
//				rs = SQLManager.executeStatement(stmt);
//				
//				_parameters = formatUserEdit(rs, _parameters, true /*multiple_users*/);
//				
//			}// end of -- retrieve user by login from db
//			catch(SQLException sqle)
//			{
//				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
//				_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while retrieving user info.", _context_path));
//				sqle.printStackTrace(System.out);
//			}
//			finally {_sqlm.freeConnection(conn);}
		}// end of -- get user info by ID
		else if((group_mnemonic != null && group_mnemonic.length() != 0) || (group_id != null && group_id.length() != 0))
		{// get user info by Login or ID
			String qry = (group_mnemonic != null && group_mnemonic.length() != 0)?"SELECT * FROM ent_user WHERE login='" + group_mnemonic + "' AND IsGroup=1;"
					:"SELECT * FROM ent_user WHERE UserID='" + group_id + "' AND IsGroup=1;";
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			try
			{// retrieve group by login from db
				conn = _sqlm.getConnection();
				stmt = conn.prepareStatement(qry);
				rs = stmt.executeQuery();
				
				_parameters = formatGroupEditor(rs, _parameters, false /*multiple_groups*/);
				
				rs.close();
				stmt.close();
			}// end of -- retrieve user by login from db
			catch(SQLException sqle)
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while retrieving group info.", _context_path));
				sqle.printStackTrace(System.out);
			}
			finally { SQLManager.recycleObjects(conn, stmt, rs); }
		}// end of -- get user info by Login or ID
		else
		{// no parameters
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, getErrorMessageHTML("Group ID or Mnemonic specified incorrectly", _context_path));
		}// end of -- no parameters
		
		return _parameters;
	}

	
	private static Map<String, String> formatGroupEditor(ResultSet _rs, Map<String, String> _parameters, boolean multiple_groups)
			throws SQLException
	{
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		String _format = _parameters.get(REST_FORMAT);
		
		
		if(!multiple_groups)
		{// single group
			if(_rs.next())
			{// group exists
				String gmnemonic = _rs.getString("Login");
				String name = _rs.getString("Name");
//				String email = _rs.getString("Email");
//				String org = _rs.getString("Organization");
//				String city = _rs.getString("City");
//				String country = _rs.getString("Country");
//				String how = _rs.getString("How");

				String result = "";
				
				if(_format == null || _format.length() == 0 || _format.equals(REST_FORMAT_HTML)) 
				{// format is html
					String home_html = "<a href='" + _context_path + "/' title='Home'>Home</a>";
					String logout_html = "<a href='" + _context_path + "/index.jsp?logout=1' title='Lougout from the system'>[logout]</a>";
					String all_groupss_html = "&nbsp;&raquo;&nbsp;<a href='" + _context_path + "/groups' title='List of all Groups'>All Groups</a>";
					String view_groups_html = "&nbsp;&raquo;&nbsp;<a href='" + _context_path + "/group/" + gmnemonic + "' title='Back to Group Info'>Group</a>";

					// retrieve user-app registrations
					String app_name_list = "";
					int max_app_id = 0;
					String um_query = "SELECT MAX(AppID) AS Max FROM ent_app;";
					SQLManager um_sqlm = new SQLManager(RestServlet.db_context_um);
					Connection um_conn = um_sqlm.getConnection();
					PreparedStatement um_stmt = um_conn.prepareStatement(um_query);
					ResultSet um_rs = um_stmt.executeQuery();
					while(um_rs.next())
					{
						max_app_id = um_rs.getInt("Max");
					}
					um_rs.close();
					um_stmt.close();
					um_conn.close();
					
					int[] app_mask = (int[])Array.newInstance(int.class, max_app_id + 1);
					for (int i=0; i<Array.getLength(app_mask); i++)
						app_mask[i]=0;
					
					um_query = "SELECT a.* FROM ent_user u JOIN rel_app_user au ON (u.UserID=au.UserID) " +
							"JOIN ent_app a ON(a.AppID=au.AppID) WHERE u.Login='" + gmnemonic + "' " +
							"AND a.Title NOT LIKE '%free spot%';";
//	System.out.println(um_query);
					um_sqlm = new SQLManager(RestServlet.db_context_um);
					um_conn = um_sqlm.getConnection();
					um_stmt = um_conn.prepareStatement(um_query);
					um_rs = um_stmt.executeQuery();
					while(um_rs.next())
					{
						int app_id = um_rs.getInt("AppID");
						app_mask[app_id] = 1;
					}
					um_rs.close();
					um_stmt.close();
					um_conn.close();
					
					um_query = "SELECT * FROM ent_app WHERE Title NOT LIKE '%free spot%';";
					um_sqlm = new SQLManager(RestServlet.db_context_um);
					um_conn = um_sqlm.getConnection();
					um_stmt = um_conn.prepareStatement(um_query);
					um_rs = um_stmt.executeQuery();
					while(um_rs.next())
					{
						int app_id = um_rs.getInt("AppID");
						String app_name = um_rs.getString("Description");
						String app_login = um_rs.getString("Title");
						app_name_list += 
							"<div><input name='" + app_login + "' type='checkbox' value='1' " + ((app_mask[app_id]>0)?"checked":"") + "/>&nbsp;" + app_name + "</div>\n";
					}
					um_rs.close();
					um_stmt.close();
					um_conn.close();
					um_sqlm = null;
					
					result = 
						getPageHeaderHTML("ADAPT&sup2; User &amp; Group Directory - Group Info Editor", _context_path) +
						"<form name='edit' id='edit' form method='post' action='" + _context_path + "/group/" + gmnemonic + "'>\n"+
						"<input name='" + REST_GROUP_MNEMONIC + "' type='hidden' value='" + gmnemonic + "'/>"+
						"<table cellpadding='2px' cellspacing='0px' class='violet_table' width='500px'>\n"+
						"	<caption class='violet_table_caption'>" + name + " - Editing</td></tr>\n"+ 
						"	<tr>\n" +
						"	  <td class='violet_table_header' colspan=2'2>" + home_html + all_groupss_html + view_groups_html +
							"<div style='text-align:right;display:block;'>" + logout_html + "</div></td>\n" +
						"  	</tr>\n" +
						"	<tr>\n"+
						"		<td>Name</td>\n"+
						"		<td width='100%'><input name='" + REST_GROUP_F_NAME + "' type='text' maxlength='60' size='45' value='" + name + "'/></td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td>Mnemonic</td>\n"+
						"		<td><input id='" + REST_GROUP_F_MNEMONIC + "' name='" + REST_GROUP_F_MNEMONIC + "' type='text' maxlength='15' size='21' value='" + gmnemonic + "'/>&nbsp;<font color='#FF0000'>*</font></td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td></td>\n"+
						"		<td><font color='#FF0000'>*&nbsp;changing user login might have un-anticipated side effects</font></td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td valign='top'>Registered&nbsp;apps</td>\n"+
						"		<td>" + app_name_list + "</td>\n"+
						"	</tr>\n"+
						"	<tr>\n"+
						"		<td class='violet_table_footer'><a href='" + _context_path + "/group/" + gmnemonic + "'><input type='button' value='Cancel'/></a></td>\n"+
						"		<td class='violet_table_footer' align='right'><a href='javascript:mySubmit();'><input type='button' value='Submit'/></a></td>\n"+
						"	</tr>\n"+
						"</table>\n" +
						"<script type='text/javascript'>\n" + 
						"function mySubmit()\n" + 
						"{\n" + 
						"	var do_submit = 1;\n" + 
						"	var msg = '';\n" + 
						"	if(document.getElementById('" + REST_GROUP_F_MNEMONIC+  "').value.length == 0)\n" + 
						"	{\n" + 
						"		msg += 'Group lmemonic can not be empty. ';\n" + 
						"		do_submit = 0;\n" + 
						"	}\n" + 
						"	if(do_submit==1)\n" + 
						"		document.edit.submit();\n" + 
						"	else\n" + 
						"		alert(msg);\n" + 
						"}\n" + 				
						"</script>\n" + 
						"</form>\n"+
						"</body></html>";
	
					_parameters.put(REST_STATUS, REST_STATUS_OK);
					_parameters.put(REST_RESULT, result);
				}// end of -- format is html
				else
				{
					_parameters.put(REST_STATUS, REST_STATUS_ERROR);
					_parameters.put(REST_RESULT, getErrorMessageHTML("Specified format of data is not supported", _context_path));
				}
				
			}// end of -- group exists
			else
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("Specified user has not been found", _context_path));
			}
		}// end of -- single group
		else
		{// multiple groups
//			if(_format != null && _format.length() == 0 && (!_format.equals(REST_FORMAT_HTML)))
//			{
//				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
//				_parameters.put(REST_RESULT, getErrorMessageHTML("Specified format of data is not supported", _context_path));
//			}
//			else
//			{// format ok
//				String result = getPageHeaderHTML("Knowledge Tree - User Info", _context_path);
//				while(_rs.next())
//				{// for all group
//					String login = _rs.getString("Login");
//					String name = _rs.getString("Name");
//					String email = _rs.getString("Email");
//					String org = _rs.getString("Organization");
//					String city = _rs.getString("City");
//					String country = _rs.getString("Country");
//					String how = _rs.getString("How");
//					
//					if(_format == null || _format.length() == 0 || _format.equals(REST_FORMAT_HTML))
//					{// format is html
//						result += 
//							"<table cellpadding='2px' cellspacing='0px' class='blue_table' width='600px'>\n"+
//							"	<caption class='blue_name'>" + name + "</td></tr>\n"+
//							"	<tr>\n"+
//							"		<td>Login</td>\n"+
//							"		<td>" + login + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td>Email</td>\n"+
//							"		<td>" + email.replaceAll("@", "(at)").replaceAll("\\.", "(dot)") + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td>Organization</td>\n"+
//							"		<td>" + org + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td>City</td>\n"+
//							"		<td>" + city + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td>Country</td>\n"+
//							"		<td>" + country + "</td>\n"+
//							"	</tr>\n"+
//							"	<tr>\n"+
//							"		<td></td>\n"+
//							"		<td>" + how + "</td>\n"+
//							"	</tr>\n"+
//							"</table>\n<p/>\n";
//		
//					}// end of -- format is html
//				}// end of -- for all group
//				result += "</body></html>";
//				_parameters.put(REST_STATUS, REST_STATUS_OK);
//				_parameters.put(REST_RESULT, result);
//			}// end of -- format ok
		}// end of -- multiple group
		return _parameters;
	}

	
	public static Map<String, String> getGroupUserAdd(Map<String, String> _parameters, SQLManager _sqlm)
	{
		String group_id = _parameters.get(REST_GROUP_ID);
		String group_mnemonic = _parameters.get(REST_GROUP_MNEMONIC);
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		
		String qry = "";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		// Check for existence of the specified group
		qry = "SELECT * FROM ent_user WHERE " + 
				((group_mnemonic != null && group_mnemonic.length()>0)
						?"Login='" + group_mnemonic + "'"
						:"UserID=" + group_id) + 
				" AND IsGroup=1;";
		boolean group_exists = false;
		boolean failed = false;
		String found_group_mnemonic = "";
		String found_group_name = "";
		try
		{
			conn = _sqlm.getConnection();
			stmt = conn.prepareStatement(qry);
			rs = stmt.executeQuery();
			
			if(rs.next())
			{
				group_exists = true;
				found_group_mnemonic = rs.getString("Login");
				found_group_name = rs.getString("Name");
			}
			
			rs.close();
			stmt.close();
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace(System.out);
			failed = true;
		}
		finally { SQLManager.recycleObjects(conn, stmt, rs); }
		// end of -- Check for existence of the specified group
		
		if(failed)
		{
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while retrieving group info.", _context_path));
			return _parameters;
		}
		if(!group_exists)
		{
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, getErrorMessageHTML("Group specified does not exist.", _context_path));
			return _parameters;
		}
		
		// Display group addind interface
		qry = "SELECT Login, Name, IsMember(UserId,'" + found_group_mnemonic + "') AS IsMember FROM ent_user WHERE IsGroup=0 AND UserID>2;";
		try
		{
			conn = _sqlm.getConnection();
			stmt = conn.prepareStatement(qry);
			rs = stmt.executeQuery();
			
			_parameters.put(REST_GROUP_MNEMONIC, found_group_mnemonic);
			_parameters.put(REST_GROUP_F_NAME, found_group_name);
			
			_parameters = formatGroupUserAdd(rs, _parameters, false /*login taken*/);
			
			rs.close();
			stmt.close();
		}
		catch(SQLException sqle)
		{
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while retrieving user info.", _context_path));
			sqle.printStackTrace(System.out);
		}
		finally { SQLManager.recycleObjects(conn, stmt, rs); }
		// end if -- Display group addind interface
		
		return _parameters;
	}

	
	private static Map<String, String> formatGroupUserAdd(ResultSet _rs, Map<String, String> _parameters, boolean login_taken) throws SQLException
	{
		String _group_mnemonic = _parameters.get(REST_GROUP_MNEMONIC);
		String _group_name = _parameters.get(REST_GROUP_F_NAME);
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		
		String user_list = "";
		
		while(_rs.next())
		{
			String _user_login = _rs.getString("Login");
			String _user_name = _rs.getString("Name");
			int _is_member = _rs.getInt("IsMember");
			
			user_list += 
					"				<option value='" + _user_login + "'>" + 
					((_is_member==1)?" &bull; ":" ") + 
					_user_name + " (" + _user_login + ")</option>\n";	
		}
		
		String home_html = "<a href='" + _context_path + "/' title='Home'>Home</a>";
		String logout_html = "<a href='" + _context_path + "/index.jsp?logout=1' title='Lougout from the system'>[logout]</a>";
		String all_groups_html = "&nbsp;&raquo;&nbsp;<a href='" + _context_path + "/groups' title='List of all Groups'>All Groups</a>";
		String view_group_html = "&nbsp;&raquo;&nbsp;<a href='" + _context_path + "/group/" + _group_mnemonic + "' title='Back to Group Info'>Group</a>";

		String result = 
				getPageHeaderHTML("Knowledge Tree - Add New User to a Group", _context_path) +
				
				
//				"<form method='post' action='" + _context_path + "/user_add'>\n" +
				"<form id='edit' name='edit' method='post' action='" + _context_path + "/group/" + _group_mnemonic + "/users/new'>\n" +
//				"<input type='hidden' name='" + REST_GROUP_MNEMONIC + "' value='" + _group_mnemonic + "' />\n" +
				"<table cellpadding='2px' cellspacing='0px' class='violet_table' width='500px'>\n" +
				"	<caption class='violet_table_caption'>" + _group_name + " :: Adding new user</td></tr>\n" +
				"	<tr>\n" +
				"	  <td class='violet_table_header' colspan=2'2>" + home_html + all_groups_html + view_group_html +
					"<div style='text-align:right;display:block;'>" + logout_html + "</div></td>\n" +
				"  	</tr>\n" +
				"	<tr>\n" +
				"		<td colspan='2' class='subheader'>Add Existing (default)</td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td>&nbsp;</td>\n" +
				"		<td>\n" +
				"			<select name='" + REST_USER_LOGIN + "' id='" + REST_USER_LOGIN + "'>\n" +
				"				<option value=''>-- not selected --</option>\n" + user_list +
				"			</select>\n" +
				"		</td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td>&nbsp;</td>\n" +
				"		<td>Names with a bullet (&bull;) denote current members of the group</td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td colspan='2' class='subheader' align='center'>OR</td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td colspan='2' class='subheader'>Add New </td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td>Name</td>\n" +
				"		<td><input name='" + REST_USER_F_NAME + "' type='text' maxlength='60' size='45' value='" + ((login_taken)?_parameters.get(REST_USER_F_NAME):"") + "'/></td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td>Login</td>\n" +
				"		<td><input id='" + REST_USER_F_LOGIN + "' name='" + REST_USER_F_LOGIN + "' type='text' maxlength='15' size='21' value='" + ((login_taken)?_parameters.get(REST_USER_F_LOGIN):"") + "'/>" +
						((login_taken)?"<font color='#FF0000'>&nbsp;Login already taken!</font>":"") + "</td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td>Password</td>\n" +
				"		<td><input id='" + REST_USER_F_PASS + "' name='" + REST_USER_F_PASS + "' type='text' maxlength='15' size='21' value='" + ((login_taken)?_parameters.get(REST_USER_F_PASS):"") + "'/></td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td>Email</td>\n" +
				"		<td><input name='" + REST_USER_F_EMAIL + "' type='text' maxlength='50' size='45' value='" + ((login_taken)?_parameters.get(REST_USER_F_EMAIL):"") + "'/></td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td>Organization</td>\n" +
				"		<td><input name='" + REST_USER_F_ORGANIZATION + "' type='text' maxlength='100' size='45' value='" + ((login_taken)?_parameters.get(REST_USER_F_ORGANIZATION):"") + "'/></td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td>City</td>\n" +
				"		<td><input name='" + REST_USER_F_CITY + "' type='text' maxlength='30' size='45' value='" + ((login_taken)?_parameters.get(REST_USER_F_CITY):"") + "'/></td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td>Country</td>\n" +
				"		<td><input type='text' name='" + REST_USER_F_COUNTRY + "' maxlength='50' size='45' value='" + ((login_taken)?_parameters.get(REST_USER_F_COUNTRY):"") + "'/></td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td valign='top'>Note</td>\n" +
				"		<td><textarea id='" + REST_USER_F_HOW + "' name='" + REST_USER_F_HOW + "' cols='44' rows='5'>" + ((login_taken)?_parameters.get(REST_USER_F_HOW):"") + "</textarea></td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td colspan='2' class='subheader' align='center'>OR</td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td colspan='2' class='subheader'>Add Several New</td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td></td>\n" +
				"		<td>\n" +
				"			Data Format (one of the options below)<br/>\n" +
				"			<span style='font-size:0.8em;color:#999999;'>Login, Password<br/>\n" +
				"			Name, Login, Password<br/>\n" +
				"			Name, Login, Password, Email<br/>\n" +
				"			Name, Login, Password, Email, Org, City, Country, Note</span>\n" +
				"		</td>\n" +
				"	</tr>\n" +
				
				
				"	<tr>\n" +
				"		<td valign='top'>Tab-separated values</td>\n" +
				"		<td><textarea id='" + REST_USER_F_BULKADD + "' name='" + REST_USER_F_BULKADD + "' cols='44' rows='5'></textarea></td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td class='violet_table_footer'><a href='" + _context_path + "/group/" + _group_mnemonic + "'><input type='button' value='Cancel'/></a></td>\n" +
				"		<td class='violet_table_footer' align='right'><a href='javascript:mySubmit();'><input type='button' value='Submit'/></a></td>\n" +
				"	</tr>\n" +
				"</table>\n" +
				"<script type='text/javascript'>\n" + 
				"function mySubmit()\n" + 
				"{\n" + 
				"	var do_submit = 1;\n" + 
				"	var msg = '';\n" + 
				"	if((document.getElementById('" + REST_USER_F_LOGIN+  "').value.length == 0) &&\n" + 
				"		(document.getElementById('" + REST_USER_LOGIN + "').value.length == 0))\n" + 
				"	{\n" + 
				"		msg += 'User login can not be empty. ';\n" + 
				"		do_submit = 0;\n" + 
				"	}\n" + 
				"	if((document.getElementById('" + REST_USER_F_PASS + "').value.length == 0) &&\n" + 
				"		(document.getElementById('" + REST_USER_LOGIN + "').value.length == 0))\n" + 
				"	{\n" + 
				"		msg += 'User password can not be empty.';\n" + 
				"		do_submit = 0;\n" + 
				"	}\n" + 
				"	if((document.getElementById('" + REST_USER_F_BULKADD + "').value.length != 0) &&\n" + 
				"		(document.getElementById('" + REST_USER_F_BULKADD + "').value.length != 0))\n" + 
				"	{\n" + 
				"		msg += '';\n" + 
				"		do_submit = 1;\n" + 
				"	}\n" + 
				"	if(do_submit==1)\n" + 
				"		document.edit.submit();\n" + 
				"	else\n" + 
				"		alert(msg);\n" + 
				"}\n" + 				
				"</script>\n" + 
				"</form>\n" +		
				"</body></html>";
					
				_parameters.put(REST_STATUS, REST_STATUS_OK);
				_parameters.put(REST_RESULT, result);
				
		return _parameters;
	}

	
	public static Map<String, String> setGroupUserAdd(Map<String, String> _parameters, SQLManager _sqlm)
	{
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
//
//		String group_id = _parameters.get(REST_GROUP_ID);
		String group_mnemonic = _parameters.get(REST_GROUP_MNEMONIC);
//		String _user_login = _parameters.get(REST_USER_LOGIN);
//		
//		String _login = _parameters.get(REST_USER_F_LOGIN);
//		String _pass = _parameters.get(REST_USER_F_PASS);
//		String _name = _parameters.get(REST_USER_F_NAME);
//		String _email = _parameters.get(REST_USER_F_EMAIL);
//		String _org = _parameters.get(REST_USER_F_ORGANIZATION);
//		String _country = _parameters.get(REST_USER_F_COUNTRY);
//		String _city = _parameters.get(REST_USER_F_CITY);
//		String _how = _parameters.get(REST_USER_F_HOW);
		String _bulk_add = _parameters.get(REST_USER_F_BULKADD);
		
		if(_bulk_add != null && _bulk_add.length() >0 )
		{// GO TO THE BULK MODE
			ArrayList<String> attempted_logins = new ArrayList<String>();
			ArrayList<String> result_messages = new ArrayList<String>();
			ArrayList<String> result_statuses = new ArrayList<String>();
			// Compile the pattern
		    String patternStr = "^(.*)$";
		    Pattern pattern = Pattern.compile(patternStr, Pattern.MULTILINE);
		    Matcher matcher = pattern.matcher(_bulk_add);
		    
		    // Read the lines
			while (matcher.find())
			{// FOR ALL LINES
	//		    // Get the line with the line termination character sequence
	//			String lineWithTerminator = matcher.group(0);
				// Get the line without the line termination character sequence
				String line = matcher.group(1);
//				System.out.println("UL:" + line);
				
				// tockenize
				StringTokenizer st = new StringTokenizer(line, "\t");
				int no_o_tokens = st.countTokens();
				// no_o_tokens = 2 Login, Password
				// no_o_tokens = 3 Name, Login, Password
				// no_o_tokens = 4 Name, Login, Password, Email
				// no_o_tokens = 8 Name, Login, Password, Email, Org, City, Country, Note
				
				if(no_o_tokens==2 || no_o_tokens==3 || no_o_tokens==4 || no_o_tokens==8)
				{
					Map<String, String> _user_parameters = new HashMap<String, String>();
					
					_user_parameters.put(REST_CONTEXT_PATH, _parameters.get(REST_CONTEXT_PATH));
					_user_parameters.put(REST_GROUP_MNEMONIC, _parameters.get(REST_GROUP_MNEMONIC));
					_user_parameters.put(REST_GROUP_ID, _parameters.get(REST_GROUP_ID));
					
					String _name = "";
					String _login = "";
					String _pass = "";
					String _email = "";
					String _org = "";
					String _city = "";
					String _country = "";
					String _note = "";
					
					attempted_logins.add(_login);

					switch(no_o_tokens)
					{
						case 2:
							_login = st.nextToken();
							_pass = st.nextToken();
							_user_parameters.put(REST_USER_F_LOGIN, _login);
							_user_parameters.put(REST_USER_F_PASS, _pass);
						break;
						case 3: // Name, Login, Password
							_name = st.nextToken();
							_login = st.nextToken();
							_pass = st.nextToken();
							_user_parameters.put(REST_USER_F_LOGIN, _login);
							_user_parameters.put(REST_USER_F_PASS, _pass);
							_user_parameters.put(REST_USER_F_NAME, _name);
						break;
							
						case 4: // Name, Login, Password, Email
							_name = st.nextToken();
							_login = st.nextToken();
							_pass = st.nextToken();
							_email = st.nextToken();
							_user_parameters.put(REST_USER_F_LOGIN, _login);
							_user_parameters.put(REST_USER_F_PASS, _pass);
							_user_parameters.put(REST_USER_F_NAME, _name);
							_user_parameters.put(REST_USER_F_EMAIL, _email);
						break;
							
						case 8: // Name, Login, Password, Email, Org, City, Country, Note
							_name = st.nextToken();
							_login = st.nextToken();
							_pass = st.nextToken();
							_email = st.nextToken();
							_org = st.nextToken();
							_city = st.nextToken();
							_country = st.nextToken();
							_note = st.nextToken();
							_user_parameters.put(REST_USER_F_NAME, _name);
							_user_parameters.put(REST_USER_F_LOGIN, _login);
							_user_parameters.put(REST_USER_F_PASS, _pass);
							_user_parameters.put(REST_USER_F_EMAIL, _email);
							_user_parameters.put(REST_USER_F_ORGANIZATION, _org);
							_user_parameters.put(REST_USER_F_CITY, _city);
							_user_parameters.put(REST_USER_F_COUNTRY, _country);
							_user_parameters.put(REST_USER_F_HOW, _note);
						break;
					}
					
//System.out.println("1 (_user_parameters==null):"+(_user_parameters==null));					
					_user_parameters = addSingleUser(_user_parameters, _sqlm, true);
//System.out.println("2 (_user_parameters==null):"+(_user_parameters==null));					
					result_statuses.add(_user_parameters.get(REST_STATUS));
					result_messages.add(_user_parameters.get(REST_RESULT));
				}
				else
				{
					result_statuses.add(REST_STATUS_ERROR);
					result_messages.add("Number of columns not supported!");
				}
			}// end of -- FOR ALL LINES
			// compile bulk result
			String bulk_result = "";
			for(int i=0; i<result_statuses.size(); i++)
			{
				bulk_result += "<div>Adding user with login='" +	attempted_logins.get(i) + "' " +
					result_statuses.get(i) + " " + result_messages.get(i) + "/<div>\n";
			}
			String result = getMessageHTML("Results of Adding Multiple Users",
					bulk_result + 
					"<a href='"+ _context_path+"/group/" + group_mnemonic + 
					"'>Back</a> to group's record OR <a href='"+_context_path+"/group/" + 
					group_mnemonic + "/users/new'>add more users</a>", _context_path);
			_parameters.put(REST_STATUS, REST_STATUS_OK);
			_parameters.put(REST_RESULT, result);
		}// end of -- GO TO THE BULK MODE
		else
		{// SINGLE USER
			_parameters = addSingleUser(_parameters, _sqlm, false);
		}// end of -- SINGLE USER
	    
		return _parameters;
	}

	
	public static Map<String, String> addSingleUser(Map<String, String> _parameters, SQLManager _sqlm, boolean _batch)
	{
		String _context_path = _parameters.get(REST_CONTEXT_PATH);

		String group_id = _parameters.get(REST_GROUP_ID);
		String group_mnemonic = _parameters.get(REST_GROUP_MNEMONIC);
		String _user_login = _parameters.get(REST_USER_LOGIN);
		
		String _login = _parameters.get(REST_USER_F_LOGIN);
		String _pass = _parameters.get(REST_USER_F_PASS);
		String _name = _parameters.get(REST_USER_F_NAME);
		String _email = _parameters.get(REST_USER_F_EMAIL);
		String _org = _parameters.get(REST_USER_F_ORGANIZATION);
		String _country = _parameters.get(REST_USER_F_COUNTRY);
		String _city = _parameters.get(REST_USER_F_CITY);
		String _how = _parameters.get(REST_USER_F_HOW);

		_name = (_name == null || _name.length() == 0)?"":_name;
		_email = (_email == null || _email.length() == 0)?"":_email;
		_org = (_org == null || _org.length() == 0)?"":_org;
		_country = (_country == null || _country.length() == 0)?"":_country;
		_city = (_city == null || _city.length() == 0)?"":_city;
		_how = (_how == null || _how.length() == 0)?"":_how;
		
		boolean parameters_good = true;
		String parameters_error_msg = "";
		// Check the Data
		// 		Check login
		if((_user_login == null || _user_login.length() == 0) && (_login == null || _login.length() == 0) )
		{
			parameters_error_msg += "User login not specified! ";
			parameters_good = false;
		}
		// 		end of -- Check login
		// 		Check password
		if((_user_login == null || _user_login.length() == 0) && (_pass == null || _pass.length() == 0))
		{
			parameters_error_msg += "User password not specified!\n";
			parameters_good = false;
		}
		if(!parameters_good)
		{
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, (!_batch)?getErrorMessageHTML(parameters_error_msg, _context_path):parameters_error_msg);
			return _parameters;
		}
		// 		end of -- password
		// end of -- Check the Data
		
		
		String qry = "";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		// UM2
		String qry2 = "";
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		// end of -- UM2
		
		try
		{
			conn = _sqlm.getConnection();

			// Check for existence of the specified group
			qry = "SELECT * FROM ent_user WHERE " + 
					((group_mnemonic != null && group_mnemonic.length()>0)
							?"Login='" + group_mnemonic + "'"
							:"UserID=" + group_id) + 
					" AND IsGroup=1;";
			
			// UM2
			qry2 = qry.replaceAll("ent_user", "um2.ent_user");
			// end of -- UM2
			
			boolean group_exists = false;
			String found_group_mnemonic = "";
			int found_group_id = 0;
			
			// UM2
			String found_group_mnemonic2 = "";
			int found_group_id2 = 0;
			// end of -- UM2
			
			stmt = conn.prepareStatement(qry);
			rs = stmt.executeQuery();
			
			// UM2
			stmt2 = conn.prepareStatement(qry2);
			rs2 = stmt2.executeQuery();
			// end of -- UM2
			
			if(rs.next() /*UM2*/&& rs2.next())
			{
				group_exists = true;
				found_group_mnemonic = rs.getString("Login");
				found_group_id = rs.getInt("UserID");
				
				// UM2
				found_group_mnemonic2 = rs2.getString("Login");
				found_group_id2 = rs2.getInt("UserID");
				// end of -- UM2
			}
			rs.close();
			stmt.close();
			// UM2
			rs2.close();
			stmt2.close();
			// end of -- UM2
			// end of -- Check for existence of the specified group
			
			if(!group_exists)
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, (!_batch)?getErrorMessageHTML("Group specified does not exist.", _context_path):"Group specified does not exist.");
				return _parameters;
			}
			
			if(_user_login != null && _user_login.length()>0 && !_user_login.equals(REST_GROUP_USER_NOT_SELECTED))
			{// add existing user by login
				boolean user_exists = false;
				int found_user_id = 0;;
				// Check wether user login exists
				qry = "SELECT * FROM ent_user WHERE Login='"+ _user_login + "' AND IsGroup=0;";
				// UM2
				int found_user_id2 = 0;;
				qry2 = qry.replaceAll("ent_user", "um2.ent_user");
				// end of -- UM2
				
				stmt = conn.prepareStatement(qry);
				rs = stmt.executeQuery();
				
				// UM2
				stmt2 = conn.prepareStatement(qry2);
				rs2 = stmt2.executeQuery();
				// end of -- UM2
				
				if(rs.next() /*UM2*/&& rs2.next())
				{
					user_exists = true;
					found_user_id = rs.getInt("UserID");
					// UM2
					found_user_id2 = rs2.getInt("UserID");
					// end of -- UM2
				}
				rs.close();
				stmt.close();
				// UM2
				rs2.close();
				stmt2.close();
				// end of -- UM2
				
				if(!user_exists)
				{
					_parameters.put(REST_STATUS, REST_STATUS_ERROR);
					_parameters.put(REST_RESULT, (!_batch)?getErrorMessageHTML("User specified does not exist.", _context_path):"User specified does not exist.");
					return _parameters;
				}
				
				boolean user_is_member = false;
				// Check whether user already a member
				qry = "SELECT * FROM ent_user u JOIN rel_user_user uu ON(u.UserID=uu.ChildUserID) " +
						"JOIN ent_user g ON(g.UserID=uu.PArentUserID) " +
						"WHERE u.Login='" + _user_login + "' AND g.Login='" + found_group_mnemonic + "';";
				stmt = conn.prepareStatement(qry);
				rs = stmt.executeQuery();
				if(rs.next())
					user_is_member = true;
//System.out.println("qry="+qry);				
//System.out.println("user_is_member="+user_is_member);				
				rs.close();
				stmt.close();
				
				if(user_is_member)
				{
					String result = getMessageHTML("User is Already a Member","<a href='"+_context_path+"/group/" + 
							found_group_mnemonic + "'>Back</a> to group's record", _context_path);
					
					_parameters.put(REST_STATUS, REST_STATUS_OK);
					_parameters.put(REST_RESULT, result);
					return _parameters;
				}
				else
				{// do add user by login
					qry = "INSERT INTO rel_user_user (ParentUserID, ChildUserID) VALUES (" + found_group_id + "," + found_user_id +");";
					stmt = conn.prepareStatement(SQLManager.stringUnbreak(qry));
					stmt.executeUpdate();
					stmt.close();
					stmt = null;
//					SQLManager.executeUpdate(conn, SQLManager.stringUnbreak(qry));

					// UM2
					qry2 = "INSERT INTO um2.rel_user_user (GroupID, UserID) VALUES (" + found_group_id2 + "," + found_user_id2 +");";
					stmt = conn.prepareStatement(SQLManager.stringUnbreak(qry2));
					stmt.executeUpdate();
					stmt.close();
					stmt = null;
//					SQLManager.executeUpdate(conn, SQLManager.stringUnbreak(qry2));
					// end of -- UM2
					
					String result = (!_batch)
							?getMessageHTML("User Successfully Added to the Group",
									"<a href='"+_context_path+"/group/" + found_group_mnemonic + 
									"'>Back</a> to group's record OR <a href='"+_context_path+"/group/" + 
									found_group_mnemonic + "/users/new'>add another user</a>", _context_path)
							:"User successfully added to the group";
					_parameters.put(REST_STATUS, REST_STATUS_OK);
					_parameters.put(REST_RESULT, result);
				}// end of -- do add user by login
			}// end of -- add existing user by login
			else
			{// add a new user by all fields
				boolean login_exists = false;
				// check whether login is taken
				qry = "SELECT * FROM ent_user u WHERE u.Login='" + _login + "';";
				
				stmt = conn.prepareStatement(qry);
				rs = stmt.executeQuery();
				
				if(rs.next())
					login_exists = true;
				rs.close();
				stmt.close();
				// end of -- check whether login is taken
				
				if(login_exists)
				{
					qry = "SELECT Login, Name, IsMember(UserId,'" + found_group_mnemonic + "') AS IsMember FROM ent_user WHERE IsGroup=0 AND UserID>2;";
					stmt = conn.prepareStatement(qry);
					rs = stmt.executeQuery();

					_parameters = formatGroupUserAdd(rs, _parameters, true /*login taken*/);

					rs.close();
					stmt.close();
				}
				else
				{// do add new user from scratch
					int new_user_id = 0;
					
					try
					{
						boolean auto_comm = conn.getAutoCommit();
						conn.setAutoCommit(false);
						
						qry = "INSERT INTO ent_user (Login, Name, Pass, IsGroup, Sync, EMail, Organization, City, Country, How) " +
						"VALUES ('" + _login + "','" + _name + "',MD5('" + _pass + "'),0,1,'" + _email + "','" + _org + "','" + _city + "','" + _country + "','" + _how + "')";
						stmt = conn.prepareStatement(SQLManager.stringUnbreak(qry));
						stmt.executeUpdate();
						stmt.close();
						stmt = null;
//						SQLManager.executeUpdate(conn, SQLManager.stringUnbreak(qry));
				
						String qry_sec = "INSERT INTO seq_role (Login, Role) VALUES ('" + _login + "','user')";
						stmt = conn.prepareStatement(SQLManager.stringUnbreak(qry_sec));
						stmt.executeUpdate();
						stmt.close();
						stmt = null;
//						SQLManager.executeUpdate(conn, SQLManager.stringUnbreak(qry_sec));
		
						// UM2
						qry2 = qry.replaceAll("ent_user", "um2.ent_user");
						stmt = conn.prepareStatement(SQLManager.stringUnbreak(qry2));
						stmt.executeUpdate();
						stmt.close();
						stmt = null;
//						SQLManager.executeUpdate(conn, SQLManager.stringUnbreak(qry2));
						// end of -- UM2
						
						qry = "SELECT LAST_INSERT_ID(UserID) AS UserID FROM ent_user WHERE Login='" + _login + "';";
						stmt = conn.prepareStatement(qry);
						rs = stmt.executeQuery();
						if(rs.next())
							new_user_id = rs.getInt("UserID");
						rs.close();
						stmt.close();
						
						qry = "INSERT INTO rel_user_user (ParentUserID, ChildUserID) " +
								"VALUES (" + found_group_id + "," + new_user_id + ")";
						stmt = conn.prepareStatement(SQLManager.stringUnbreak(qry));
						stmt.executeUpdate();
						stmt.close();
						stmt = null;
//						SQLManager.executeUpdate(conn, SQLManager.stringUnbreak(qry));
						
						// UM2
						qry2 = "SELECT LAST_INSERT_ID(UserID) AS UserID FROM um2.ent_user WHERE Login='" + _login + "';";
						stmt2 = conn.prepareStatement(qry2);
						rs2 = stmt2.executeQuery();
						if(rs2.next())
							new_user_id = rs2.getInt("UserID");
						rs2.close();
						stmt2.close();
						
						qry2 = "INSERT INTO um2.rel_user_user (GroupID, UserID) " +
								"VALUES (" + found_group_id2 + "," + new_user_id + ")";
						stmt = conn.prepareStatement(SQLManager.stringUnbreak(qry2));
						stmt.executeUpdate();
						stmt.close();
						stmt = null;
//						SQLManager.executeUpdate(conn, SQLManager.stringUnbreak(qry2));
						// end of -- UM2
						
//						conn.commit();
						conn.setAutoCommit(auto_comm);
					}
					catch(SQLException sqle)
					{
						_parameters.put(REST_STATUS, REST_STATUS_ERROR);
						_parameters.put(REST_RESULT, (!_batch)?getErrorMessageHTML("SQL Exception while adding user.", _context_path):"SQL Exception while adding user.");
						sqle.printStackTrace(System.out);
					}
					
					String result = (!_batch)? 
							getMessageHTML("User Successfully Added to the Group",
							"<a href='"+_context_path+"/group/" + found_group_mnemonic + 
							"'>Back</a> to group's record OR <a href='"+_context_path+"/group/" + 
							found_group_mnemonic + "/users/new'>add another user</a>", _context_path)
							:"User Successfully Added to the Group";
					_parameters.put(REST_STATUS, REST_STATUS_OK);
					_parameters.put(REST_RESULT, result);
				}// end of -- do add new user from scratch
				
			}// end of -- add a new user by all fields
		}
		catch(SQLException sqle)
		{
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, (!_batch)? getErrorMessageHTML("SQL Exception while retrieving data.", _context_path):"SQL Exception while retrieving data.");
			sqle.printStackTrace(System.out);
		}
		finally { SQLManager.recycleObjects(conn, stmt, rs); }
		return _parameters;
	}
	
	public static Map<String, String> getGroupAdd(Map<String, String> _parameters, SQLManager _sqlm)
	{
		try
		{
			_parameters = formatGroupAdd(null, _parameters, false /*login taken*/);
		}
		catch(Exception e) { e.printStackTrace(System.out); }
		return _parameters;
	}

	
	private static Map<String, String> formatGroupAdd(ResultSet _rs, Map<String, String> _parameters, boolean login_taken) throws SQLException
	{
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		
		String home_html = "<a href='" + _context_path + "/' title='Home'>Home</a>";
		String logout_html = "<a href='" + _context_path + "/index.jsp?logout=1' title='Lougout from the system'>[logout]</a>";
		String all_groupss_html = "&nbsp;&raquo;&nbsp;<a href='" + _context_path + "/groups' title='List of all Groups'>All Groups</a>";

		String result = 
				getPageHeaderHTML("ADAPT&sup2; User &amp; Group Directory - Add New Group", _context_path) +
				
				"<form name='edit' id ='edit' method='post' action='" + _context_path + "/groups/new'>\n" +
				"<table cellpadding='2px' cellspacing='0px' class='violet_table' width='500px'>\n" +
				"	<caption class='violet_table_caption'>Add New Group</td></tr>\n" +
				"	<tr>\n" +
				"	  <td class='violet_table_header' colspan=2'2>" + home_html + all_groupss_html +
					"<div style='text-align:right;display:block;'>" + logout_html + "</div></td>\n" +
				"  	</tr>\n" +
				"	<tr>\n" +
				"		<td>Name</td>\n" +
				"		<td><input name='" + REST_GROUP_F_NAME + "' type='text' maxlength='60' size='45' value='" + ((login_taken)?_parameters.get(REST_GROUP_F_NAME):"") + "'/></td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td>Mnemonic</td>\n" +
				"		<td><input id='" + REST_GROUP_F_MNEMONIC + "' name='" + REST_GROUP_F_MNEMONIC + "' type='text' maxlength='15' size='21' value='" + ((login_taken)?_parameters.get(REST_GROUP_F_MNEMONIC):"") + "'/>" +
						((login_taken)?"<font color='#FF0000'>&nbsp;Mnemonic already taken!</font>":"") + "</td>\n" +
				"	</tr>\n" +
				"	<tr>\n" +
				"		<td class='violet_table_footer'><a href='" + _context_path + "/groups'><input type='button' value='Cancel'/></a></td>\n" +
				"		<td class='violet_table_footer' align='right'><a href='javascript:mySubmit();'><input type='button' value='Submit'/></a></td>\n" +
				"	</tr>\n" +
				"</table>\n" +
				"<script type='text/javascript'>\n" + 
				"function mySubmit()\n" + 
				"{\n" + 
				"	var do_submit = 1;\n" + 
				"	var msg = '';\n" + 
				"	if(document.getElementById('" + REST_GROUP_F_MNEMONIC+  "').value.length == 0)\n" + 
				"	{\n" + 
				"		msg += 'Group lmemonic can not be empty. ';\n" + 
				"		do_submit = 0;\n" + 
				"	}\n" + 
				"	if(do_submit==1)\n" + 
				"		document.edit.submit();\n" + 
				"	else\n" + 
				"		alert(msg);\n" + 
				"}\n" + 				
				"</script>\n" + 
				"</form>\n" +		
				"</body></html>";
					
				_parameters.put(REST_STATUS, REST_STATUS_OK);
				_parameters.put(REST_RESULT, result);
				
		return _parameters;
	}

	
	public static Map<String, String> setGroupAdd(Map<String, String> _parameters, SQLManager _sqlm)
	{
		String group_name = _parameters.get(REST_GROUP_F_NAME);
		String group_mnemonic = _parameters.get(REST_GROUP_F_MNEMONIC);
		
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		
		String qry = "";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = _sqlm.getConnection();

			boolean login_exists = false;
			// check whether login is taken
			qry = "SELECT * FROM ent_user u WHERE u.Login='" + group_mnemonic + "';";
			stmt = conn.prepareStatement(qry);
			rs = stmt.executeQuery();
			
			if(rs.next())
				login_exists = true;
			rs.close();
			stmt.close();
			// end of -- check whether login is taken
			
			if(login_exists)
			{
				_parameters = formatGroupAdd(null, _parameters, true /*login taken*/);
			}
			else
			{// do add new group from scratch
				try
				{
					qry = "INSERT INTO ent_user (Login, Name, Pass, IsGroup, Sync, EMail, Organization, City, Country, How) " +
							"VALUES ('" + group_mnemonic + "','" + group_name + "','',1,1,'','','','','')";
					stmt = conn.prepareStatement(SQLManager.stringUnbreak(qry));
					stmt.executeUpdate();
					stmt.close();
					stmt = null;
//					SQLManager.executeUpdate(conn, SQLManager.stringUnbreak(qry));
					
					// UM2
					qry = qry.replaceAll("ent_user", "um2.ent_user");
					stmt = conn.prepareStatement(SQLManager.stringUnbreak(qry));
					stmt.executeUpdate();
					stmt.close();
					stmt = null;
//					SQLManager.executeUpdate(conn, SQLManager.stringUnbreak(qry));
					// end of -- UM2
				}
				catch(SQLException sqle)
				{
					_parameters.put(REST_STATUS, REST_STATUS_ERROR);
					_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while adding user.", _context_path));
					sqle.printStackTrace(System.out);
				}
				
				String result = getMessageHTML("Group Successfully Added",
						"<a href='"+_context_path+"/group/" + group_mnemonic + 
						"'>Back</a> to group's record", _context_path);
				_parameters.put(REST_STATUS, REST_STATUS_OK);
				_parameters.put(REST_RESULT, result);
			}// end of -- do add new group from scratch
			
			
		}
		catch(SQLException sqle)
		{
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while retrieving data.", _context_path));
			sqle.printStackTrace(System.out);
		}
		finally { SQLManager.recycleObjects(conn, stmt, rs); }
		
		return _parameters;
	}
	
	
	public static Map<String, String> removeGroupUser(Map<String, String> _parameters, SQLManager _sqlm)
	{
		String group_id = _parameters.get(REST_GROUP_ID);
		String group_mnemonic = _parameters.get(REST_GROUP_MNEMONIC);
		String user_id = _parameters.get(REST_USER_ID);
		String user_login = _parameters.get(REST_USER_LOGIN);
		String method = _parameters.get(REST_METHOD);
		String _context_path = _parameters.get(REST_CONTEXT_PATH);
		
		if(!method.equalsIgnoreCase(REST_METHOD_DELETE))
		{
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, getErrorMessageHTML("Method specified incorrectly.", _context_path));
			return _parameters;
		}
		
		String qry_check_group = "";
		String qry_check_user = "";
		String qry_remove = "";
		
		int found_group_id = 0;
		int found_user_id = 0;
		String found_group_mnemonic = "";
		
		// UM2
		int found_group_id2 = 0;
		int found_user_id2 = 0;
		String found_group_mnemonic2 = "";
		// end of -- UM2
		
		if( group_id != null && group_id.length() != 0 && user_id != null && user_id.length() != 0 )
		{// group and user by id 
			qry_check_group = "SELECT * FROM ent_user WHERE IsGroup=1 AND UserID='" + group_id + "';";
			qry_check_user = "SELECT * FROM ent_user WHERE IsGroup=0 AND UserID='" + user_id + "';";
		}// end of -- group and user by id 
		else if( group_mnemonic != null && group_mnemonic.length() != 0 && user_login != null && user_login.length() != 0 )
		{// group and user by login 
			qry_check_group = "SELECT * FROM ent_user WHERE IsGroup=1 AND Login='" + group_mnemonic + "';";
			qry_check_user = "SELECT * FROM ent_user WHERE IsGroup=0 AND Login='" + user_login + "';";
		}// end of -- group and user by login
		else
		{
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, getErrorMessageHTML("Group and/or User specified incorrectly.", _context_path));
			return _parameters;
		}
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		// UM2
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		// end of -- UM2

		try
		{// work with DB
			conn = _sqlm.getConnection();
			
			// check group
			stmt = conn.prepareStatement(qry_check_group);
			rs = stmt.executeQuery();
			
			// UM2
			stmt2 = conn.prepareStatement(qry_check_group.replaceAll("ent_user", "um2.ent_user"));
			rs2 = stmt2.executeQuery();
			// end of -- UM2

			if(rs.next() /*UM2*/&& rs2.next())
			{
				found_group_id = rs.getInt("UserID");
				found_group_mnemonic = rs.getString("Login");
				// UM2
				found_group_id2 = rs2.getInt("UserID");
				found_group_mnemonic2 = rs2.getString("Login");
				// end of -- UM2
			}
			else
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("Specified group is not found.", _context_path));
				return _parameters;
			}
			rs.close();
			stmt.close();
			// UM2
			rs2.close();
			stmt2.close();
			// end of -- UM2
			
			if(found_group_mnemonic.equals("world"))
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("Users cannot be deleted from group 'World'.", _context_path));
				return _parameters;
			}
			
			// check user
			stmt = conn.prepareStatement(qry_check_user);
			rs = stmt.executeQuery();
			// UM2
			stmt2 = conn.prepareStatement(qry_check_user.replaceAll("ent_user", "um2.ent_user"));
			rs2 = stmt2.executeQuery();
			// end of -- UM2
			if(rs.next() /*UM2*/&&rs2.next())
			{
				found_user_id = rs.getInt("UserID");
				// UM2
				found_user_id2 = rs2.getInt("UserID");
				// end of -- UM2
			}
			else
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("Specified user is not found.", _context_path));
				return _parameters;
			}
			rs.close();
			stmt.close();
			// UM2
			rs2.close();
			stmt2.close();
			// end of -- UM2
			
			
			// check membership
			String qry = "SELECT * FROM rel_user_user WHERE ParentUserID=" + found_group_id + " AND ChildUserID=" + found_user_id + ";";
			stmt = conn.prepareStatement(qry);
			rs = stmt.executeQuery();
			if(!rs.next())
			{
				_parameters.put(REST_STATUS, REST_STATUS_ERROR);
				_parameters.put(REST_RESULT, getErrorMessageHTML("Specified user is not a member of a specified group.", _context_path));
				return _parameters;
			}
			rs.close();
			stmt.close();
			
			qry_remove = "DELETE FROM rel_user_user WHERE ParentUserID=" + found_group_id + " AND ChildUserID=" + found_user_id + ";";
			stmt = conn.prepareStatement(qry_remove);
			stmt.executeUpdate();
			stmt.close();
			stmt = null;
//			SQLManager.executeUpdate(conn, qry_remove);
			// UM2
			qry_remove = "DELETE FROM um2.rel_user_user WHERE GroupID=" + found_group_id2 + " AND UserID=" + found_user_id2 + ";";
			stmt = conn.prepareStatement(qry_remove);
			stmt.executeUpdate();
			stmt.close();
			stmt = null;
//			SQLManager.executeUpdate(conn, qry_remove);
			// end of -- UM2
			
			_parameters.put(REST_STATUS, REST_STATUS_OK);
			String result = getMessageHTML("User Successfully Removed",
					"<a href='"+_context_path+"/group/" + found_group_mnemonic + 
					"'>Back</a> to group record",
					_context_path);
			_parameters.put(REST_RESULT, result);
			
			
		}// end of -- work with DB
		catch(SQLException sqle)
		{
			_parameters.put(REST_STATUS, REST_STATUS_ERROR);
			_parameters.put(REST_RESULT, getErrorMessageHTML("SQL Exception while removing user", _context_path));
			sqle.printStackTrace(System.out);
		}
		finally { SQLManager.recycleObjects(conn, stmt, rs); }

		return _parameters;
	}
			
	private static String getPageHeaderHTML(String _title, String _context_path)
	{
		String result =
			"<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01//EN' 'http://www.w3.org/TR/html4/strict.dtd'>\n"+ 
			"<html><head>\n"+
			"<title>" + _title + "</title>\n"+
			"<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>\n"+
			"<link rel='StyleSheet' href='" + _context_path + "/assets/rest.css' type='text/css' />\n"+
			"<script type='text/javascript' src='" + _context_path + "/assets/rest_opener.js'></script>\n"+
			"</head><body onload='opener.init(\"" + _context_path + "/assets/\");'>\n";
		
		return result;
	}
	
	
	public static String getPageHeaderRDF()
	{
		String result =
			"<?xml version='1.0' encoding='utf-8'?>\n" +
			"<rdf:RDF\n" +
			"		xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n" +
			"		xmlns:rdfs='http://www.w3.org/2000/01/rdf-schema#'\n" +
			"		xmlns:foaf='http://xmlns.com/foaf/0.1/'\n" +
			"		xmlns:vCard='http://www.w3.org/2006/vcard/ns#'\n" +
			"		xmlns:dc='http://purl.org/dc/elements/1.1/'\n" +
			"		xmlns:dcterms='http://purl.org/dc/terms/'\n" +
			"		xmlns:rss='http://purl.org/rss/1.0/'>";
		
		return result;
	}
	
	
	public static String getErrorMessageHTML(String _message, String _context_path)
	{
		String result = 
			getPageHeaderHTML("Knowledge Tree - Error", _context_path) +
			"<table cellpadding='0px' cellspacing='0px' class='burg_table'>"+
			"<tr>"+
			"	<td class='burg_table_caption'>Error</td>"+
			"</tr>"+
			"<tr>"+
			"	<td class='burg_table_message'>" + _message + "</td>"+
			"</tr>"+
			"</table></body></html>";
		
		return result;
	}

	
	public static String getMessageHTML(String _title, String _message, String _context_path)
	{
		String result = 
			getPageHeaderHTML("Knowledge Tree - " + _title, _context_path) +
			"<table cellpadding='2px' cellspacing='0px' class	='green_table'>\n"+
			"<tr>\n"+
			"	<td class='green_table_caption'>" + _title + "</td>\n"+
			"</tr>\n"+
			"<tr><td class='green_table_message'>"+ _message + "</td></tr>\n"+
			"</table></body></html>";
		return result;
	}

	private static String clrStr(String _str)
	{
		return (_str == null || _str.length() == 0)?"":_str;
	}
}
