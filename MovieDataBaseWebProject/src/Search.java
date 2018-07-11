import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import javax.annotation.Resource;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Servlet implementation class Search
 */
@WebServlet(name = "Search", urlPatterns = "/api/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		response.setContentType("application/json");
		String movie = request.getParameter("match");
		PrintWriter out = response.getWriter();
		
		try 
		{
			Connection dbcon = dataSource.getConnection();
			String query  = "SELECT * FROM movies WHERE MATCH (title) AGAINST "
					+ "(? in boolean mode) limit 20 offset 0"; // TODO change limit later
			PreparedStatement statement = dbcon.prepareStatement(query);
			String match = "";
        	String[] parts = movie.split(" ");
        	for(String s: parts)
        	{
        		match += "+("+s+"*)";
        	}
        	//String final_match = match.substring(0, match.length()-1);
        	System.out.printf("title is: %s\n",match); //test
        	statement.setString(1, match);
			
        	ResultSet rs = statement.executeQuery();
        	JsonArray jsonArray = new JsonArray();
        	while(rs.next())
        	{
        		String id = rs.getString("id");
        		String title = rs.getString("title");
        		jsonArray.add(generateJsonObject(id, title, "Movies"));
        	}
        	out.write(jsonArray.toString());
        	response.setStatus(200);
        	
        	rs.close();
            statement.close();
            dbcon.close();
		} 
		catch (Exception e) 
		{
        	
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);

        }
		out.close();
	}
	
	private static JsonObject generateJsonObject(String movieID, String movieName, String categoryName) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", movieName);
		
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("category", categoryName);
		additionalDataJsonObject.addProperty("movieID", movieID);
		
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}

}
