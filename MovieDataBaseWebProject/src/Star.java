import java.util.*;

public class Star
{
	private String id;
	private String birthYear; // <dob>
	private ArrayList<String> movies;
	private String stageName;
	
	public Star(String id, String stageName, String birthYear, ArrayList<String> movies)
	{
		this.id = id;
		this.birthYear = birthYear;
		this.movies = movies;
	}
	
	public Star()
	{
		this("", "", "", new ArrayList<String>());
	}
	public String getId()
	{
		return this.id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getStageName()
	{
		return this.stageName;
	}
	public void setStageName(String stageName)
	{
		this.stageName = stageName;
	}
	public String getDOB()
	{
		return this.birthYear;
	}
	public void setDOB(String birthYear)
	{
		this.birthYear = birthYear;
	}
	public ArrayList<String> getMovies()
	{
		return this.movies;
	}
	public void setMovies(ArrayList<String> movies)
	{
		this.movies = movies;
	}
	public void addMovie(String movieId)
	{ 
		movies.add(movieId);
	}
	public String toString()
	{
		StringBuffer str = new StringBuffer();
		str.append("Star - ");
		str.append("ID:" + getId() + ",");
		str.append("Name:" + getStageName() + ",");
		str.append("birthYear:" + getDOB() + ",");
		str.append("Movies:{");
		for (int i = 0; i < movies.size(); i++)
		{
			str.append(movies.get(i));
			if (i != movies.size() - 1)
				str.append(",");
		}
		str.append("}");
		return str.toString();
	}
}
