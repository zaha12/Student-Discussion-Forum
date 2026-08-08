package guiPrototype;

/**
 * Aspect: Security & Safety
 * Requirement: Private Feedback Authorization
 * Role: Instructional Team (Staff)
 */
//package security;

/**
 * FeedbackManager handles the authorization for viewing 
 * private mentor feedback.
 */
public class FeedbackManager {

	
	/**
     * FeedbackManager constructor.
     */
	public FeedbackManager() {
        // No initialization needed for static utility class
    }
	
	/**
     * Determines if a user is authorized to view a specific piece of feedback.
     * @param userRole    The role of the current user ("staff" or "student").
     * @param currentUserId The unique ID of the user attempting to view data.
     * @param targetStudentId The ID of the student the feedback was written for.
     * @return true if access is granted; false otherwise.
     */
	
    public static boolean isAuthorizedToView(String userRole, int currentUserId, int targetStudentId) {
        
        // The Staff Role is allows to provide feedback
        // They can write private posts to have mentor for students with encouraging messages. 
        if ("staff".equalsIgnoreCase(userRole)) {
            return true;
        }

        // Student Role is restricted to only what they wrote, there is no private views.
        // The current user ID access must match the receiver ID 
        // to prevent unauthorized data from being leaked.
        return "student".equalsIgnoreCase(userRole) && (currentUserId == targetStudentId);
    }
}
