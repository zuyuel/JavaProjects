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

import java.io.File;
import java.io.FileOutputStream;


/**
 * Servlet implementation class MovieList
 */
@WebServlet(name = "MovieList" , urlPatterns = "/api/MovieList")
public class MovieList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    //Create a datasource registed in web.xml
	@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
	
    public MovieList() {
        super();
        // TODO Auto-generated constructor stub might have error
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long startTime_total = System.nanoTime(); 
		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String movie = request.getParameter("movie");
		String dropDown = request.getParameter("drop");
		String Genre = request.getParameter("genre");
		String alphabet = request.getParameter("alphabet");
		String numRecords = request.getParameter("numRecords");
		String firstRecord = request.getParameter("firstRecord");
		if(numRecords.equals("null"))
				numRecords = "10";
		if(firstRecord.equals("null"))
			firstRecord = "0";
		System.out.println("movie is: "+movie); //test
		System.out.println("numRecords is: "+numRecords); //test
		System.out.println("drop is: "+dropDown); //test
		System.out.println("Genre is: "+Genre); 
		System.out.println("alpahbet is: "+alphabet); 
		System.out.println("firstRecord is: "+firstRecord); 
		
        PrintWriter out = response.getWriter();

        try {
        	long startTime = System.nanoTime(); 
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            
            
            String query="";
            PreparedStatement statement = dbcon.prepareStatement(query);
            if(!Genre.equals("null"))
            {
            	System.out.println("Genre");//test
            	// TODO change query for pagination
            	query = "select rightquery.title, year, director,genres,stars,rating from (select title from ((select gm.movieID from (select * from genres where name = ?) g left join\r\n" +  
            			"(select * from genres_in_movies) gm\r\n" + 
            			"on g.id=gm.genreID limit ? offset ?) gim left join \r\n" + // TODO change a limit here
            			"(select * from movies) m on m.id=gim.movieID)) leftquery\r\n" + 
            			"inner join\r\n" + 
            			"((select q3.title, q3.year, q3.director,  q4.name as genres, q3.name as stars, q3.rating from (select q1.id,q1.title,q1.year,q1.director, q2.name, q1.rating  from\r\n" +  
            			"(select m.id, m.title, m.year, m.director, r.rating from ratings r left join movies m on m.id=r.movieId\r\n" + 
            			"order by rating desc) q1 left join (select s.name, sim.movieID from (stars_in_movies sim left join stars s  on sim.starID = s.id)) q2 on q1.id = q2.movieID)\r\n" + 
            			"q3 left join (SELECT gm.movieId, g.name FROM genres g\r\n" + 
            			"JOIN genres_in_movies gm ON gm.genreId = g.id) q4 on q3.id = q4.movieId) ) rightquery on leftquery.title=rightquery.title \r\n"; //TODO change limit by pagination
            	
            	statement = dbcon.prepareStatement(query);
            	statement.setString(1, Genre);
            	int limit = Integer.parseInt(numRecords);
            	statement.setInt(2, limit);
            	int offset = Integer.parseInt(firstRecord);
            	statement.setInt(3, offset);
            }
            else if(!alphabet.equals("null"))
            {
            	
            	//System.out.println("alphabet");//test
            	query = "select * from ((select q3.title, q3.year, q3.director,  q4.name as genres, q3.name as stars, q3.rating from (select q1.id,q1.title,q1.year,q1.director, q2.name, q1.rating  from\r\n" + 
            			"(select m.id, m.title, m.year, m.director, r.rating from ratings r right join (select * from movies where title like ? limit ? offset ?) m on m.id=r.movieId\r\n" + 
            			"order by rating desc) q1 left join (select s.name, sim.movieID from (stars_in_movies sim left join stars s  on sim.starID = s.id)) q2 on q1.id = q2.movieID)\r\n" + 
            			"q3 left join (SELECT gm.movieId, g.name FROM genres g\r\n" + 
            			"JOIN genres_in_movies gm ON gm.genreId = g.id) q4 on q3.id = q4.movieId) ) as q";
            	statement = dbcon.prepareStatement(query);
            	statement.setString(1, alphabet+"%");
            	int limit = Integer.parseInt(numRecords);
            	statement.setInt(2, limit);
            	int offset = Integer.parseInt(firstRecord);
            	statement.setInt(3, offset);
            }
            else
            {
            	//System.out.println("movie");//test
            	// TODO change query to match either title, genre, year, or stars pattern
            	// TODO change query to do pagination
            	if(dropDown.equals("title"))
            	{
            		//System.out.println("title");//test
	            	query = "select q3.title, q3.year, q3.director,  q4.name as genres, q3.name as stars, q3.rating from (select q1.id,q1.title,q1.year,q1.director, q2.name, q1.rating  from\r\n" + 
	    				"(select m.id, m.title, m.year, m.director, r.rating from ratings r right outer join movies m on m.id=r.movieId\r\n" + 
	    				"where match (title) against (? in boolean mode) limit ? offset ?) q1 left outer join (select s.name, sim.movieID from (stars_in_movies sim left join stars s  on sim.starID = s.id)) q2 on q1.id = q2.movieID)\r\n" + 
	    				"q3 left join (SELECT gm.movieId, g.name FROM genres g\r\n" + 
	    				"JOIN genres_in_movies gm ON gm.genreId = g.id) q4 on q3.id = q4.movieId"; // left join on second line
	            	statement = dbcon.prepareStatement(query);
	            	String match = "";
	            	String[] parts = movie.split(" ");
	            	for(String s: parts)
	            	{
	            		match += "+("+s+"*)";
	            	}
	            	//String final_match = match.substring(0, match.length()-1);
	            	System.out.printf("title is: %s\n",match);
	            	statement.setString(1, match);
	            	int limit = Integer.parseInt(numRecords);
	            	statement.setInt(2, limit);
	            	int offset = Integer.parseInt(firstRecord);
	            	statement.setInt(3, offset);
            	}
            	else if (dropDown.equals("year"))
            	{
            		//System.out.println("year");//test
            		query = "select q3.title, q3.year, q3.director,  q4.name as genres, q3.name as stars, q3.rating from (select q1.id,q1.title,q1.year,q1.director, q2.name, q1.rating  from\r\n" + 
    	    				"(select m.id, m.title, m.year, m.director, r.rating from ratings r left outer join movies m on m.id=r.movieId\r\n" + 
    	    				"where year=? order by rating desc limit ? offset ?) q1 left join (select s.name, sim.movieID from (stars_in_movies sim left join stars s  on sim.starID = s.id)) q2 on q1.id = q2.movieID)\r\n" + 
    	    				"q3 left join (SELECT gm.movieId, g.name FROM genres g\r\n" + 
    	    				"JOIN genres_in_movies gm ON gm.genreId = g.id) q4 on q3.id = q4.movieId"; // was left join on ratings and movies
            		statement = dbcon.prepareStatement(query); // left join on second line
            		int movieYear = Integer.parseInt(movie); 
                	statement.setInt(1, movieYear);
                	int limit = Integer.parseInt(numRecords);
                	statement.setInt(2, limit);
                	int offset = Integer.parseInt(firstRecord);
                	statement.setInt(3, offset);
            	}
            	else if(dropDown.equals("director"))
            	{
            		//System.out.println("director");//test
	            	query = "select q3.title, q3.year, q3.director,  q4.name as genres, q3.name as stars, q3.rating from (select q1.id,q1.title,q1.year,q1.director, q2.name, q1.rating  from\r\n" + 
	    				"(select m.id, m.title, m.year, m.director, r.rating from ratings r left outer join movies m on m.id=r.movieId\r\n" + 
	    				"where director LIKE ? order by rating desc limit ? offset ?) q1 left join (select s.name, sim.movieID from (stars_in_movies sim left join stars s  on sim.starID = s.id)) q2 on q1.id = q2.movieID)\r\n" + 
	    				"q3 left join (SELECT gm.movieId, g.name FROM genres g\r\n" + 
	    				"JOIN genres_in_movies gm ON gm.genreId = g.id) q4 on q3.id = q4.movieId"; // was left join on ratings and movies
	            	statement = dbcon.prepareStatement(query); // left join on line 2
	            	statement.setString(1, "%"+movie+"%");
	            	int limit = Integer.parseInt(numRecords);
	            	statement.setInt(2, limit);
	            	int offset = Integer.parseInt(firstRecord);
	            	statement.setInt(3, offset);
            	}
            	else
            	{
            		//System.out.println("star");//test
            		query = "select rq.title, rq.year, rq.director,rq.genres, rq.stars, rq.rating from  \r\n" + 
            				"(select title from\r\n" + 
            				"(select movieID from ((select * from stars where name like ?) s left join stars_in_movies sim on s.id=sim.starID ) limit ? offset ?) s\r\n" + 
            				"left join\r\n" + 
            				"movies m\r\n" + 
            				"on s.movieID=m.id) lq\r\n" + 
            				"left join\r\n" + 
            				"((select q3.title, q3.year, q3.director,  q4.name as genres, q3.name as stars, q3.rating from (select q1.id,q1.title,q1.year,q1.director, q2.name, q1.rating  from\r\n" + 
            				"(select m.id, m.title, m.year, m.director, r.rating from ratings r right outer join movies m on m.id=r.movieId\r\n" + 
            				") q1 left join (select s.name, sim.movieID from (stars_in_movies sim left join stars s  on sim.starID = s.id)) q2 on q1.id = q2.movieID)\r\n" + 
            				"q3 left join (SELECT gm.movieId, g.name FROM genres g\r\n" + 
            				"JOIN genres_in_movies gm ON gm.genreId = g.id) q4 on q3.id = q4.movieId) ) rq on lq.title = rq.title"; // was left join on ratings and movies
            		//System.out.println("query= "+query); //left join on line 9
            		statement = dbcon.prepareStatement(query);
                	statement.setString(1, "%"+movie+"%");
                	int limit = Integer.parseInt(numRecords);
                	statement.setInt(2, limit);
                	int offset = Integer.parseInt(firstRecord);
                	statement.setInt(3, offset);
            	}
            }
            // Perform the query
            //System.out.println("query");//test
            ResultSet rs = statement.executeQuery();
            //System.out.println("1"); //test
            JsonArray jsonArray = new JsonArray();
            // TODO merge all genres and stars
            //keep track of the previous movie information
    		String genres = "";
    		String stars = "";
    		String previousTitle = "";
			String previousYear = "";
			String previousDirector = "";
			String previousRating = "";
            
            // Iterate through each row of rs
            while (rs.next()) 
            {

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
    			
    			if(title==null)// TODO change or delete if not working test 
    			{
    				System.out.println("Title is null");
    				System.out.println("1 title");
    				rs.next();
    			}
    			if(previousTitle.compareTo("")==0) 
    			{
    				System.out.println("Title 2");
    				previousTitle = title;
    				previousYear = year;
    				previousDirector = director;
    				genres += genre + ";";
    				stars += star + ";";
    				previousRating = rating;
    				System.out.println("Finished Title 2");
    			}
    			else if(title.compareTo(previousTitle) == 0)
    			{
    				// print test genre
    				System.out.println("Title 3");
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
    				System.out.println("Finished Title 3");
    			}
    			else
    			{
                // Create a JsonObject based on the data we retrieve from rs
    			System.out.printf("Adding to JSON 1 Title: %s, year: %s, director: %s, genre: %s, stars: %s, rating: %s\n",previousTitle,previousYear,previousDirector,genres,stars,previousRating);	//Print json elements
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("title", previousTitle);
                jsonObject.addProperty("year", previousYear);
                jsonObject.addProperty("director", previousDirector);
                jsonObject.addProperty("genre", genres);
                genres = "";
                jsonObject.addProperty("stars", stars);
                stars = "";
                jsonObject.addProperty("rating",previousRating);
                jsonArray.add(jsonObject);
                
                previousTitle = title;
    			previousYear = year;
    			previousDirector = director;
    			previousRating = rating;
    			System.out.println("Finished adding to jsonObject"); //test json
    			}
            }
            System.out.printf("Adding to JSON 2 Title: %s, year: %s, director: %s, genre: %s, stars: %s, rating: %s\n",previousTitle,previousYear,previousDirector,genres,stars,previousRating);	//Print json elements
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("title", previousTitle);
            jsonObject.addProperty("year", previousYear);
            jsonObject.addProperty("director", previousDirector);
            jsonObject.addProperty("genre", genres);
            genres = "";
            jsonObject.addProperty("stars", stars);
            stars = "";
            jsonObject.addProperty("rating",previousRating);
            jsonArray.add(jsonObject);
            System.out.println("Finished adding to jsonObject"); //test json
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
            //here
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            long elapsedTime_total = endTime- startTime_total;
            
            System.out.println("TJ "+elapsedTime);
            System.out.println("TS "+elapsedTime_total);
            
            
            File f = new File("log.txt");
            PrintWriter writer = null;
            if ( f.exists() && !f.isDirectory() ) {
                writer = new PrintWriter(new FileOutputStream(new File("log.txt"), true));
            }
            else {
                writer = new PrintWriter("log.txt");
            }
            writer.println("TJ "+elapsedTime+"\n");
            writer.println("TS "+elapsedTime_total+"\n");
           
            writer.close();
            //end here
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	*/
}
