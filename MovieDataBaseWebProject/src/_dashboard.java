

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import org.jasypt.util.password.StrongPasswordEncryptor;

/**
 * Servlet implementation class _dashboard
 */
@WebServlet(name = "_dashboard", urlPatterns = "/api/_dashboard")
public class _dashboard extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?autoReconnect=true&useSSL=false";
        response.setContentType("text/html"); 
        PrintWriter out = response.getWriter();
        
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            out.println("<html>");
            out.println("<head><title>Error</title></head>");
            out.println("<body>");
            out.println("<p>recaptcha verification error</p>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</body>");
            out.println("</html>");
            out.close();
            return;
        }
        
        try
        {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		Connection connection = (Connection) DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String query = "SELECT * FROM employees WHERE email = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            
            if (rs.next())
            {
            	String employee = rs.getString("email");
            	request.getSession().setAttribute("employee", employee);
            	JsonObject responseJsonObject = new JsonObject();
            	responseJsonObject.addProperty("status", "success");
            	responseJsonObject.addProperty("message", "success");
            	response.getWriter().write(responseJsonObject.toString());
            }
            else
            {
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "username/password is incorrect");
                response.getWriter().write(responseJsonObject.toString());
            }
            rs.close();
            statement.close();
            connection.close();
        }
        catch (Exception ex)
        {
        	out.println("<html><head><title>Error</title></head><body>SQL error: " + ex.getMessage() + "</body></html>");
        	return;
        }
        out.close();
	}

}
