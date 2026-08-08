package guiPrototype;

/**
 * Aspect: Security & Safety
 * Requirement: Create Discussion Thread (Staff Only)
 */
public class ThreadManager {

    /**
     * Default constructor.
     */
    public ThreadManager() { }

    /**
     * Determines if a user is authorized to perform an action on a thread.
     * @param userRole The role of the current user.
     * @param action   The action being performed (e.g., "create").
     * @return true if the user is staff; false otherwise.
     */
    public static boolean isAuthorizedForThreadAction(String userRole, String action) {
        
        // INTERNAL COMMENT: Staff members have exclusive authority to CRUD threads.
        // This ensures consistent management of the discussion environment.
        return "staff".equalsIgnoreCase(userRole);
    }
}
