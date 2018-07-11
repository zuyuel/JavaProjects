import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class XMLMain
{	
	static String loginUser = "mytestuser";
    static String loginPasswd = "mypassword";
    static String loginUrl = "jdbc:mysql://localhost:3306/moviedb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";

    // Check if the String is a number. This is useful for
    // movie year of the movie and birth year of the star.
    // returns true or false depending on if string is a number/Integer.
	public static boolean isNumber(String string) {
		if (string == null || string.isEmpty()) {
			return false;
		}
		int i = 0;
		if (string.charAt(0) == '-')
		{
			if (string.length() > 1)
			{
				i++;
			}
			else
			{
				return false;
			}
		}
		for (; i < string.length(); i++)
		{
			if (!Character.isDigit(string.charAt(i)))
			{
				return false;
			}
		}
		return true;
	}
	// Function written to add movies, genres, and genres_in_movies
	// from mains243.xml file. It takes a movieList which is just a 
	// list parsed from MovieParser. Then it returns a "Master List"
	// mapped with the movieIds (so we can link the according movieId
	// with the genres to the movies (genres_in_movies)
	public static HashMap<String, String> addMovies(HashMap<String, Movie> movieList,
			HashMap<String, String> moviesMasterList) throws SQLException,
			InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		// First we establish connection to mySQL
		ResultSet rs;
	    Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection connection = (Connection) DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
		Statement statement = connection.createStatement();
		
		// Creating/Initializing all necessary variables
		ArrayList<String> newGenres = new ArrayList<String>();
		HashMap<String, Integer> genreMasterList = new HashMap<String, Integer>();
		String STR_MovieID = "";
		int maxMovieID = 0;
		int maxGenreID = 0;
		try
		{
			// Keep track of the max/last ID from movies and genres.
			// This is executed once, changed into an Integer, and
			// incremented within the function, because it is 
			// very expensive to call it every time.
			rs = statement.executeQuery("SELECT MAX(ID) M FROM movies");
			while (rs.next())
			{
				STR_MovieID = rs.getString("M");
				maxMovieID = Integer.parseInt(STR_MovieID.replaceAll("[\\D]", ""));
			}
			rs = statement.executeQuery("SELECT MAX(ID) M FROM genres");
			while (rs.next())
			{
				maxGenreID = rs.getInt("M");
			}
			
			// Taking care of movie entries
			String InsertMoviesQ = "INSERT INTO movies (id, title, year, director) VALUES (?,?,?,?)";
			PreparedStatement InsertMoviesStatement = (PreparedStatement) connection.prepareStatement(InsertMoviesQ);
			String movieQuery = "SELECT ID from movies where title = ?";
			PreparedStatement movieQueryS = connection.prepareStatement(movieQuery);
			for (Movie m: movieList.values())
			{
				movieQueryS.setString(1, m.getTitle());
				rs = movieQueryS.executeQuery();
				if (!rs.first())
				{
					String title = m.getTitle();
					ArrayList<String> directors = m.getDirector();
					String dirName = "";
					String movieYear = m.getYear();
					int intYear = 0;
					maxMovieID = maxMovieID + 1;
					STR_MovieID = "tt0"+ Integer.toString(maxMovieID);
					// Make sure the year the movie was made is an Integer
					if (!isNumber(movieYear))
						intYear = 0;
					else
						intYear = Integer.parseInt(movieYear);
					// moviedb does not allow more than one director to be in
					// the film of the same id, so only the first director is saved
					if (directors.size() == 0)
						dirName = "";
					else if (directors.size() != 0)
						dirName = m.getDirector().get(0);					
					InsertMoviesStatement.setString(1, STR_MovieID);
					InsertMoviesStatement.setString(2, title);
					InsertMoviesStatement.setInt(3, intYear);
					InsertMoviesStatement.setString(4, dirName);
					moviesMasterList.put(m.getId(), STR_MovieID);				
					InsertMoviesStatement.executeUpdate();
					System.out.println(InsertMoviesStatement);	//print test
//					else if (directors.size() > 1)
//					{
//						String prevDirector = "";
//						String currDirector = "";
//						for (String d: directors)
//						{
//							currDirector = d;
//							System.out.println("current movie is: " + m.getTitle());
//							InsertMoviesStatement.setString(4, currDirector);
//							prevDirector = currDirector;
//							InsertMoviesStatement.executeUpdate();
//							System.out.println(InsertMoviesStatement);
//						}
//					}
				}
				else
				{
					String movieId = rs.getString(1);
					moviesMasterList.put(m.getId(), movieId);
				}
				// Taking care of genres
				ArrayList<String> genres = m.getGenres();
				String InsertGenresQ = "INSERT INTO genres (id, name) VALUES (?,?)";
				PreparedStatement InsertGenresStatement = (PreparedStatement) connection.prepareStatement(InsertGenresQ);
				String genreQuery = "SELECT id FROM genres WHERE name = ?";
				PreparedStatement genreQueryS = connection.prepareStatement(genreQuery);
				for (String g: genres)
				{
					g = g.replaceAll("\\s+","").toLowerCase();
					genreQueryS.setString(1, g);
					rs = genreQueryS.executeQuery();
					int genreID = 0;
					if (!rs.first())
					{
						if (!newGenres.contains(g) )
						{
							newGenres.add(g);
							maxGenreID = maxGenreID + 1;
							InsertGenresStatement.setInt(1, maxGenreID);
							InsertGenresStatement.setString(2, g);
							genreMasterList.put(g, maxGenreID);
							InsertGenresStatement.executeUpdate();
							System.out.println(InsertGenresStatement); //print test
						}
					}
					else 
					{
						genreID = rs.getInt(1);
						genreMasterList.put(g, genreID);
					}
					String InsertGIMQ = "INSERT INTO genres_in_movies (genreId, movieId) VALUES(?,?)";
					PreparedStatement InsertGIMStatement = (PreparedStatement) connection.prepareStatement(InsertGIMQ);
					String newGenreinMovie = moviesMasterList.get(m.getId());
					// Taking care of genres_in_movies
					for (String genre: genreMasterList.keySet())
					{
						if (genre.equals(g))
						{
							genreID = genreMasterList.get(genre);
							InsertGIMStatement.setInt(1, genreID);
							InsertGIMStatement.setString(2, newGenreinMovie);
							InsertGIMStatement.executeUpdate();
							System.out.println(InsertGIMStatement); //print test
						}
					}
				}
			}
			System.out.println("Done with parsing Movies!");
			System.out.println("Done with parsing Genres!");
			System.out.println("Done with parsing Genre in Movies!");	
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		connection.close();
		return moviesMasterList;
	}
	public static void addStars(HashMap<String, Star> starList, HashMap<String, String> newmoviesMasterList)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		// First we establish connection to mySQL
		ResultSet rs;
	    Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection connection = (Connection) DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
		Statement statement = connection.createStatement();
		String STR_StarID = "";
		String movieId;
		int maxStarID = 0;
		try
		{
			// Keep track of the max/last ID from stars.
			// This is executed once, changed into an Integer, and
			// incremented within the function, because it is 
			// very expensive to call it every time.
			rs = statement.executeQuery("SELECT MAX(ID) M FROM stars");
			while (rs.next())
			{
				STR_StarID = rs.getString("M");
				maxStarID = Integer.parseInt(STR_StarID.replaceAll("[\\D]", ""));
			}
			String InsertStarsQ = "INSERT INTO stars (id, name, birthYear) VALUES (?,?,?)";
			PreparedStatement InsertStarStatement = (PreparedStatement) connection.prepareStatement(InsertStarsQ);
			String starQuery = "SELECT id FROM stars WHERE name = ?";
			PreparedStatement starQueryS = (PreparedStatement) connection.prepareStatement(starQuery);
			// Taking care of stars
			for (Star s: starList.values())
			{
				starQueryS.setString(1, s.getStageName());
				rs = starQueryS.executeQuery();
				if (!rs.first())
				{
					maxStarID = maxStarID + 1;
					STR_StarID = "nm" + Integer.toString(maxStarID);
					String fullName = s.getStageName();
					String birthYear = s.getDOB();
					int intYear = 0;
					if (!isNumber(birthYear))
						intYear = 0;
					else
						intYear = Integer.parseInt(birthYear);				
					InsertStarStatement.setString(1, STR_StarID);
					InsertStarStatement.setString(2, fullName);
					InsertStarStatement.setInt(3, intYear);
					InsertStarStatement.executeUpdate();
					System.out.println(InsertStarStatement);	// print test			
				}
				else 
					STR_StarID = rs.getString(1);
				String InsertSIMQ = "INSERT INTO stars_in_movies(starId, movieId) VALUES (?,?)";
				PreparedStatement InsertSIMStatement = (PreparedStatement) connection.prepareStatement(InsertSIMQ);
				// Taking care of stars_in_movies
				for (String m: s.getMovies())
				{
					if (newmoviesMasterList.get(m) == null)
							continue;
					movieId = newmoviesMasterList.get(m);
					
					InsertSIMStatement.setString(1, STR_StarID);
					InsertSIMStatement.setString(2, movieId);
					InsertSIMStatement.executeUpdate();
					System.out.println(InsertSIMStatement); //print test
				}
			}
			System.out.println("Done with Stars!");
			System.out.println("Done with Stars in Movies!");
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		connection.close();
	}
	// Main, has a timer to time the duration of adding all of the entries
	// to mySQL. It runs all three parsers first, MovieParser, StarParser, CastParser.
	// Then it runs through addMovies and addStars to do their individual functions
	// to add all the entries into moviedb. Should take around 5 minutes to run.
	public static void main(String[] args) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		final long startTime = System.nanoTime();
		MovieParser MParser = new MovieParser();
		StarParser SParser = new StarParser();
		CastParser CParser = new CastParser();
		
		HashMap<String, Movie> movieList = MParser.parseDocument();
		HashMap<String, Star> starList = SParser.parseDocument();
		HashMap<String, Star> castList = CParser.parseDocument(starList);
		HashMap<String, String> moviesMasterList = new HashMap<String, String>();
		HashMap<String, String> newmoviesMasterList = new HashMap<String, String>();
		newmoviesMasterList = addMovies(movieList, moviesMasterList);
		addStars(castList, newmoviesMasterList);
		
		final long duration = System.nanoTime() - startTime;
		double seconds = (double)duration / 1000000000.0;
		System.out.println("Done, check MySQL for all the entries.");
		System.out.println("XML Parsing total duration: " + seconds + " seconds");
	}
}
