package guiRole2;

import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import java.util.List;
//import database.Database;
import entityClasses.User;
import entityClasses.PrivateFeedback;
import entityClasses.AdminRequest;
import javafx.scene.control.ListView;

import entityClasses.Post;
import entityClasses.Reply;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListCell;
import java.util.ArrayList;

/*******
 * <p> Title: ViewRole2Home Class. </p>
 * 
 * <p> Description: The Java/FX-based Role2 Home Page.  The page is a stub for some role needed for
 * the application.  The widgets on this page are likely the minimum number and kind for other role
 * pages that may be needed.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-04-20 Initial version
 *  
 */

public class ViewRole2Home {
	
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

	// GUI ARea 2: This is a stub, so there are no widgets here.  For an actual role page, this are
	// would contain the widgets needed for the user to play the assigned role.
	

  
    protected static Label label_PostsTitle = new Label("All Posts & Replies");
    protected static ListView<Post> listView_Posts = new ListView<>();
    protected static Button button_SendFeedbackPost = new Button("Feedback on Post");
    protected static Button button_SendFeedbackReply = new Button("Feedback on Reply");
    protected static Button button_ViewAllPosts = new Button("Refresh Posts");
    protected static Label label_FeedbackTitle = new Label("Private Feedback");
    protected static ListView<PrivateFeedback> listView_Feedback = new ListView<>();
    protected static Button button_ViewMyFeedback = new Button("View My Feedback");
    protected static Button button_EditFeedback = new Button("Edit Selected");
    protected static Button button_DeleteFeedback = new Button("Delete Selected");
    protected static Button button_CreateAdminRequest = new Button("Submit New AdminRequest");
    protected static Button button_ViewAllAdminRequests = new Button("View All Requests");
    protected static Button button_ViewOpenAdminRequests = new Button("View Open Requests");
    protected static Button button_ViewClosedAdminRequests = new Button("View Closed Requests");

    protected static Button button_CloseRequest = new Button("Close Selected Request");
    protected static Button button_ReopenRequest = new Button("Reopen Selected Request");

    private static ListView<AdminRequest> adminRequestListView = new ListView<>();

    protected static Label label_AdminRequestsSection = new Label();

