import java.util.*;

/**
 * --- Book My Stay App: Use Case 8 ---
 * This file implements historical tracking and reporting features.
 */

// 1. Supporting Class: Reservation
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

/**
 * CLASS - BookingHistory
 * Maintains a persistent record of confirmed reservations in memory.
 */
class BookingHistory {
    /**
     * List that stores confirmed reservations.
     * ArrayList is chosen because it preserves insertion order (FIFO)
     * and provides efficient sequential access for reports.
     */
    private List<Reservation> confirmedReservations;

    /**
     * Initializes an empty booking history.
     */
    public BookingHistory() {
        this.confirmedReservations = new ArrayList<>();
    }

    /**
     * Adds a confirmed reservation to booking history.
     * @param reservation confirmed booking
     */
    public void addReservation(Reservation reservation) {
        confirmedReservations.add(reservation);
    }

    /**
     * Returns all confirmed reservations.
     * @return list of reservations
     */
    public List<Reservation> getConfirmedReservations() {
        return new ArrayList<>(confirmedReservations); // Return copy to protect internal state
    }
}

/**
 * CLASS - BookingReportService
 * Generates summaries and reports from stored booking data.
 */
class BookingReportService {
    /**
     * Displays a summary report of all confirmed bookings.
     * This method decouples data storage from data presentation.
     * * @param history booking history object
     */
    public void generateReport(BookingHistory history) {
        List<Reservation> records = history.getConfirmedReservations();

        System.out.println("\nBooking History Report");

        if (records.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (Reservation res : records) {
            System.out.println("Guest: " + res.getGuestName() +
                    ", Room Type: " + res.getRoomType());
        }
    }
}

/**
 * MAIN CLASS - UseCase8BookingHistoryReport
 * Demonstrates the ordered audit trail and reporting.
 */
public class BookMyStayAPP {

    /**
     * Application entry point.
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("Booking History and Reporting");

        // Initialize History and Reporting Services
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();

        // Simulate successful bookings being added to history
        // In a full system, these would be added after Use Case 6 allocation logic
        history.addReservation(new Reservation("Abhi", "Single"));
        history.addReservation(new Reservation("Subha", "Double"));
        history.addReservation(new Reservation("Vanmathi", "Suite"));

        // Generate the final report
        reportService.generateReport(history);
    }
}