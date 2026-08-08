package entityClasses;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import database.Database;


import java.sql.Timestamp;

/*******
 *  <p> Title: Post Class. </p>
 *  <p> Description: Represents a forum post with a title, body, author, thread, and
 * a list of replies. Supports soft-deletion — a deleted post preserves its replies
 * but displays a deletion notice instead of its content. </p>
 * 
 * 
 */
public class Post {
	//These are the private attributes for this entity object
    private int id;
    private String title;
    private String body;
    private String author;
    private LocalDateTime createdAt;
    private String threadName;
    private List<Reply> replies;
    private boolean deleted;
    private static List<Post> posts = new ArrayList<>();
    /*******
     * <p> Constructor: Post(String title, String body, String author, String threadName) </p>
     * <p> Description: Creates a new Post for submission. Used when a user creates a
	 * new post through the UI </p>
	 *
     * @param title    the title of the post
     * @param body     the body content of the post
     * @param author   the username of the post author
     * @param threadName   the thread this post belongs to
     */
    public Post(String title, String body,String author,String threadName) {
        this.title = title;
        this.body = body;
        this.author = author;
        this.createdAt = LocalDateTime.now();
        this.threadName = threadName;
        this.deleted    = false;
        this.replies = new ArrayList<>();
    }
     
    /*******
     * <p> Constructor: Post(int id, String title, String body, String author,
	 * String threadName, Timestamp createdAt, boolean deleted) </p>
	 * 
	 * <p> Description: Creates a Post loaded from the database.</p>
     * @param id          the database-assigned post ID
     * @param title       the title of the post
     * @param body        the body content of the post
     * @param author      the username of the post author
     * @param threadName  the thread this post belongs to
     * @param createdAt   the creation timestamp from the database
     * @param deleted     whether this post has been deleted
     */
    public Post(int id, String title, String body, String author, String threadName, Timestamp createdAt, boolean deleted) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.author = author;
     // Default to "General" if thread name is missing
        this.threadName = (threadName == null || threadName.isBlank()) ? "General" : threadName;
       