    /** Label for the Staff Management section header. */
	protected static Label label_StaffTools = new Label("Staff Management Tools");
	/** Label identifying the thread selection dropdown. */
	protected static Label label_SelectThread = new Label("Select Thread to Manage:");
	/** Dropdown menu for selecting a thread to manage. */
	protected static ComboBox<String> combo_ThreadSelection = new ComboBox<>(); 
	/** Text field for entering the name of a new thread. */
	protected static TextField field_NewThreadName = new TextField();
	/** Button to trigger thread creation. */
	protected static Button button_CreateThread = new Button("Create New Thread");
	/** Button to trigger thread deletion. */
	protected static Button button_DeleteThread = new Button("Delete Thread");
	/** separator line. */
	

   
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 800, width-20,800);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application and for
	// logging out.
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewRole2Home theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us	
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User
	
	private static Scene theRole2HomeScene;		// The shared Scene each invocation populates
	protected static final int theRole = 3;		// Admin: 1; Role1: 2; Role2: 3

	/*-*******************************************************************************************

	Constructors
	
	 */

	/**********
	 * <p> Method: displayRole2Home(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Role2 Home page to be displayed.
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
	public static void displayRole2Home(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewRole2Home();		// Instantiate singleton if needed
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;
		
		label_UserDetails.setText("User: " + theUser.getUserName());// Set the username
		   // Fetch the latest list from the static database
	    ArrayList<String> latestThreads = theDatabase.getThreadTitles(); 
	    
	    //Clear and Re-populate the items
	    combo_ThreadSelection.getItems().clear();
	    combo_ThreadSelection.getItems().addAll(latestThreads);
	    
	    if (!latestThreads.isEmpty()) {
	        combo_ThreadSelection.getSelectionModel().selectFirst();
	    }

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundations: Staff Home Page");
		theStage.setScene(theRole2HomeScene);						// Set this page onto the stage
		theStage.show();	// Display it to the user
		 ControllerRole2Home.performViewAllPosts();
	      ControllerRole2Home.performViewAllAdminRequests();

	}
	
	/**********
	 * <p> Method: ViewRole2Home() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayRole2Home method.</p>
	 * 
	 */
	private ViewRole2Home() {
		
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theRole2HomeScene = new Scene(theRootPane, width, height);	// Create the scene
		
		// Set the title for the window
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Role2 Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> {ControllerRole2Home.performUpdate(); });
		
		setupLabelUI(label_StaffTools, "Arial", 15, width, Pos.BASELINE_LEFT, 20, 695);
		label_StaffTools.setStyle("-fx-text-fill: #555555; -fx-font-weight: bold;");

		// Creation UI: Input Field and Button
		field_NewThreadName.setPromptText("Enter new thread name...");
		field_NewThreadName.setLayoutX(20);
		field_NewThreadName.setLayoutY(720);
		field_NewThreadName.setMinWidth(250);
		
		setupButtonUI(button_CreateThread, "Dialog", 13, 200, Pos.CENTER, 20, 755);
		button_CreateThread.setOnAction((_) -> { 
			// Grab text from the field and pass to controller
			ControllerRole2Home.handleCreateThread("staff", field_NewThreadName.getText()); 
		});

		// Deletion UI: Label, Dropdown, and Button
		setupLabelUI(label_SelectThread, "Arial", 13, 250, Pos.BASELINE_LEFT, 300, 700);
		combo_ThreadSelection.setLayoutX(300);
		combo_ThreadSelection.setLayoutY(720);
		combo_ThreadSelection.setMinWidth(300);

		setupButtonUI(button_DeleteThread, "Dialog", 14, 200, Pos.CENTER, 300, 755);
		button_DeleteThread.setOnAction((_) -> { 
			// Pass the selected thread title to the controller for cascading delete
			String selected = combo_ThreadSelection.getValue();
			ControllerRole2Home.handleDeleteThread("staff", selected); 
		});

		
		setupLabelUI(label_PostsTitle, "Arial", 16, 370, Pos.BASELINE_LEFT, 20, 105);

        listView_Posts.setLayoutX(20);
        listView_Posts.setLayoutY(130);
        listView_Posts.setPrefWidth(370);
        listView_Posts.setPrefHeight(220);

       
        listView_Posts.setCellFactory(lv -> new ListCell<Post>() {
      
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);
                if (empty || post == null) {
                    setText(null);
                } else {
                    setText(post.format());
                    setWrapText(true);
                    setFont(Font.font("Arial", 11));
                }
            }
        });

        setupButtonUI(button_ViewAllPosts,"Dialog", 13, 175, Pos.CENTER,  20, 360);
        setupButtonUI(button_SendFeedbackPost,"Dialog", 13, 175, Pos.CENTER, 205, 360);
        setupButtonUI(button_SendFeedbackReply,"Dialog", 13, 175, Pos.CENTER,  20, 395);
  

        button_ViewAllPosts.setOnAction((_) -> { ControllerRole2Home.performViewAllPosts(); });
        button_SendFeedbackPost.setOnAction((_) -> {
            Post selected = listView_Posts.getSelectionModel().getSelectedItem();
            if (selected == null) {
                displayStatusMessage("Please select a post from the list first.");
                return;
            }
            openSendFeedbackWindow("post", selected, null);
        });
        button_SendFeedbackReply.setOnAction((_) -> {
            Post selected = listView_Posts.getSelectionModel().getSelectedItem();
            if (selected == null) {
                displayStatusMessage("Please select a post from the list first.");
                return;
            }
            if (selected.getReplies().isEmpty()) {
                displayStatusMessage("This post has no replies.");
                return;
            }
            openSelectReplyForFeedbackWindow(selected);
        });
       

        setupLabelUI(label_FeedbackTitle, "Arial", 16, 370, Pos.BASELINE_LEFT, 410, 105);

        listView_Feedback.setLayoutX(410);
        listView_Feedback.setLayoutY(130);
        listView_Feedback.setPrefWidth(370);
        listView_Feedback.setPrefHeight(165);

        listView_Feedback.setCellFactory(lv -> new ListCell<PrivateFeedback>() {
      
            protected void updateItem(PrivateFeedback fb, boolean empty) {
                super.updateItem(fb, empty);
                if (empty || fb == null) { setText(null); return; }

                String context;
                if (fb.getRelatedPostId() != -1) {
                    Post post = ViewRole2Home.theDatabase.getPostById(fb.getRelatedPostId());
                    String postTitle = (post != null) ? post.getTitle() : "Unknown Post";
                    context = "Post: \"" + postTitle + "\"";
                } else if (fb.getRelatedReplyId() != -1) {
                    String replyPreview = "a reply";
                    try {
                        List<Post> allPosts = ViewRole2Home.theDatabase.getAllPostsWithReplies();
                        outer:
                        for (Post p : allPosts) {
                            for (Reply r : p.getReplies()) {
                                if (r.getId() == fb.getRelatedReplyId()) {
                                    String body = r.getBody();
                                    replyPreview = "\""
                                        + (body.length() > 40 ? body.substring(0, 40) + "..." : body)
                                        + "\" on post: \"" + p.getTitle() + "\"";
                                    break outer;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    context = "Reply: " + replyPreview;
                } else {
                    context = "General feedback";
                }

                setText(String.format("To: %s\nAbout: %s\n%s",
                    fb.getRecipientUsername(), context, fb.getMessage()));
                setWrapText(true);
                setFont(Font.font("Monospaced", 11));
            }
        });

        setupButtonUI(button_ViewMyFeedback,"Dialog", 13, 118, Pos.CENTER, 410, 305);
        setupButtonUI(button_EditFeedback,"Dialog", 13, 118, Pos.CENTER, 537, 305);
        setupButtonUI(button_DeleteFeedback,"Dialog", 13, 118, Pos.CENTER, 664, 305);

        button_ViewMyFeedback.setOnAction((_) -> { ControllerRole2Home.performViewMyFeedback(); });
        button_EditFeedback  .setOnAction((_) -> {PrivateFeedback sel = listView_Feedback.getSelectionModel().getSelectedItem();
            if (sel == null) { displayStatusMessage("Select a feedback item first."); return; }
            openEditFeedbackWindow(sel);
        });
        button_DeleteFeedback.setOnAction((_) -> {
            PrivateFeedback sel = listView_Feedback.getSelectionModel().getSelectedItem();
            if (sel == null) { displayStatusMessage("Select a feedback item first."); return; }
            ControllerRole2Home.performDeleteFeedback(sel);
        });

        label_AdminRequestsSection = new Label("── Admin Requests ──────────────────────────────");
        setupLabelUI(label_AdminRequestsSection, "Dialog", 13, 800, Pos.BASELINE_LEFT, 20, 430);

        // Row 1: Create + View buttons
        setupButtonUI(button_CreateAdminRequest, "Dialog", 13, 190, Pos.CENTER, 20, 450);
        button_CreateAdminRequest.setOnAction((_) -> openCreateAdminRequestWindow());

        setupButtonUI(button_ViewAllAdminRequests, "Dialog", 13, 190, Pos.CENTER, 220, 450);
        button_ViewAllAdminRequests.setOnAction((_) -> ControllerRole2Home.performViewAllAdminRequests());

        setupButtonUI(button_ViewOpenAdminRequests, "Dialog", 13, 190, Pos.CENTER, 480, 450);
        button_ViewOpenAdminRequests.setOnAction((_) -> ControllerRole2Home.performViewOpenAdminRequests());

        // Row 2: View closed + Action buttons
        setupButtonUI(button_ViewClosedAdminRequests, "Dialog", 13, 190, Pos.CENTER, 20, 490);
        button_ViewClosedAdminRequests.setOnAction((_) -> ControllerRole2Home.performViewClosedAdminRequests());

        // Reopen button
        setupButtonUI(button_ReopenRequest, "Dialog", 13, 190, Pos.CENTER, 300, 490);
        button_ReopenRequest.setOnAction((_) -> {
            AdminRequest selected = adminRequestListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                displayMessage("Please select a request first.");
                return;
            }
            if (!"Closed".equals(selected.getStatus())) {
                displayMessage("Only closed requests can be reopened.");
                return;
            }
            openReopenRequestWindow(selected);
        });

        // ListView for displaying AdminRequests
        adminRequestListView.setCellFactory((_) -> new ListCell<AdminRequest>() {
            protected void updateItem(AdminRequest request, boolean empty) {
                super.updateItem(request, empty);
                setText(empty || request == null ? null : request.format());
            }
        });
        adminRequestListView.setPrefWidth(760);
        adminRequestListView.setPrefHeight(150);
        adminRequestListView.setLayoutX(20);
        adminRequestListView.setLayoutY(530);

		
		
		// GUI Area 3
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 820);
        button_Logout.setOnAction((_) -> {ControllerRole2Home.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 820);
        button_Quit.setOnAction((_) -> {ControllerRole2Home.performQuit(); });

		// This is the end of the GUI initialization code
		
		// Place all of the widget items into the Root Pane's list of children
        theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
	        line_Separator4, button_Logout, button_Quit,label_PostsTitle, listView_Posts,
            button_ViewAllPosts, button_SendFeedbackPost, button_SendFeedbackReply, 
            label_FeedbackTitle, listView_Feedback,
            button_ViewMyFeedback, button_EditFeedback, button_DeleteFeedback , label_AdminRequestsSection,
            button_CreateAdminRequest, button_ViewAllAdminRequests, button_ViewOpenAdminRequests,
            button_ViewClosedAdminRequests, button_ReopenRequest,label_StaffTools, field_NewThreadName, button_CreateThread, 
			label_SelectThread, combo_ThreadSelection, button_DeleteThread,
            adminRequestListView);
	}
	
	/**********
     * <p> Method: openSendFeedbackWindow(String) </p>
     *
     * <p> Description: Opens a modal pop-up where the staff member specifies a recipient
     * username, the ID of the post or reply this feedback concerns, and the message text.
     * On submit, calls ControllerRole2Home.performSendFeedback. </p>
     *
     *
     * @param contentType "post" or "reply" — pre-sets the context selector
     */
	protected static void openSendFeedbackWindow(String contentType, Post post, Reply reply) {

	    String recipient = post.getAuthor();
	    int postId  = contentType.equals("post")  ? post.getId() : -1;
	    int replyId = contentType.equals("reply") ? reply.getId() : -1;

	    // If reply, the recipient is the reply author, not the post author
	    if (contentType.equals("reply")) {
	        recipient = reply.getAuthor();
	    }

	    Stage popup = new Stage();
	   
	    popup.setTitle("Send Private Feedback");

	    Pane p = new Pane();

	    Label lTo = new Label("To: " + recipient);
	    setupLabelUI(lTo, "Dialog", 14, 410, Pos.BASELINE_LEFT, 20, 20);

	    String aboutText = contentType.equals("post")
	        ? "Post: \"" + post.getTitle() + "\""
	        : "Reply: \"" + (reply.getBody().length() > 40
	            ? reply.getBody().substring(0, 40) + "..."
	            : reply.getBody()) + "\"";

	    Label lAbout = new Label("About: " + aboutText);
	    setupLabelUI(lAbout, "Dialog", 14, 410, Pos.BASELINE_LEFT, 20, 50);

	    Label lMsg = new Label("Message:");
	    setupLabelUI(lMsg, "Dialog", 14, 200, Pos.BASELINE_LEFT, 20, 90);

	    TextArea taMsg = new TextArea();
	    taMsg.setPromptText("Write your private feedback here...");
	    taMsg.setLayoutX(20);   taMsg.setLayoutY(115);
	    taMsg.setPrefWidth(410); taMsg.setPrefHeight(150);
	    taMsg.setWrapText(true);

	    Label lError = new Label("");
	    lError.setStyle("-fx-text-fill: #000000;");
	    setupLabelUI(lError, "Dialog", 12, 410, Pos.BASELINE_LEFT, 20, 275);

	    Button btnSend   = new Button("Send");
	    Button btnCancel = new Button("Cancel");
	    setupButtonUI(btnSend,   "Dialog", 14, 120, Pos.CENTER, 20,  300);
	    setupButtonUI(btnCancel, "Dialog", 14, 120, Pos.CENTER, 155, 300);

	    final int finalPostId  = postId;
	    final int finalReplyId = replyId;
	    final String finalRecipient = recipient;

	    btnSend.setOnAction((_) -> {
	        String message = taMsg.getText().trim();
	        if (message.isEmpty()) { lError.setText("Message cannot be empty."); return; }
	        ControllerRole2Home.performSendFeedback(finalRecipient, finalPostId, finalReplyId, message);
	        popup.close();
	    });

	    btnCancel.setOnAction((_) -> popup.close());

	    p.getChildren().addAll(lTo, lAbout, lMsg, taMsg, lError, btnSend, btnCancel);
	    popup.setScene(new Scene(p, 460, 340));
	    popup.showAndWait();
	}

    /**********
     * <p> Method: openEditFeedbackWindow(PrivateFeedback) </p>
     *
     * <p> Description: Opens a modal pop-up pre-filled with the selected feedback's current
     * message text. The staff member edits the text and clicks Save to apply the update.
     * Calls ControllerRole2Home#performUpdateFeedback. </p>
     *

     *
     * @param feedback the PrivateFeedback item to edit
     */
    protected static void openEditFeedbackWindow(PrivateFeedback feedback) {

        Stage popup = new Stage();
       
        popup.setTitle("Edit Feedback");

        Pane p = new Pane();

        Label lInfo = new Label("Editing feedback to: " + feedback.getRecipientUsername());
        setupLabelUI(lInfo, "Dialog", 14, 400, Pos.BASELINE_LEFT, 20, 20);

        TextArea taMsg = new TextArea(feedback.getMessage());
        taMsg.setLayoutX(20);  taMsg.setLayoutY(50);
        taMsg.setPrefWidth(410); taMsg.setPrefHeight(150);
        taMsg.setWrapText(true);

        Label lError = new Label("");
        lError.setStyle("-fx-text-fill: #000000;");
        setupLabelUI(lError, "Dialog", 12, 410, Pos.BASELINE_LEFT, 20, 210);

        Button btnSave   = new Button("Save");
        Button btnCancel = new Button("Cancel");
        setupButtonUI(btnSave,   "Dialog", 14, 120, Pos.CENTER, 20,  235);
        setupButtonUI(btnCancel, "Dialog", 14, 120, Pos.CENTER, 155, 235);

        btnSave.setOnAction((_) -> {
            String updated = taMsg.getText().trim();
            if (updated.isEmpty()) { lError.setText("Message cannot be empty."); return; }
            ControllerRole2Home.performUpdateFeedback(feedback, updated);
            popup.close();
        });

        btnCancel.setOnAction((_) -> popup.close());

        p.getChildren().addAll(lInfo, taMsg, lError, btnSave, btnCancel);

        popup.setScene(new Scene(p, 460, 280));
        popup.showAndWait();
    }

    protected static void openSelectReplyForFeedbackWindow(Post post) {

        Stage popup = new Stage();
        popup.setTitle("Select Reply to Give Feedback On");

        Pane p = new Pane();

        Label lLabel = new Label("Select a reply:");
        setupLabelUI(lLabel, "Dialog", 14, 400, Pos.BASELINE_LEFT, 20, 20);

        ComboBox<Reply> replyCombo = new ComboBox<>();
        replyCombo.setCellFactory(lv -> new ListCell<Reply>() {
            protected void updateItem(Reply r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) { setText(null); return; }
                String body = r.getBody();
                setText("By " + r.getAuthor() + ": " +
                    (body.length() > 60 ? body.substring(0, 60) + "..." : body));
            }
        });
        replyCombo.setButtonCell(new ListCell<Reply>() {
            protected void updateItem(Reply r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) { setText("Select a reply..."); return; }
                String body = r.getBody();
                setText("By " + r.getAuthor() + ": " +
                    (body.length() > 60 ? body.substring(0, 60) + "..." : body));
            }
        });
        replyCombo.getItems().addAll(post.getReplies());
        replyCombo.setLayoutX(20);
        replyCombo.setLayoutY(50);
        replyCombo.setPrefWidth(420);

        Button btnSelect = new Button("Give Feedback on This Reply");
        Button btnCancel = new Button("Cancel");
        setupButtonUI(btnSelect, "Dialog", 13, 220, Pos.CENTER, 20,  100);
        setupButtonUI(btnCancel, "Dialog", 13, 120, Pos.CENTER, 250, 100);

        btnSelect.setOnAction((_) -> {
            Reply selected = replyCombo.getSelectionModel().getSelectedItem();
            if (selected == null) {
                displayStatusMessage("Please select a reply.");
                return;
            }
            popup.close();
            openSendFeedbackWindow("reply", post, selected);
        });

        btnCancel.setOnAction((_) -> popup.close());

        p.getChildren().addAll(lLabel, replyCombo, btnSelect, btnCancel);
        popup.setScene(new Scene(p, 460, 160));
        popup.show();
    }
    
    protected static void displayStatusMessage(String message) {
        javafx.scene.control.Alert alert = 
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /** Opens window to create a new AdminRequest */
    private void openCreateAdminRequestWindow() {
        Stage stage = new Stage();
        stage.setTitle("Submit New AdminRequest");

        Label titleLabel = new Label("Title:");
        TextField titleField = new TextField();
        titleField.setMinWidth(300);

        Label descLabel = new Label("Description:");
        TextArea descArea = new TextArea();
        descArea.setPrefHeight(150);

        Button submitButton = new Button("Submit Request");
        Button cancelButton = new Button("Cancel");

        Pane pane = new Pane();
        titleLabel.setLayoutX(20); titleLabel.setLayoutY(20);
        titleField.setLayoutX(20); titleField.setLayoutY(45);

        descLabel.setLayoutX(20); descLabel.setLayoutY(85);
        descArea.setLayoutX(20); descArea.setLayoutY(110);
        descArea.setMinWidth(350);

        submitButton.setLayoutX(20); submitButton.setLayoutY(280);
        cancelButton.setLayoutX(150); cancelButton.setLayoutY(280);

        pane.getChildren().addAll(titleLabel, titleField, descLabel, descArea, submitButton, cancelButton);

        submitButton.setOnAction((_) -> {
            String title = titleField.getText();
            String desc = descArea.getText();

            if (title == null || title.isBlank()) {
                displayMessage("Title cannot be empty.");
                return;
            }
            if (desc == null || desc.isBlank()) {
                displayMessage("Description cannot be empty.");
                return;
            }

            ControllerRole2Home.performCreateAdminRequest(title, desc);
            stage.close();
        });

        cancelButton.setOnAction((_) -> stage.close());

        stage.setScene(new Scene(pane, 400, 340));
        stage.show();
    }

    /** Opens window for admin to close a request with notes */
    private void openCloseRequestWindow(AdminRequest request) {
        Stage stage = new Stage();
        stage.setTitle("Close AdminRequest");

        Label notesLabel = new Label("Actions Taken / Notes:");
        TextArea notesArea = new TextArea();
        notesArea.setPrefHeight(120);

        Button closeButton = new Button("Close Request");
        Button cancelButton = new Button("Cancel");

        Pane pane = new Pane();
        notesLabel.setLayoutX(20); notesLabel.setLayoutY(20);
        notesArea.setLayoutX(20); notesArea.setLayoutY(45);

        closeButton.setLayoutX(20); closeButton.setLayoutY(190);
        cancelButton.setLayoutX(150); cancelButton.setLayoutY(190);

        pane.getChildren().addAll(notesLabel, notesArea, closeButton, cancelButton);


        cancelButton.setOnAction((_) -> stage.close());

        stage.setScene(new Scene(pane, 400, 240));
        stage.show();
    }

    /** Opens window to reopen a closed request */
    private void openReopenRequestWindow(AdminRequest request) {
        Stage stage = new Stage();
        stage.setTitle("Reopen AdminRequest");

        Label descLabel = new Label("Updated Description:");
        TextArea descArea = new TextArea(request.getDescription());
        descArea.setPrefHeight(120);

        Button reopenButton = new Button("Reopen Request");
        Button cancelButton = new Button("Cancel");

        Pane pane = new Pane();
        descLabel.setLayoutX(20); descLabel.setLayoutY(20);
        descArea.setLayoutX(20); descArea.setLayoutY(45);

        reopenButton.setLayoutX(20); reopenButton.setLayoutY(190);
        cancelButton.setLayoutX(150); cancelButton.setLayoutY(190);

        pane.getChildren().addAll(descLabel, descArea, reopenButton, cancelButton);

        reopenButton.setOnAction((_) -> {
            String newDesc = descArea.getText();
            if (newDesc == null || newDesc.isBlank()) {
                displayMessage("Description cannot be empty.");
                return;
            }
            ControllerRole2Home.performReopenAdminRequest(request, newDesc);
            stage.close();
        });

        cancelButton.setOnAction((_) -> stage.close());

        stage.setScene(new Scene(pane, 400, 260));
        stage.show();
    }

    /** Displays message dialog */
    public static void displayMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Returns the ListView so Controller can populate it */
    public static ListView<AdminRequest> getAdminRequestListView() {
        return adminRequestListView;
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
