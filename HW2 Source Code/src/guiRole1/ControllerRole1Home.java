package guiRole1;

import entityClasses.Post;
import entityClasses.Reply;

import entityClasses.PrivateFeedback;
import java.util.List;

import java.util.ArrayList;

/*******
 * <p> Title: ControllerRole1Home Class. </p>
 * 
 * <p> Description: The Java/FX-based Role 1 Home Page.  This class provides the controller
 * actions basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * Provides all the business logic for the student role — creating, viewing, updating, deleting, searching posts and
 * replies, and tracking read/unread status.
 * 
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
 * @version 2.00        2026-02-25 Full post and reply CRUD, search, read and unread tracking
 */

public class ControllerRole1Home {

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object)
	
	 */
	
	  

	/**
	 * Default constructor is not used.
	 */
	public ControllerRole1Home() {
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: This method directs the user to the User Update Page so the user can change
	 * the user account attributes. </p>
	 * 
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewRole1Home.theStage, ViewRole1Home.theUser);
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole1Home.theStage);
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
	
    /*******
     *  <p> Method: performCreatePost(String title, String body, String thread) </p>
     *  
     *  <p> Description: Creates a new post with the given title, body, and thread,
	 * written by the currently logged in user, and saves it to the database. </p>
	 * 
     * @param title  the title of the post
     * @param body   the body content of the post
     * @param thread the thread the post belongs to
     */

	protected static void performCreatePost(String title, String body, String thread) {
        String username = ViewRole1Home.theUser.getUserName();
        try {
            Post.performCreatePost(ViewRole1Home.theDatabase, title, body, username, thread);
            ViewRole1Home.displayMessage("Post created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            ViewRole1Home.displayMessage("Error creating post.");
        }
    }

    /*******
     * <p> Method: performViewUnreadPosts() </p>
     * 
     * <p> Description: Loads all posts not yet read by the current user, displays them
	 * in the ListView, and marks each one as read. </p>
     */
	 protected static void performViewUnreadPosts() {
	        try {
	            List<Post> unreadPosts = Post.performViewUnreadPosts(
	                    ViewRole1Home.theDatabase,
	                    ViewRole1Home.theUser.getUserName());
	            ViewRole1Home.getPostListView().getItems().clear();
	            if (unreadPosts.isEmpty()) {
	                ViewRole1Home.displayMessage("No unread post found.");
	                return;
	            }
	            ViewRole1Home.getPostListView().getItems().addAll(unreadPosts);
	        } catch (Exception e) {
	            ViewRole1Home.displayMessage("Error loading unread posts: " + e.getMessage());
	            e.printStackTrace();
	        }
	    }

 /*******
  * <p> Method: performViewReadPosts() </p>
  * 
  * <p> Description: Loads all posts previously read by the current user and
  * displays them in the ListView. </p>
  */
	 protected static void performViewReadPosts() {
	        try {
	            List<Post> readPosts = Post.performViewReadPosts(
	                    ViewRole1Home.theDatabase,
	                    ViewRole1Home.theUser.getUserName());
	            ViewRole1Home.getPostListView().getItems().clear();
	            if (readPosts.isEmpty()) {
	                ViewRole1Home.displayMessage("No read posts found.");
	                return;
	            }
	            ViewRole1Home.getPostListView().getItems().addAll(readPosts);
	        } catch (Exception e) {
	            ViewRole1Home.displayMessage("Error loading read posts: " + e.getMessage());
	            e.printStackTrace();
	        }
	    }
	    

	       /*******
	        * <p> Method: performViewAllPostsReplies() </p>
	        * 
	        *  <p> Description: Loads all posts with their replies from the database and
		    * displays them in the ListView. This is also used as the default refresh method
		    * after any create, update, or delete operation. </p>
		    */
	 protected static void performViewAllPostsReplies() {
	        try {
	            List<Post> postsWithReplies = Post.performViewAllPostsReplies(
	                    ViewRole1Home.theDatabase);
	            ViewRole1Home.getPostListView().getItems().clear();
	            if (postsWithReplies.isEmpty()) {
	                ViewRole1Home.displayMessage("No posts or replies found.");
	                return;
	            }
	            ViewRole1Home.getPostListView().getItems().addAll(postsWithReplies);
	        } catch (Exception e) {
	            ViewRole1Home.displayMessage("Error loading posts and replies: " + e.getMessage());
	            e.printStackTrace();
	        }
	    }
	    
	    /*******
	     * <p> Method: performCreateReply(int postId, String body) </p>
	     * 
	     * <p> Description: Creates a new reply to the post with the given postId,
	     * written by the currently logged-in user, and saves it to the database.
	     * Refreshes the ListView after creation. </p>
	     * @param postId the ID of the post being replied to
	     * @param body the body of the reply
	     */
	    protected static void performCreateReply(int postId, String body) {
	    	
	        try {
	        	// Get the username of the currently logged in user to set as the reply author
	            String author =   ViewRole1Home.theUser.getUserName(); 
               
	         // Save the reply to the database, linked to the parent post via postId foreign key
	            ViewRole1Home.theDatabase.createReply(postId, body, author);

	            ViewRole1Home.displayMessage("Reply posted successfully!");
	         // Refresh the ListView so the new reply appears under its parent post immediately
	            performViewAllPostsReplies();
	        } catch (Exception e) {
	            e.printStackTrace();
	            ViewRole1Home.displayMessage("Error creating reply.");
	        }
	    }
	    
	    /*******
	     *  <p> Method: performViewUnreadReplies() </p>
	     *  
	     *  <p> Description: Loads all replies not yet read by the current user. For each
	     * unread reply, fetches and displays the full parent post with all its replies,
	     * then marks the reply as read. </p>
	     */
	    protected static void performViewUnreadReplies() {
	        try {
	        	// Fetch all replies the current user has not yet read
	            List<Reply> unreadReplies = ViewRole1Home.theDatabase.getUnreadReplies(ViewRole1Home.theUser.getUserName());
	         // Clear the ListView before populating with fresh data
	            ViewRole1Home.getPostListView().getItems().clear();
	            if (unreadReplies.isEmpty()) {
	            	ViewRole1Home.displayMessage("No unread replies found.");
	                return;
	            }

	            for (Reply reply : unreadReplies) {
					Post parentPost = ViewRole1Home.theDatabase.getPostWithReplies(reply.getPostId());
					if (parentPost != null)
						ViewRole1Home.getPostListView().getItems().add(parentPost);
					ViewRole1Home.theDatabase.markReplyAsRead(
						reply.getId(), reply.getPostId(), ViewRole1Home.theUser.getUserName());
				}

	        } catch (Exception e) {
	            ViewRole1Home.displayMessage("Error loading unread replies: " + e.getMessage());
	            e.printStackTrace();
	        }
	    }
	    
	    /*******
	     * <p> Method: performViewReadReplies() </p>
	     * 
	     * <p> Description: Loads all replies previously read by the current user and
	     * displays their parent posts with all replies in the ListView. </p>
	     */
	    protected static void performViewReadReplies() {
	        try {
	        	// Fetch all replies the current user has already read
	            List<Reply> readReplies = ViewRole1Home.theDatabase
	                    .getReadReplies(ViewRole1Home.theUser.getUserName());
	         // Clear the ListView before populating with fresh data
	            ViewRole1Home.getPostListView().getItems().clear();
	            if (readReplies.isEmpty()) {
	            	ViewRole1Home.displayMessage("No read replies found.");
	                return;
	            }

	            for (Reply reply : readReplies) {
	            	// Fetch the parent post with all replies so the user sees full thread context
					Post parentPost = ViewRole1Home.theDatabase.getPostWithReplies(reply.getPostId());
					// Only add if the parent post still exists in the database
					if (parentPost != null)
						ViewRole1Home.getPostListView().getItems().add(parentPost);
				}

	        } catch (Exception e) {
	            ViewRole1Home.displayMessage("Error loading read replies.");
	            e.printStackTrace();
	        }
	    }
	    
	    /*******
	     * <p> Method: performUpdatePost(Post post) </p>
	     * 
	     *  <p> Description: Verifies that the current user is the author of the selected post,
	     *   then opens the update post window. </p>
	     *   
	     * @param post the Post object selected by the user
	     */
	    protected static void performUpdatePost(Post post) {
	        String username = ViewRole1Home.theUser.getUserName();
	        // Delegate ownership check to Post — returns false if not the author
	        if (!Post.performUpdatePost(post, username)) {
	            ViewRole1Home.displayMessage("You can only update your own posts.");
	            return;
	        }
	        // Open the update window prefilled with the post's current title and body
	        ViewRole1Home.openUpdatePostWindow(post);
	    }
       /*******
        * <p> Method: performDeletePost(Post post) </p>
        * <p> Description: Verifies that the current user is the author of the selected post,
	    * shows a confirmation dialog, then deletes the post. Replies are preserved and
	    * will show a "post deleted" message to viewers. </p>
        * 
        * @param post the Post object selected by the user
        */
	    protected static void performDeletePost(Post post) {
	        String username = ViewRole1Home.theUser.getUserName();
	 
	        // Check ownership before showing the confirmation dialog
	        if (!post.getAuthor().equals(username)) {
	            ViewRole1Home.displayMessage("You can only delete your own posts.");
	            return;
	        }
	 
	        // Show confirmation dialog
	        javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(
	                javafx.scene.control.Alert.AlertType.CONFIRMATION);
	        confirm.setTitle("Delete Post");
	        confirm.setHeaderText("Are you sure?");
	        confirm.setContentText(
	                "This will mark the post as deleted. Replies will remain visible.");
	 
	        // Only proceed with deletion if the user clicks OK
	        confirm.showAndWait().ifPresent(response -> {
	            if (response == javafx.scene.control.ButtonType.OK) {
	                // Delegate the actual delete to Post
	                boolean deleted = Post.performDeletePost(
	                        ViewRole1Home.theDatabase, post, username);
	                if (deleted) {
	                    ViewRole1Home.displayMessage("Post deleted successfully.");
	                    // Refresh the ListView to show the updated deleted state
	                    performViewAllPostsReplies();
	                } else {
	                    ViewRole1Home.displayMessage("Failed to delete post.");
	                }
	            }
	        });
	    }
/*******
 * <p> Method: performUpdateReply(Post post) </p>
 * Description: Finds all replies written by the current user on the selected post.
* If only one reply exists, opens the update window directly. If multiple replies exist,
* opens a selection window for the user to choose which reply to update. </p>
 * @param post the Post object containing the replies
 */
	    protected static void performUpdateReply(Post post) {
	    	// Get the currently logged in username
	        String username = ViewRole1Home.theUser.getUserName();
	    	// Loop through all replies on this post and collect only those by the current user
	        List<Reply> userReplies = new ArrayList<>();
	        for (Reply r : post.getReplies()) {
	            if (r.getAuthor().equals(username)) {
	                userReplies.add(r);
	            }
	        }
	     // If the user has no replies on this post, display error message
	        if (userReplies.isEmpty()) {
	            ViewRole1Home.displayMessage("No reply by you found in this post.");
	            return;
	        }
	        
	        if (userReplies.size() == 1) {
	        	// Only one reply found, open the update window directly with that reply pre-filled
	            ViewRole1Home.openUpdateReplyWindow(userReplies.get(0), post);
	        } else {
	        	// Multiple replies found, show a selection window so the user picks which to update
	            ViewRole1Home.openSelectReplyWindow(userReplies, post, false);
	        }
	    }
      /*******
       * <p> Method: performDeleteReply(Post post) </p>
       * 
       *  <p> Description: Finds all replies authored by the current user on the selected post.
	   * If only one reply exists, deletes it directly. If multiple replies exist,
	   * opens a selection window for the user to choose which reply to delete. </p>
       * @param post the Post object containing the replies
       */
	    protected static void performDeleteReply(Post post) {
	    	// Get the currently logged in username
	        String username = ViewRole1Home.theUser.getUserName();
	     // Loop through all replies on this post and collect only those by the current user
	        List<Reply> userReplies = new ArrayList<>();
	        for (Reply r : post.getReplies()) {
	            if (r.getAuthor().equals(username)) {
	                userReplies.add(r);
	            }
	        }
	     // If the user has no replies on this post, display an error message
	        if (userReplies.isEmpty()) {
	            ViewRole1Home.displayMessage("No reply by you found in this post.");
	            return;
	        }
	        
	        if (userReplies.size() == 1) {
	            // Only one reply, delete directly
	            try {
	                boolean deleted = ViewRole1Home.theDatabase.deleteReply(
	                    userReplies.get(0).getId(), username);
	                if (deleted) {
	                    ViewRole1Home.displayMessage("Reply deleted successfully.");
	                	// Refresh the ListView to reflect the deletion
	                    performViewAllPostsReplies();
	                } else {
	                    ViewRole1Home.displayMessage("Failed to delete reply.");
	                }
	            } catch (Exception e) {
	                ViewRole1Home.displayMessage("Error deleting reply: " + e.getMessage());
	                e.printStackTrace();
	            }
	        } else {
	        	// Multiple replies found — show a selection window so the user picks which to delete
	            ViewRole1Home.openSelectReplyWindow(userReplies, post, true);
	        }
	    }
	    
	    /*******
	     *  <p> Method: performSearchPosts() </p>
	     *  
	     *  <p> Description: Searches posts by the keyword entered in the search field.
	     * If a thread is selected, the search is filtered to that thread only.
	     * If "All Threads" is selected, all threads are searched. </p>
	     */
	    
	    protected static void performSearchPosts() {
	        // Read the keyword and thread filter values from the search widgets in the View
	        String keyword = ViewRole1Home.getSearchKeyword();
	        String thread  = ViewRole1Home.getSearchThread();
	 
	        // A keyword is required — the thread filter is optional
	        if (keyword == null || keyword.isBlank()) {
	            ViewRole1Home.displayMessage("Please enter a search keyword.");
	            return;
	        }
	 
	        try {
	            List<Post> results = Post.performSearchPosts(
	                    ViewRole1Home.theDatabase, keyword, thread);
	            // Clear the ListView before showing search results
	            ViewRole1Home.getPostListView().getItems().clear();
	            if (results.isEmpty()) {
	                ViewRole1Home.displayMessage("No posts found matching: " + keyword);
	                return;
	            }
	            // Display all matching posts each with their replies loaded
	            ViewRole1Home.getPostListView().getItems().addAll(results);
	        } catch (Exception e) {
	            ViewRole1Home.displayMessage("Error searching posts: " + e.getMessage());
	            e.printStackTrace();
	        }
	    }
	    
	    /**********
	     * <p> Method: performViewMyFeedback() </p>
	     *
	     * <p> Description: Loads all private feedback received by the currently
	     * logged-in student into listView_MyFeedback. Each entry shows who sent
	     * it, what content it relates to, and the message text.
	     */
	    protected static void performViewMyFeedback() {
	        try {
	            String username = ViewRole1Home.theUser.getUserName();
	            List<PrivateFeedback> list =
	                ViewRole1Home.theDatabase.getFeedbackByRecipient(username);

	            ViewRole1Home.listView_MyFeedback.getItems().clear();

	            if (list.isEmpty()) {
	                ViewRole1Home.displayMessage("You have no private feedback yet.");
	                return;
	            }

	            for (PrivateFeedback fb : list) {
	                // Mark as read when student views it
	                ViewRole1Home.theDatabase.markFeedbackAsRead(fb.getId());

	                String context;
	                if (fb.getRelatedPostId() != -1) {
	                    // Feedback is about a post — fetch the post title
	                    Post post = ViewRole1Home.theDatabase.getPostById(fb.getRelatedPostId());
	                    String postTitle = (post != null) ? post.getTitle() : "Unknown Post";
	                    context = "Post: \"" + postTitle + "\"";

	                } else if (fb.getRelatedReplyId() != -1) {
	                    // Feedback is about a reply — search all posts to find it
	                    String replyPreview = "a reply";
	                    try {
	                        List<Post> allPosts = ViewRole1Home.theDatabase.getAllPostsWithReplies();
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

	                String entry = String.format(
	                    "From: %s\nAbout: %s\n%s",
	                    fb.getSenderUsername(), context, fb.getMessage()
	                );
	                ViewRole1Home.listView_MyFeedback.getItems().add(entry);
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            ViewRole1Home.displayMessage("Error loading feedback: " + e.getMessage());
	        }
	    }
	}