        this.deleted = deleted;
        this.replies = new ArrayList<>();
    }
    // Getters
   
    public int getId() { return id; }

    public String getTitle() { return title; }

    public String getBody() { return body; }

    public String getAuthor() { return author; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public String getThreadName() { return threadName;}
    
    //setter
    public void setId(int id) {
    	 this.id = id;
    }
    
    /**********
   	 * <p> Method: addPost(Post post) </p>
   	 *
   	 * <p> Description: Adds a Post to the in memory list only.
   	 * Use addPostToDBAndList to also save to the database. </p>
   	 *
   	 * @param post the Post object to add
   	 */
       public static void addPost(Post post) {
           posts.add(post);
       }
       
       /**********
   	 * <p> Method: addPostToDBAndList(Database db, Post post) </p>
   	 *
   	 * <p> Description: Saves a new post to the database and adds it to the in-memory
   	 * list. The database generated ID is retrieved and set back on the Post object
   	 * so it can be referenced later for updates, deletes, and replies. </p>
   	 *
   	 * @param db   the Database instance to save the post to
   	 * @param post the Post object to save
   	 */
       public static void addPostToDBAndList(Database db, Post post) throws Exception {
           // Save to database
          int generatedId= db.createPost(post.getTitle(), post.getBody(), post.getAuthor(), post.getThreadName());
          post.setId(generatedId);
           // Add to in memory list
           addPost(post);
       }
       
       /**********
   	 * <p> Method: getPosts() </p>
   	 *
   	 * <p> Description: Returns the full in-memory list of posts. </p>
   	 *
   	 * @return the list of Post objects
   	 */
       public static List<Post> getPosts() {
           return posts;
       }
       
       /**********
   	 * <p> Method: isEmpty() </p>
   	 *
   	 * <p> Description: Returns true if there are no posts in the in-memory list. </p>
   	 *
   	 * @return true if the list is empty, false otherwise
   	 */
       public static boolean isEmpty() {
           return posts.isEmpty();
       }
       
       /**
        * <p> Method: clearPosts() </p>
        *
        * <p> Description: Clears the in-memory post list. Called on logout or
        * before a full refresh to prevent stale data from a previous session. </p>
        */
       public static void clearPosts() {
           posts.clear();
       }
       
       /**********
   	 * <p> Method: formatAll() </p>
   	 *
   	 * <p> Description: Returns a formatted string of all posts in the list by
   	 * calling each post's format() method. Returns a message if the list is empty. </p>
   	 *
   	 * @return a formatted string of all posts, or "No posts found." if empty
   	 */
       public static String formatAll() {

           if (posts.isEmpty()) {
               return "No posts found.";
           }

           StringBuilder sb = new StringBuilder();

           for (Post post : posts) {
               sb.append(post.format());
           }

           return sb.toString();
       }

    /*******
	 * <p> Method: addReply(Reply reply) </p>
	 *
	 * <p> Description: Adds a reply to this post's reply list. Called when loading
	 * posts from the database with their replies. </p>
	 *
	 * @param reply the Reply object to add
	 */
    public void addReply(Reply reply) {
        replies.add(reply);
    }
    
    /*******
	 * <p> Method: getReplies() </p>
	 *
	 * <p> Description: Returns the list of replies attached to this post. </p>
	 *
	 * @return the list of Reply objects
	 */
    public List<Reply> getReplies() {
        return replies;
    }
    
    public static void performCreatePost(Database db, String title, String body, String author, String thread) {
    	
    	       

    	        try {
    	        	// Build a new Post object with the provided fields and current user as author
    	        	Post newPost = new Post(title, body, author, thread);
    	        	// Save the post to the database and add it to the in-memory list
    				// addPostToDBAndList also sets the generated database ID back on the Post object
    	            addPostToDBAndList(db, newPost);
    	            
    	        } catch (Exception e) {
    	        	 e.printStackTrace(); 
    	           
    	        }
    	    }
    
    /*******
     * <p> Method: performViewUnreadPosts() </p>
     * 
     * <p> Description: Loads all posts not yet read by the current user, displays them
	 * in the ListView, and marks each one as read. </p>
     */
    public static List<Post> performViewUnreadPosts(Database db, String username) {
	    try {
	        // Fetch unread posts from database for current user
	        List<Post> unreadPosts = db.getUnreadPosts(username);
	        
	     
	      
	        if (unreadPosts.isEmpty()) {
	        	
	            return new ArrayList<>();
	        }
	     // Display each unread post and immediately mark it as read in the database
	     // so it will not show up as unread the next time the user checks
	        for (Post post : unreadPosts) {
				
				db.markPostAsRead(username, post.getId());
				}
	        return unreadPosts;

	    } catch (Exception e) {
	       
	        e.printStackTrace();
	        return new ArrayList<>();
	    }
	}

    /*******
     * <p> Method: performViewReadPosts(Database db, String username) </p>
     *
     * <p> Description: Loads all posts previously read by the current user.
     * Original code from {@code ControllerRole1Home.performViewReadPosts}
     * preserved. </p>
     *
     * @param db       the Database instance
     * @param username the currently logged-in username
     * @return a list of previously read Post objects, or empty list if none
     */
    public static List<Post> performViewReadPosts(Database db, String username) {
    	try {
         // Fetch read posts from database for current user
         List<Post> readPosts = db.getReadPosts(username);
         	if (readPosts.isEmpty()) {
         		return new ArrayList<>();
         	}
         	return readPosts;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new ArrayList<>();
    	}
 }
	    
    /*******
     * <p> Method: performViewAllPostsReplies(Database db) </p>
     *
     * <p> Description: Loads all posts with their replies from the database.
     * This is also used as the default refresh method after any create, update,
     * or delete operation. Original code from
     * {@code ControllerRole1Home.performViewAllPostsReplies} preserved. </p>
     *
     * @param db the Database instance
     * @return a list of all Post objects with replies populated, or empty list
     */
    public static List<Post> performViewAllPostsReplies(Database db) {
    	try {
    			// Fetch all posts from the database with their replies already loaded
    			List<Post> postsWithReplies = db.getAllPostsWithReplies();
    			if (postsWithReplies.isEmpty()) {
    				return new ArrayList<>();
    			}
    		// Add all posts to the list
    		return postsWithReplies;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new ArrayList<>();
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
    	public static boolean performUpdatePost(Post post, String username) {
     // Only the original author is allowed to update the post
    		if (!post.getAuthor().equals(username)) {
    			return false;
    		}
   
    		return true;
    	}
       /*******
        * <p> Method: performDeletePost(Post post) </p>
        * <p> Description: Verifies that the current user is the author of the selected post,
	    * shows a confirmation dialog, then deletes the post. Replies are preserved and
	    * will show a "post deleted" message to viewers. </p>
        * 
        * @param post the Post object selected by the user
        */
    	public static boolean performDeletePost(Database db, Post post, String username) {
    		// Only the original author is allowed to delete the post
    		if (!post.getAuthor().equals(username)) {
    			return false;
    		}
    		try {
         // sets deleted = TRUE in the database instead of removing the row
         // This preserves any replies attached to the post so they remain visible
    			boolean deleted = db.deletePost(post.getId(), username);
    			return deleted;
    		} catch (Exception e) {
    			e.printStackTrace();
    			return false;
    		}
 }

	    
	    /*******
	     *  <p> Method: performSearchPosts() </p>
	     *  
	     *  <p> Description: Searches posts by the keyword entered in the search field.
	     * If a thread is selected, the search is filtered to that thread only.
	     * If "All Threads" is selected, all threads are searched. </p>
	     */
	    
    	public static List<Post> performSearchPosts(Database db, String keyword,String thread) {

    		if (keyword == null || keyword.isBlank()) {
    			return new ArrayList<>();
    		}


    		String threadFilter = (thread == null || thread.isBlank()) ? null : thread;

    		try {

    			List<Post> results = db.searchPosts(keyword, threadFilter);
    			if (results.isEmpty()) {
    				return new ArrayList<>();
    			}
    			// Display all matching posts each with their replies loaded
    			return results;
    		} catch (Exception e) {
    			e.printStackTrace();
    			return new ArrayList<>();
    		}
    	}

    	/*******
    	 * <p> Method: getPostWithReplies(Database db, int postId) </p>
    	 *
    	 * <p> Description: Loads a single post by ID with all of its replies.
    	 * Returns null if the post does not exist. Used when displaying the parent
    	 * post for an unread or read reply (SUS-9). </p>
    	 *
 	* @param db     the Database instance
 	* @param postId the ID of the post to load
 	* @return the Post with replies attached, or null if not found
 	*/
    	public static Post getPostWithReplies(Database db, int postId) {
    		try {
    			return db.getPostWithReplies(postId);
    		} catch (Exception e) {
    			e.printStackTrace();
    			return null;
    		}
    	}
	

    /**********
	 * <p> Method: toString() </p>
	 *
	 * <p> Description: Returns a basic string representation of the post.
	 * 
	 *
	 * @return a formatted string with the post's key fields
	 */
   
    public String toString() {
        return "Title: " + title +
               "\nAuthor: " + author +
               "\nThread: " + threadName +
               "\nBody: " + body +
               "\n-------------------------";
    }
    
    /**********
	 * <p> Method: format() </p>
	 *
	 * <p> Description: Returns a formatted string for display in the ListView.
	 * If the post has been deleted, shows a deletion notice instead of the
	 * content. All replies are always shown regardless of deletion status. </p>
	 *
	 * @return a formatted display string including the post and all its replies
	 */
    public String format() {
        StringBuilder sb = new StringBuilder();
        if (deleted) {
            sb.append("[ This post has been deleted by the author ]\n");
            sb.append("Thread: ").append(threadName).append("\n");
        } else {
            sb.append("Title: ").append(title).append("\n");
            sb.append("Author: ").append(author).append("\n");
            sb.append("Thread: ").append(threadName).append("\n");
            sb.append("Body: ").append(body).append("\n\n");
        }

        for (Reply r : replies) {
            sb.append("\tReply by ").append(r.getAuthor()).append(": ").append(r.getBody()).append("\n");
        }

        sb.append("\n--------------------------------\n");
        return sb.toString();
    }
}