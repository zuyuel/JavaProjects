

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

/**
 * Servlet implementation class Checkout
 */
@WebServlet(name = "Checkout", urlPatterns = "/api/checkout")
public class Checkout extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;       
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		String email = (String) request.getSession().getAttribute("email");
		PrintWriter out = response.getWriter();
		
		try
		{
			Connection dbcon = (Connection) dataSource.getConnection();
			String query = "SELECT c.id, c.firstName, c.lastName, c.ccId, c.address, cc.expiration, c.email, c.password\n" + 
					"FROM customers c LEFT JOIN creditcards cc\n" + 
					"ON cc.id = c.id WHERE c.email = ?";
			PreparedStatement statement = (PreparedStatement) dbcon.prepareStatement(query);
			statement.setString(1, email);
			
			ResultSet rs = statement.executeQuery();
			JsonArray jsonArray = new JsonArray();
			
			while (rs.next())
			{
				String firstName = rs.getString("firstName");
				String lastName = rs.getString("lastName");
				String ccId = rs.getString("ccId");
				String address = rs.getString("address");
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("firstName", firstName);
				jsonObject.addProperty("lastName", lastName);
				jsonObject.addProperty("ccId", ccId);
				jsonObject.addProperty("address", address);
				jsonArray.add(jsonObject);
			}
			out.write(jsonArray.toString());
			response.setStatus(200);
			
			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception ex) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", ex.getMessage());
			out.write(jsonObject.toString());

			response.setStatus(500);
		}
		out.close();
	}


}
