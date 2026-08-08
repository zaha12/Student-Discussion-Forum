package guiRole2;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.PrivateFeedback;
import entityClasses.User;

/*******
 * <p> Title: StaffModerationTests Class. </p>
 *
 * <p> Description: JUnit 5 test class for the Staff Moderation Epic.
 *
 * @version 1.00    2025-04-20 Initial version
 */
public class StaffModerationTests {


    /** The database instance used by each test. Re-created before every test. */
    private Database db;

    /** Staff member username used across tests. */
    private static final String STAFF_USER   = "staffJohn";

    /** Second staff member username used for ownership tests. */
    private static final String STAFF_USER2  = "staffJane";

    /** Student username used as feedback recipient across tests. */
    private static final String STUDENT_USER = "studentZaha";

   @BeforeEach
    public void setUp() throws SQLException {
        db = new Database();
        // Each test gets its own fresh in-memory database — no shared state
        db.connectToDatabase("jdbc:h2:mem:testdb_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");

        db.register(new User(STAFF_USER,   "Pass1!", "John", "", "Staff",   "", "john@test.com", false, false, true));
        db.register(new User(STAFF_USER2,  "Pass1!", "Jane", "", "Staff",   "", "jane@test.com", false, false, true));
        db.register(new User(STUDENT_USER, "Pass1!", "Zaha", "", "Student", "", "zaha@test.com", false, true,  false));
    }

    /**********
     * <p> Method: tearDown() </p>
     *
     * <p> Description: Runs after every test. Closes the database connection
     * to release resources and ensure no state leaks between tests. </p>
     */
   @AfterEach
    public void tearDown() {
        db.closeConnection();
    }

    /**********
     * <p> Method: testSendFeedbackAboutPost() </p>
     *
     * <p> Description: Verifies that a staff member can send private feedback
     * about a post. After inserting, the feedback must be retrievable via
     * getFeedbackBySender and contain the correct recipient and message. </p>
     *
     */
    @Test
    public void testSendFeedbackAboutPost() throws SQLException {
        //create feedback about post ID 1
        PrivateFeedback fb = new PrivateFeedback(
            STAFF_USER, STUDENT_USER, 1, -1, "Good question on post 1!");

        //insert into database
        db.createPrivateFeedback(fb);

        //retrieve by sender and verify contents
        List<PrivateFeedback> results = db.getFeedbackBySender(STAFF_USER);
        assertFalse(results.isEmpty(), "Feedback list should not be empty after insert");
        assertEquals(STUDENT_USER, results.get(0).getRecipientUsername(),
            "Recipient should match the student username");
        assertEquals(1, results.get(0).getRelatedPostId(),
            "Related post ID should be 1");
        assertEquals(-1, results.get(0).getRelatedReplyId(),
            "Reply ID should be -1 for post-based feedback");
        assertEquals("Good question on post 1!", results.get(0).getMessage(),
            "Message content should match what was inserted");
    }


    /**********
     * <p> Method: testSendFeedbackAboutReply() </p>
     *
     * <p> Description: Verifies that a staff member can send private feedback
     * specifically about a reply (relatedPostId = -1, relatedReplyId set).

     */
    @Test
    public void testSendFeedbackAboutReply() throws SQLException {
        //create feedback about reply ID 5
        PrivateFeedback fb = new PrivateFeedback(
            STAFF_USER, STUDENT_USER, -1, 5, "Your reply was off-topic.");

   
        db.createPrivateFeedback(fb);

    
        List<PrivateFeedback> results = db.getFeedbackBySender(STAFF_USER);
        assertFalse(results.isEmpty(), "Feedback list should not be empty");
        assertEquals(-1, results.get(0).getRelatedPostId(),
            "Post ID should be -1 for reply-based feedback");
        assertEquals(5, results.get(0).getRelatedReplyId(),
            "Reply ID should be 5");
    }

  

    /**********
     * <p> Method: testViewFeedbackBySender() </p>
     *
     * <p> Description: Verifies that getFeedbackBySender returns only the
     * feedback sent by the specified staff member, not feedback from others.
     * This ensures staff members cannot see each other's private feedback. </p>
     *
     */
    @Test
    public void testViewFeedbackBySender() throws SQLException {
        //insert feedback from two different staff members
        db.createPrivateFeedback(new PrivateFeedback(
            STAFF_USER,  STUDENT_USER, 1, -1, "Feedback from John"));
        db.createPrivateFeedback(new PrivateFeedback(
            STAFF_USER2, STUDENT_USER, 2, -1, "Feedback from Jane"));

        //retrieve only John's feedback
        List<PrivateFeedback> johnsFeedback = db.getFeedbackBySender(STAFF_USER);

        //only John's feedback is returned, not Jane's
        assertEquals(1, johnsFeedback.size(),
            "Only one feedback item should be returned for staffJohn");
        assertEquals("Feedback from John", johnsFeedback.get(0).getMessage(),
            "The returned feedback should be the one John sent");
    }

