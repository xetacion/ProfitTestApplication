package main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/submitCustomer")
public class SubmitCustomer extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String dateofbirth = request.getParameter("dateofbirth");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		Connection conn = null; 
	    DatabaseActions dba = new DatabaseActions();
		try {
			Class.forName(Main.JDBC_DRIVER);
	        conn = DriverManager.getConnection(Main.DB_URL, Main.USER, Main.PASS);
	        if(dba.checkDataCorrectness(firstname, lastname, dateofbirth, username, password)) {
	        	dba.insertCustomer(conn, firstname, lastname, dateofbirth, username, password);
	        }
	        dba.readTable(conn, request);
	        
	        conn.close();
	        RequestDispatcher dispatcher 
	         = this.getServletContext().getRequestDispatcher("/WEB-INF/views/index.jsp");

			 dispatcher.forward(request, response);
	        
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
