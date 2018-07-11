import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/*
 * We create a separate android login Servlet here because
 *   the recaptcha secret key for web and android are different.
 * 
 */
@WebServlet(name = "AndroidLoginServlet", urlPatterns = "/api/android-login")
public class AndroidLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AndroidLoginServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        
    	String username = request.getParameter("username");
        String password = request.getParameter("password");

        Map<String, String[]> map = request.getParameterMap();
        for (String key: map.keySet()) {
            System.out.println(map.get(key)[0]);
            System.out.println(key);
        }
        
        
        // then verify username / password
        JsonObject loginResult = LoginVerifyUtils.verifyUsernamePassword(username, password);
        
        if (loginResult.get("status").getAsString().equals("success")) {
            // login success
        	User u = new User();
            request.getSession().setAttribute("user", u);
            response.getWriter().write(loginResult.toString());
        } else {
            response.getWriter().write(loginResult.toString());
        }

    }

}
