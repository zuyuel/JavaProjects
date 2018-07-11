import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.Statement;

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
 * Servlet implementation class ShoppingCart
 */
@WebServlet(name = "ShoppingCart", urlPatterns = "/api/cart")
public class ShoppingCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        String customerId = (String) request.getSession().getAttribute("customerId");
        String movieId = (String) request.getSession().getAttribute("title");
        String item = request.getParameter("quantity");
        String shopping_cart = "INSERT INTO shoppingcart VALUES ("
        		+ customerId + "," + movieId + "," + item + ");";

		PrintWriter out = response.getWriter();
		System.out.println(item);
        try
        {
//        	Class.forName("com.mysql.jdbc.Driver").newInstance();
//    		Connection connection = (Connection) DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
//    		Statement statement = connection.createStatement();
        	Connection dbcon = (Connection) dataSource.getConnection();
//        	Statement statement = dbcon.createStatement();

//    		String query = "SELECT c.id, c.firstName, c.lastName, c.ccId, c.address, cc.expiration, c.email, c.password\n" + 
//    				"FROM customers c LEFT JOIN creditcards cc\n" + 
//    				"ON cc.id = c.id;";
//    		
    		String query = "SELECT * FROM shoppingcart sc WHERE sc.quantity = ?";
    		PreparedStatement statement = (PreparedStatement) dbcon.prepareStatement(query);
    		statement.setString(1, item);
    		ResultSet rs = statement.executeQuery();
    		JsonArray jsonArray = new JsonArray();
    		
//            String firstName = (String) session.getAttribute("firstName");
//            String lastName = (String) session.getAttribute("lastName");
//            System.out.println(firstName);
			while (rs.next())
			{
				movieId = rs.getString("movieId");
				customerId = rs.getString("customerId"); 
				
				JsonObject jsonObject = new JsonObject();			
				jsonObject.addProperty("movieId", movieId);
				jsonObject.addProperty("customerId", customerId);
				jsonArray.add(jsonObject);
				
//	            jsonObject.addProperty("firstName", firstName);
//	            jsonObject.addProperty("lastName", lastName);
			}
			
			out.write(jsonArray.toString());
			response.setStatus(200);
			
			rs.close();
			statement.close();
			dbcon.close();
        }
        catch (Exception ex)
        {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", ex.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
        }
        out.close();
	}

}
