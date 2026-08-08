package entityClasses;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import database.Database;
import java.sql.Timestamp;

/*******
 * <p> Title: AdminRequest Class. </p>
 * <p> Description: Represents an administrative request submitted by staff members.
 * Supports the full lifecycle: creation by staff, viewing by staff and admins,
 * action documentation by admins, closing, and reopening with link to original request. </p>
 *
 */
public class AdminRequest {

    // Private attributes for this entity object
    private int id;
    private String title;           // Short description of the request
    private String description;     // Detailed description of the issue/action needed
    private String requester;       // Staff member who submitted the request
    private String status;          // "Open", "In Progress", "Closed"
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private String adminNotes;      // Actions documented by admin
    private Integer originalRequestId; // Link to original closed request when reopened (null if not reopened)
    private boolean isReopened;

    // In-memory list of all AdminRequests (similar to Post class)
    private static List<AdminRequest> adminRequests = new ArrayList<>();

    /*******
     * <p> Constructor: AdminRequest(String title, String description, String requester) </p>
     * <p> Description: Creates a new AdminRequest when a staff member submits it. </p>
     *
     * @param title the short title of the admin request
     * @param description the detailed description of the request
     * @param requester the username of the staff member submitting the request
     */
    public AdminRequest(String title, String description, String requester) {
        this.title = title;
        this.description = description;
        this.requester = requester;
        this.status = "Open";
        this.createdAt = LocalDateTime.now();
        this.closedAt = null;
        this.adminNotes = "";
        this.originalRequestId = null;
        this.isReopened = false;
    }

    /*******
     * <p> Constructor: AdminRequest(int id, String title, String description, String requester,
     * String status, Timestamp createdAt, Timestamp closedAt, String adminNotes,
     * Integer originalRequestId, boolean isReopened) </p>
     *
     * <p> Description: Creates an AdminRequest loaded from the database. </p>
     *
     * @param id the database-assigned request ID
     * @param title the title of the request
     * @param description the detailed description
     * @param requester the staff member who created the request
     * @param status current status ("Open", "In Progress", "Closed")
     * @param createdAt creation timestamp
     * @param closedAt when the request was closed (null if still open)
     * @param adminNotes actions taken by admin
     * @param originalRequestId link to original request if this is reopened
     * @param isReopened whether this request was reopened
     */
    public AdminRequest(int id, String title, String description, String requester,
                       String status, Timestamp createdAt, Timestamp closedAt,
                       String adminNotes, Integer originalRequestId, boolean isReopened) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requester = requester;
        this.status = status != null ? status : "Open";
        this.createdAt = createdAt != null ? createdAt.toLocalDateTime() : LocalDateTime.now();
        this.closedAt = closedAt != null ? closedAt.toLocalDateTime() : null;
        this.adminNotes = adminNotes != null ? adminNotes : "";
        this.originalRequestId = originalRequestId;
        this.isReopened = isReopened;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getRequester() { return requester; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public String getAdminNotes() { return adminNotes; }
    public Integer getOriginalRequestId() { return originalRequestId; }
    public boolean isReopened() { return isReopened; }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReopened(boolean reopened) {
        this.isReopened = reopened;
    }


    /*******
     * <p> Method: addAdminRequest(AdminRequest request) </p>
     *
     * <p> Description: Adds the given AdminRequest object to the in-memory list
     * of all admin requests. This does not interact with the database. </p>
     *
     * @param request  the AdminRequest to add to the in-memory list
     */
    public static void addAdminRequest(AdminRequest request) {
        adminRequests.add(request);
    }

    /*******
     * <p> Method: addAdminRequestToDBAndList(Database db, AdminRequest request) </p>
     *
     * <p> Description: Inserts the AdminRequest into the database, retrieves the
     * auto-generated ID, assigns it to the request object, and then adds the
     * request to the in-memory list. </p>
     *
     * @param db       the Database object used to insert the request
     * @param request  the AdminRequest to store and track
     *
     * @throws Exception if the database insert operation fails
     */
    public static void addAdminRequestToDBAndList(Database db, AdminRequest request) throws Exception {
        int generatedId = db.createAdminRequest(
            request.getTitle(),
            request.getDescription(),
            request.getRequester()
        );
        request.setId(generatedId);
        addAdminRequest(request);
    }

    /*******
     * <p> Method: getAllAdminRequests() </p>
     *
     * <p> Description: Returns a copy of the full in-memory list of AdminRequests.
     * A new list is returned to prevent external modification of the internal list. </p>
     *
     * @return a list containing all AdminRequests
     */
    public static List<AdminRequest> getAllAdminRequests() {
        return new ArrayList<>(adminRequests); // return copy to prevent external modification
    }

    /*******
     * <p> Method: getOpenAdminRequests() </p>
     *
     * <p> Description: Retrieves all AdminRequests whose status is either "Open"
     * or "In Progress". </p>
     *
     * @return a list of open or in-progress AdminRequests
     */
    public static List<AdminRequest> getOpenAdminRequests() {
        return adminRequests.stream()
                .filter(r -> "Open".equals(r.getStatus()) || "In Progress".equals(r.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }

    /*******
     * <p> Method: getClosedAdminRequests() </p>
     *
     * <p> Description: Retrieves all AdminRequests whose status is "Closed". </p>
     *
     * @return a list of closed AdminRequests
     */

    public static List<AdminRequest> getClosedAdminRequests() {
        return adminRequests.stream()
                .filter(r -> "Closed".equals(r.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }

    /*******
     * <p> Method: isEmpty() </p>
     *
     * <p> Description: Checks whether the in-memory AdminRequest list contains
     * any entries. </p>
     *
     * @return true if no AdminRequests exist, false otherwise
     */
    public static boolean isEmpty() {
        return adminRequests.isEmpty();
    }

    /*******
     * <p> Method: clearAdminRequests() </p>
     *
     * <p> Description: Removes all AdminRequests from the in-memory list. This
     * does not affect the database. </p>
     */
    public static void clearAdminRequests() {
        adminRequests.clear();
    }


    /*******
     * <p> Method: closeRequest(String notes) </p>
     *
     * <p> Description: Closes this AdminRequest by setting its status to "Closed",
     * recording the closure timestamp, and saving any admin notes provided. </p>
     *
     * @param notes  optional notes documenting admin actions taken
     */
    public void closeRequest(String notes) {
        this.status = "Closed";
        this.closedAt = LocalDateTime.now();
        if (notes != null && !notes.isBlank()) {
            this.adminNotes = notes;
        }
    }

    /**
     * Reopens a closed request and links it to the original.
     */
    public void reopenRequest(String newDescription, int originalId) {
        this.status = "Open";
        this.description = newDescription;
        this.originalRequestId = originalId;
        this.isReopened = true;
        this.closedAt = null;
    }

    /**********
     * <p> Method: format() </p>
     * <p> Description: Returns a formatted string for display in lists or GUI. </p>
     */
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(title).append("\n");
        sb.append("From: ").append(requester).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Created: ").append(createdAt.toLocalDate()).append("\n");

        if (isReopened && originalRequestId != null) {
            sb.append("Reopened from Original Request ID: ").append(originalRequestId).append("\n");
        }

        if (!adminNotes.isBlank()) {
            sb.append("Admin Notes: ").append(adminNotes).append("\n");
        }

        sb.append("Description: ").append(description).append("\n");
        sb.append("--------------------------------\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "AdminRequest #" + id + " | " + title + " | Status: " + status;
    }
}