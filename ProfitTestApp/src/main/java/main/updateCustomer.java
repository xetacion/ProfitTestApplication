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

@WebServlet("/updateCustomer")
public class updateCustomer extends HttpServlet {
static final long serialVersionUID = 3L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String action = request.getParameter("action");
		String id = request.getParameter("id");
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
			request.getSession().removeAttribute("tableErrorMessage");
			if(action.equals("Update")) {
				int correctnessResult = dba.checkDataCorrectness(firstname, lastname, dateofbirth, username, password);
				if(correctnessResult == 0 || (correctnessResult == -4 && password.length() == 0)) {
					dba.updateCustomer(conn, id, firstname, lastname, dateofbirth, username, password);
				} else {
					request.getSession().setAttribute("tableErrorMessage", "Inserted data was invalid!");
				}
			} else {
				dba.deleteCustomer(conn, id);
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
