import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.google.gson.JsonObject;
import com.mysql.jdbc.Connection;
import org.jasypt.util.password.StrongPasswordEncryptor;

public class LoginVerifyUtils {
    
    public static JsonObject verifyUsernamePassword(String username, String password) {
        // after recatpcha verfication, then verify username and password
    	String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?autoReconnect=true&useSSL=false";
        JsonObject responseJsonObject = new JsonObject();
        try
        {
        	Class.forName("com.mysql.jdbc.Driver").newInstance();
        	Connection connection = (Connection) DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        	String query = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            
            
            ResultSet rs = statement.executeQuery();
        	//verify password
            boolean success = false;
            if (rs.next()) {
    		    // get the encrypted password from the database
    			String encryptedPassword = rs.getString("password");
    			
    			// use the same encryptor to compare the user input password with encrypted password stored in DB
    			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
    		}
            
            if (success) {
                // login success:
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                return responseJsonObject;

            } else {
                // login fail
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "username/password is incorrect");
                return responseJsonObject;
            }
        }
        catch (Exception ex)
        {
        	ex.getMessage();
        	return responseJsonObject;
        }

    }

}
