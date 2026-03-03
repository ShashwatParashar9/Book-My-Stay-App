import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain Model: Represents physical room characteristics.
 */
class Room {
    private String type;
    private int beds;
    private int size;
    private double price;

    public Room(String type, int beds, int size, double price) {
        this.type = type;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public String getType() { return type; }
    public int getBeds() { return beds; }
    public int getSize() { return size; }
    public double getPrice() { return price; }
}

/**
 * Logic Layer: Acts as the Single Source of Truth for availability.
 */
class RoomInventory {
    // Key -> Room type name | Value -> Available room count
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
        initializeInventory();
    }

    /**
     * Centralized setup replaces scattered variables from previous use cases.
     */
    private void initializeInventory() {
        roomAvailability.put("Single Room", 5);
        roomAvailability.put("Double Room", 3);
        roomAvailability.put("Suite Room", 2);
    }

    public Map<String, Integer> getRoomAvailability() {
        return roomAvailability;
    }

    /**
     * Controlled update method to ensure state consistency.
     */
    public void updateAvailability(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }
}

/**
 * Main Application Class
 * Version 3.0
 */
public class BookMyStayAPP {

    public static void main(String[] args) {
        // 1. Initialize Room Characteristics (Domain)
        List<Room> roomCatalog = new ArrayList<>();
        roomCatalog.add(new Room("Single Room", 1, 250, 1500.0));
        roomCatalog.add(new Room("Double Room", 2, 400, 2500.0));
        roomCatalog.add(new Room("Suite Room", 3, 750, 5000.0));

        // 2. Initialize Centralized Inventory
        RoomInventory inventory = new RoomInventory();

        // Update Suite Room to 3 to reflect the latest status
        inventory.updateAvailability("Suite Room", 3);

        // 3. Display Inventory Status
        System.out.println("Hotel Room Inventory Status\n");

        for (Room room : roomCatalog) {
            String type = room.getType();
            // O(1) Lookup complexity using HashMap
            Integer count = inventory.getRoomAvailability().get(type);

            System.out.println(type + ":");
            System.out.println("Beds: " + room.getBeds());
            System.out.println("Size: " + room.getSize() + " sqft");
            System.out.println("Price per night: " + room.getPrice());
            System.out.println("Available Rooms: " + (count != null ? count : 0));
            System.out.println();
        }
    }
}