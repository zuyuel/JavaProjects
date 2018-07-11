

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

/**
 * Servlet implementation class MetaData
 */
@WebServlet(name = "MetaData", urlPatterns = "/api/MetaData")
public class MetaData extends HttpServlet {
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
		PrintWriter out = response.getWriter();
		
		try
		{
			Connection dbcon = dataSource.getConnection();
			String table_query = "show tables";
			PreparedStatement show_table_statement = dbcon.prepareStatement(table_query);
			ResultSet rs = show_table_statement.executeQuery();
			
			JsonArray jsonArray = new JsonArray();
			
			
			while(rs.next())
			{
				String table = rs.getString("Tables_in_moviedb");
				
				//add table colum elements here
				String table_elements  = "show columns from "+ table;
				PreparedStatement show_table_element = dbcon.prepareStatement(table_elements);
				ResultSet element_rs = show_table_element.executeQuery();
				String elements = "";
				while(element_rs.next())
				{
					String Field = element_rs.getString("Field");
					String Type = element_rs.getString("Type");
					String Null = element_rs.getString("Null");
					String Key = element_rs.getString("Key");
					elements += Field+" ,"+Type+" , Null: "+Null+" ,"+Key+"| ";
					
				}
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("table_name",table);
				jsonObject.addProperty("elements", elements);
				jsonArray.add(jsonObject);
				
			}
			out.write(jsonArray.toString());
			rs.close();
			show_table_statement.close();
			dbcon.close();
		} 
		catch (Exception e) 
		{
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