    /**********
     * <p> Method: testViewFeedbackBySenderEmpty() </p>
     *
     * <p> Description: Verifies that getFeedbackBySender returns an empty list
     * when the staff member has not sent any feedback yet. The method must
     * not throw an exception or return null for an empty result. </p>
     *
     */
    @Test
    public void testViewFeedbackBySenderEmpty() throws SQLException {
        //query with no feedback in database
        List<PrivateFeedback> results = db.getFeedbackBySender(STAFF_USER);

        //must return empty list, not null
        assertNotNull(results, "Result should not be null");
        assertTrue(results.isEmpty(), "Result should be empty when no feedback has been sent");
    }

    /**********
     * <p> Method: testViewFeedbackByRecipient() </p>
     *
     * <p> Description: Verifies that getFeedbackByRecipient returns all feedback
     * addressed to the given student. This is the method used by the student
     * page to show received feedback. </p>
     *
     */
    @Test
    public void testViewFeedbackByRecipient() throws SQLException {
        //two feedback items sent to the same student
        db.createPrivateFeedback(new PrivateFeedback(
            STAFF_USER,  STUDENT_USER, 1, -1, "First feedback"));
        db.createPrivateFeedback(new PrivateFeedback(
            STAFF_USER2, STUDENT_USER, 2, -1, "Second feedback"));

  
        List<PrivateFeedback> received = db.getFeedbackByRecipient(STUDENT_USER);

        //student sees both items regardless of who sent them
        assertEquals(2, received.size(),
            "Student should see all feedback sent to them");
    }



    /**********
     * <p> Method: testUpdateFeedback() </p>
     *
     * <p> Description: Verifies that updatePrivateFeedback correctly changes
     * the message text of an existing feedback record. The updated message
     * must be retrievable from the database after the update. </p>
     *
     */
    @Test
    public void testUpdateFeedback() throws SQLException {
        //insert a feedback record and retrieve its ID
        db.createPrivateFeedback(new PrivateFeedback(
            STAFF_USER, STUDENT_USER, 1, -1, "Original message"));
        List<PrivateFeedback> list = db.getFeedbackBySender(STAFF_USER);
        int feedbackId = list.get(0).getId();

        //update the message
        db.updatePrivateFeedback(feedbackId, "Updated message");

        //retrieve again and verify the message changed
        List<PrivateFeedback> updated = db.getFeedbackBySender(STAFF_USER);
        assertEquals("Updated message", updated.get(0).getMessage(),
            "Message should reflect the update");
    }

    /**********
     * <p> Method: testOwnershipEnforcement() </p>
     *
     * <p> Description: Verifies the ownership check in the controller layer —
     * a staff member must not be able to update feedback they did not send.
     * The check is done in the controller, not the database, so this test
     * simulates the controller logic directly. </p>
     *
     */
    @Test
    public void testOwnershipEnforcement() throws SQLException {
        //John sends feedback, Jane tries to edit it
        db.createPrivateFeedback(new PrivateFeedback(
            STAFF_USER, STUDENT_USER, 1, -1, "John's feedback"));
        List<PrivateFeedback> list = db.getFeedbackBySender(STAFF_USER);
        PrivateFeedback feedback = list.get(0);

        
        boolean isOwner = feedback.getSenderUsername().equals(STAFF_USER2);

        //Jane should not be allowed to edit
        assertFalse(isOwner,
            "A staff member who did not send the feedback should fail the ownership check");
    }


    /**********
     * <p> Method: testDeleteFeedback() </p>
     *
     * <p> Description: Verifies that deletePrivateFeedback permanently removes
     * the feedback record from the database. After deletion, the feedback must
     * no longer appear in getFeedbackBySender results. </p>
     *
     */
    @Test
    public void testDeleteFeedback() throws SQLException {
        //insert and retrieve the feedback ID
        db.createPrivateFeedback(new PrivateFeedback(
            STAFF_USER, STUDENT_USER, 1, -1, "To be deleted"));
        List<PrivateFeedback> list = db.getFeedbackBySender(STAFF_USER);
        int feedbackId = list.get(0).getId();

        //delete the record
        db.deletePrivateFeedback(feedbackId);

        //list should now be empty
        List<PrivateFeedback> afterDelete = db.getFeedbackBySender(STAFF_USER);
        assertTrue(afterDelete.isEmpty(),
            "Feedback list should be empty after deletion");
    }

   
  

    /**********
     * <p> Method: testMarkFeedbackAsRead() </p>
     *
     * <p> Description: Verifies that markFeedbackAsRead sets isRead = TRUE for
     * the correct feedback record. This is called when a student opens their
     * feedback so the staff member knows it has been seen. </p>
     */
    @Test
    public void testMarkFeedbackAsRead() throws SQLException {
        //insert feedback
        db.createPrivateFeedback(new PrivateFeedback(
            STAFF_USER, STUDENT_USER, 1, -1, "Please review your post"));
        List<PrivateFeedback> list = db.getFeedbackBySender(STAFF_USER);
        int feedbackId = list.get(0).getId();

        // Verify it starts as unread
        assertFalse(list.get(0).isRead(),
            "Feedback should be unread immediately after creation");

        //mark as read
        db.markFeedbackAsRead(feedbackId);

        //retrieve again and verify isRead is now TRUE
        List<PrivateFeedback> after = db.getFeedbackBySender(STAFF_USER);
        assertTrue(after.get(0).isRead(),
            "Feedback should be marked as read after markFeedbackAsRead is called");
    }

}
