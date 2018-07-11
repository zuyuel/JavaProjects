import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "Pagination", urlPatterns = "/api/Pagination")
public class Pagination extends HttpServlet {
	private static final long serialVersionUID = 2L;

	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String movie = request.getParameter("movie");
		String dropDown = request.getParameter("drop");
		String genre = request.getParameter("genre");
		String sortBy = request.getParameter("sortBy");
		String alphabet = request.getParameter("alphabet");
		System.out.println("\nPaginantion: ");
		System.out.printf("movie = %s, drop = %s, genre = %s, sortBy = %s, alphabet = %s\n",movie,dropDown,genre,sortBy,alphabet);
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();

			// Construct a query with parameter represented by "?"
			String pattern = "";
			String query = "";
			if(!movie.equals("null"))
			{
				if(dropDown.equals("title"))
            	{
					query = "select count(*) as total from movies where title like ?";
					pattern = "%"+movie+"%";
            	}
            	else if (dropDown.equals("year"))
            	{
            		query = "select count(*) as total from movies where year = ?";
            		pattern = "%"+movie+"%";
            	}
            	else if(dropDown.equals("director"))
            	{
            		query = "select count(*) as total from movies where director like ?";
            		pattern = "%"+movie+"%";
            	}
            	else
            	{
            		query = "select count(movieID) as total from (select * from stars where name like ?) s \r\n" + 
            				"inner join \r\n" + 
            				"stars_in_movies sim on s.id = sim.starID";
            		pattern = "%"+movie+"%";
            	}
			}
			else if(!genre.equals("null"))
			{
				query = "select count(gm.movieID) as total from (select * from genres where name = ?) g left join\r\n" + 
						"(select * from genres_in_movies) gm\r\n" + 
						"on g.id=gm.genreID";
				pattern = genre;
			}
			else if(!alphabet.equals("null"))
			{
				query = "select count(*) as total from movies where title like ?";
				pattern = alphabet+"%";
			}
			//query = "SELECT COUNT(*) as total FROM genres";

			// Declare our statement
			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, pattern);

			// Set the parameter represented by "?" in the query to the id we get from url,
			// num 1 indicates the first "?" in the query
			//statement.setString(1, name);

			// Perform the query
			ResultSet rs = statement.executeQuery();
			rs.next();
			String total = rs.getString("total");
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("total", total);
			// TODO delete
			
            // write JSON string to output
            out.write(jsonObject.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();

	}

}