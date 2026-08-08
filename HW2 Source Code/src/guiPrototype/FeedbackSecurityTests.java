package guiPrototype;
/**
 * Aspect: Security & Safety Testing
 */

/**
 * FeedbackSecurityTests makes sure that the private feedback 
 * safety boundaries are met and followed.
 */
public class FeedbackSecurityTests {

	/**
     * Default constructor for FeedbackSecurityTests.
     */
    public FeedbackSecurityTests() {
        // No initialization required
    }
    
    /**
     * Executes positive and negative scenarios for feedback testing.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("Running Private Feedback Security Prototype Tests...\n");

        // Positive Test: Student viewing their own data
        boolean test1 = FeedbackManager.isAuthorizedToView("student", 1001, 1001);
        printTestResult("Student Viewing Own Feedback", test1, true);

        // Negative Test: Student attempting to view peer data (which is private)
        boolean test2 = FeedbackManager.isAuthorizedToView("student", 1001, 1002);
        printTestResult("Student Viewing Peer Feedback", test2, false);

        // Positive Test: Staff reviewing any student's data
        boolean test3 = FeedbackManager.isAuthorizedToView("staff", 500, 1001);
        printTestResult("Staff Reviewing Student Feedback", test3, true);
        
        //TP3 - ADDED THESE 2 TESTS
     // Positive Test: Staff creating a thread
        boolean threadTest1 = ThreadManager.isAuthorizedForThreadAction("staff", "create");
        printTestResult("Staff Creating New Thread", threadTest1, true);

        // Negative Test: Student attempting to delete a thread
        boolean threadTest2 = ThreadManager.isAuthorizedForThreadAction("student", "delete");
        printTestResult("Student Deleting Thread", threadTest2, false);
    }
    
    /**
     * Helper method to print the result of each security test case.
     * @param scenario Description of the test scenario.
     * @param actual   The boolean result returned by the manager.
     * @param expected The expected boolean outcome for the test.
     */
    
    private static void printTestResult(String scenario, boolean actual, boolean expected) {
        String status = (actual == expected) ? "SUCCESS" : "FAILURE";
        System.out.printf("%-35s | Status: %s%n", scenario, status);
    }
}
