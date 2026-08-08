package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: PrivateFeedback Entity Class. </p>
 *
 * <p> Description: Represents a private feedback message sent by a staff member to a student
 * or another staff member. Private feedback is linked to a specific post or reply and is
 * only visible to the sender and the recipient — never shown in the public discussion feed.</p>
 *
 *  
 *
 * @version 1.00    2025-04-20 Initial version
 */
public class PrivateFeedback {
	private int id;
	private String senderUsername;
	private String recipientUsername;
	private int relatedPostId;
    private int relatedReplyId;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;

   

    /**********
     * <p> Method: PrivateFeedback(int, String, String, int, int, String, LocalDateTime, boolean) </p>
     *
     * <p> Description: Full constructor used when loading an existing record from the database. </p>
     *
     * @param id                unique database ID
     * @param senderUsername    username of the staff member who wrote the feedback
     * @param recipientUsername username of the recipient (student or staff)
     * @param relatedPostId     post this feedback concerns (-1 if reply-based)
     * @param relatedReplyId    reply this feedback concerns (-1 if post-based)
     * @param message           the feedback message text
     * @param createdAt         timestamp of creation
     * @param isRead            whether the recipient has read it
     */
    public PrivateFeedback(int id, String senderUsername, String recipientUsername,int relatedPostId, int relatedReplyId,
            String message, LocalDateTime createdAt, boolean isRead) {
                           
        this.id                 = id;
        this.senderUsername     = senderUsername;
        this.recipientUsername  = recipientUsername;
        this.relatedPostId      = relatedPostId;
        this.relatedReplyId     = relatedReplyId;
        this.message            = message;
        this.createdAt          = createdAt;
        this.isRead             = isRead;
    }

    /**********
     * <p> Method: PrivateFeedback(String, String, int, int, String) </p>
     *
     * <p> Description: Creation constructor used when a staff member sends new feedback.
     * The ID and timestamp are assigned by the database on insert. </p>
     *
     * @param senderUsername    username of the staff member sending the feedback
     * @param recipientUsername username of the recipient (student or staff)
     * @param relatedPostId     post this feedback concerns (-1 if reply-based)
     * @param relatedReplyId    reply this feedback concerns (-1 if post-based)
     * @param message           the feedback message text
     */
    public PrivateFeedback(String senderUsername, String recipientUsername,int relatedPostId, int relatedReplyId, String message) {
                           
        this(-1, senderUsername, recipientUsername,
             relatedPostId, relatedReplyId, message, LocalDateTime.now(), false);
    }

   

    /**********
     * <p> Method: format() </p>
     *
     * <p> Description: Returns a human-readable string for display in the staff moderation
     * ListViews. Includes context (post/reply ID), sender, recipient, read status, and message. </p>
     *
     * @return formatted multi-line display string for this feedback record
     */
    public String format() {
        String context   = (relatedPostId != -1)
                           ? "Post #" + relatedPostId
                           : "Reply #" + relatedReplyId;
        String readMark  = isRead ? "READ" : "UNREAD";
       
        return String.format("FEEDBACK: %s | %s\nFrom: %s  To: %s\n%s\n",
                             context, readMark, senderUsername, recipientUsername,
                             message);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSenderUsername() { return senderUsername; }
    public String getRecipientUsername() { return recipientUsername; }
    public int getRelatedPostId() { return relatedPostId; }
    public int getRelatedReplyId() { return relatedReplyId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { this.isRead = read; }
}
