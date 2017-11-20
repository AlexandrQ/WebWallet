package controller.regUser;

import java.io.IOException;
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




@WebServlet("/regUser/AddCost")
public class AddCost extends HttpServlet {
	private static final long serialVersionUID = 1L;       
    
    public AddCost() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();			
		
		String type = request.getParameter("j_idt13:0:type_input");
		String sum = request.getParameter("j_idt13:0:sum");
		String date = request.getParameter("j_idt13:0:date_input");
		String category = request.getParameter("j_idt13:0:category_input");
		String description = request.getParameter("j_idt13:0:description");
		
		String userid = getUseridFromDB((String)session.getAttribute("username"));		
		if(AddRowInDB(type, sum, date, category, description, userid)) {
			session.setAttribute("dbMessage", "Cost adds successful!");				
			request.getRequestDispatcher("account?username=" + (String)session.getAttribute("username") + "&password=" + (String)session.getAttribute("password")).forward(request, response);
		}
		else {
			session.setAttribute("dbMessage", "Cost does not added");			
			request.getRequestDispatcher("/faces/regUser/costs.xhtml").forward(request, response);
		}	
		
	}
	
	private String getUseridFromDB(String username) {
		String querryStr = "SELECT userid FROM public.\"Users\" WHERE username = '" + username +"'";		
		Connection dbConnection = null;
	    Statement statement = null;
	    String Userid = "";
	    
	    
	    try {
		    dbConnection = getDBConnection();
		    statement = dbConnection.createStatement();	 
		    
		    ResultSet rs = statement.executeQuery(querryStr);
		    
		    while (rs.next()) {	
		    	Userid = rs.getString("userid");		    	
		    }
		    
		    return Userid;		    		    
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		    return Userid;
		    
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
	
	
	private boolean AddRowInDB(String Type, String Sum, String Date, String Category, String Description, String Userid ) {
		if (Userid.length()>0 && Type.length()>0 && Sum.length()>0 && Date.length()>0) {
			String querryStr = "INSERT INTO public.\"Costs\"(type, sum, date, category, description, userid) VALUES ('" + Type + "','" + Sum + "','" + Date + "','" + Category + "','" + Description + "','" + Userid + "')";
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

}
