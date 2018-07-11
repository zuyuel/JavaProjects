import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

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

/**
 * Servlet implementation class MovieList
 */
@WebServlet(name = "AndroidSearch" , urlPatterns = "/api/android-search")
public class AndroidSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
	
    public AndroidSearch() {
        super();
        // TODO Auto-generated constructor stub might have error
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NullPointerException {
	
		// Retrieve parameter id from url request.
		String search = request.getParameter("movie");
		String dropDown = request.getParameter("drop");
		PrintWriter out = response.getWriter();
        Map<String, String[]> map = request.getParameterMap();
        for (String key: map.keySet()) {
            System.out.println(map.get(key)[0]);
            System.out.println(key);
        }
//        JsonObject searchResult = AndroidExecuteSearch.executeSearch(movie, dropDown);
//        response.getWriter().write(searchResult.toString());
        
    	String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?autoReconnect=true&useSSL=false";
        try
        {
        	Class.forName("com.mysql.jdbc.Driver").newInstance();
        	Connection connection = (Connection) DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        	String query = "";
        	PreparedStatement statement = connection.prepareStatement(query);
        	if(dropDown.equals("title"))
        	{
        		query = "SELECT Q3.title, Q3.year, Q3.director, Q4.name as genres, Q3.name as stars, Q3.rating\n" + 
        			"FROM (SELECT Q1.id, Q1.title, Q1.year, Q1.director, Q2.name, Q1.rating\n" + 
        			"FROM (SELECT M.id, M.title, M.year, M.director, R.rating\n" + 
        			"FROM ratings R RIGHT OUTER JOIN movies M ON M.id = R.movieId\n" + 
        			"WHERE match (title) against (? in boolean mode) limit 50) Q1 LEFT OUTER JOIN (SELECT S.name, SIM.movieId\n" + 
        			"FROM (stars_in_movies SIM LEFT JOIN stars S ON SIM.starId = S.id)) Q2\n" + 
        			"ON Q1.id = Q2.movieId) Q3 LEFT JOIN (SELECT GIM.movieId, G.name FROM genres G\n" + 
        			"JOIN genres_in_movies GIM ON GIM.genreId = G.id) Q4 ON Q3.id = Q4.movieId;";
        		statement = connection.prepareStatement(query);
        		
        		String match = "";
            	String[] parts = search.split(" ");
            	for(String s: parts)
            	{
            		match += "+("+s+"*)";
            	}
        		
        		statement.setString(1, match);
        	}
        	else if (dropDown.equals("year"))
        	{
        		query = "SELECT Q3.title, Q3.year, Q3.director, Q4.name AS genres, Q3.name as stars, Q3.rating\n" + 
        				"FROM (SELECT Q1.id, Q1.title, Q1.year, Q1.director, Q2.name, Q1.rating\n" + 
        				"FROM (SELECT M.id, M.title, M.year, M.director, R.rating FROM ratings R\n" + 
        				"LEFT OUTER JOIN movies M on M.id = R.movieId\n" + 
        				"WHERE YEAR = ?) Q1 LEFT JOIN (SELECT S.name, SIM.movieId\n" + 
        				"FROM (stars_in_movies SIM LEFT JOIN stars S on SIM.starId = S.id)) Q2\n" + 
        				"ON Q1.id = Q2.movieId) Q3 LEFT JOIN (SELECT GIM.movieId, G.name\n" + 
        				"FROM genres G JOIN genres_in_movies GIM ON GIM.genreId = G.id) Q4\n" + 
        				"ON Q3.id = Q4.movieId;";
        		statement = connection.prepareStatement(query);
        		int movieYear = Integer.parseInt(search);
        		statement.setInt(1, movieYear);
        	}
        	else if (dropDown.equals("director"))
        	{
        		query = "SELECT Q3.title, Q3.year, Q3.director, Q4.name AS genres, Q3.name AS stars, Q3.rating\n" + 
        				"FROM (SELECT Q1.id, Q1.title, Q1.year, Q1.director, Q2.name, Q1.rating\n" + 
        				"FROM (SELECT M.id, M.title, M.year, M.director, R.rating FROM ratings R LEFT OUTER JOIN\n" + 
        				"movies M ON M.id = R.movieId WHERE director LIKE ?) Q1 LEFT JOIN\n" + 
        				"(SELECT S.name, SIM.movieId FROM (stars_in_movies SIM LEFT JOIN stars S ON SIM.starId = S.id))\n" + 
        				"Q2 ON Q1.id = Q2.movieId) Q3 LEFT JOIN (SELECT GIM.movieId, G.name FROM genres G\n" + 
        				"JOIN genres_in_movies GIM ON GIM.genreId = G.id) Q4 ON Q3.id = Q4.movieId;";
        		statement = connection.prepareStatement(query);
        		statement.setString(1, "%"+search+"%");
        	}
        	else
        	{
        		query = "SELECT RQ.title, RQ.year, RQ.director, RQ.genres, RQ.stars, RQ.rating FROM\n" + 
        				"(SELECT title FROM (SELECT movieId FROM ((SELECT * FROM STARS WHERE\n" + 
        				"NAME LIKE ?) S LEFT JOIN stars_in_movies SIM ON S.id = SIM.starId)) S\n" + 
        				"LEFT JOIN MOVIES M ON S.movieId = M.id) LQ LEFT JOIN \n" + 
        				"((SELECT Q3.title, Q3.year, Q3.director, Q4.name as genres, Q3.name as stars, Q3.rating \n" + 
        				"FROM (SELECT Q1.id, Q1.title, Q1.year, Q1.director, Q2.name, Q1.rating FROM\n" + 
        				"(SELECT M.id, M.title, M.year, M.director, R.rating FROM ratings R RIGHT OUTER JOIN\n" + 
        				"movies M on M.id = R.movieId) Q1 LEFT JOIN (SELECT S.name, SIM.movieId FROM (stars_in_movies\n" + 
        				"SIM LEFT JOIN stars S on SIM.starId = s.id)) Q2 ON Q1.id = Q2.movieId) Q3\n" + 
        				"LEFT JOIN (SELECT GIM.movieId, G.name FROM genres G \n" + 
        				"JOIN genres_in_movies GIM ON GIM.genreId = G.id) Q4 ON Q3.id = Q4.movieId)) RQ ON LQ.title = RQ.title;";
        		statement = connection.prepareStatement(query);
        		statement.setString(1, "%"+search+"%");
        	}
            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();
    		String genres = "";
    		String stars = "";
    		String previousTitle = "";
			String previousYear = "";
			String previousDirector = "";
			String previousRating = "";
        	while (rs.next()) {
            	String title = rs.getString("title");
    			String year = rs.getString("year");
    			String director = rs.getString("director");
    			String genre = rs.getString("genres");
    			String star = rs.getString("stars");
    			String rating = rs.getString("rating");
    			if(genre==null)
    			{
    				genre = "null";
    			}
    			if(rating=="null")
    			{
    				rating = "null";
    			}
    			System.out.println(title+","+year+","+director+","+genre+","+star+","+rating);//test
    			if(title==null)
    			{
    				rs.next();
    			}
    			if(previousTitle.compareTo("")==0) 
    			{
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
    					System.out.println("Does not contain genre");
    					System.out.println(genre);
    					genres += genre + ";";
    				}
    				if(!stars.contains(star))
    				{
    					System.out.println("Does not contain star");
    					System.out.println(star);
    					stars += star + ";";
    				}
    			}
    			else
    			{
    			JsonObject responseJsonObject = new JsonObject();
    			responseJsonObject.addProperty("title", previousTitle);
    			responseJsonObject.addProperty("year", previousYear);
    			responseJsonObject.addProperty("director", previousDirector);
    			responseJsonObject.addProperty("genre", genres);
                genres = "";
                responseJsonObject.addProperty("stars", stars);
                stars = "";
                responseJsonObject.addProperty("rating",previousRating);
                jsonArray.add(responseJsonObject);
                
                previousTitle = title;
    			previousYear = year;
    			previousDirector = director;
    			previousRating = rating;
    			}
            }
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("title", previousTitle);
			responseJsonObject.addProperty("year", previousYear);
			responseJsonObject.addProperty("director", previousDirector);
			responseJsonObject.addProperty("genre", genres);
            genres = "";
            responseJsonObject.addProperty("stars", stars);
            stars = "";
            responseJsonObject.addProperty("rating", previousRating);
            jsonArray.add(responseJsonObject);
           
            out.write(jsonArray.toString());
            
        	rs.close();
        	statement.close();
        	connection.close();
            
        }
        catch (Exception ex)
        {
        	JsonObject responseJsonObject = new JsonObject();
        	responseJsonObject.addProperty("errorMessage", ex.getMessage());
        	out.write(responseJsonObject.toString());
        }

    }
    
}
        

