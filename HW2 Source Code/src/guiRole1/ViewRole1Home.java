package guiRole1;


import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;

import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListCell;
import java.util.List;
/*******
 * <p> Title: GUIReviewerHomePage Class. </p>
 * 
 * <p> Description: The JavaFX-based Student Home Page. Provides the UI for students to
 * create, view, update, delete, search, and reply to posts and replies. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-20 Initial version
 * @version 2.00        2026-02-25 Full post and reply CRUD, search, read and unread tracking
 */

public class ViewRole1Home {
	
	/*-*******************************************************************************************

	Attributes
	
	 */
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI ARea 2: This has all the buttons for the user to create, read, update and delete posts and replies.
	//             It also enables a user to search for posts with a specific keyword
	
	protected static Button button_CreatePost = new Button("Create Post");
	protected static Button button_ViewAllPostsReplies = new Button("View Posts & Replies");

	protected static Button button_ViewUnreadPosts = new Button("View Unread Posts");
	protected static Button button_ViewUnreadReplies = new Button("View Unread Replies");

	protected static Button button_ViewReadPosts = new Button("View Read Posts");
	protected static Button button_ViewReadReplies = new Button("View Read Replies");

	protected static Button button_UpdatePost = new Button("Update Post");
	protected static Button button_DeletePost = new Button("Delete Post");
	protected static Button button_replyButton = new Button("Reply To Selected Post");
	protected static Button button_UpdateReply = new Button("Update Reply");
	protected static Button button_DeleteReply = new Button("Delete Reply");
	private static ListView<Post> postListView = new ListView<>();
	
	
	protected static Button button_Search = new Button("Search Posts");
	protected static TextField searchField = new TextField();
	protected static javafx.scene.control.ComboBox<String> searchThreadCombo = new javafx.scene.control.ComboBox<>();
	protected static Label label_Search = new Label("Keyword:");
	protected static Label label_SearchThread = new Label("Thread:");
	protected static Label label_PostsSection = new Label();
	protected static Label label_RepliesSection = new Label();
	protected static Label label_SearchSection = new Label();
	protected static Button button_ViewMyFeedback = new Button("View My Feedback");
	protected static ListView<String> listView_MyFeedback = new ListView<>();
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 825, width-20,825);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application and for
	// logging out.
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewRole1Home theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	
	protected static Stage theStage;			// The Stage that JavaFX has established for us	
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User
	

	private static Scene theViewRole1HomeScene;	// The shared Scene each invocation populates
	protected static final int theRole = 2;		// Admin: 1; Role1: 2; Role2: 3

	/*-*******************************************************************************************

	Constructors
	
	 */


	/**********
	 * <p> Method: displayRole1Home(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Role1 Home page to be displayed.
	 * 
	 * It first sets up every shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GIUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void displayRole1Home(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewRole1Home();		// Instantiate singleton if needed
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;
		
		label_UserDetails.setText("User: " + theUser.getUserName());
				
		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundations: Role1 Home Page");
		theStage.setScene(theViewRole1HomeScene);
		theStage.show();
	}
	
	/**********
	 * <p> Method: ViewRole1Home() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayRole2Home method.</p>
	 * 
	 */
	private ViewRole1Home() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theViewRole1HomeScene = new Scene(theRootPane, width, height);	// Create the scene
		
		// Set the title for the window
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Student Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> {ControllerRole1Home.performUpdate(); });
		
	
		
		
      
		
		label_PostsSection = new Label("── Posts ──────────────────────────────");
		setupLabelUI(label_PostsSection, "Dialog", 13, 800, Pos.BASELINE_LEFT, 40, 108);

		
		setupButtonUI(button_CreatePost, "Dialog", 16, 220, Pos.CENTER, 40, 130);
		button_CreatePost.setOnAction((_) -> { openCreatePostWindow(); });

		setupButtonUI(button_ViewAllPostsReplies, "Dialog", 16, 220, Pos.CENTER, 300, 130);
		button_ViewAllPostsReplies.setOnAction((_) -> { ControllerRole1Home.performViewAllPostsReplies(); });

		setupButtonUI(button_ViewUnreadPosts, "Dialog", 16, 220, Pos.CENTER, 560, 130);
		button_ViewUnreadPosts.setOnAction((_) -> { ControllerRole1Home.performViewUnreadPosts(); });

	
		setupButtonUI(button_ViewReadPosts, "Dialog", 16, 220, Pos.CENTER, 40, 200);
		button_ViewReadPosts.setOnAction((_) -> { ControllerRole1Home.performViewReadPosts(); });

		setupButtonUI(button_UpdatePost, "Dialog", 16, 220, Pos.CENTER, 300, 200);
		button_UpdatePost.setOnAction((_) -> {
		    Post selected = postListView.getSelectionModel().getSelectedItem();
		    if (selected == null) { displayMessage("Please select a post first."); return; }
		    ControllerRole1Home.performUpdatePost(selected);
		});

		setupButtonUI(button_DeletePost, "Dialog", 16, 220, Pos.CENTER, 560, 200);
		button_DeletePost.setOnAction((_) -> {
		    Post selected = postListView.getSelectionModel().getSelectedItem();
		    if (selected == null) { displayMessage("Please select a post first."); return; }
		    ControllerRole1Home.performDeletePost(selected);
		});

	
		label_RepliesSection = new Label("── Replies ─────────────────────────────");
		setupLabelUI(label_RepliesSection, "Dialog", 13, 800, Pos.BASELINE_LEFT, 40, 248);

		
		setupButtonUI(button_replyButton, "Dialog", 16, 220, Pos.CENTER, 40, 270);
		button_replyButton.setOnAction((_) -> {
		    Post selected = postListView.getSelectionModel().getSelectedItem();
		    if (selected == null) { displayMessage("Please select a post first."); return; }
		    openReplyWindow(selected.getId());
		});

		setupButtonUI(button_ViewUnreadReplies, "Dialog", 16, 220, Pos.CENTER, 300, 270);
		button_ViewUnreadReplies.setOnAction((_) -> { ControllerRole1Home.performViewUnreadReplies(); });

		setupButtonUI(button_ViewReadReplies, "Dialog", 16, 220, Pos.CENTER, 560, 270);
		button_ViewReadReplies.setOnAction((_) -> { ControllerRole1Home.performViewReadReplies(); });

		
		setupButtonUI(button_UpdateReply, "Dialog", 16, 220, Pos.CENTER, 40, 340);
		button_UpdateReply.setOnAction((_) -> {
		    Post selected = postListView.getSelectionModel().getSelectedItem();
		    if (selected == null) { displayMessage("Please select a post first."); return; }
		    ControllerRole1Home.performUpdateReply(selected);
		});

		setupButtonUI(button_DeleteReply, "Dialog", 16, 220, Pos.CENTER, 300, 340);
		button_DeleteReply.setOnAction((_) -> {
		    Post selected = postListView.getSelectionModel().getSelectedItem();
		    if (selected == null) { displayMessage("Please select a post first."); return; }
		    ControllerRole1Home.performDeleteReply(selected);
		});

		
		
		label_SearchSection = new Label("── Search ──────────────────────────────");
		setupLabelUI(label_SearchSection, "Dialog", 13, 800, Pos.BASELINE_LEFT, 40, 388);
        //keyword label and input field
		setupLabelUI(label_Search, "Dialog", 14, 80, Pos.BASELINE_LEFT, 40, 418);
		searchField.setLayoutX(125);
		searchField.setLayoutY(413);
		searchField.setMinWidth(200);
        //Thread Combo box
		setupLabelUI(label_SearchThread, "Dialog", 14, 60, Pos.BASELINE_LEFT, 340, 418);
		searchThreadCombo.getItems().addAll("All Threads", "General", "Lectures", "Sections", "Problem Sets", "Assignments", "Social");
		searchThreadCombo.setValue("All Threads");
		searchThreadCombo.setLayoutX(410);
		searchThreadCombo.setLayoutY(413);
		searchThreadCombo.setMinWidth(160);
		

		setupButtonUI(button_Search, "Dialog", 14, 140, Pos.CENTER, 590, 409);
		button_Search.setOnAction((_) -> { ControllerRole1Home.performSearchPosts(); });

		//Custom cell factory to display post content using post.format
		postListView.setCellFactory((_) -> new ListCell<Post>() {
		    protected void updateItem(Post post, boolean empty) {
		        super.updateItem(post, empty);
		        setText(empty || post == null ? null : post.format());
		    }
		});
		postListView.setPrefWidth(760);
		postListView.setPrefHeight(180);
		postListView.setLayoutX(40);
		postListView.setLayoutY(455);
		
		setupButtonUI(button_ViewMyFeedback, "Dialog", 16, 250, Pos.CENTER, 40, 660);
		button_ViewMyFeedback.setOnAction((_) -> { ControllerRole1Home.performViewMyFeedback(); });

		listView_MyFeedback.setLayoutX(40);
		listView_MyFeedback.setLayoutY(695);
		listView_MyFeedback.setPrefWidth(width - 80);
		listView_MyFeedback.setPrefHeight(120);
		listView_MyFeedback.setCellFactory(lv -> new ListCell<String>() {
		  
		    protected void updateItem(String item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty || item == null) { setText(null); return; }
		        setText(item);
		        setWrapText(true);
		        setFont(Font.font("Monospaced", 11));
		    }
		});
		// GUI Area 3
		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 40, 840);
		button_Logout.setOnAction((_) -> { ControllerRole1Home.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 840);
		button_Quit.setOnAction((_) -> { ControllerRole1Home.performQuit(); });

		// This is the end of the GUI initialization code
		
		// Place all of the widget items into the Root Pane's list of children
         theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
			button_CreatePost, button_ViewAllPostsReplies, label_PostsSection, label_RepliesSection,
		    button_ViewUnreadPosts, button_ViewUnreadReplies, button_ViewReadPosts, label_SearchSection,
		    button_ViewReadReplies, button_UpdatePost, button_DeletePost,
		    button_replyButton, postListView,button_UpdateReply, button_DeleteReply,
		    label_Search, searchField, label_SearchThread, searchThreadCombo, button_Search,
		    line_Separator4, button_Logout, button_Quit,button_ViewMyFeedback, listView_MyFeedback);
}
	/*******
	 * 
	 * <p> Method: openReplyWindow(int postId) </p>
	 * 
	 * <p> Description: opens a pop up window allowing the user to type a reply to the post
	 *     with the given postId. </p>
	 *     
	 * @param postId the Id of the post being replied to.
	 */
	
	
	private void openReplyWindow(int postId) {
	    Stage stage = new Stage();
	    stage.setTitle("Reply To Post");

	    Label replyLabel    = new Label("Reply:");
	    TextArea replyArea  = new TextArea();
	    Button submitButton = new Button("Submit Reply");

	    Pane pane = new Pane();

	    
	    replyLabel.setLayoutX(20);   replyLabel.setLayoutY(20);
	    replyArea.setLayoutX(20);    replyArea.setLayoutY(45);
	    replyArea.setPrefWidth(360); replyArea.setPrefHeight(150);
	    submitButton.setLayoutX(20); submitButton.setLayoutY(210);

	    pane.getChildren().addAll(replyLabel, replyArea, submitButton);

	    submitButton.setOnAction((_) -> {
	        String replyText = replyArea.getText();
	        if (replyText == null || replyText.isBlank()) {
	            displayMessage("Reply cannot be empty.");
	            return;
	        }
	        ControllerRole1Home.performCreateReply(postId, replyText);
	        stage.close();
	    });

	    stage.setScene(new Scene(pane, 400, 260));
	    stage.show();
	}
	
	/*******
	 * <p> MEthod: openCreatePostWindow() </p>
	 * 
	 * <p> Description: opens a pop up window for creating a new post. The user enters a title,
	 *     selects a thread from the list and types a body. </p>
	 */
	protected static void openCreatePostWindow() {

	    Stage stage = new Stage();
	    stage.setTitle("Create Post");

	    Label titleLabel = new Label("Title:");
	    TextField titleField = new TextField();

	    Label threadLabel = new Label("Thread:");
	     javafx.scene.control.ComboBox<String> threadCombo = new javafx.scene.control.ComboBox<>();
	    threadCombo.getItems().addAll("General", "Lectures", "Sections", "Problem Sets", "Assignments", "Social");
	    threadCombo.setValue("General");
	    threadCombo.setLayoutX(20);
	    threadCombo.setLayoutY(105);
	    threadCombo.setMinWidth(200);
	    
	    Label bodyLabel = new Label("Body:");
	    TextArea bodyArea = new TextArea();
	    bodyArea.setPrefHeight(150);

	    Button saveButton = new Button("Save");
	    Button cancelButton = new Button("Cancel");

	    Pane pane = new Pane();

	    titleLabel.setLayoutX(20);  
	    titleLabel.setLayoutY(20);
	    titleField.setLayoutX(20);  
	    titleField.setLayoutY(45);
	    titleField.setMinWidth(300);

	    threadLabel.setLayoutX(20); 
	    threadLabel.setLayoutY(80);
	    

	    bodyLabel.setLayoutX(20);  
	    bodyLabel.setLayoutY(140);
	    bodyArea.setLayoutX(20);  
	    bodyArea.setLayoutY(165);
	    bodyArea.setMinWidth(350);

	    saveButton.setLayoutX(20); 
	    saveButton.setLayoutY(330);
	    cancelButton.setLayoutX(120); 
	    cancelButton.setLayoutY(330);

	    pane.getChildren().addAll(
	            titleLabel, titleField,
	            threadLabel,threadCombo,
	            bodyLabel, bodyArea,
	            saveButton, cancelButton
	    );

	    saveButton.setOnAction((_)-> {

	    	String title = titleField.getText();
	    	String body = bodyArea.getText();
	    	 String thread = threadCombo.getValue();
	    	 //validate that post title and body cannot be empty
	    	 if (title == null || title.isBlank()) {
	    	        displayMessage("Post title cannot be empty.");
	    	        return;
	    	    }
	    	    if (body == null || body.isBlank()) {
	    	        displayMessage("Post body cannot be empty.");
	    	        return;
	    	    }
	      
	        ControllerRole1Home.performCreatePost(title, body, thread);

	        stage.close();
	    });

	    cancelButton.setOnAction((_) -> stage.close());

	    Scene scene = new Scene(pane, 400, 400);
	    stage.setScene(scene);
	    stage.show();
	}
	
	/*******
	 *<p> Method: openUpdatePostWindow(Post post) </p>
	 * 
	 *<p> Description: Opens a pop-up window prefilled with the selected post's title
	 * and body, allowing the user to edit and save changes. </p>
	 * 
	 * @param post the Post object to be updated
	 */

	public static void openUpdatePostWindow(Post post) {
	    Stage stage = new Stage();
	    stage.setTitle("Update Post");

	    Label titleLabel = new Label("Title:");
	    TextField titleField = new TextField(post.getTitle());

	    Label bodyLabel = new Label("Body:");
	    TextArea bodyArea = new TextArea(post.getBody());
	    bodyArea.setPrefHeight(150);

	    Button saveButton = new Button("Save");
	    Button cancelButton = new Button("Cancel");

	    Pane pane = new Pane();

	    titleLabel.setLayoutX(20); 
	    titleLabel.setLayoutY(20);
	    titleField.setLayoutX(20); 
	    titleField.setLayoutY(45);
	    titleField.setMinWidth(300);

	    bodyLabel.setLayoutX(20); 
	    bodyLabel.setLayoutY(90);
	    bodyArea.setLayoutX(20);  
	    bodyArea.setLayoutY(115); 
	    bodyArea.setMinWidth(350);

	    saveButton.setLayoutX(20);  
	    saveButton.setLayoutY(280);
	    cancelButton.setLayoutX(120);
	    cancelButton.setLayoutY(280);

	    pane.getChildren().addAll(titleLabel, titleField, bodyLabel, bodyArea,saveButton, cancelButton);

	    saveButton.setOnAction((_) -> {
	        String newTitle = titleField.getText();
	        String newBody = bodyArea.getText();
	        //validate that post body and title cannot be empty
	        if (newTitle == null || newTitle.isBlank()) {
	            displayMessage("Title cannot be empty.");
	            return;
	        }
	        if (newBody == null || newBody.isBlank()) {
	            displayMessage("Body cannot be empty.");
	            return;
	        }
	        try {
	            boolean updated = theDatabase.updatePost(
	                post.getId(), newTitle, newBody, theUser.getUserName());
	            if (updated) {
	                displayMessage("Post updated successfully.");
	                stage.close();
	                // Refresh list
	                ControllerRole1Home.performViewAllPostsReplies();
	            } else {
	                displayMessage("Failed to update post.");
	            }
	        } catch (Exception ex) {
	            displayMessage("Error updating post: " + ex.getMessage());
	        }
	    });

	    cancelButton.setOnAction((_) -> stage.close());

	    stage.setScene(new Scene(pane, 400, 420));
	    stage.show();
	}
	
    /*******
     *<p> Method: openUpdateReplyWindow(Reply reply, Post post) </p>
     *
     *<p> Description: Opens a pop-up window prefilled with the selected reply's body,
	 * allowing the user to edit and save changes. </p>
	 * 
     * @param reply the Reply object to be updated
     * @param post  the parent Post of the reply
     */
	public static void openUpdateReplyWindow(Reply reply, Post post) {
	    Stage stage = new Stage();
	    stage.setTitle("Update Reply");

	    Label bodyLabel = new Label("Reply:");
	    TextArea bodyArea = new TextArea(reply.getBody());
	    bodyArea.setPrefHeight(150);

	    Button saveButton = new Button("Save");
	    Button cancelButton = new Button("Cancel");

	    Pane pane = new Pane();

	    bodyLabel.setLayoutX(20); 
	    bodyLabel.setLayoutY(20);
	    bodyArea.setLayoutX(20); 
	    bodyArea.setLayoutY(45);  
	    bodyArea.setMinWidth(350);

	    saveButton.setLayoutX(20);  
	    saveButton.setLayoutY(210);
	    cancelButton.setLayoutX(120); 
	    cancelButton.setLayoutY(210);

	    pane.getChildren().addAll(bodyLabel, bodyArea, saveButton, cancelButton);

	    saveButton.setOnAction((_) -> {
	        String newBody = bodyArea.getText();
	        //validate that reply body cannot be empty.
	        if (newBody == null || newBody.isBlank()) {
	            displayMessage("Reply body cannot be empty.");
	            return;
	        }
	        try {
	            boolean updated = theDatabase.updateReply(
	                reply.getId(), newBody, theUser.getUserName());
	            if (updated) {
	                displayMessage("Reply updated successfully.");
	                stage.close();
	                // Refresh list
	                ControllerRole1Home.performViewAllPostsReplies();
	            } else {
	                displayMessage("Failed to update reply.");
	            }
	        } catch (Exception ex) {
	            displayMessage("Error updating reply: " + ex.getMessage());
	        }
	    });

	    cancelButton.setOnAction((_) -> stage.close());

	    stage.setScene(new Scene(pane, 400, 260));
	    stage.show();
	}
	
	/*******
	 * <p> Method: openSelectReplyWindow(List replies, Post post, boolean isDelete) </p>
	 * 
	 * <p> Description: Opens a pop up window showing a list of the user's replies on a post,
	 * allowing them to select which reply to update or delete. </p>
	 * 
	 * @param replies the list of Reply objects authored by the current user on this post
	 * @param post the parent Post
	 * @param isDelete true if deleting, false if updating
	 */
	public static void openSelectReplyWindow(List<entityClasses.Reply> replies, Post post, boolean isDelete) {
	    Stage stage = new Stage();
	    stage.setTitle(isDelete ? "Select Reply to Delete" : "Select Reply to Update");

	    Label label = new Label("Select a reply:");
	    label.setLayoutX(20);
	    label.setLayoutY(20);
         
	    //display each reply's body in the list
	    ListView<entityClasses.Reply> replyListView = new ListView<>();
	    replyListView.setCellFactory((_) -> new ListCell<entityClasses.Reply>() {
	      
	        protected void updateItem(entityClasses.Reply reply, boolean empty) {
	            super.updateItem(reply, empty);
	            setText(empty || reply == null ? null : reply.getBody());
	        }
	    });
	    replyListView.getItems().addAll(replies);
	    replyListView.setLayoutX(20);
	    replyListView.setLayoutY(50);
	    replyListView.setPrefWidth(360);
	    replyListView.setPrefHeight(200);
        
	    
	    Button confirmButton = new Button(isDelete ? "Delete" : "Update");
	    Button cancelButton = new Button("Cancel");
	    confirmButton.setLayoutX(20);
	    confirmButton.setLayoutY(265);
	    cancelButton.setLayoutX(120);
	    cancelButton.setLayoutY(265);

	    confirmButton.setOnAction((_) -> {
	        entityClasses.Reply selected = replyListView.getSelectionModel().getSelectedItem();
	        if (selected == null) {
	            displayMessage("Please select a reply.");
	            return;
	        }
	        stage.close();
	        if (isDelete) {
	            try {
	                boolean deleted = theDatabase.deleteReply(
	                    selected.getId(), theUser.getUserName());
	                if (deleted) {
	                    displayMessage("Reply deleted successfully.");
	                    ControllerRole1Home.performViewAllPostsReplies();
	                } else {
	                    displayMessage("Failed to delete reply.");
	                }
	            } catch (Exception ex) {
	                displayMessage("Error deleting reply: " + ex.getMessage());
	                ex.printStackTrace();
	            }
	        } else {
	            openUpdateReplyWindow(selected, post);
	        }
	    });

	    cancelButton.setOnAction((_) -> stage.close());

	    Pane pane = new Pane();
	    pane.getChildren().addAll(label, replyListView, confirmButton, cancelButton);

	    stage.setScene(new Scene(pane, 400, 310));
	    stage.show();
	}

	
   /*******
    * <p> Method: displayMessage(String message) </p>
    * 
    * <p> Description: Displays an alert dialog with the given message. </p>
    * 
    * @param message the message text to display
    */
	public static void displayMessage(String message) {
	    Alert alert = new Alert(AlertType.INFORMATION);
	    alert.setContentText(message);
	    alert.showAndWait();
	}

	
	/*******
	 *  <p> Method: getPostListView() </p>
	 *  
	 *  <p> Description: Returns the shared ListView used to display posts and replies. </p>
	 * @return the postListView instance
	 */
	public static ListView<Post> getPostListView() {
	    return postListView;
	}
	
	/*******
	 * <p> Method: getSearchKeyword() </p>
	 * 
	 * <p> Description: Returns the current text in the keyword search field. </p>
	 * @return the search keyword string
	 */
	public static String getSearchKeyword() { 
		return searchField.getText(); 
		}
     
	/*******
	 *  <p> Method: getSearchThread() </p>
	 *  
	 *  <p> Description: Returns the selected thread from the search combo box.
	 * Returns an empty string if "All Threads" is selected or nothing is selected. </p>
	 * 
	 * @return the selected thread name, or empty string for all threads
	 */
	public static String getSearchThread() {
	    String selected = searchThreadCombo.getValue();
	    return (selected == null || selected.equals("All Threads")) ? "" : selected;
	}

	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, 
			double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, 
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
}
