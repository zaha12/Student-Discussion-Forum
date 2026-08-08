package guiRole2;
import entityClasses.PrivateFeedback;
import entityClasses.Post;
import entityClasses.Reply;
import java.sql.ResultSet;
import java.util.List;
import entityClasses.AdminRequest;
import javafx.scene.control.Alert;
import guiPrototype.ThreadManager;

/*******
 * <p> Title: ControllerRole2Home Class. </p>
 * 
 * <p> Description: The Java/FX-based Role 2 Home Page.  This class provides the controller
 * actions basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page is a stub for establish future roles for the application.
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

public class ControllerRole2Home {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerRole2Home() {
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: This method directs the user to the User Update Page so the user can change
	 * the user account attributes. </p>
	 * 
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewRole2Home.theStage, ViewRole2Home.theUser);
	}	
	
	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole2Home.theStage);
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
		System.exit(0);
	}
	
	/**********
     * <p> Method: performSendFeedback(String, int, int, String) </p>
     *
     * <p> Description: Creates and saves a new private feedback record from the currently
     * logged-in staff member to a specified recipient. </p>
     *
     *
     * @param recipientUsername the username of the feedback recipient
     * @param relatedPostId     the post this feedback concerns (-1 if reply-based)
     * @param relatedReplyId    the reply this feedback concerns (-1 if post-based)
     * @param message           the feedback message text
     */
    protected static void performSendFeedback(String recipientUsername,int relatedPostId,int relatedReplyId, String message) {
                                             
        if (message == null || message.isBlank()) {
            ViewRole2Home.displayStatusMessage("Feedback message cannot be empty.");
            return;
        }
        if (recipientUsername == null || recipientUsername.isBlank()) {
            ViewRole2Home.displayStatusMessage("Please specify a recipient username.");
            return;
        }
        try {
            String sender = ViewRole2Home.theUser.getUserName();
            PrivateFeedback fb = new PrivateFeedback(  sender, recipientUsername, relatedPostId, relatedReplyId, message);
            ViewRole2Home.theDatabase.createPrivateFeedback(fb);
            ViewRole2Home.displayStatusMessage( "Feedback sent to " + recipientUsername + " successfully.");
               
            performViewMyFeedback(); // Refresh the feedback list after sending
        } catch (Exception e) {
            e.printStackTrace();
            ViewRole2Home.displayStatusMessage("Error sending feedback: " + e.getMessage());
        }
    }

   
    /**********
     * <p> Method: performViewMyFeedback() </p>
     *
     * <p> Description: Loads all private feedback sent by the currently logged-in staff
     * member into ViewRole2Home#listView_Feedback. </p>
     */
    protected static void performViewMyFeedback() {
        try {
            String sender = ViewRole2Home.theUser.getUserName();
            List<PrivateFeedback> list = ViewRole2Home.theDatabase.getFeedbackBySender(sender);

            ViewRole2Home.listView_Feedback.getItems().clear();

            if (list.isEmpty()) {
                ViewRole2Home.displayStatusMessage("No feedback sent yet.");
                return;
            }

            ViewRole2Home.listView_Feedback.getItems().addAll(list);

        } catch (Exception e) {
            e.printStackTrace();
            ViewRole2Home.displayStatusMessage("Error loading feedback: " + e.getMessage());
        }
    }
     
    /**********
     * <p> Method: performUpdateFeedback(PrivateFeedback, String) </p>
     *
     * <p> Description: Updates the message of an existing private feedback record.
     * Only the original sender (the currently logged-in staff member) may update
     * their own feedback — ownership is enforced here before calling the database. </p>
     *
     * @param feedback       the record to update
     * @param updatedMessage the new message text
     */
    protected static void performUpdateFeedback(PrivateFeedback feedback, String updatedMessage) {
        if (updatedMessage == null || updatedMessage.isBlank()) {
            ViewRole2Home.displayStatusMessage("Updated message cannot be empty.");
            return;
        }
        // Ownership check; only the original sender may edit
        String currentUser = ViewRole2Home.theUser.getUserName();
        if (!feedback.getSenderUsername().equals(currentUser)) {
            ViewRole2Home.displayStatusMessage("You can only edit your own feedback.");
            return;
        }
        try {
            ViewRole2Home.theDatabase.updatePrivateFeedback(feedback.getId(), updatedMessage);
            ViewRole2Home.displayStatusMessage("Feedback updated successfully.");
            performViewMyFeedback(); // Refresh list to show updated text
        } catch (Exception e) {
            e.printStackTrace();
            ViewRole2Home.displayStatusMessage("Error updating feedback: " + e.getMessage());
        }
    }

   

    /**********
     * <p> Method: performDeleteFeedback(PrivateFeedback) </p>
     *
     * <p> Description: Permanently deletes a private feedback record.
     * Only the original sender (the currently logged-in staff member) may delete
     * their own feedback — ownership is enforced here before calling the database. </p>
     *
     * @param feedback the record to delete
     *
     */
    protected static void performDeleteFeedback(PrivateFeedback feedback) {
        // Ownership check; only the original sender may delete
        String currentUser = ViewRole2Home.theUser.getUserName();
        if (!feedback.getSenderUsername().equals(currentUser)) {
            ViewRole2Home.displayStatusMessage("You can only delete your own feedback.");
            return;
        }
        try {
            ViewRole2Home.theDatabase.deletePrivateFeedback(feedback.getId());
            ViewRole2Home.displayStatusMessage("Feedback deleted.");
            performViewMyFeedback(); // Refresh list after deletion
        } catch (Exception e) {
            e.printStackTrace();
            ViewRole2Home.displayStatusMessage("Error deleting feedback: " + e.getMessage());
        }
    }

   
    /**********
     * <p> Method: performViewAllPosts() </p>
     *
     * <p> Description: Loads all posts with their replies into
     *  ViewRole2Home#listView_Posts for staff browsing. Staff can read every
     * post and reply regardless of author to assess the discussion atmosphere before
     * deciding to send private feedback. </p>
     *
     * <p> Called automatically on page load and when the staff member clicks "Refresh Posts". </p>
     */
    protected static void performViewAllPosts() {
        try {
            ViewRole2Home.listView_Posts.getItems().clear();
            var posts = ViewRole2Home.theDatabase.getAllPostsWithReplies();
            if (posts == null || posts.isEmpty()) {
                ViewRole2Home.displayStatusMessage("No posts found.");
                return;
            }
            ViewRole2Home.listView_Posts.getItems().addAll(posts);
        } catch (Exception e) {
            e.printStackTrace();
            ViewRole2Home.displayStatusMessage("Error loading posts: " + e.getMessage());
        }
    }
    /*******
     * <p> Method: performCreateAdminRequest(String title, String description) </p>
     *
     * <p> Description: Creates and submits a new AdminRequest by the currently logged-in staff member. </p>
     *
     * @param title the short title of the admin request
     * @param description the detailed description of the issue or action needed
     */
    protected static void performCreateAdminRequest(String title, String description) {
        String username = ViewRole2Home.theUser.getUserName();

        try {
            AdminRequest newRequest = new AdminRequest(title, description, username);
            AdminRequest.addAdminRequestToDBAndList(ViewRole2Home.theDatabase, newRequest);

            ViewRole2Home.displayMessage("AdminRequest submitted successfully.");
            // Refresh the list to show the new request immediately
            performViewOpenAdminRequests();
        } catch (Exception e) {
            e.printStackTrace();
            ViewRole2Home.displayMessage("Error submitting AdminRequest.");
        }
    }

    /*******
     * <p> Method: performViewOpenAdminRequests() </p>
     *
     * <p> Description: Loads and displays all open/in-progress AdminRequests in the ListView. </p>
     */
    public static void performViewOpenAdminRequests() {
        try {
            List<AdminRequest> openRequests = AdminRequest.getOpenAdminRequests();
            ViewRole2Home.getAdminRequestListView().getItems().clear();

            if (openRequests.isEmpty()) {
                ViewRole2Home.displayMessage("No open AdminRequests found.");
                return;
            }

            ViewRole2Home.getAdminRequestListView().getItems().addAll(openRequests);
        } catch (Exception e) {
            ViewRole2Home.displayMessage("Error loading open AdminRequests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*******
     * <p> Method: performViewClosedAdminRequests() </p>
     *
     * <p> Description: Loads and displays all closed AdminRequests in the ListView. </p>
     */
    public static void performViewClosedAdminRequests() {
        try {
            List<AdminRequest> closedRequests = AdminRequest.getClosedAdminRequests();
            ViewRole2Home.getAdminRequestListView().getItems().clear();

            if (closedRequests.isEmpty()) {
                ViewRole2Home.displayMessage("No closed AdminRequests found.");
                return;
            }

            ViewRole2Home.getAdminRequestListView().getItems().addAll(closedRequests);
        } catch (Exception e) {
            ViewRole2Home.displayMessage("Error loading closed AdminRequests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*******
     * <p> Method: performViewAllAdminRequests() </p>
     *
     * <p> Description: Loads and displays all AdminRequests (open and closed).
     * Shows no message when called during login (silent refresh). 
     * Shows message only when user manually clicks the button. </p>
     */
    public static void performViewAllAdminRequests() {
        try {
            List<AdminRequest> allRequests = AdminRequest.getAllAdminRequests();
            ViewRole2Home.getAdminRequestListView().getItems().clear();

            if (allRequests.isEmpty()) {
                // Silent - do NOT show message when called on login
                // Only show message if user manually clicks "View All Requests"
                return;
            }

            ViewRole2Home.getAdminRequestListView().getItems().addAll(allRequests);
        } catch (Exception e) {
            ViewRole2Home.displayMessage("Error loading AdminRequests: " + e.getMessage());
            e.printStackTrace();
        }
    } 



    /*******
     * <p> Method: performReopenAdminRequest(AdminRequest closedRequest, String newDescription) </p>
     *
     * <p> Description: Reopens a closed request with an updated description and links back to original. </p>
     *
     * @param closedRequest the closed request to reopen
     * @param newDescription updated description for the reopened request
     */
    protected static void performReopenAdminRequest(AdminRequest closedRequest, String newDescription) {
        try {
            closedRequest.reopenRequest(newDescription, closedRequest.getId());
            // In a full implementation, save the change to database

            ViewRole2Home.displayMessage("AdminRequest reopened successfully.");
            performViewOpenAdminRequests();   // Show it back in the open list
        } catch (Exception e) {
            e.printStackTrace();
            ViewRole2Home.displayMessage("Error reopening AdminRequest.");
        }
    }

    /*******
     * <p> Method: performRefreshAdminRequests() </p>
     *
     * <p> Description: Convenience method to refresh the current view (used after create/close/reopen). </p>
     */
    protected static void performRefreshAdminRequests() {
        performViewAllAdminRequests();
    }
    
    /**********
	 * <p> Method: handleCreateThread(String role, String threadName) </p>
	 * <p> Description: Validates input and verifies authorization for thread creation. 
	 * If authorized, it adds the new thread to the database and refreshes the view. </p>
	 * @param role The role of the user attempting the action
	 * @param threadName The title for the new thread provided by the UI TextField
	 */
	protected static void handleCreateThread(String role, String threadName) {
	    if (ThreadManager.isAuthorizedForThreadAction(role, "create")) {
	        
	        boolean success = applicationMain.FoundationsMain.database.createNewThread(threadName);

	        if (success) {
	            // Show the confirmation first
	            showAlert(Alert.AlertType.INFORMATION, "Success", "Thread Created", "New thread is live.");

	            ViewRole2Home.displayRole2Home(ViewRole2Home.theStage, ViewRole2Home.theUser);
	            
	            // Clear the text field for the next entry
	            ViewRole2Home.field_NewThreadName.clear();
	        } 
	        
	    }
	}
	
	/**********
	 * <p> Method: handleDeleteThread(String role, String threadTitle) </p>
	 * <p> Description: Verifies staff authority and executes the deletion 
	 * of a selected thread and its associated posts from the database memory. </p>
	 * @param role The role of the user attempting the action
	 * @param threadTitle The title of the thread selected from the ComboBox
	 */
	protected static void handleDeleteThread(String role, String threadTitle) {
		// 1. Ensure a selection exists
		if (threadTitle == null || threadTitle.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "Selection Error", "No Thread Selected", 
					"Please select a thread from the dropdown to delete.");
			return;
		}

		// 2. Authorization check
	    if (guiPrototype.ThreadManager.isAuthorizedForThreadAction(role, "delete")) {
	        
	        // 3. Execute cascading deletion in the central Database instance
	        boolean success = applicationMain.FoundationsMain.database.deleteThreadByTitle(threadTitle);

	        if (success) {
	        	// 4. Force UI refresh to remove the deleted item from the view dropdown
	            ViewRole2Home.displayRole2Home(ViewRole2Home.theStage, ViewRole2Home.theUser);
	            
	            showAlert(Alert.AlertType.INFORMATION, "Success", "Cascading Delete Complete", 
	            		"The thread '" + threadTitle + "' and all its posts have been removed.");
	        }
	    } else {
	    	showSecurityError();
	    }
	}

	/**********
	 * <p> Method: showSecurityError() </p>
	 * <p> Description: Displays a standardized error message when unauthorized access is detected. </p>
	 */
	private static void showSecurityError() {
		showAlert(Alert.AlertType.ERROR, "Security Violation", "Access Denied", 
				"You do not have the required permissions for this action.");
	}
	
	/**********
	 * <p> Method: showAlert(AlertType type, String title, String header, String content) </p>
	 * <p> Description: Internal helper method to display JavaFX alerts for user feedback. </p>
	 * @param type The type of alert to display
	 * @param title The title of the alert window
	 * @param header The header text for the alert
	 * @param content The main message body
	 */
	private static void showAlert(Alert.AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

}


