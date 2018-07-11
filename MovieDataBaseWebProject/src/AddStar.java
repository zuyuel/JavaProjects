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
@WebServlet(name = "AddStar", urlPatterns = "/api/add-star")
public class AddStar extends HttpServlet {
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
		String name = request.getParameter("star");
		System.out.println(name);
		String year = request.getParameter("birthyear");
		System.out.println(year);

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();

			// Construct a query with parameter represented by "?"
			String check_exist = "SELECT EXISTS(SELECT 1 FROM stars WHERE name = ?) AS EXIST";
			PreparedStatement exist_statement = dbcon.prepareStatement(check_exist);
			exist_statement.setString(1, name);
			ResultSet exist_rs = exist_statement.executeQuery();
			JsonArray jsonArray = new JsonArray();
			while (exist_rs.next())
			{
				String exist = exist_rs.getString("EXIST");
				int ex = Integer.parseInt(exist);
				String starId = "";
				String starName = "";
				String starDob = "";
				if (ex == 1)
				{
					String query = "SELECT * from stars as s, stars_in_movies as sim, movies as m where m.id = sim.movieId and sim.starId = s.id and s.name = ?";
					// Declare our statement
					PreparedStatement statement = dbcon.prepareStatement(query);
					// Set the parameter represented by "?" in the query to the id we get from url,
					// num 1 indicates the first "?" in the query
					statement.setString(1, name);
					// Perform the query
					ResultSet rs = statement.executeQuery();

					while (rs.next()) {

						starId = rs.getString("starId");
						starName = rs.getString("name");
						starDob = rs.getString("birthYear");

						String movieId = rs.getString("movieId");
						String movieTitle = rs.getString("title");
						String movieYear = rs.getString("year");
						String movieDirector = rs.getString("director");

						// Create a JsonObject based on the data we retrieve from rs

						JsonObject jsonObject = new JsonObject();
						jsonObject.addProperty("star_id", starId);
						jsonObject.addProperty("star_name", starName);
						jsonObject.addProperty("star_dob", starDob);
						jsonObject.addProperty("movie_id", movieId);
						jsonObject.addProperty("movie_title", movieTitle);
						jsonObject.addProperty("movie_year", movieYear);
						jsonObject.addProperty("movie_director", movieDirector);

						jsonArray.add(jsonObject);
					}
					rs.close();
					statement.close();
				}
				else if (ex == 0)
				{
					// Get MAX(ID) from stars
					String MAX_ID = "SELECT MAX(id) M_ID FROM stars";
					String NEW_ID = "";
					PreparedStatement ID_statement = dbcon.prepareStatement(MAX_ID);
					ResultSet ID_RS = ID_statement.executeQuery();
					int max = 0;
					while (ID_RS.next())
					{
						
						String LAST_ID = ID_RS.getString("M_ID");
						System.out.println(LAST_ID);
						max = Integer.parseInt(LAST_ID.replaceAll("[\\D]", ""));
						max = max + 1;
						// Get the new ID to Insert into new star
						NEW_ID = ("nm" + Integer.toString(max));
					}
					System.out.println(NEW_ID);
					String insertQuery = "INSERT INTO stars (id, name, birthYear) VALUES(?,?,?)";
					PreparedStatement insertStatement = dbcon.prepareStatement(insertQuery);
					insertStatement.setString(1, NEW_ID);
					insertStatement.setString(2, name);
					int star_year = Integer.parseInt(year);
					insertStatement.setInt(3, star_year);
					//System.out.printf("",NEW_ID,name,);
					insertStatement.executeUpdate();// adds the row to the database
					String new_query = "SELECT * FROM stars WHERE name = ?";
					PreparedStatement newStatement = dbcon.prepareStatement(new_query);
					newStatement.setString(1, name);
					ResultSet new_rs = newStatement.executeQuery(new_query);

					
					while (new_rs.next())
					{
						starId = new_rs.getString("id");
						starName = new_rs.getString("name");
						starDob = new_rs.getString("birthYear");
						
						JsonObject jsonObject = new JsonObject();
						jsonObject.addProperty("star_id", starId);
						jsonObject.addProperty("star_name", starName);
						jsonObject.addProperty("star_dob", starDob);
						
						jsonArray.add(jsonObject);
					}
					new_rs.close();
					insertStatement.close();
				}	
			}
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);


			dbcon.close();
		} catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();

	}

}
