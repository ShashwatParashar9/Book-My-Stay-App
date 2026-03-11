import java.util.*;

/**
 * --- Book My Stay App: Final Integrated System ---
 * Filename: BookMyStayApp.java
 */

// --- CUSTOM EXCEPTION ---
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) { super(message); }
}

// --- DATA MODELS ---
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
}

// --- CORE SERVICES ---

class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 2);
    }

    public boolean exists(String type) { return inventory.containsKey(type); }
    public int getCount(String type) { return inventory.getOrDefault(type, 0); }

    public void decrement(String type) { inventory.put(type, inventory.get(type) - 1); }
    public void increment(String type) { inventory.put(type, inventory.get(type) + 1); }
}

class BookingService {
    private Set<String> allocatedIds = new HashSet<>();
    private List<Reservation> history = new ArrayList<>();
    private Map<String, Reservation> activeBookings = new HashMap<>();
    private Stack<String> rollbackStack = new Stack<>();
    private int counter = 1;

    // UC6 & UC8: Allocation and History
    public void confirmBooking(String name, String type, RoomInventory inv) {
        String id = type + "-" + counter++;
        Reservation res = new Reservation(name, type);
        res.setRoomId(id);

        allocatedIds.add(id);
        inv.decrement(type);
        history.add(res);
        activeBookings.put(id, res);

        System.out.println("Booking confirmed for Guest: " + name + ", Room ID: " + id);
    }

    // UC10: Cancellation & Rollback
    public void cancelBooking(String roomId, RoomInventory inv) throws InvalidBookingException {
        if (!activeBookings.containsKey(roomId)) {
            throw new InvalidBookingException("Reservation ID not found or already cancelled.");
        }

        Reservation res = activeBookings.remove(roomId);
        inv.increment(res.getRoomType());
        rollbackStack.push(roomId); // LIFO tracking

        System.out.println("Booking cancelled successfully. Inventory restored for room type: " + res.getRoomType());
    }

    public void showReports() {
        System.out.println("\n--- Booking History Report ---");
        for (Reservation r : history) {
            String status = activeBookings.containsKey(r.getRoomId()) ? "[Active]" : "[Cancelled]";
            System.out.println(status + " Guest: " + r.getGuestName() + " | Room: " + r.getRoomId());
        }
    }

    public void showRollbackHistory() {
        System.out.println("\nRollback History (Most Recent First):");
        if (rollbackStack.isEmpty()) {
            System.out.println("No cancellations recorded.");
        } else {
            // Using a temporary stack to preserve the order without destroying data
            Stack<String> temp = (Stack<String>) rollbackStack.clone();
            while (!temp.isEmpty()) {
                System.out.println("Released Reservation ID: " + temp.pop());
            }
        }
    }
}

class ReservationValidator {
    public void validate(String name, String type, RoomInventory inv) throws InvalidBookingException {
        if (name == null || name.trim().isEmpty())
            throw new InvalidBookingException("Guest name cannot be empty.");
        if (!inv.exists(type))
            throw new InvalidBookingException("Invalid room type selected.");
        if (inv.getCount(type) <= 0)
            throw new InvalidBookingException("No availability for " + type + ".");
    }
}

// --- MAIN APPLICATION CLASS ---
public class BookMyStayApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RoomInventory inventory = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();
        BookingService bookingService = new BookingService();

        System.out.println("Booking System Initialized.");

        while (true) {
            try {
                System.out.println("\n1. Book | 2. Cancel | 3. Reports | 4. Rollback History | 5. Exit");
                System.out.print("Action: ");
                String choice = scanner.nextLine();

                if (choice.equals("5")) break;

                switch (choice) {
                    case "1":
                        System.out.print("Enter guest name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter room type (Single/Double/Suite): ");
                        String type = scanner.nextLine();

                        validator.validate(name, type, inventory);
                        bookingService.confirmBooking(name, type, inventory);
                        break;

                    case "2":
                        System.out.print("Enter Room ID to cancel: ");
                        String id = scanner.nextLine();
                        bookingService.cancelBooking(id, inventory);
                        break;

                    case "3":
                        bookingService.showReports();
                        break;

                    case "4":
                        bookingService.showRollbackHistory();
                        System.out.println("Current Single Availability: " + inventory.getCount("Single"));
                        break;

                    default:
                        System.out.println("Invalid selection.");
                }
            } catch (InvalidBookingException e) {
                System.out.println("Operation failed: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected system error.");
            }
        }
        scanner.close();
        System.out.println("Application terminated.");
    }
}