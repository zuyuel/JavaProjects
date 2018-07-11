

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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Servlet implementation class AddMovie
 */
@WebServlet(name = "AddMovie", urlPatterns = "/api/AddMovie")
public class AddMovie extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("application/json");
		//gets all the parameters from add-movie form
		String title = request.getParameter("title");
		String year = request.getParameter("year");
		String director = request.getParameter("director");
		String star = request.getParameter("star");
		String genre = request.getParameter("genre");
		String rating = request.getParameter("rating");
		int int_year = Integer.parseInt(year);
		
		PrintWriter out = response.getWriter();
		
		try
		{
			Connection dbcon = dataSource.getConnection();
			//check if movie exists in database
			String movie_exist = "SELECT EXISTS(SELECT * FROM movies WHERE title = ? and year = ? and director = ?) AS EXIST";
			PreparedStatement movie_statement = dbcon.prepareStatement(movie_exist);
			movie_statement.setString(1, title);
			movie_statement.setInt(2, int_year);
			movie_statement.setString(3, director);
			ResultSet movie_rs = movie_statement.executeQuery();
			System.out.printf("title: %s, year: %s, director: %s, star: %s, genre: %s, rating: %s\n",title,year,director,star,genre,rating); //test statements
			
			
			JsonObject responseJsonObject = new JsonObject();
			while(movie_rs.next())
			{
				String exist = movie_rs.getString("EXIST");
				int exist_int = Integer.parseInt(exist);
				if(exist_int==1)
				{
					System.out.println("Movie exists");//print exist value
					// display error message because it exists
					responseJsonObject.addProperty("exist", "success");
					responseJsonObject.addProperty("message", "This Movie Already Exists");
					out.write(responseJsonObject.toString());
				}
				else
				{
					System.out.println("Movie does not exists");//print exist value
					//add it to the database
					String Insert_Movie = "{call add_movie(?,?,?,?,?,?)}";
					CallableStatement call_statement = dbcon.prepareCall(Insert_Movie);
					call_statement.setString(1,title);
					call_statement.setInt(2,int_year);
					call_statement.setString(3,director);
					call_statement.setString(4,star);
					call_statement.setString(5,genre);
					float float_rating = Float.parseFloat(rating);
					call_statement.setFloat(6,float_rating);
					System.out.printf("title: %s, year: %d, director: %s, star: %s, genre: %s, rating: %f\n",title,int_year,director,star,genre,float_rating);//print all the call values
					call_statement.executeUpdate(); //calls the stored function
					responseJsonObject.addProperty("exist", "fail");
					responseJsonObject.addProperty("message", "Movie Added");
					out.write(responseJsonObject.toString());
					//close call_statement?
					call_statement.close();
					System.out.println("Call Statement closed"); //TODO  delete test print call statement closed
				}
			}
			
			//close all sets and connections
			movie_rs.close();
			movie_statement.close();
			dbcon.close();
			System.out.println("All connections are closed");
		}
		catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

		// set response status to 500 (Internal Server Error)
		response.setStatus(500);
		}
		System.out.println("Function Ended");
	}



}
