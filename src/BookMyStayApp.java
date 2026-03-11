import java.util.*;

/**
 * --- Book My Stay App: Use Case 11 ---
 * Filename: BookMyStayApp.java
 * This version demonstrates Thread Safety and Concurrent Processing.
 */

// --- Supporting Data Structures ---

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

class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation res) {
        queue.add(res);
    }

    public Reservation pollRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 2);
    }

    public boolean isAvailable(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrementInventory(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public void displayInventory() {
        System.out.println("\nRemaining Inventory:");
        inventory.forEach((type, count) -> System.out.println(type + ": " + count));
    }
}

class RoomAllocationService {
    private Map<String, Integer> allocationCount = new HashMap<>();

    public void allocateRoom(Reservation reservation, RoomInventory inventory) {
        String type = reservation.getRoomType();
        if (inventory.isAvailable(type)) {
            inventory.decrementInventory(type);
            int id = allocationCount.getOrDefault(type, 0) + 1;
            allocationCount.put(type, id);
            System.out.println("Booking confirmed for Guest: " + reservation.getGuestName() +
                    ", Room ID: " + type + "-" + id);
        }
    }
}

// --- CLASS - ConcurrentBookingProcessor ---

class ConcurrentBookingProcessor implements Runnable {
    private BookingRequestQueue bookingQueue;
    private RoomInventory inventory;
    private RoomAllocationService allocationService;

    public ConcurrentBookingProcessor(BookingRequestQueue bookingQueue,
                                      RoomInventory inventory,
                                      RoomAllocationService allocationService) {
        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
        this.allocationService = allocationService;
    }

    @Override
    public void run() {
        while (true) {
            Reservation reservation = null;

            /*
             * Synchronize on the booking queue to ensure that
             * only one thread can retrieve a request at a time.
             */
            synchronized (bookingQueue) {
                if (bookingQueue.isEmpty()) {
                    break; // Exit if no more requests
                }
                reservation = bookingQueue.pollRequest();
            }

            if (reservation != null) {
                /*
                 * Allocation also mutates shared inventory.
                 * Synchronization ensures atomic allocation and prevents double-booking.
                 */
                synchronized (inventory) {
                    allocationService.allocateRoom(reservation, inventory);
                }
            }

            // Brief sleep to simulate processing time and encourage thread interleaving
            try { Thread.sleep(50); } catch (InterruptedException e) { break; }
        }
    }
}

// --- MAIN CLASS - BookMyStayApp ---

public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("Concurrent Booking Simulation");

        // Initialize Shared Resources
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();

        // Populate Queue with requests
        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Double"));
        bookingQueue.addRequest(new Reservation("Kural", "Suite"));
        bookingQueue.addRequest(new Reservation("Subha", "Single"));

        // Create booking processor tasks (Multi-threaded workers)
        Thread t1 = new Thread(new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService));
        Thread t2 = new Thread(new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService));

        // Start concurrent processing
        t1.start();
        t2.start();

        try {
            // Wait for threads to finish processing all requests
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }

        // Display final state
        inventory.displayInventory();
    }
}