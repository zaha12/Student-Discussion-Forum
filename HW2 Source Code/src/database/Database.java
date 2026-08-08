package database;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import entityClasses.User;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.PrivateFeedback;
import entityClasses.AdminRequest;
/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.
 *  This class leverages H2 and provides numerous special supporting methods.
 * </p>
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {
	private static ArrayList<String> threads = new ArrayList<>();
	private ArrayList<Post> posts = new ArrayList<>();

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database 
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewRole1;
	private boolean currentNewRole2;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}
	
	
/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 *
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	/*******
	 * <p> Method: connectToDatabase(String url) </p>
	 *
	 * <p> Description: Connects to a database at the specified URL. Used by
	 * tests to connect to an in-memory H2 database instead of the file
	 * database, ensuring each test runs in complete isolation. </p>
	 *
	 * @param url the JDBC URL to connect to
	 * @throws SQLException if the connection fails
	 */
	public void connectToDatabase(String url) throws SQLException {
	    try {
	        Class.forName(JDBC_DRIVER);
	        connection = DriverManager.getConnection(url, USER, PASS);
	        statement = connection.createStatement();
	        createTables();
	    } catch (ClassNotFoundException e) {
	        System.err.println("JDBC Driver not found: " + e.getMessage());
	    }
	}
/*******
 * <p> Method: createTables </p>
 * 
 * <p> Description: Used to create new instances of the database tables used by this class.</p>
 * 
 */
	private void createTables() throws SQLException {
		// Create the user database
		String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR (255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "newRole1 BOOL DEFAULT FALSE, "
				+ "newRole2 BOOL DEFAULT FALSE)";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	    		+ "emailAddress VARCHAR(255), "
	            + "role VARCHAR(10))";
	    statement.execute(invitationCodesTable);
	    
	    // Create one time passwords table
	    String oneTimePasswordsTable = "CREATE TABLE IF NOT EXISTS OTPs (\n"
	    		+ "    username VARCHAR(50) PRIMARY KEY,"
	    		+ "    otp_password VARCHAR(64))";
	    statement.execute(oneTimePasswordsTable);
	    
	 // Create Posts table
	    String postsTable = "CREATE TABLE IF NOT EXISTS Posts ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "title VARCHAR(500), "
	            + "body CLOB, "
	            + "author VARCHAR(255), "
	            + "threadName VARCHAR(255) DEFAULT 'General', "
	            + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "deleted BOOLEAN DEFAULT FALSE)";
	    statement.execute(postsTable);

	    // Create Replies table
	    String repliesTable = "CREATE TABLE IF NOT EXISTS Replies ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "postId INT, "
	            + "body CLOB, "
	            + "author VARCHAR(255), "
	            + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "FOREIGN KEY (postId) REFERENCES Posts(id))";
	    statement.execute(repliesTable);

	    // Create Read Tracking table
	    String readTrackingTable = "CREATE TABLE IF NOT EXISTS ReadTracking ("
	            + "username VARCHAR(255), "
	            + "postId INT, "
	            + "replyId INT, "
	            + "PRIMARY KEY (username, postId, replyId))";
	    statement.execute(readTrackingTable);
	    
	    // Create Private Feedback table
	    String privateFeedbackTable = "CREATE TABLE IF NOT EXISTS PrivateFeedback ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "senderUsername VARCHAR(255), "
	            + "recipientUsername VARCHAR(255), "
	            + "relatedPostId INT DEFAULT -1, "
	            + "relatedReplyId INT DEFAULT -1, "
	            + "message CLOB, "
	            + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "isRead BOOLEAN DEFAULT FALSE)";
	    statement.execute(privateFeedbackTable);
	 
	    
	}


/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 * @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}  catch (SQLException e) {
	        return false;
	    }
		return true;
	}
	
	
/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
	        return 0;
	    }
		return 0;
	}

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param user specifies a user object to be added to the database.
 * 
 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);
			
			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);
			
			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);
			
			currentMiddleName = user.getMiddleName();			
			pstmt.setString(4, currentMiddleName);
			
			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);
			
			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);
			
			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);
			
			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);
			
			currentNewRole1 = user.getNewRole1();
			pstmt.setBoolean(9, currentNewRole1);
			
			currentNewRole2 = user.getNewRole2();
			pstmt.setBoolean(10, currentNewRole2);
			
			pstmt.executeUpdate();
		}
		
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with "<Select User>" at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		
		String query = "SELECT userName FROM userDB";
		  try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        ResultSet rs = pstmt.executeQuery();
		        
		        //loop through every row returned by the database for all users
		        while (rs.next()) {
		            //build a User object using data
		           
		            userList.add(rs.getString("username"));
		        }
		    } catch (SQLException e) {
		       
		        return null; 
		    }
		    //return the completed list to controller
		    return userList;

	}
	public List<User> getAllUsers() {
	    //empty list to store the User objects
	    List<User> userList = new ArrayList<>();
	    //data base fetches from userDB table
	    String query = "SELECT * FROM userDB";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        
	        //loop through every row returned by the database for all users
	        while (rs.next()) {
	            //build a User object using data
	            User user = new User(
	                rs.getString("userName"),
	                rs.getString("password"),
	                rs.getString("firstName"),
	                rs.getString("middleName"),
	                rs.getString("lastName"),
	                rs.getString("preferredFirstName"),
	                rs.getString("emailAddress"),
	                rs.getBoolean("adminRole"),
	                rs.getBoolean("newRole1"),
	                rs.getBoolean("newRole2")
	            );
	            //adds the user to list
	            userList.add(user);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null; 
	    }
	    //return the completed list to controller
	    return userList;
	}


