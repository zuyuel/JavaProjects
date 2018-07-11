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
 * Servlet implementation class MovieList
 */
@WebServlet(name = "SingleMoviePage" , urlPatterns = "/api/single-movie")
public class SingleMoviePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    //Create a datasource registed in web.xml
	@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
	
    public SingleMoviePage() {
        super();
        // TODO Auto-generated constructor stub might have error
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String pattern = request.getParameter("movie");
        PrintWriter out = response.getWriter();
        System.out.println("patter");
        System.out.println(pattern);
        try {

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            
            // TODO change query to do matching string
            System.out.printf("Movie is %s\n",pattern);
            String query = "select q3.id, q3.title, q3.year, q3.director,  q4.name as genres, q3.name as stars, q3.rating from (select q1.id,q1.title,q1.year,q1.director, q2.name, q1.rating  from\r\n" + 
    				"(select m.id, m.title, m.year, m.director, r.rating from ratings r left join movies m on m.id=r.movieId\r\n" + 
    				"where title = ? order by rating desc limit 20) q1 left join (select s.name, sim.movieID from (stars_in_movies sim left join stars s  on sim.starID = s.id)) q2 on q1.id = q2.movieID)\r\n" + 
    				"q3 left join (SELECT gm.movieId, g.name FROM genres g\r\n" + 
    				"JOIN genres_in_movies gm ON gm.genreId = g.id) q4 on q3.id = q4.movieId";

            // Perform the query
            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1, pattern);
            ResultSet rs = statement.executeQuery();
            System.out.println("Prepare Statement worked!");
            JsonArray jsonArray = new JsonArray();
            // TODO merge all genres and stars
            //keep track of the previous movie information
    		String genres = "";
    		String stars = "";
    		String previousID = "";
    		String previousTitle = "";
			String previousYear = "";
			String previousDirector = "";
			String previousRating = "";
            
            // Iterate through each row of rs
            while (rs.next()) 
            {
            	String id = rs.getString("id");
            	String title = rs.getString("title");
    			String year = rs.getString("year");
    			String director = rs.getString("director");
    			String genre = rs.getString("genres");
    			String star = rs.getString("stars");
    			String rating = rs.getString("rating");

    			if(previousTitle.compareTo("")==0)
    			{
    				previousID = id;
    				previousTitle = title;
    				previousYear = year;
    				previousDirector = director;
    				genres += genre + ";";
    				stars += star + ";";
    				previousRating = rating;
    			}
    			else if(title.compareTo(previousTitle) == 0)
    			{
    				if(!genres.contains(genre))
    				{
    					genres += genre + ";";
    				}
    				if(!stars.contains(star))
    				{
    					stars += star + ";";
    				}
    			}
    			else
    			{
                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", previousID);
                jsonObject.addProperty("title", previousTitle);
                jsonObject.addProperty("year", previousYear);
                jsonObject.addProperty("director", previousDirector);
                jsonObject.addProperty("genre", genres);
                genres = "";
                jsonObject.addProperty("stars", stars);
                stars = "";
                jsonObject.addProperty("rating",previousRating);
                jsonArray.add(jsonObject);
                
                previousID = id;
                previousTitle = title;
    			previousYear = year;
    			previousDirector = director;
    			previousRating = rating;
    			}
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", previousID);
            jsonObject.addProperty("title", previousTitle);
            jsonObject.addProperty("year", previousYear);
            jsonObject.addProperty("director", previousDirector);
            jsonObject.addProperty("genre", genres);
            genres = "";
            jsonObject.addProperty("stars", stars);
            stars = "";
            jsonObject.addProperty("rating",previousRating);
            jsonArray.add(jsonObject);
            // write JSON string to output
            out.write(jsonArray.toString());
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
