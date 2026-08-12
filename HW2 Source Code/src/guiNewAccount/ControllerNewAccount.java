package guiNewAccount;



import java.sql.SQLException;

import database.Database;
import entityClasses.User;
import guiFirstAdmin.ViewFirstAdmin;
import userNameRecognizerTestbed.UserNameRecognizer;

/*******
 * <p> Title: ControllerNewAccount Class. </p>
 * 
 * <p> Description: The Java/FX-based New Account Page.  This class provides the controller actions
 * to allow the user to establish a new account after responding to an invitation and the use of a
 * one time code.
 * 
 * The controller deals with the user pressing the "User Step" button widget being click.  If also
 * supports the user click on the "Quit" button widget.
 */

public class ControllerNewAccount {
	
	/*-********************************************************************************************

	The User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	/**
	 * Default constructor is not used.
	 */
	public ControllerNewAccount() {
	}
	
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	
	
	

	
	/**********
	 * <p> Method: public doCreateUser() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the User Setup
	 * button.  This method checks the input fields to see that they are valid.  If so, it then
	 * creates the account by adding information to the database.
	 * 
	 * The method reaches batch to the view page and to fetch the information needed rather than
	 * passing that information as parameters.
	 * 
	 */	
	protected static void doCreateUser() {
		
		// Fetch the username and password. (We use the first of the two here, but we will validate
		// that the two password fields are the same before we do anything with it.)
		String username = ViewNewAccount.text_Username.getText();
		String password = ViewNewAccount.text_Password1.getText();
		// check if the username fulfills all the requirements
		String errmessage = UserNameRecognizer.checkForValidUserName(username);
		if (errmessage != "") {
			// Display the error message
			System.out.println(errmessage);
			ViewNewAccount.text_Username.setText("");
			ViewNewAccount.alertInvalidUsernameError.setTitle("Invalid Username");
			ViewNewAccount.alertInvalidUsernameError.setHeaderText("Invalid Username");
			ViewNewAccount.alertInvalidUsernameError.setContentText(errmessage);
            ViewNewAccount.alertInvalidUsernameError.getDialogPane().setPrefHeight(300);
			ViewNewAccount.alertInvalidUsernameError.showAndWait();
			return;
			}
		  
		// Display key information to the log
		System.out.println("** Account for Username: " + username + "; theInvitationCode: "+
				ViewNewAccount.theInvitationCode + "; email address: " + 
				ViewNewAccount.emailAddress + "; Role: " + ViewNewAccount.theRole);
		
		// Initialize local variables that will be created during this process
		int roleCode = 0;
		User user = null;
		
        //check if the password is valid
		if (!ValidatePassword(password)) {
	        ViewNewAccount.text_Password1.setText("");
	        ViewNewAccount.text_Password2.setText("");
	        return;
	    }
		// Make sure the two passwords are the same.	
		if (ViewNewAccount.text_Password1.getText().
				compareTo(ViewNewAccount.text_Password2.getText()) == 0) {
			
			// The passwords match so we will set up the role and the User object base on the 
			// information provided in the invitation
			if (ViewNewAccount.theRole.compareTo("Admin") == 0) {
				roleCode = 1;
				user = new User(username, password, "", "", "", "", "", true, false, false);
			} else if (ViewNewAccount.theRole.compareTo("Role1") == 0) {
				roleCode = 2;
				user = new User(username, password, "", "", "", "", "", false, true, false);
			} else if (ViewNewAccount.theRole.compareTo("Role2") == 0) {
				roleCode = 3;
				user = new User(username, password, "", "", "", "", "", false, false, true);
			} else {
				System.out.println(
						"**** Trying to create a New Account for a role that does not exist!");
				System.exit(0);
			}
			
			// Unlike the FirstAdmin, we know the email address, so set that into the user as well.
        	user.setEmailAddress(ViewNewAccount.emailAddress);

        	// Inform the system about which role will be played
			applicationMain.FoundationsMain.activeHomePage = roleCode;
			
        	// Create the account based on user and proceed to the user account update page
            try {
            	// Create a new User object with the pre-set role and register in the database
            	theDatabase.register(user);
            } catch (SQLException e) {
                System.err.println("*** ERROR *** Database error: " + e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
            
            // The account has been set, so remove the invitation from the system
            theDatabase.removeInvitationAfterUse(
            		ViewNewAccount.text_Invitation.getText());
            
            // Set the database so it has this user and the current user
            theDatabase.getUserAccountDetails(username);

            // Navigate to the Welcome Login Page
            guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewNewAccount.theStage, user);
		}
		else {
			// The two passwords are NOT the same, so clear the passwords, explain the passwords
			// must be the same, and clear the message as soon as the first character is typed.
			ViewNewAccount.text_Password1.setText("");
			ViewNewAccount.text_Password2.setText("");
			ViewNewAccount.alertUsernamePasswordError.showAndWait();
			//ViewFirstAdmin.ResetAssessments(); //Reset the password requirements to red.
		}
	}

	/*******
	 * <p> Method: ValidatePassword(String password) </p>
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
	protected static boolean ValidatePassword(String password) {
        // flags for each password requirement
		boolean foundUpperCase = false;
	    boolean foundLowerCase = false;
		boolean foundNumericDigit = false;
		boolean foundSpecialChar = false;
		boolean foundLongEnough = false;
		boolean foundShortEnough = false;	 
		
		 char currentChar;					// The current character in the line
		int currentCharNdx;					// The index of the current character
		boolean running;						// The flag that specifies if the FSM is running
		 												

		// The following are the local variable used to perform the Directed Graph simulation
				
					
		currentCharNdx = 0;					// The index of the current character
		
		// return false if password is empty
		if(password.length() <= 0) {
			return false;
		}
		
		
		

		
		// This flag determines whether the directed graph (FSM) loop is operating or not
		running = true;						// Start the loop
		
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
		ViewNewAccount.label_UpperCase.setText(
			        "At least one uppercase letter - " + (foundUpperCase ? "Satisfied" : "Not yet Satisfied"));
		ViewNewAccount.label_UpperCase.setTextFill(foundUpperCase ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);

		ViewNewAccount.label_LowerCase.setText(
			        "At least one lowercase letter - " + (foundLowerCase ? "Satisfied" : "Not yet Satisfied"));
		ViewNewAccount.label_LowerCase.setTextFill(foundLowerCase ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);

		ViewNewAccount.label_NumericDigit.setText(
			        "At least one numeric digit - " + (foundNumericDigit ? "Satisfied" : "Not yet Satisfied"));
		ViewNewAccount.label_NumericDigit.setTextFill(foundNumericDigit ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);

		ViewNewAccount.label_SpecialChar.setText(
			        "At least one special character - " + (foundSpecialChar ? "Satisfied" : "Not yet Satisfied"));
		ViewNewAccount.label_SpecialChar.setTextFill(foundSpecialChar ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);

		ViewNewAccount.label_LongEnough.setText(
			        "At least 8 characters - " + (foundLongEnough ? "Satisfied" : "Not yet Satisfied"));
		ViewNewAccount.label_LongEnough.setTextFill(foundLongEnough ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);

		ViewNewAccount.label_ShortEnough.setText(
			        "At most 16 characters - " + (foundShortEnough ? "Satisfied" : "Not yet Satisfied"));
		ViewNewAccount.label_ShortEnough.setTextFill(foundShortEnough ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);

			    // Show valid password if all satisfied
			    boolean allValid = foundUpperCase && foundLowerCase && foundNumericDigit && foundSpecialChar && foundLongEnough && foundShortEnough;
		
	  

	    return allValid;
	}
	
	
	/**********
	 * <p> Method: public performQuit() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the Quit button.  Doing
	 * this terminates the execution of the application.  All important data must be stored in the
	 * database, so there is no cleanup required.  (This is important so we can minimize the impact
	 * of crashed.)
	 * 
	 */	
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}	
}
