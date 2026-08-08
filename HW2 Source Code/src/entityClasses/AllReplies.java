package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: AllReplies Class. </p>
 *
 * <p> Description: A container for managing an in memory list of Reply objects.
 * Provides methods to add, retrieve, and format replies. </p>
 *
 *
 */
public class AllReplies {
    //private attribute
    private List<Reply> replies;

    /**********
	 * <p> Constructor: AllReplies() </p>
	 *
	 * <p> Description: Initializes an empty list of replies. </p>
	 */
    public AllReplies() {
        replies = new ArrayList<>();
    }

    /**********
	 * <p> Method: addReply(Reply reply) </p>
	 *
	 * <p> Description: Adds a Reply to the in memory list. </p>
	 *
	 * @param reply the Reply object to add
	 */
    public void addReply(Reply reply) {
        replies.add(reply);
    }
   
	/**********
	 * <p> Method: getReplies() </p>
	 *
	 * <p> Description: Returns the full in-memory list of replies. </p>
	 *
	 * @return the list of Reply objects
	 */
    public List<Reply> getReplies() {
        return replies;
    }


	/**********
	 * <p> Method: isEmpty() </p>
	 *
	 * <p> Description: Returns true if there are no replies in the in-memory list. </p>
	 *
	 * @return true if the list is empty, false otherwise
	 */
    public boolean isEmpty() {
        return replies.isEmpty();
    }

    /**********
	 * <p> Method: formatAll() </p>
	 *
	 * <p> Description: Returns a formatted string of all replies in the list by
	 * calling each reply's format() method. Returns a message if the list is empty. </p>
	 *
	 * @return a formatted string of all replies, or "No replies found." if empty
	 */
    public String formatAll() {

        if (replies.isEmpty()) {
            return "No replies found.";
        }

        StringBuilder sb = new StringBuilder();

        for (Reply reply : replies) {
            sb.append(reply.format());
        }

        return sb.toString();
    }
}