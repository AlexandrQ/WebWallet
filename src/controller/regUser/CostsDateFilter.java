package controller.regUser;

import java.io.IOException;
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


@WebServlet("/regUser/dateFilter")
public class CostsDateFilter extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public CostsDateFilter() {
        super();
        // TODO Auto-generated constructor stub
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();	
		
		String username = (String)session.getAttribute("username");
		String fromDate = request.getParameter("fromDate_input");
		String toDate = request.getParameter("toDate_input");
		
		ArrayList<Costs> myCosts = new ArrayList<Costs>();
		
		if (fromDate.length()>0 && toDate.length()>0 ) {
			myCosts = getCostsFromDB(username, fromDate, toDate);
		}
		else {
			session.setAttribute("Message", "");
			session.setAttribute("dbMessage", "");
			session.setAttribute("dateFilterMessage", "Both date fills is required");				
			request.getRequestDispatcher("/faces/regUser/costs.xhtml").forward(request, response);
		}	
			
			
		if (myCosts.size() != 0) {
			session.setAttribute("costs", myCosts);
			session.setAttribute("Message", "");
			session.setAttribute("dbMessage", "");
			session.setAttribute("dateFilterMessage", "");			
			request.getRequestDispatcher("/faces/regUser/costs.xhtml").forward(request, response);
		}
		else {
			session.setAttribute("costs", myCosts);
			session.setAttribute("Message", "Costs was not found");
			session.setAttribute("dbMessage", "");	
			session.setAttribute("dateFilterMessage", "");
			request.getRequestDispatcher("/faces/regUser/costs.xhtml").forward(request, response);
		}			
	}		

	
	
	private ArrayList<Costs> getCostsFromDB(String username, String fromDate, String toDate) {
		String querryStr = "SELECT type, sum, date, category, description FROM public.\"Users\", public.\"Costs\" WHERE username = '" + username +"' AND public.\"Costs\".userid = public.\"Users\".userid AND date >= '" + fromDate + "' AND date <= '" + toDate + "'";		
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
