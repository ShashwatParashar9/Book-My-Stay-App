import java.util.*;

/**
 * --- Book My Stay App: Final Integrated Version ---
 * Filename: BookMyStayApp.java
 * * This file integrates:
 * - Use Case 6: Room Allocation (Uniqueness)
 * - Use Case 7: Add-On Services (Mapping)
 * - Use Case 8: Booking History (Persistence)
 * - Use Case 9: Error Handling (Validation)
 */

// --- CUSTOM EXCEPTION (Use Case 9) ---
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// --- DATA MODELS ---
class Service {
    private String name;
    private double cost;

    public Service(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }
    public String getName() { return name; }
    public double getCost() { return cost; }
}

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
}

class BookingEngine {
    private Set<String> allocatedIds = new HashSet<>();
    private List<Reservation> history = new ArrayList<>();
    private Map<String, List<Service>> addOns = new HashMap<>();
    private int counter = 1;

    public void allocate(Reservation res, RoomInventory inv) {
        String id = res.getRoomType() + "-" + counter++;
        res.setRoomId(id);
        allocatedIds.add(id);
        inv.decrement(res.getRoomType());
        history.add(res);
        System.out.println("Booking confirmed for Guest: " + res.getGuestName() + ", Room ID: " + id);
    }

    public void addService(String roomId, Service service) {
        addOns.computeIfAbsent(roomId, k -> new ArrayList<>()).add(service);
        System.out.println("Added " + service.getName() + " to Room " + roomId);
    }

    public List<Reservation> getHistory() { return history; }
}

class ReservationValidator {
    public void validate(String name, String type, RoomInventory inv) throws InvalidBookingException {
        if (name == null || name.trim().isEmpty())
            throw new InvalidBookingException("Guest name cannot be empty.");
        if (!inv.exists(type))
            throw new InvalidBookingException("Invalid room type selected. (Note: Case Sensitive)");
        if (inv.getCount(type) <= 0)
            throw new InvalidBookingException("No availability for " + type + ".");
    }
}

// --- MAIN CLASS ---
public class BookMyStayApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RoomInventory inventory = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();
        BookingEngine engine = new BookingEngine();

        System.out.println("=== Book My Stay App Control Panel ===");

        while (true) {
            try {
                System.out.println("\n1. New Booking | 2. View History | 3. Exit");
                System.out.print("Select Option: ");
                String choice = scanner.nextLine();

                if (choice.equals("3")) break;

                if (choice.equals("1")) {
                    System.out.print("Enter Guest Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Room Type (Single/Double/Suite): ");
                    String type = scanner.nextLine();

                    // Validation (Use Case 9)
                    validator.validate(name, type, inventory);

                    // Allocation (Use Case 6)
                    Reservation res = new Reservation(name, type);
                    engine.allocate(res, inventory);

                    // Optional Add-on (Use Case 7)
                    System.out.print("Add Breakfast for 500? (yes/no): ");
                    if (scanner.nextLine().equalsIgnoreCase("yes")) {
                        engine.addService(res.getRoomId(), new Service("Breakfast", 500.0));
                    }

                } else if (choice.equals("2")) {
                    // Reporting (Use Case 8)
                    System.out.println("\n--- Audit Trail / History ---");
                    for (Reservation r : engine.getHistory()) {
                        System.out.println("Guest: " + r.getGuestName() + " | Room: " + r.getRoomId());
                    }
                }
            } catch (InvalidBookingException e) {
                System.out.println("Booking Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected Error occurred.");
            }
        }
        scanner.close();
        System.out.println("System Shutdown.");
    }
}