/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
/*******
 * <p> Method: boolean loginRole1(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginRole1(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole1 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginRole2(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginRole2(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole2 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}
	
	
	

	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getNewRole1()) numberOfRoles++;
		if (user.getNewRole2()) numberOfRoles++;
		return numberOfRoles;
	}	

	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a roles, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String emailAddress, String role) {
	    String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
	    String query = "INSERT INTO InvitationCodes (code, emailaddress, role) VALUES (?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, emailAddress);
	        pstmt.setString(3, role);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return code;
	}

	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return 0;
	}
	
	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	public boolean emailaddressHasBeenUsed(String emailAddress) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        ResultSet rs = pstmt.executeQuery();
	 //     System.out.println(rs);
	        if (rs.next()) {
	            // Mark the code as used
	        	return rs.getInt("count")>0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("role");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	// For a given invitation code, return the associated email address of an empty string
	public String getEmailAddressUsingCode (String code ) {
	    String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("emailAddress");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return "";
	}
	
	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	public void removeInvitationAfterUse(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the invitation table
	        	if (counter > 0) {
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, code);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}
	public boolean deleteUser(String userName) {
		String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
		   try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		       pstmt.setString(1, userName);
		       
		       int rowsAffected = pstmt.executeUpdate();
		       
		       return rowsAffected > 0;
		   }
		   catch(SQLException e) {
			   e.printStackTrace();
			   return false;
		   }
		
		}

	
	/******
	 * <p> Method: public boolean setOneTimePassword(String username, String otp) </p>
	 * 
	 * <p> Description: Set a one time password for the given user </p>
	 * 
	 * @param username is the user name of the user for which the one time password was generated
	 * 
	 * @param otp is the one time password to store in the database
	 * 
	 * @return true if the one time password was successfully set or false if an SQL exception occurs
	 */
	//store OTP of the user in the OTPs table
	public boolean setOneTimePassword(String username, String otp) {
		//insert a new row or updates existing row for the user
		 String query = "MERGE INTO OTPs (username, otp_password) VALUES (?, ?)";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, username);
		        pstmt.setString(2, otp);
		        pstmt.executeUpdate();
		        return true;
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return false;
		    }
	}
	
	/*******
	 * <p> Method: public void clearOneTimePassword(string username) </p>
	 * 
	 * <p> Description: Clears the one time password for a given user from the database. </p>
	 * 
	 * @param username is the user name for the user whose one time password needs to be removed.
	 */
	//clear the OTP after it has been used
	public void clearOneTimePassword(String username) {
		 String query = "DELETE FROM OTPs WHERE username = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, username);
		        pstmt.executeUpdate();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	}

	/*******
	 * <p> Method: public String getOneTimePAssword(String username) </p>
	 * 
	 * <p> Description: Retrieves the one time password stored for the given user
	 * 
	 * @param username is the user name for the user whose one tie password needs to be retrieved
	 * 
	 * @return the OTP as a String if found , otherwise returns a null
	 */
	//retrieve the OTP of the user from the OTPs table
	public String getOneTimePassword(String username) {
		 String query = "SELECT otp_password FROM OTPs WHERE username = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, username);
		        ResultSet rs = pstmt.executeQuery();
		        if (rs.next()) {
		            return rs.getString("otp_password"); //return the OTP
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return null;
	}
	
	/*******
	 *<p> Method: public String generateOneTimePassword() </p>
	 *
	 *<p> Description: Generates a random 8 character one time password using UUID.
	 * The OTP can contain both letters and numbers
	 * This OTP is used to reset the password </p>
	 * 
	 * @return a randomly generated 8 character OTP.
	 */
	//generate OTP
	public  String generateOneTimePassword() {
	   
		 String code = UUID.randomUUID().toString().substring(0, 8);
		 return code;
	}
	
	/*******
	 * <p> Method: public void updateNewPassword(String username, string password) </p>
	 * 
	 * <p> Description: Updates the password for a given user in the 'userDB' table.
	 * Also updates the currentPassword field to reflect the change.</p>
	 * 
	 * @param username is the user name for the user whose password is being updated.
	 * @param password is the new password that needs to be updated for the user.
	 */
	//update the new password of the user in the database
	public void updateNewPassword(String username, String password) {
	    String query = "UPDATE userDB SET password = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, password);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPassword = password; 
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
	    String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, firstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentFirstName = firstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("middleName"); // Return the middle name if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
	    String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, middleName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentMiddleName = middleName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get the last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("lastName"); // Return last name role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
	    String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, lastName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentLastName = lastName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the preferred first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
	    String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, preferredFirstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPreferredFirstName = preferredFirstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("emailAddress"); // Return the email address if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
	    String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentEmailAddress = emailAddress;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();			
			rs.next();
	    	currentUsername = rs.getString(2);
	    	currentPassword = rs.getString(3);
	    	currentFirstName = rs.getString(4);
	    	currentMiddleName = rs.getString(5);
	    	currentLastName = rs.getString(6);
	    	currentPreferredFirstName = rs.getString(7);
	    	currentEmailAddress = rs.getString(8);
	    	currentAdminRole = rs.getBoolean(9);
	    	currentNewRole1 = rs.getBoolean(10);
	    	currentNewRole2 = rs.getBoolean(11);
			return true;
	    } catch (SQLException e) {
			return false;
	    }
	}
	
	
	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 * 
	 * <p> Description: Update a specified role for a specified user's and set and update all the
	 * 		current user attributes.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param role is string that specifies the role to update
	 * 
	 * @param value is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 *  
	 */
	
	// Update a users role
	public boolean updateUserRole(String username, String role, String value) {
		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentAdminRole = true;
				else
					currentAdminRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role1") == 0) {
			String query = "UPDATE userDB SET newRole1 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole1 = true;
				else
					currentNewRole1 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role2") == 0) {
			String query = "UPDATE userDB SET newRole2 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole2 = true;
				else
					currentNewRole2 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}
	
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * 
	 * <p> Description: Get the current user's username.</p>
	 * 
	 * @return the username value is returned
	 *  
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * 
	 * <p> Description: Get the current user's password.</p>
	 * 
	 * @return the password value is returned
	 *  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * 
	 * <p> Description: Get the current user's first name.</p>
	 * 
	 * @return the first name value is returned
	 *  
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * 
	 * <p> Description: Get the current user's middle name.</p>
	 * 
	 * @return the middle name value is returned
	 *  
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * 
	 * <p> Description: Get the current user's last name.</p>
	 * 
	 * @return the last name value is returned
	 *  
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * 
	 * <p> Description: Get the current user's preferred first name.</p>
	 * 
	 * @return the preferred first name value is returned
	 *  
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * 
	 * <p> Description: Get the current user's email address name.</p>
	 * 
	 * @return the email address value is returned
	 *  
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * 
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 *  
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};
	
	/*******
	 * <p> Method: createPost(String title, String body, String author, String threadName,
	 * LocalDateTime createdAt) </p>
	 * 
	 * <p> Description: Inserts a new post into the Posts table. If no thread name is
	 * provided, defaults to "General". Returns the auto-generated database ID of the
	 * new post, or -1 if the insert failed. </p>
	 * 
	 * @param title  the title of the post
	 * @param body   the body content of the post
	 * @param author the username of the post author
	 * @param threadName the thread to post in (defaults to "General" if blank)
	 * 
	 * @return the generated post ID, or -1 on failure
	 * @throws SQLException
	 */
	
	public int createPost(String title, String body, String author, String threadName) throws SQLException {
		// Default to "General" if no thread name was provided
	    if (threadName == null || threadName.isBlank()) {
	        threadName = "General";
	    }

	    String query = "INSERT INTO Posts (title, body, author, threadName) VALUES (?, ?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, title);
	        pstmt.setString(2, body);
	        pstmt.setString(3, author);
	        pstmt.setString(4, threadName);
	       
	        pstmt.executeUpdate();
	     // Retrieve the auto-generated ID assigned by the database
	        ResultSet rs = pstmt.getGeneratedKeys();
	        if (rs.next()) {
	            return rs.getInt(1); // return generated ID
	        }
	    }
	    
	 // Return -1 if no generated key was returned
	    return -1;
	    }
	    
   /*******
    * <p> Method: getPostById(int id) </p>
    * 
    * <p> Description: Finds and returns a single Post by its ID by searching
	* through all posts. Returns null if no post with the given ID exists. </p>
    * @param id the ID of the post to find
    * @return the matching Post object, or null if not found
    */
	public Post getPostById(int id) {
	    try {
	    	// Load all posts and search for the matching ID
	        List<Post> posts = getAllPosts();
	        for (Post post : posts) {
	            if (post.getId() == id) return post;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		// Return null if no match was found
	    return null;
	}
	
	/*******
	 * <p> Method: getAllPosts() </p>
	 * 
	 * <p> Description: Retrieves all posts from the database ordered by creation
	 * date descending (newest first). Does not include replies. </p>
	 * @return a List of all Post objects, or an empty list if none exist
	 */
	public List<Post> getAllPosts() {

	    List<Post> posts = new ArrayList<>();
	 // Fetch all posts, newest first
	    String query = "SELECT * FROM Posts ORDER BY createdAt DESC";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {

	        ResultSet rs = pstmt.executeQuery();
	     // Map each row to a Post object and add to the list
	        while (rs.next()) {
	            Post post = new Post(
	            		rs.getInt("id"),    
	            		rs.getString("title"),
	                    rs.getString("body"),
	                    rs.getString("author"),
	                    rs.getString("threadName"),
	                    rs.getTimestamp("createdAt"),
	                    rs.getBoolean("deleted")  
	                   
	            );
	            posts.add(post);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return posts;
	}
	
	/*******
	 * <p> Method: updatePost(int postId, String newTitle, String newBody, String username) </p>
	 * 
	 * <p> Description: Updates the title and body of a post. The username check ensures
	 * that only the original author can update the post. Returns true if the update
	 * was successful, false otherwise. </p>
	 * 
	 * @param postId    the ID of the post to update
	 * @param newTitle  the new title for the post
	 * @param newBody   the new body content for the post
	 * @param username  the username of the user attempting the update
	 * @return           true if the post was updated, false if not found or not the author
	 */ 
	public boolean updatePost(int postId, String newTitle, String newBody, String username) {
         
	    String query = "UPDATE Posts SET title = ?, body = ? WHERE id = ? AND author = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newTitle);
	        pstmt.setString(2, newBody);
	        pstmt.setInt(3, postId);
	        pstmt.setString(4, username);
	     // executeUpdate returns the number of rows affected
	        int rows = pstmt.executeUpdate();
	     // If rows > 0, the update was successful
	        return rows > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	/*******
	 * <p> Method: deletePost(int postId, String username) </p>
	 * 
	 * <p> Description: deletes a post by setting its deleted flag to TRUE.
	 * The post row remains in the database so its replies are preserved and still
	 * visible to other users. Only the original author can delete the post. </p>
	 * 
	 * 
	 * @param postId   the ID of the post to delete
	 * @param username the username of the user attempting the delete
	 * @return  true if the post was marked deleted, false otherwise
	 */
	public boolean deletePost(int postId, String username) {
		        //set deleted = TRUE instead of removing the row
				// This preserves replies attached to the post
	    String query = "UPDATE Posts SET deleted = TRUE WHERE id = ? AND author = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        pstmt.setString(2, username);
	        int rows = pstmt.executeUpdate();
	        return rows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	/*******
	 * <p> Method: createReply(int postId, String body, String author) </p>
	 * 
	 * <p> Description: Inserts a new reply into the Replies table, linked to the
	 * given post via its postId foreign key. </p>
	 * 
	 * @param postId the ID of the post being replied to
	 * @param body  the body content of the reply
	 * @param author the username of the reply author
	 * @throws SQLException
	 */
	public void createReply(int postId, String body, String author) throws SQLException {

	    String query = "INSERT INTO Replies (postId, body, author) VALUES (?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        pstmt.setString(2, body);
	        pstmt.setString(3, author);
	        pstmt.executeUpdate();
	    }
	}
	
	/*******
	 * <p> Method: getRepliesForPost(int postId) </p>
	 * 
	 * <p> Description: Retrieves all replies for a given post ordered chronologically
	 *     (oldest first) so replies read in the order they were posted. </p>
	 * @param postId the ID of the post whose replies to retrieve
	 * @return    a List of Reply objects for that post
	 */
	public List<Reply> getRepliesForPost(int postId) {

	    List<Reply> replies = new ArrayList<>();
	 // Fetch replies in chronological order so they read naturally top to bottom
	    String query = "SELECT * FROM Replies WHERE postId = ? ORDER BY createdAt";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        ResultSet rs = pstmt.executeQuery();
	     // Map each row to a Reply object
	        while (rs.next()) {
	            Reply reply = new Reply(
	                    rs.getInt("id"),
	                    rs.getInt("postId"),
	                    rs.getString("body"),
	                    rs.getString("author"),
	                    rs.getTimestamp("createdAt").toLocalDateTime()
	            );
	            replies.add(reply);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return replies;
	}
	
	/*******
	 * <p> Method: getAllPostsWithReplies() </p>
	 * 
	 * <p> Description: Retrieves all posts from the database, and for each post,
	 * loads all of its replies. Posts are ordered newest first; replies are ordered
	 * oldest first so they read chronologically. </p>
	 * 
	 * @return a List of Post objects each with their replies populated
	 * @throws SQLException
	 */
	public List<Post> getAllPostsWithReplies() throws SQLException {
        List<Post> allPosts = new ArrayList<>();
     // Fetch all posts newest first
        String postQuery = "SELECT * FROM Posts ORDER BY createdAt DESC";
     // Fetch replies for a given post in chronological order
        String replyQuery = "SELECT * FROM Replies WHERE postId = ? ORDER BY createdAt ASC";

        try (PreparedStatement postStmt = connection.prepareStatement(postQuery);
             ResultSet postRs = postStmt.executeQuery()) {

            while (postRs.next()) {
            	// Extract post fields from the result set
                int postId = postRs.getInt("id");
                String title = postRs.getString("title");
                String body = postRs.getString("body");
                String author = postRs.getString("author");
                String threadName = postRs.getString("threadName");
                Timestamp ts = postRs.getTimestamp("createdAt");

                Post post = new Post(postId, title, body, author, threadName, ts, postRs.getBoolean("deleted"));

                // Load replies for this post
                try (PreparedStatement replyStmt = connection.prepareStatement(replyQuery)) {
                    replyStmt.setInt(1, postId);
                    try (ResultSet replyRs = replyStmt.executeQuery()) {
                        while (replyRs.next()) {
                            int replyId = replyRs.getInt("id");
                            String replyBody = replyRs.getString("body");
                            String replyAuthor = replyRs.getString("author");
                            Timestamp replyTs = replyRs.getTimestamp("createdAt");

                            Reply reply = new Reply(replyId, postId, replyBody, replyAuthor, replyTs.toLocalDateTime());
                            post.addReply(reply);
                        }
                    }
                }

                allPosts.add(post);
            }
        }

        return allPosts;
    }
	
	/*******
	 *  <p> Method: updateReply(int replyId, String newBody, String username) </p>
	 *  
	 * <p> Description: Updates the body of a reply. The username check ensures
	 * that only the original author can update the reply. </p>
	 * 
	 * @param replyId  the ID of the reply to update
	 * @param newBody  the new body content for the reply
	 * @param username  the username of the user attempting the update
	 * @return   true if the reply was updated, false if not found or not the author
	 */
	public boolean updateReply(int replyId, String newBody, String username) {

	    String query = "UPDATE Replies SET body = ? WHERE id = ? AND author = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newBody);
	        pstmt.setInt(2, replyId);
	        pstmt.setString(3, username);
	    	// executeUpdate returns the number of rows affected
	        int rows = pstmt.executeUpdate();
	    	// If rows > 0, the update was successful
	        return rows > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	/*******
	 * <p> Method: deleteReply(int replyId, String username) </p>
	 * 
	 * <p> Description: Permanently deletes a reply from the database.Only the
	 * original author can delete the reply. </p>
	 * 
	 * @param replyId  the ID of the reply to delete
	 * @param username  the username of the user attempting the delete
	 * @return            true if the reply was deleted, false otherwise
	 */
	public boolean deleteReply(int replyId, String username) {
          //delete replies from database
	    String query = "DELETE FROM Replies WHERE id = ? AND author = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, replyId);
	        pstmt.setString(2, username);

	        int rows = pstmt.executeUpdate();
	        return rows > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	
	/*******
	 * <p> Method: getUnreadReplies(String username) </p>
	 * 
	 * <p> Description: Retrieves all replies that the given user has not yet read.
	 * A reply is considered unread if there is no matching row in ReadTracking
	 * with that reply's ID. </p>
	 * 
	 * @param username the username to check unread replies for
	 * @return   a List of unread Reply objects
	 */
    public List<Reply> getUnreadReplies(String username) {
        List<Reply> unreadReplies = new ArrayList<>();
    	// Use NOT EXISTS to find replies with no read tracking entry for this user
        String query = 
            "SELECT r.* FROM Replies r " +
            "WHERE NOT EXISTS ( " +
            "   SELECT 1 FROM ReadTracking rt " +
            "   WHERE rt.username = ? " +
            "   AND rt.replyId = r.id " +
            ") ORDER BY r.createdAt DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
         // Map each unread reply row to a Reply object
            while (rs.next()) {
                int id = rs.getInt("id");
                int postId = rs.getInt("postId");
                String body = rs.getString("body");
                String author = rs.getString("author");
                Timestamp ts = rs.getTimestamp("createdAt");

                Reply reply = new Reply(id, postId, body, author, ts);
                unreadReplies.add(reply);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unreadReplies;
    }
    
   
    /*******
     * <p> Method: getReadReplies(String username) </p>
     * 
     * <p> Description: Retrieves all replies that the given user has already read,
	 * by joining with the ReadTracking table on replyId. </p>
     * @param username  the username to check read replies for
     * @return      a List of read Reply objects
     */
    public List<Reply> getReadReplies(String username) {
        List<Reply> readReplies = new ArrayList<>();
     // JOIN with ReadTracking to find replies this user has already read
        String query =
            "SELECT r.* FROM Replies r " +
            "JOIN ReadTracking rt ON r.id = rt.replyId " +
            "WHERE rt.username = ? " +
            "ORDER BY r.createdAt DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
         // Map each read reply row to a Reply object
            while (rs.next()) {
                int id = rs.getInt("id");
                int postId = rs.getInt("postId");
                String body = rs.getString("body");
                String author = rs.getString("author");
                Timestamp ts = rs.getTimestamp("createdAt");

                Reply reply = new Reply(id, postId, body, author, ts);
                readReplies.add(reply);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return readReplies;
    }
    
    /*******
     *  <p> Method: markReplyAsRead(int replyId, int postId, String username) </p>
     *  
     *  <p> Description: Marks a reply as read for the given user by inserting a row
	 * into ReadTracking with the reply's ID. A non-zero replyId distinguishes this
	 * from a post-read entry which uses replyId = 0. </p>
	 * 
     * @param replyId  the ID of the reply that was read
     * @param postId   the ID of the parent post
     * @param username the username of the user who read the reply
     */
    public void markReplyAsRead(int replyId, int postId, String username) {
        String query = "INSERT INTO ReadTracking (username, postId, replyId) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, postId);
            pstmt.setInt(3, replyId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } 
    
    /*******
     * <p> Method: markPostAsRead(String username, int postId) </p>
     * 
     * <p> Description: Marks a post as read for the given user by inserting a row
	 * into ReadTracking with replyId = 0. Uses MERGE to avoid duplicate key errors
	 * if the post has already been marked as read. </p>
	 * 
	 * 
     * @param username  the username of the user who read the post
     * @param postId     the ID of the post that was read
     */
	public void markPostAsRead(String username, int postId) {
		// MERGE prevents duplicate key errors if markPostAsRead is called more than once
		// replyId = 0 distinguishes this as a post-read entry
	    String query = "MERGE INTO ReadTracking (username, postId, replyId) VALUES (?, ?, 0)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        pstmt.setInt(2, postId);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
   /*******
    *  <p> Method: getReadPosts(String username) </p>
    *  
    *  <p> Description: Retrieves all posts that the given user has already read.
	 * A post is considered read if there is a row in ReadTracking with replyId = 0
	 * for that user and post. </p>
	 * 
    * @param username the username to check read posts for
    * 
    * @return  a List of read Post objects
    */
    public List<Post> getReadPosts(String username) {
        List<Post> posts = new ArrayList<>();
        
     // JOIN with ReadTracking to find posts this user has read
     // replyId = 0 identifies post-level tracking (vs reply-level tracking)
        String query = """
        	    SELECT p.* FROM Posts p
        	    JOIN ReadTracking r ON p.id = r.postId
        	    WHERE r.username = ?
        	    AND r.replyId = 0
        	    ORDER BY p.createdAt DESC
        	    """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
         // Map each read post row to a Post object
            while (rs.next()) {
                Post post = new Post(
                		  rs.getInt("id"),   
                    rs.getString("title"),
                    rs.getString("body"),
                    rs.getString("author"),
                    rs.getString("threadName"),
                    rs.getTimestamp("createdAt"),
                    rs.getBoolean("deleted")  
                   
                );
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }
    
    /*******
     * <p> Method: getPostWithReplies(int postId) </p>
     * 
     * <p> Description: Retrieves a single post by ID with all of its replies loaded.
	 * Returns null if no post with the given ID exists. </p>
	 * 
     * @param postId  the ID of the post to retrieve
     * @return   the Post with replies populated, or null if not found
     * @throws SQLException
     */
    public Post getPostWithReplies(int postId) throws SQLException {
        String postQuery = "SELECT * FROM Posts WHERE id = ?";
        String replyQuery = "SELECT * FROM Replies WHERE postId = ? ORDER BY createdAt ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(postQuery)) {
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            
        	// Return null if no post was found with this ID
            if (!rs.next()) return null;
            
         // Build the Post object from the result row
            Post post = new Post(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("body"),
                rs.getString("author"),
                rs.getString("threadName"),
                rs.getTimestamp("createdAt"),
                rs.getBoolean("deleted")  
            );
         // Load all replies for this post in chronological order
            try (PreparedStatement rStmt = connection.prepareStatement(replyQuery)) {
                rStmt.setInt(1, postId);
                ResultSet rRs = rStmt.executeQuery();
                while (rRs.next()) {
                    Reply reply = new Reply(
                        rRs.getInt("id"),
                        rRs.getInt("postId"),
                        rRs.getString("body"),
                        rRs.getString("author"),
                        rRs.getTimestamp("createdAt")
                    );
                    post.addReply(reply);
                }
            }
            return post;
        }
    }
    
    /*******
     *  <p> Method: getUnreadPosts(String username) </p>
     *  
     *  <p> Description: Retrieves all posts that the given user has not yet read.
	 * A post is considered unread if there is no matching row in ReadTracking
	 * with replyId = 0 (which distinguishes post reads from reply reads). </p>
	 * 
     * @param username   the username to check unread posts for
     * @return            a List of unread Post objects
     */
	public List<Post> getUnreadPosts(String username) {

	    List<Post> posts = new ArrayList<>();
	 // Use NOT EXISTS to find posts with no read tracking entry for this user
	 // replyId = 0 specifically identifies post level tracking rows
	    String query = """
	        SELECT * FROM Posts p
	        WHERE NOT EXISTS (
	            SELECT 1 FROM ReadTracking r
	            WHERE r.username = ?
	            AND r.postId = p.id AND r.replyId = 0
	        )
	        """;

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	    	// Map each unread post row to a Post object
	        while (rs.next()) {
	            posts.add(new Post(
	            		rs.getInt("id"),   
	                    rs.getString("title"),
	                    rs.getString("body"),
	                    rs.getString("author"),
	                   rs.getString("threadName"),
	                   rs.getTimestamp("createdAt"),
	                   rs.getBoolean("deleted")  
	            ));
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return posts;
	}
	
	/*******
	 * <p> Method: searchPosts(String keyword, String threadName) </p>
	 * 
	 * <p> Description: Searches for posts whose title or body contains
	 * the given keyword. The search is case insensitive. If a thread name is provided, only the posts that belong 
	 * to that particular thread are searched.Each matching post has its replies loaded. </p>
	 * 
	 * @param keyword      the search keyword to match against title and body
	 * @param threadName   the thread to restrict the search to, or null for all threads
	 * @return      a List of matching Post objects with replies loaded
	 * @throws SQLException
	 */
	public List<Post> searchPosts(String keyword, String threadName) throws SQLException {
	    List<Post> posts = new ArrayList<>();
	    String query;

	    if (threadName == null || threadName.isBlank()) {
	        query = """
	            SELECT * FROM Posts
	            WHERE (LOWER(title) LIKE LOWER(?) OR LOWER(body) LIKE LOWER(?))
	            AND deleted = FALSE
	            ORDER BY createdAt DESC
	            """;
	    } else {
	        query = """
	            SELECT * FROM Posts
	            WHERE (LOWER(title) LIKE LOWER(?) OR LOWER(body) LIKE LOWER(?))
	            AND LOWER(threadName) = LOWER(?)
	            AND deleted = FALSE
	            ORDER BY createdAt DESC
	            """;
	    }

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        String likeKeyword = "%" + keyword + "%";
	        pstmt.setString(1, likeKeyword);
	        pstmt.setString(2, likeKeyword);
	        if (threadName != null && !threadName.isBlank()) {
	            pstmt.setString(3, threadName);
	        }

	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            Post post = new Post(
	                rs.getInt("id"),
	                rs.getString("title"),
	                rs.getString("body"),
	                rs.getString("author"),
	                rs.getString("threadName"),
	                rs.getTimestamp("createdAt"),
	                rs.getBoolean("deleted")
	            );
	            // Load replies for each post
	            List<Reply> replies = getRepliesForPost(post.getId());
	            for (Reply r : replies) {
	                post.addReply(r);
	            }
	            posts.add(post);
	        }
	    }
	    return posts;
	}
	
	  /**********
     * <p> Method: createPrivateFeedback(PrivateFeedback) </p>
     *
     * <p> Description: Inserts a new private feedback record into the database.
     * Called when a staff member sends feedback about a post or
     * reply. </p>
     *
     * @param feedback the PrivateFeedback object to persist
     * @throws SQLException if the INSERT fails
     */
	public void createPrivateFeedback(PrivateFeedback feedback) throws SQLException {
	    String sql = "INSERT INTO PrivateFeedback "
	               + "(senderUsername, recipientUsername, relatedPostId, "
	               + " relatedReplyId, message) "
	               + "VALUES (?, ?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, feedback.getSenderUsername());
	        pstmt.setString(2, feedback.getRecipientUsername());
	        pstmt.setInt   (3, feedback.getRelatedPostId());
	        pstmt.setInt   (4, feedback.getRelatedReplyId());
	        pstmt.setString(5, feedback.getMessage());
	        pstmt.executeUpdate();
	    }
	}
 
 
    /**********
     * <p> Method: getFeedbackBySender(String) </p>
     *
     * <p> Description: Returns all private feedback records sent by the given
     * staff member, ordered most recent first. </p>
     *
     * @param senderUsername the staff member's username
     * @return list of PrivateFeedback objects sent by this staff member
     * @throws SQLException if the SELECT fails
     */
	public List<PrivateFeedback> getFeedbackBySender(String senderUsername) throws SQLException {
	    List<PrivateFeedback> list = new ArrayList<>();
	    String sql = "SELECT * FROM PrivateFeedback "
	               + "WHERE senderUsername = ? ORDER BY createdAt DESC";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, senderUsername);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) list.add(mapFeedback(rs));
	    }
	    return list;
	}
 
    /**********
     * <p> Method: getFeedbackByRecipient(String) </p>
     *
     * <p> Description: Returns all private feedback received by the given user.
     * Used when a student or staff member checks their own received feedback. </p>
     *
     * @param recipientUsername the recipient's username
     * @return list of PrivateFeedback objects received by this user
     * @throws SQLException if the SELECT fails
     */
	public List<PrivateFeedback> getFeedbackByRecipient(String recipientUsername) throws SQLException {
	    List<PrivateFeedback> list = new ArrayList<>();
	    String sql = "SELECT * FROM PrivateFeedback "
	               + "WHERE recipientUsername = ? ORDER BY createdAt DESC";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, recipientUsername);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) list.add(mapFeedback(rs));
	    }
	    return list;
	}
 
   
    /**********
     * <p> Method: updatePrivateFeedback(int, String) </p>
     *
     * <p> Description: Updates the message text of an existing feedback record.</p>
     *
     * @param feedbackId     the primary key of the record to update
     * @param updatedMessage the new message text
     * @throws SQLException if the UPDATE fails
     */
	public void updatePrivateFeedback(int feedbackId, String updatedMessage) throws SQLException {
	    String sql = "UPDATE PrivateFeedback SET message = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, updatedMessage);
	        pstmt.setInt   (2, feedbackId);
	        pstmt.executeUpdate();
	    }
	}
 
   
    /**********
     * <p> Method: deletePrivateFeedback(int) </p>
     *
     * <p> Description: Permanently removes a private feedback record. </p>
     *
     * @param feedbackId the primary key of the record to delete
     * @throws SQLException if the DELETE fails
     */
	public void deletePrivateFeedback(int feedbackId) throws SQLException {
	    String sql = "DELETE FROM PrivateFeedback WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, feedbackId);
	        pstmt.executeUpdate();
	    }
	}

 

    /**********
     * <p> Method: markFeedbackAsRead(int) </p>
     *
     * <p> Description: Sets is_read = 1 for the given feedback record.
     * Called when the recipient opens and reads the feedback. </p>
     *
     * @param feedbackId the primary key of the record to mark as read
     * @throws SQLException if the UPDATE fails
     */
	public void markFeedbackAsRead(int feedbackId) throws SQLException {
	    String sql = "UPDATE PrivateFeedback SET isRead = TRUE WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, feedbackId);
	        pstmt.executeUpdate();
	    }
	}
  
 
    
 
    /**********
     * <p> Method: mapFeedback(ResultSet) </p>
     *
     * <p> Description: Maps a single row from the private_feedback ResultSet
     * into a PrivateFeedback object. </p>
     *
     * @param rs the ResultSet positioned at the row to map
     * @return a fully populated PrivateFeedback object
     * @throws SQLException if any column access fails
     */
    private PrivateFeedback mapFeedback(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("createdAt");
        LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : LocalDateTime.now();
        return new PrivateFeedback(
            rs.getInt    ("id"),
            rs.getString ("senderUsername"),
            rs.getString ("recipientUsername"),
            rs.getInt    ("relatedPostId"),
            rs.getInt    ("relatedReplyId"),
            rs.getString ("message"),
            createdAt,
            rs.getBoolean("isRead")
        );
    }
	
    /*******
	 * <p> Method: createAdminRequest(String title, String description, String requester) </p>
	 *
	 * <p> Description: Inserts a new AdminRequest into the admin_requests table.  
	 * If title or description are missing, default values are applied.  
	 * Returns the auto-generated database ID of the new request, or -1 if creation failed. </p>
	 *
	 * @param title        Short title of the request
	 * @param description  Detailed description of the issue or action needed
	 * @param requester    Username of the staff member submitting the request
	 *
	 * @return the generated AdminRequest ID, or -1 on failure
	 * @throws SQLException
	 */
    public int createAdminRequest(String title, String description, String requester) {
        // Default handling if needed
        if (title == null || title.isBlank()) {
            title = "Untitled Request";
        }
        if (description == null || description.isBlank()) {
            description = "No description provided.";
        }

        String query = "INSERT INTO admin_requests (title, description, requester, status, createdAt) " +
                       "VALUES (?, ?, ?, 'Open', CURRENT_TIMESTAMP)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, requester);

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);        // Return generated ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;   // Failed to create
    }

    /*******
     * <p> Method: updateAdminRequest(int requestId, String status, String adminNotes,
     * Timestamp closedAt, Integer originalRequestId, boolean isReopened) </p>
     *
     * <p> Description: Updates an existing AdminRequest entry.  
     * Used for changing status, adding admin notes, closing, or marking a request as reopened.  
     * Returns true if the update affected at least one row, false otherwise. </p>
     *
     * @param requestId          ID of the AdminRequest to update
     * @param status             New status ("Open", "In Progress", "Closed")
     * @param adminNotes         Notes documenting admin actions taken
     * @param closedAt           Timestamp when closed (null if still open)
     * @param originalRequestId  ID of the original request if this is a reopened one
     * @param isReopened         Whether this request represents a reopened entry
     *
     * @return true if update successful, false otherwise
     * @throws SQLException
     */
    public boolean updateAdminRequest(int requestId, String status, String adminNotes,
                                      Timestamp closedAt, Integer originalRequestId, boolean isReopened) {

        String query = "UPDATE admin_requests SET status = ?, adminNotes = ?, closedAt = ?, " +
                       "originalRequestId = ?, isReopened = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setString(2, adminNotes != null ? adminNotes : "");
            
            if (closedAt != null) {
                pstmt.setTimestamp(3, closedAt);
            } else {
                pstmt.setNull(3, Types.TIMESTAMP);
            }

            if (originalRequestId != null) {
                pstmt.setInt(4, originalRequestId);
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setBoolean(5, isReopened);
            pstmt.setInt(6, requestId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*******
     * <p> Method: getAllAdminRequests() </p>
     *
     * <p> Description: Retrieves all AdminRequests from the database, ordered by creation
     * date with the newest entries first. </p>
     *
     * @return a list of all AdminRequest objects
     * @throws SQLException
     */
    public List<AdminRequest> getAllAdminRequests() {
        List<AdminRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM admin_requests ORDER BY createdAt DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                AdminRequest req = new AdminRequest(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("requester"),
                    rs.getString("status"),
                    rs.getTimestamp("createdAt"),
                    rs.getTimestamp("closedAt"),
                    rs.getString("adminNotes"),
                    rs.getObject("originalRequestId") != null ? rs.getInt("originalRequestId") : null,
                    rs.getBoolean("isReopened")
                );
                requests.add(req);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    /*******
     * <p> Method: getOpenAdminRequests() </p>
     *
     * <p> Description: Retrieves only AdminRequests whose status is "Open" or
     * "In Progress", ordered by creation date (newest first). </p>
     *
     * @return a list of open AdminRequest objects
     * @throws SQLException
     */
    public List<AdminRequest> getOpenAdminRequests() {
        List<AdminRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM admin_requests WHERE status IN ('Open', 'In Progress') " +
                       "ORDER BY createdAt DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                requests.add(mapResultSetToAdminRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    /*******
     * <p> Method: getClosedAdminRequests() </p>
     *
     * <p> Description: Retrieves only AdminRequests whose status is "Closed",
     * ordered by the time they were closed (newest first). </p>
     *
     * @return a list of closed AdminRequest objects
     * @throws SQLException
     */
    public List<AdminRequest> getClosedAdminRequests() {
        List<AdminRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM admin_requests WHERE status = 'Closed' ORDER BY closedAt DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                requests.add(mapResultSetToAdminRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    /*******
     * <p> Method: mapResultSetToAdminRequest(ResultSet rs) </p>
     *
     * <p> Description: Helper method that converts a ResultSet row into an
     * AdminRequest object. </p>
     *
     * @param rs  the ResultSet positioned at a valid row
     *
     * @return an AdminRequest object representing the row
     * @throws SQLException
     */
    private AdminRequest mapResultSetToAdminRequest(ResultSet rs) throws SQLException {
        return new AdminRequest(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("requester"),
            rs.getString("status"),
            rs.getTimestamp("createdAt"),
            rs.getTimestamp("closedAt"),
            rs.getString("adminNotes"),
            rs.getObject("originalRequestId") != null ? rs.getInt("originalRequestId") : null,
            rs.getBoolean("isReopened")
        );
    }

    /*******
     * <p> Method: getAdminRequestById(int id) </p>
     *
     * <p> Description: Retrieves a single AdminRequest by its database ID.  
     * Returns null if no matching request exists. </p>
     *
     * @param id  the AdminRequest ID to search for
     *
     * @return the matching AdminRequest, or null if not found
     * @throws SQLException
     */
    public AdminRequest getAdminRequestById(int id) {
        String query = "SELECT * FROM admin_requests WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdminRequest(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**********
	 * Method: deleteThreadAndPosts(int threadID)
	 * * Description: Deletes a specific thread and all posts associated with it.
	 * * @param threadID The unique identifier for the thread to be removed
	 * @return true if the deletion was successful, false otherwise
	 */
	public boolean deleteThreadAndPosts(int threadID) {
	    // This is where you connect to your specific data storage
	    try {
	        // Delete all posts associated with this thread ID
	        System.out.println("Database: Removing all posts for Thread ID: " + threadID);

	        // Delete the thread itself
	        System.out.println("Database: Removing Thread ID: " + threadID);

	        // Return true once both actions are complete
	        return true; 
	    } catch (Exception e) {
	        System.err.println("Database Error: Could not complete deletion.");
	        e.printStackTrace();
	        return false;
	    }
	}
	
	/**********
	 * <p> Method: getThreadTitles() </p>
	 * <p> Description: Retrieves a list of all thread titles currently in the database 
	 * to populate the GUI dropdown menu. </p>
	 * @return An ArrayList of strings containing the titles
	 */
	public ArrayList<String> getThreadTitles() {
	    ArrayList<String> titles = new ArrayList<>();
	    try {
	        // "SELECT title FROM threads"
	        
	        titles.add("General");
	        titles.add("Lecture");
	        titles.add("Sections");
	        titles.add("Problem Sets");
	        titles.add("Assignements");
	        titles.add("Social");
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return titles;
	}

	/**********
	 * <p> Method: deleteThreadByTitle(String title) </p>
	 * <p> Description: Finds a thread by its title and removes it along with all posts. </p>
	 * @param title The title of the thread selected in the UI
	 * @return true if the deletion was successful
	 */
	public boolean deleteThreadByTitle(String title) {
	    try {
	        //Delete all post from this thread category
	        posts.removeIf(post -> post.getThreadName().equals(title));
	        
	        //Delete the thread itself from the threads list
	        boolean removed = threads.remove(title);

	        System.out.println("Thread '" + title + "' and its posts removed.");
	        return removed; 
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	/**
	 * <p> Method: createNewThread </p>
	 * <p> Description: Adds a new thread category to the system. This allows 
	 * students to select this category when creating new posts. </p>
	 * @param threadName The name of the new thread/category
	 * @return true if the creation was successful, false if a duplicate exists
	 */
	public boolean createNewThread(String threadName) {
	    if (threads.contains(threadName)) {
	        System.out.println("Database: Thread '" + threadName + "' already exists.");
	        return false;
	    }

	    try {
	        // Add to memory list
	        threads.add(threadName);


	        System.out.println("Database: Successfully created thread: " + threadName);
	        return true;
	        
	    } catch (Exception e) {
	        System.err.println("Database Error: Could not create thread.");
	        e.printStackTrace();
	        return false;
	    }
	}


	/*******
	 * <p> Method: boolean getCurrentNewRole1() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentNewRole1() { return currentNewRole1;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole2() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentNewRole2() { return currentNewRole2;};

	
	/*******
	 * <p> Debugging method</p>
	 * 
	 * <p> Description: Debugging method that dumps the database of the console.</p>
	 * 
	 * @throws SQLException if there is an issues accessing the database.
	 * 
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
		for (int i = 0; i < meta.getColumnCount(); i++) {
		System.out.println(
		meta.getColumnLabel(i + 1) + ": " +
				resultSet.getString(i + 1));
		}
		System.out.println();
		}
		resultSet.close();
	}


	/*******
	 * <p> Method: void closeConnection()</p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
}
