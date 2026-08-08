package entityClasses;

import java.util.ArrayList;
import java.util.List;
import database.Database;


/*******
 * <p> Title: AllPosts Class. </p>
 *
 * <p> Description: A container for managing an in memory list of Post objects.
 * Provides methods to add, retrieve, filter, and format posts, as well as
 * saving new posts to the database. </p>
 *
 * 
 */
public class AllPosts {
    //private attribute
    private List<Post> posts;
    
    /**********
	 * <p> Constructor: AllPosts() </p>
	 *
	 * <p> Description: Initializes an empty list of posts. </p>
	 */
    public AllPosts() {
        posts = new ArrayList<>();
    }

    /**********
	 * <p> Method: addPost(Post post) </p>
	 *
	 * <p> Description: Adds a Post to the in memory list only.
	 * Use addPostToDBAndList to also save to the database. </p>
	 *
	 * @param post the Post object to add
	 */
    public void addPost(Post post) {
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
    public void addPostToDBAndList(Database db, Post post) throws Exception {
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
    public List<Post> getPosts() {
        return posts;
    }

    /**********
	 * <p> Method: isEmpty() </p>
	 *
	 * <p> Description: Returns true if there are no posts in the in-memory list. </p>
	 *
	 * @return true if the list is empty, false otherwise
	 */
    public boolean isEmpty() {
        return posts.isEmpty();
    }


	/**********
	 * <p> Method: formatAll() </p>
	 *
	 * <p> Description: Returns a formatted string of all posts in the list by
	 * calling each post's format() method. Returns a message if the list is empty. </p>
	 *
	 * @return a formatted string of all posts, or "No posts found." if empty
	 */
    public String formatAll() {

        if (posts.isEmpty()) {
            return "No posts found.";
        }

        StringBuilder sb = new StringBuilder();

        for (Post post : posts) {
            sb.append(post.format());
        }

        return sb.toString();
    }

    
}