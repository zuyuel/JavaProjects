import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasypt.util.password.StrongPasswordEncryptor;

import com.google.gson.JsonObject;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

//
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
//    public void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws IOException, ServletException {
//		String loginUser = "mytestuser";
//        String loginPasswd = "mypassword";
//        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?autoReconnect=true&useSSL=false";
//        String username = null;
//        response.setContentType("text/html");
//        try {
//        	Class.forName("com.mysql.jdbc.Driver").newInstance();
//        	Connection dbcon = (Connection) DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
//        	Statement statement = (Statement) dbcon.createStatement();
//        }
//        catch (Exception ex)
//        {
//        	System.out.println(ex.getMessage());
//        }
//        PrintWriter out = response.getWriter();
//        out.close();
//    }
        
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	
    	String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?autoReconnect=true&useSSL=false";
        response.setContentType("text/html"); 
        PrintWriter out = response.getWriter();
        
        //verifies recaptcha
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

    		HttpSession session = request.getSession();
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String query = "SELECT * FROM customers WHERE email = ?";
          
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            
            // Check if the password matches the encrypted password
            boolean success = false;
    		if (rs.next()) {
    		    // get the encrypted password from the database
    			String encryptedPassword = rs.getString("password");
    			
    			// use the same encryptor to compare the user input password with encrypted password stored in DB
    			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
    		}
            
            if (success)
            {
            	String id = rs.getString("id");
            	String firstName = rs.getString("firstName");
            	String lastName = rs.getString("lastName");
            	String address = rs.getString("address");
            	String ccId = rs.getString("ccId");
            	String email = rs.getString("email");
            	User u = new User();
            	request.getSession().setAttribute("user", u);
            	request.getSession().setAttribute("email", email);
            	request.getSession().setAttribute("customerId", id);
            	request.getSession().setAttribute("firstName", firstName);
            	request.getSession().setAttribute("lastName", lastName);
            	request.getSession().setAttribute("address", address);
            	request.getSession().setAttribute("ccId", ccId);
            	
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
