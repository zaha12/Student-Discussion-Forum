package guiFirstAdmin;

import java.sql.SQLException;
import database.Database;
import entityClasses.User;
//import guiNewAccount.ViewNewAccount;
import javafx.stage.Stage;
import userNameRecognizerTestbed.UserNameRecognizer;

/*******
 * <p> Title: ControllerFirstAdmin Class. </p>
 * 
 * <p> Description: ControllerFirstAdmin class provides the controller actions based on the user's
 *  use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHhen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 */

public class ControllerFirstAdmin {
	/*-********************************************************************************************

	The controller attributes for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	private static String adminUsername = "";
	private static String adminPassword1 = "";
	private static String adminPassword2 = "";		
	protected static Database theDatabase = applicationMain.FoundationsMain.database;		

	/*-********************************************************************************************

	The User Interface Actions for this page
	
	*/
	
	/**
	 * Default constructor is not used.
	 */
	public ControllerFirstAdmin() {
	}

	/**********
	 * <p> Method: setAdminUsername() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the username field in the
	 * View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminUsername() {
		adminUsername = ViewFirstAdmin.text_AdminUsername.getText();
		
	}
	
	
	/**********
	 * <p> Method: setAdminPassword1() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 1 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword1() {
		adminPassword1 = ViewFirstAdmin.text_AdminPassword1.getText();
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
	}
	
	
	/**********
	 * <p> Method: setAdminPassword2() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 2 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword2() {
		adminPassword2 = ViewFirstAdmin.text_AdminPassword2.getText();		
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
	}
	
	
	/**********
	 * <p> Method: doSetupAdmin() </p>
	 * 
	 * <p> Description: This method is called when the user presses the button to set up the Admin
	 * account.  It start by trying to establish a new user and placing that user into the
	 * database.  If that is successful, we proceed to the UserUpdate page.</p>
	 * 
	 */
	
	
	protected static void doSetupAdmin(Stage ps, int r) {
		String errmessage = UserNameRecognizer.checkForValidUserName(adminUsername);  //validate the username and return the appropiate message
		if (errmessage != "") {
			// Display the error message
			System.out.println(errmessage);
			ViewFirstAdmin.text_AdminUsername.setText("");
			ViewFirstAdmin.alertInvalidUsernameError.setTitle("Invalid Username");
			ViewFirstAdmin.alertInvalidUsernameError.setHeaderText("Invalid Username");
			ViewFirstAdmin.alertInvalidUsernameError.setContentText(errmessage);
			ViewFirstAdmin.alertInvalidUsernameError.getDialogPane().setPrefHeight(300);
			ViewFirstAdmin.alertInvalidUsernameError.showAndWait();
			return;
			}
		
		//Validate password
		if (!validatePassword(adminPassword1)) {
	        ViewFirstAdmin.text_AdminPassword1.setText("");
	        ViewFirstAdmin.text_AdminPassword2.setText("");
	        return;
	    }
		
		// Make sure the two passwords are the same
		if (adminPassword1.compareTo(adminPassword2) == 0) {
        	// Create the passwords and proceed to the user home page
        	User user = new User(adminUsername, adminPassword1, "", "", "", "", "", true, false, 
        			false);
            try {
            	// Create a new User object with admin role and register in the database
            	theDatabase.register(user);
            	}
            catch (SQLException e) {
                System.err.println("*** ERROR *** Database error trying to register a user: " + 
                		e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
            
            // User was established in the database, so navigate to the User Update Page
        	guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewFirstAdmin.theStage, user);
		}
		else {
			// The two passwords are NOT the same, so clear the passwords, explain the passwords
			// must be the same, and clear the message as soon as the first character is typed.
			ViewFirstAdmin.text_AdminPassword1.setText("");
			ViewFirstAdmin.text_AdminPassword2.setText("");
			ViewFirstAdmin.ResetAssessments();             //Reset the password requirements to red.
            ViewFirstAdmin.label_PasswordsDoNotMatch.setText(
					"The two passwords must match. Please try again!");
			
		}
	}
	
	/*******
	 * <p> Method: validatePassword(String password) </p>
	 * 
	 * <p> Description: Validates a password against a given set of rules.
	 *     The password must contain at least one uppercase letter, one lowercase letter,
	 *     one numeric digit, one special character, be at least 8 characters long and no more than 16 characters long. </p>
	 *     
	 * <p> the method also updates the GUI labels as the user types the password to show which requirements have
	 *         been satisfied.
	 *         
	 * @param password is the password to validate
	 * 
	 * @return true if all password requirements are satisfied or false otherwise.
	 */
	protected static boolean validatePassword(String password) {
        //flags for each requirement
		boolean foundUpperCase = false;
	    boolean foundLowerCase = false;
		boolean foundNumericDigit = false;
		boolean foundSpecialChar = false;
		boolean foundLongEnough = false;
		boolean foundShortEnough = false;	 		
		
		char currentChar;					// The current character in the line
		int currentCharNdx;					// The index of the current character
		boolean running;						// The flag that specifies if the FSM is running
		 												

		currentCharNdx = 0;					// The index of the current character
		
		//if password is empty return false.
		if(password.length() <= 0) {
			return false;
		}
		
		// This flag determines whether the directed graph (FSM) loop is operating or not
		running = true;// Start the loop
		
		 foundLongEnough = password.length() >= 8;   //check if the password length is at least 8 characters
		 foundShortEnough = password.length() <= 16; //check if the password length is at most 16 characters
		 
		// The Directed Graph simulation continues until the end of the input is reached or at some
		// state the current character does not match any valid transition
		while (running && currentCharNdx < password.length()) {
			currentChar = password.charAt(currentCharNdx);
			
			// The cascading if statement sequentially tries the current character against all of
			// the valid transitions, each associated with one of the requirements
			if (currentChar >= 'A' && currentChar <= 'Z') {
				System.out.println("Upper case letter found");
				foundUpperCase = true;
			} else if (currentChar >= 'a' && currentChar <= 'z') {
				System.out.println("Lower case letter found");
				foundLowerCase = true;
			} else if (currentChar >= '0' && currentChar <= '9') {
				System.out.println("Digit found");
				foundNumericDigit = true;
			} else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
				System.out.println("Special character found");
				foundSpecialChar = true;
			} else {
				foundUpperCase = foundLowerCase = foundNumericDigit = foundSpecialChar = false;
				
				running = false;
				
			}
			
			// Go to the next character if there is one
			currentCharNdx++;
			
				
			
			System.out.println();
		}
		
		//update UI labels with validation results
		 ViewFirstAdmin.label_UpperCase.setText(
			        "At least one uppercase letter - " + (foundUpperCase ? "Satisfied" : "Not yet Satisfied"));
			    ViewFirstAdmin.label_UpperCase.setTextFill(foundUpperCase ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);

			    ViewFirstAdmin.label_LowerCase.setText(
			        "At least one lowercase letter - " + (foundLowerCase ? "Satisfied" : "Not yet Satisfied"));
			    ViewFirstAdmin.label_LowerCase.setTextFill(foundLowerCase ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);

			    ViewFirstAdmin.label_NumericDigit.setText(
			        "At least one numeric digit - " + (foundNumericDigit ? "Satisfied" : "Not yet Satisfied"));
			    ViewFirstAdmin.label_NumericDigit.setTextFill(foundNumericDigit ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);

			    ViewFirstAdmin.label_SpecialChar.setText(
			        "At least one special character - " + (foundSpecialChar ? "Satisfied" : "Not yet Satisfied"));
			    ViewFirstAdmin.label_SpecialChar.setTextFill(foundSpecialChar ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);

			    ViewFirstAdmin.label_LongEnough.setText(
			        "At least 8 characters - " + (foundLongEnough ? "Satisfied" : "Not yet Satisfied"));
			    ViewFirstAdmin.label_LongEnough.setTextFill(foundLongEnough ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);

			    ViewFirstAdmin.label_ShortEnough.setText(
			        "At most 16 characters - " + (foundShortEnough ? "Satisfied" : "Not yet Satisfied"));
			    ViewFirstAdmin.label_ShortEnough.setTextFill(foundShortEnough ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);

			    // Show valid password if all satisfied
			    boolean allValid = foundUpperCase && foundLowerCase && foundNumericDigit && foundSpecialChar && foundLongEnough && foundShortEnough;
		
	  

	    return allValid;
	}
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}	
}

