package controller.regUser;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entity.Costs;



/**
 * Servlet implementation class AccountController
 */
@WebServlet("/regUser/account")
public class AccountController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AccountController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");		
		if (action == null) {
			request.getRequestDispatcher("/faces/regUser/login.xhtml").forward(request, response);
		}
		else if(action.equalsIgnoreCase("logout")) {
			HttpSession session = request.getSession();
			session.removeAttribute("username");
			request.getRequestDispatcher("/faces/regUser/login.xhtml").forward(request, response);
		}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");		
		if (UserAutenticate(username, password)) {
			HttpSession session = request.getSession();
			session.setAttribute("username", username);
			session.setAttribute("password", password);			
			ArrayList<Costs> myCosts = getCostsFromDB(username);
						
			if (myCosts.size() != 0) {
				session.setAttribute("costs", myCosts);
				session.setAttribute("Message", "");				
				request.getRequestDispatcher("/faces/regUser/costs.xhtml").forward(request, response);
			}
			else {
				session.setAttribute("Message", "Costs was not found");
				session.setAttribute("costs", myCosts);
				request.getRequestDispatcher("/faces/regUser/costs.xhtml").forward(request, response);
			}
			
		}
		else {
			request.setAttribute("message", "Invalid login and/or password");
			request.getRequestDispatcher("/faces/regUser/login.xhtml").forward(request, response);
		}	
	}
	
	
	private ArrayList<Costs> getCostsFromDB(String username) {
		String querryStr = "SELECT type, sum, date, category, description FROM public.\"Users\", public.\"Costs\" WHERE username = '" + username +"' AND public.\"Costs\".userid = public.\"Users\".userid";		
		Connection dbConnection = null;
	    Statement statement = null;
	    ArrayList<Costs> myCosts = new ArrayList<Costs>();
	    
	    try {
		    dbConnection = getDBConnection();
		    statement = dbConnection.createStatement();	 
		    
		    ResultSet rs = statement.executeQuery(querryStr);		    
		    
		    while (rs.next()) {	
		    	Costs myCost = new Costs(rs.getString("type"), 
		    			rs.getString("sum").substring(0, rs.getString("sum").length()-1), 
		    			rs.getString("date"), 
		    			rs.getString("category"), 
		    			rs.getString("description") );	 
		    	myCosts.add(myCost);
		    }
		    
		    return myCosts;		    		    
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		    return myCosts;
		    
		} finally {
			if (dbConnection != null) {
	            try {
					dbConnection.close();
				} catch (SQLException e) {				
					e.printStackTrace();
				}
	        }				
		}		    
	}
	
	
	private boolean UserAutenticate(String username, String password) {
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance( "SHA-256" );
		} catch (NoSuchAlgorithmException e1) {			
			e1.printStackTrace();
		}
	    md.update( password.getBytes( StandardCharsets.UTF_8 ) );
	    byte[] digest = md.digest();
	    String Password = String.format( "%064x", new BigInteger( 1, digest ) );
		
		String querryStr = "SELECT COUNT(username) AS Count FROM public.\"Users\" WHERE username = '" + username + "' AND password = '" + Password + "'";
		Connection dbConnection = null;
	    Statement statement = null;
		
		try {
		    dbConnection = getDBConnection();
		    statement = dbConnection.createStatement();		 
		    
		    ResultSet rs = statement.executeQuery(querryStr);		    
		    
		    if (rs.next()) {		    	
		    	if(rs.getString("Count").equals("1") ) {
			    	return true;
			    }
		    	else return false;   
		    }
		    else return false; 		    
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		    return false;
		} finally {
			if (dbConnection != null) {
	            try {
					dbConnection.close();
				} catch (SQLException e) {				
					e.printStackTrace();
				}
	        }			
		}	
	}
	
	private static Connection getDBConnection() {
	    Connection dbConnection = null;
	    try {
	        Class.forName("org.postgresql.Driver");
	    } catch (ClassNotFoundException e) {
	        System.out.println("!!!ClassForNameError: " + e.getMessage());
	        
	    }
	    try {
	        dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/myTestPostgresDB","postgres", "admin");
	        return dbConnection;
	    } catch (SQLException e) {
	        System.out.println("!!!DriverMangerError: " + e.getMessage());
	    }
	    return dbConnection;
	}

}
