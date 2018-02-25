package main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
		String repeatPassword = request.getParameter("repeatPassword");
		
		Connection conn = null; 
	    DatabaseActions dba = new DatabaseActions();
		try {
			Class.forName(Main.JDBC_DRIVER);
	        conn = DriverManager.getConnection(Main.DB_URL, Main.USER, Main.PASS);
	        int correctnessResult = dba.checkDataCorrectness(firstname, lastname, dateofbirth, username, password);
	        request.getSession().removeAttribute("errorMessage");
	        request.getSession().removeAttribute("passwordErrorMessage");
	        request.getSession().removeAttribute("dateErrorMessage");
	        if(correctnessResult == 0 && password.equals(repeatPassword)) {
	        	dba.insertCustomer(conn, firstname, lastname, dateofbirth, username, password);
	        } else {
				if(correctnessResult == -1) {
					request.getSession().setAttribute("dateErrorMessage", "Inserted date in wrong format!");
				} else if(correctnessResult == -2) {
					request.getSession().setAttribute("dateErrorMessage", "Inserted date in wrong format!");
					request.getSession().setAttribute("passwordErrorMessage", "Password needs to be minimum eight characters, contain at least one letter and one number!");
				} else if(correctnessResult == -3) {
					request.getSession().setAttribute("passwordErrorMessage", "Password needs to be minimum eight characters, contain at least one letter and one number!");
					request.getSession().setAttribute("errorMessage", "Inserted data was invalid!");
				}
			}
	        dba.readTable(conn, request);
	        
	        conn.close();
	        response.sendRedirect(request.getContextPath() + "/");
	        
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
