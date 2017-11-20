package controller.unknownUser;

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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import validator.EmailValidator;

/**
 * Servlet implementation class Registration
 */
@WebServlet("/Registration")
public class Registration extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Registration() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		response.sendRedirect("/WebWallet/faces/registerPage.xhtml");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		String username = request.getParameter("username");		
		String password = request.getParameter("password");	
		String confirmPassword = request.getParameter("confirmPassword");
		String email = request.getParameter("email");	
		
		
		if(password.length() == 0 || confirmPassword.length() == 0 || password.compareTo(confirmPassword) != 0) {					
			session.setAttribute("errMessage", "Please, confirm password");	
			session.setAttribute("message", "");			
			response.sendRedirect("/WebWallet/faces/registerPage.xhtml");
			return;
		}		
		
		
		if(!CheckEmail(email)) {		
			
			session.setAttribute("errMessage", "Email is incorrect!");	
			session.setAttribute("message", "");	
			response.sendRedirect("/WebWallet/faces/registerPage.xhtml");
			return;			
		}	
		
		if(CheckUserDuplicate(username)) {
			if(CreateUser(username, password, email)) {
				session.setAttribute("message", "Registration was successful!");		
				session.setAttribute("errMessage", "");
				response.sendRedirect("/WebWallet/faces/regUser/login.xhtml");
			}
			else {
				session.setAttribute("errMessage", "Registration failed! Please try again!");	
				session.setAttribute("message", "");
				response.sendRedirect("/WebWallet/faces/registerPage.xhtml");
			}
		}
		else {
			session.setAttribute("errMessage", "Username already taken! Please try again!");
			session.setAttribute("message", "");
			response.sendRedirect("/WebWallet/faces/registerPage.xhtml");
		}
		
		
	}
	
	private boolean CheckUserDuplicate(String Username) {
		String querryStr = "SELECT COUNT(username) AS Count FROM public.\"Users\" WHERE username = '" + Username + "'";
		Connection dbConnection = null;
	    Statement statement = null;
		
		try {
		    dbConnection = getDBConnection();
		    statement = dbConnection.createStatement();		 
		    
		    ResultSet rs = statement.executeQuery(querryStr);		    
		    
		    if (rs.next()) {		    	
		    	if(rs.getString("Count").equals("1") ) {
			    	return false;
			    }
		    	else return true;   
		    }
		    else return true; 		    
			
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
	
	
	private boolean CreateUser(String Username, String Password, String Email ) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance( "SHA-256" );
		} catch (NoSuchAlgorithmException e1) {			
			e1.printStackTrace();
		}
	    md.update( Password.getBytes( StandardCharsets.UTF_8 ) );
	    byte[] digest = md.digest();
	    String password = String.format( "%064x", new BigInteger( 1, digest ) );
	    
		if (Username.length()>0 && Password.length()>0 && Email.length()>0) {
			String querryStr = "INSERT INTO public.\"Users\"(username, password, email) VALUES ('" + Username + "','" + password + "','" + Email + "')";
			Connection dbConnection = null;
		    Statement statement = null;
		    
		    try {
			    dbConnection = getDBConnection();
			    statement = dbConnection.createStatement();		 
			    
			    statement.executeUpdate(querryStr);			    
			    
			    if (dbConnection != null) {
			    	dbConnection.close();	
			    }
		        		
			    return true;
			    
			} catch (SQLException e) {
			    System.out.println(e.getMessage());	
			    
			    if (dbConnection != null) {
		            try {
						dbConnection.close();
					} catch (SQLException ee) {				
						ee.printStackTrace();
					}		            
		        }	
			    
			    return false;
			} 
		    
		}
		else return false;
	}
	
	private static Connection getDBConnection() {
	    Connection dbConnection = null;
	    try {
	        Class.forName("org.postgresql.Driver");
	    } catch (ClassNotFoundException e) {
	        System.out.println(e.getMessage());
	        
	    }
	    try {
	        dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/myTestPostgresDB","postgres", "admin");
	        return dbConnection;
	    } catch (SQLException e) {
	        System.out.println(e.getMessage());
	    }
	    return dbConnection;
	}
	
	private boolean CheckEmail(String email) {
		EmailValidator myValidator = new EmailValidator();
		return myValidator.validate(email);
	}

}
