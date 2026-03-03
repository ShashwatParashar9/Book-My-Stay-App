import java.util.LinkedList;
import java.util.Queue;

/**
 * CLASS - Reservation
 * Represents a guest’s intent to book a room.
 */
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
 * CLASS - BookingRequestQueue
 * Manages booking requests using a FIFO queue.
 */
class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        this.requestQueue = new LinkedList<>();
    }

    /** Adds a booking request to the end of the queue. */
    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
    }

    /** Retrieves and removes the head of the queue. */
    public Reservation getNextRequest() {
        return requestQueue.poll();
    }

    /** Checks if there are requests waiting to be processed. */
    public boolean hasPendingRequests() {
        return !requestQueue.isEmpty();
    }
}

/**
 * MAIN CLASS - UseCase5BookingRequestQueue
 * Demonstrates the First-Come-First-Served booking logic.
 */
public class BookMyStayAPP{

    public static void main(String[] args) {
        // 1. Display application header
        System.out.println("Booking Request Queue");

        // 2. Initialize booking queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // 3. Create booking requests (Simulating guest intent)
        Reservation r1 = new Reservation("Abhi", "Single");
        Reservation r2 = new Reservation("Subha", "Double");
        Reservation r3 = new Reservation("Vanmathi", "Suite");

        // 4. Add requests to the queue (Insertion order is preserved)
        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        // 5. Process and display queued booking requests in FIFO order
        while (bookingQueue.hasPendingRequests()) {
            Reservation current = bookingQueue.getNextRequest();
            System.out.println("Processing booking for Guest: " + current.getGuestName() +
                    ", Room Type: " + current.getRoomType());
        }
    }
}