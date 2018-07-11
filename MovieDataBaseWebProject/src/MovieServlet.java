import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
/**
 * Servlet implementation class MovieServlet
 */
@WebServlet("/MovieServlet")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MovieServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		
        // set response mime type
        response.setContentType("text/html"); 

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<style>");
        out.println("body {\r\n" + 
        		"    background-color: lightblue;\r\n" + 
        		"}");
        out.println("td {\r\n" + 
        		"    text align: center;\r\n" + 
        		"}");
        out.println("td.thick {font-weight: bold;}\n");
        out.println("td {text-align: center;}\n");
        out.println("table {align: center}\n");
        out.println("</style>");
        out.println("<title>Movie Search</title>");
        out.println("</head>");
        
        try {
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
        		// create database connection
        		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		// declare statement
        		Statement statement = connection.createStatement();
        		
        		// TODO match it according to the string
        		String pattern  = request.getParameter("movie");
        		
        		
        		//Old query
        		/*String query = "select q3.title, q3.year, q3.director,  q4.name as genres, q3.name as stars, q3.rating from (select q1.id,q1.title,q1.year,q1.director, q2.name, q1.rating  from\r\n" + 
        				"(select m.id, m.title, m.year, m.director, r.rating from ratings r left join movies m on m.id=r.movieId\r\n" + 
        				"order by rating desc limit 20) q1 left join (select s.name, sim.movieID from (stars_in_movies sim left join stars s  on sim.starID = s.id)) q2 on q1.id = q2.movieID)\r\n" + 
        				"q3 left join (SELECT gm.movieId, g.name FROM genres g\r\n" + 
        				"JOIN genres_in_movies gm ON gm.genreId = g.id) q4 on q3.id = q4.movieId";
				*/
        		// prepare query
        		String query = "select q3.title, q3.year, q3.director,  q4.name as genres, q3.name as stars, q3.rating from (select q1.id,q1.title,q1.year,q1.director, q2.name, q1.rating  from\r\n" + 
        				"(select m.id, m.title, m.year, m.director, r.rating from ratings r left join movies m on m.id=r.movieId\r\n" + 
        				"where title like '%"+pattern+"%' or director like '%"+pattern+"%' order by rating desc limit 20) q1 left join (select s.name, sim.movieID from (stars_in_movies sim left join stars s  on sim.starID = s.id)) q2 on q1.id = q2.movieID)\r\n" + 
        				"q3 left join (SELECT gm.movieId, g.name FROM genres g\r\n" + 
        				"JOIN genres_in_movies gm ON gm.genreId = g.id) q4 on q3.id = q4.movieId";
        		// execute query
        		ResultSet resultSet = statement.executeQuery(query);

        		out.println("<body>");
        		out.println("<h1 align = \"center\">Search Results</h1>");
        		
        		out.println("<table border align=\"center\">");
        		
        		// add table header row
        		out.println("<tr>");
        		out.println("<td>Title</td>");
        		out.println("<td>Year</td>");
        		out.println("<td>Director</td>");
        		out.println("<td>Genres</td>");
        		out.println("<td>Stars</td>");
        		out.println("<td>Rating</td>");
        		out.println("</tr>");
        		
        		//keep track of the previous movie information
        		ArrayList<String> genres = new ArrayList<String>();
        		ArrayList<String> stars = new ArrayList<String>();
        		String previousTitle = "";
    			String previousYear = "";
    			String previousDirector = "";
    			String previousRating = "";
        		// add a row for every star result
    			
        		while (resultSet.next()) {
        			// get a star from result set
        			String title = resultSet.getString("title");
        			String year = resultSet.getString("year");
        			String director = resultSet.getString("director");
        			String genre = resultSet.getString("genres");
        			String star = resultSet.getString("stars");
        			String rating = resultSet.getString("rating");
        			
        			if(previousTitle.compareTo("")==0)
        			{
        				previousTitle = title;
        				previousYear = year;
        				previousDirector = director;
        				genres.add(genre);
        				stars.add(star);
        				previousRating = rating;
        			}
        			else if(title.compareTo(previousTitle) == 0)
        			{
        				if(!genres.contains(genre))
        				{
        					genres.add(genre);
        				}
        				if(!stars.contains(star))
        				{
        					stars.add(star);
        				}
        			}
        			else
        			{
        				out.println("<tr>");
            			out.println("<td class = \"thick\">" + previousTitle + "</td>");
            			out.println("<td>" + previousYear + "</td>");
            			out.println("<td>" + previousDirector + "</td>");
            			
            			out.println("<td>"); //change later //TODO show genre list
            			out.println("<ul>");
            			for(int i=0;i<genres.size();i++)
            				out.println("<li>"+genres.get(i)+"</li>");
            			out.println("</ul>");
            			out.println("</td>");
            			genres.clear();
            			
            			out.println("<td>"); //show stars list
            			out.println("<ul>");
            			for(int i=0;i<stars.size();i++)
            				out.println("<li>"+stars.get(i)+"</li>");
            			out.println("</ul>");
            			out.println("</td>");
            			stars.clear(); 
            			
            			out.println("<td>" + previousRating + "</td>");
            			out.println("</tr>");
        				
        				//add current query info into previous
            			previousTitle = title;
            			previousYear = year;
            			previousDirector = director;
            			previousRating = rating;
        			}
        			
        		}
        		// TODO add last table row here
        		out.println("<tr>");
    			out.println("<td class = \"thick\">" + previousTitle + "</td>");
    			out.println("<td>" + previousYear + "</td>");
    			out.println("<td>" + previousDirector + "</td>");
    			
    			out.println("<td>"); //change later //TODO show genre list
    			out.println("<ul>");
    			for(int i=0;i<genres.size();i++)
    				out.println("<li>"+genres.get(i)+"</li>");
    			out.println("</ul>");
    			out.println("</td>");
    			genres.clear();
    			
    			out.println("<td>"); //show stars list
    			out.println("<ul>");
    			for(int i=0;i<stars.size();i++)
    				out.println("<li>"+stars.get(i)+"</li>");
    			out.println("</ul>");
    			out.println("</td>");
    			stars.clear(); 
    			
    			out.println("<td>" + previousRating + "</td>");
    			out.println("</tr>");
        		
        		
        		out.println("</table>");
        		out.println("</body>");
        		
        		resultSet.close();
        		statement.close();
        		connection.close();
        		
        } catch (Exception e) {
        		/*
        		 * After you deploy the WAR file through tomcat manager webpage,
        		 *   there's no console to see the print messages.
        		 * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
        		 * 
        		 * To view the last n lines (for example, 100 lines) of messages you can use:
        		 *   tail -100 catalina.out
        		 * This can help you debug your program after deploying it on AWS.
        		 */
        		e.printStackTrace();
        		
        		out.println("<body>");
        		out.println("<p>");
        		out.println("Exception in doGet: " + e.getMessage());
        		out.println("</p>");
        		out.print("</body>");
        }
        
        out.println("</html>");
        out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}