import java.util.*;

public class Movie
{
	private String id;
	private String title;
	private String year;
	private ArrayList<String> directors;
	private ArrayList<String> genres;
	
	public Movie (String id, String title, String year, ArrayList<String> directors, ArrayList<String> genres)
	{
		this.id = id;
		this.title = title;
		this.year = year;
		this.directors = directors;
		this.genres = genres;
	}
	public Movie()
	{
		this("", "", "", new ArrayList<String>(), new ArrayList<String>());
	}
	public String getId()
	{
		return this.id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getTitle()
	{
		return this.title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getYear()
	{
		return this.year;
	}
	public void setYear (String year)
	{
		this.year = year;
	}
	public ArrayList<String> getDirector()
	{
		return this.directors;
	}
	public void setDirector (ArrayList<String> directors)
	{
		this.directors = directors;
	}
	public void addDirector(String director)
	{
		this.directors.add(director);
	}
	public ArrayList<String> getGenres()
	{
		return genres;
	}
	public void setGenres(ArrayList<String> genres)
	{
		this.genres = genres;
	}
	public void addGenre(String genre)
	{
		this.genres.add(genre);
	}
	public String toString()
	{
		StringBuffer str = new StringBuffer();
		str.append("Movie - ");
		str.append("ID:" + getId() + ",");
		str.append("Title:" + getTitle() + ",");
		str.append("Year:" + getYear() + ",");
		str.append("Director:{");
		for (int i = 0; i < directors.size(); i++)
		{
			str.append(directors.get(i));
			if (i != directors.size() - 1)
			{
				str.append(",");
			} 
		}
		str.append("}");
		str.append("Genres:{");
		for (int i = 0; i < genres.size(); i++)
		{
			str.append(genres.get(i));
			if (i != genres.size() - 1)
			{
				str.append(",");
			}
		}
		str.append("}");
		return str.toString();
	}
}
