package guiAdminHome;

import database.Database;
import entityClasses.User;
import java.util.List;
import entityClasses.AdminRequest;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	/**
	 * Default constructor is not used.
	 */
	public ControllerAdminHome() {
	}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations () {
		System.out.println("\n*** WARNING ***: Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Manage Invitations Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that allows the admin to send a one time password(OTP) to the selected user's email address.
	 *     After the user uses the OTP to login they are prompted to reset their password and then log in again with their new password</p>
	 */
	protected static void setOnetimePassword () {
		guiOTP.ViewOTP.displayOneTimePassword(ViewAdminHome.theStage, ViewAdminHome.theUser);
		
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void deleteUser(String username) {
		if(username == null) {
	        ViewAdminHome.alertNotImplemented.setTitle("Delete User Error");
	        ViewAdminHome.alertNotImplemented.setHeaderText("Invalid Username");
	        ViewAdminHome.alertNotImplemented.setContentText("Username cannot be empty.");
	        ViewAdminHome.alertNotImplemented.showAndWait();		
			
			return;
		}
		
		boolean deleted = theDatabase.deleteUser(username);
		
		if(deleted) {
			  ViewAdminHome.alertEmailSent.setTitle("User Deleted");
		        ViewAdminHome.alertEmailSent.setHeaderText("Success");
		        ViewAdminHome.alertEmailSent.setContentText("User '" + username + "' has been deleted.");
		        ViewAdminHome.alertEmailSent.showAndWait();
		}
		
		else {
	        ViewAdminHome.alertEmailError.setTitle("Delete Failed");
	        ViewAdminHome.alertEmailError.setHeaderText("User Not Found");
	        ViewAdminHome.alertEmailError.setContentText("No user found with username: " + username);
	        ViewAdminHome.alertEmailError.showAndWait();
	    }

	
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void listUsers() {
		List<User> userList = theDatabase.getAllUsers();
		StringBuilder report = new StringBuilder();
		//displaying titles for list
		report.append(String.format("%-15s | %-25s | %-25s | %-20s\n", 
                "Username", "Full Name", "Email Address", "Roles"));
		//show full name in list, for loop for every user in list
		for (User u : userList) {
            String fullName = u.getFirstName() + " " + u.getMiddleName() + " " + u.getLastName();
            
            // shows the roles of user
            StringBuilder roles = new StringBuilder();
            if (u.getAdminRole()) roles.append("Admin ");
            if (u.getNewRole1()) roles.append("Role1 ");
            if (u.getNewRole2()) roles.append("Role2 ");

            // format the line with spacing for readability
            report.append(String.format("%-15s | %-25s | %-25s | %-20s\n", 
                          u.getUserName(), fullName, u.getEmailAddress(), roles.toString()));
        }

        // sends full string to textArea
        ViewAdminHome.textArea_UserList.setText(report.toString());

	}
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	public static boolean invalidEmailAddress(String emailAddress) {
		if (emailAddress.length() == 0) {
			ViewAdminHome.alertEmailError.setContentText(
					"Correct the email address and try again.");
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		return false;
	}
	/*******
     * <p> Method: performViewStaffAdminRequests() </p>
     *
     * <p> Description: Opens the dedicated Admin Requests screen. </p>
     */
    protected static void performViewStaffAdminRequests() {
        ViewAdminRequests.displayAdminRequestsScreen(ViewAdminHome.theStage, ViewAdminHome.theUser);
    }
	
	/*******
     * <p> Method: performViewAllAdminRequests() </p>
     *
     * <p> Description: Loads and displays all AdminRequests (open and closed). </p>
     */
    protected static void performViewAllAdminRequests() {
        try {
            List<AdminRequest> allRequests = AdminRequest.getAllAdminRequests();
            ViewAdminRequests.getAdminRequestListView().getItems().clear();

            if (allRequests.isEmpty()) {
                ViewAdminHome.displayMessage("No AdminRequests found.");
                return;
            }

            ViewAdminRequests.getAdminRequestListView().getItems().addAll(allRequests);
        } catch (Exception e) {
            ViewAdminHome.displayMessage("Error loading AdminRequests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*******
     * <p> Method: performViewOpenAdminRequests() </p>
     *
     * <p> Description: Loads and displays all open AdminRequests. </p>
     */
    protected static void performViewOpenAdminRequests() {
        try {
            List<AdminRequest> openRequests = AdminRequest.getOpenAdminRequests();
            ViewAdminRequests.getAdminRequestListView().getItems().clear();

            if (openRequests.isEmpty()) {
                ViewAdminHome.displayMessage("No open AdminRequests found.");
                return;
            }

            ViewAdminRequests.getAdminRequestListView().getItems().addAll(openRequests);
        } catch (Exception e) {
            ViewAdminHome.displayMessage("Error loading open AdminRequests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*******
     * <p> Method: performViewClosedAdminRequests() </p>
     *
     * <p> Description: Loads and displays all closed AdminRequests. </p>
     */
    protected static void performViewClosedAdminRequests() {
        try {
            List<AdminRequest> closedRequests = AdminRequest.getClosedAdminRequests();
            ViewAdminRequests.getAdminRequestListView().getItems().clear();

            if (closedRequests.isEmpty()) {
                ViewAdminHome.displayMessage("No closed AdminRequests found.");
                return;
            }

            ViewAdminRequests.getAdminRequestListView().getItems().addAll(closedRequests);
        } catch (Exception e) {
            ViewAdminHome.displayMessage("Error loading closed AdminRequests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*******
     * <p> Method: performCloseAdminRequest(AdminRequest request, String adminNotes) </p>
     *
     * <p> Description: Allows an admin to document actions and close a request. </p>
     *
     * @param request the AdminRequest to close
     * @param adminNotes notes documenting the actions taken
     */
    protected static void performCloseAdminRequest(AdminRequest request, String adminNotes) {
        try {
            request.closeRequest(adminNotes);

            ViewAdminHome.displayMessage("AdminRequest closed successfully.");
            performViewAllAdminRequests();   // Refresh the list
        } catch (Exception e) {
            e.printStackTrace();
            ViewAdminHome.displayMessage("Error closing AdminRequest.");
        }
    }

	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
