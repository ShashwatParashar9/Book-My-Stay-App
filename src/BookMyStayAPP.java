import java.util.Map;
import java.util.HashMap;

/**
 * --- Book My Stay App: Use Case 4 ---
 * This file contains the Domain Model, Inventory, Search Service,
 * and the Main execution class.
 */

// --- Domain Model ---
class Room {
    private String type;
    private int beds;
    private int size; // in sqft
    private double pricePerNight;

    public Room(String type, int beds, int size, double pricePerNight) {
        this.type = type;
        this.beds = beds;
        this.size = size;
        this.pricePerNight = pricePerNight;
    }

    public String getType() { return type; }
    public int getBeds() { return beds; }
    public int getSize() { return size; }
    public double getPricePerNight() { return pricePerNight; }

    public void displayDetails(int availableCount) {
        System.out.println(type + " Room:");
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sqft");
        System.out.println("Price per night: " + pricePerNight);
        System.out.println("Available: " + availableCount);
        System.out.println();
    }
}

// --- Inventory Management ---
class RoomInventory {
    private Map<String, Integer> availability = new HashMap<>();

    public void updateInventory(String type, int count) {
        availability.put(type, count);
    }

    /**
     * Provides read-only access to the current availability state.
     */
    public Map<String, Integer> getRoomAvailability() {
        // Returning a copy or the reference for read-only purposes
        return new HashMap<>(availability);
    }
}

// --- Search Service ---
/**
 * Use Case 4: Room Search & Availability Check
 * Focuses on read-only access and defensive filtering.
 */
class RoomSearchService {

    /**
     * Displays available rooms along with their details and pricing.
     * Performs read-only access to inventory and room data.
     */
    public void searchAvailableRooms(
            RoomInventory inventory,
            Room singleRoom,
            Room doubleRoom,
            Room suiteRoom) {

        System.out.println("Room Search Results");
        System.out.println("-------------------");

        Map<String, Integer> availability = inventory.getRoomAvailability();

        // Check and display Single Room availability
        if (availability.getOrDefault("Single", 0) > 0) {
            singleRoom.displayDetails(availability.get("Single"));
        }

        // Check and display Double Room availability
        if (availability.getOrDefault("Double", 0) > 0) {
            doubleRoom.displayDetails(availability.get("Double"));
        }

        // Check and display Suite Room availability
        if (availability.getOrDefault("Suite", 0) > 0) {
            suiteRoom.displayDetails(availability.get("Suite"));
        }
    }
}

// --- Main Application Entry Point ---
public class BookMyStayAPP{

    public static void main(String[] args) {
        // 1. Initialize Room Definitions (Domain Objects)
        Room single = new Room("Single", 1, 250, 1500.0);
        Room doubleRm = new Room("Double", 2, 400, 2500.0);
        Room suite = new Room("Suite", 3, 750, 5000.0);

        // 2. Initialize and Populate Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.updateInventory("Single", 5);
        inventory.updateInventory("Double", 3);
        inventory.updateInventory("Suite", 2);

        // 3. Perform Room Search
        RoomSearchService searchService = new RoomSearchService();

        // The Search Service reads data but does not modify the inventory object
        searchService.searchAvailableRooms(inventory, single, doubleRm, suite);

        System.out.println("Search complete. System state remains unchanged.");
    }
}