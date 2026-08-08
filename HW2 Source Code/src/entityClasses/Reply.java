package entityClasses;
import java.sql.Timestamp;
import java.time.LocalDateTime;


/*******
 * <p> Title: Reply Class. </p>
 *
 * <p> Description: Represents a reply to a post. Stores the reply's content,
 * author, and a reference to its parent post via postId. </p>
 *
 * 
 */
public class Reply {
    //private attributes
    private int id;
    private int postId;
    private String body;
    private String author;
    private LocalDateTime createdAt;
    
    
    /*******
     * <p> Constructor: Reply(int id, int postId, String body, String author,
	 * LocalDateTime createdAt) </p>
	 * 
	 * <p> Description: Creates a Reply loaded from the database using a LocalDateTime
	 * timestamp. The timestamp is accepted to match existing call sites but is not
	 * stored since it is not displayed. </p>
	 * 
     * @param id         the database-assigned reply ID
     * @param postId     the ID of the parent post this reply belongs to
     * @param body       the body content of the reply
     * @param author     the username of the reply author
     * @param createdAt  the creation timestamp 
     */
    public Reply(int id,int postId,String body, String author,LocalDateTime createdAt) {

        this.id = id;
        this.postId = postId;
        this.body = body;
        this.author = author;
        this.createdAt = createdAt;
    }
    
    /**********
	 * <p> Constructor: Reply(int id, int postId, String body, String author,
	 * Timestamp createdAt) </p>
	 *
	 * <p> Description: Creates a Reply loaded from the database using a SQL Timestamp.
	 * The timestamp is accepted to match the database result set but is not stored
	 * since it is not displayed. </p>
	 *
	 * @param id        the database-assigned reply ID
	 * @param postId    the ID of the parent post this reply belongs to
	 * @param body      the body content of the reply
	 * @param author    the username of the reply author
	 * @param createdAt the SQL timestamp from the database
	 */
    public Reply(int id, int postId, String body, String author, Timestamp createdAt) {
        this.id = id;
        this.postId = postId;
        this.body = body;
        this.author = author;
        this.createdAt = createdAt.toLocalDateTime();
    } 
    
    //setters
    public int getId() { return id; }

    public int getPostId() { return postId; }

    public String getBody() { return body; }

    public String getAuthor() { return author; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    /**********
	 * <p> Method: format() </p>
	 *
	 * <p> Description: Returns a formatted string representation of this reply
	 * for display purposes. Timestamp is excluded since it is not stored. </p>
	 *
	 * @return a formatted display string for this reply
	 */
    public String format() {
        return """
            =================================
            Reply ID: %d
            Post ID: %d
            Author: %s
            Body: %s
            Created: %s
            ---------------------------------
            """.formatted(id, postId, author, body, createdAt);
    }
}