/**
 * This User class only has the username field in this example.
 * <p>
 * However, in the real project, this User class can contain many more things,
 * for example, the user's shopping cart items.
 */
public class User {
    private int id = -1;
    private String firstName = "";
    private String lastName = "";
    private String address = "";
    private String email = "";
    
    public User()
    {
    	this.id = id;
    	this.firstName = "";
    	this.lastName = "";
    	this.address = "";
    	this.email = "";
    }
    public User(int id, String firstName, String lastName, String address, String email) 
    {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
    }
    public int getId()
    {
    	return this.id;
    }
    public void setId(int id)
    {
    	this.id = id;
    }
    public String getfirstName()
    {
    	return this.firstName;
    }
    public void setfirstName(String firstName)
    {
    	this.firstName = firstName;
    }
    public String getlastName()
    {
    	return this.lastName;
    }
    public void setlastName(String lastName)
    {
    	this.lastName = lastName;
    }
    public String getAddress()
    {
    	return this.address;
    }
    public void setAddress(String address)
    {
    	this.address = address;
    }
    public String getEmail()
    {
    	return this.email;
    }
    public void setEmail(String email)
    {
    	this.email = email;
    }
    

}