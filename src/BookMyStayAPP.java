import java.util.*;

/**
 * --- Book My Stay App: Use Case 6 ---
 * This file contains the Reservation model, RoomInventory,
 * RoomAllocationService, and the Main execution class.
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

// 2. Supporting Class: RoomInventory
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addInventory(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public boolean isAvailable(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrementInventory(String roomType) {
        int current = inventory.getOrDefault(roomType, 0);
        if (current > 0) {
            inventory.put(roomType, current - 1);
        }
    }
}

/**
 * CLASS - RoomAllocationService
 * Responsible for assigning unique IDs and preventing double-booking.
 */
class RoomAllocationService {

    private Set<String> allocatedRoomIds;
    private Map<String, Set<String>> assignedRoomsByType;

    public RoomAllocationService() {
        this.allocatedRoomIds = new HashSet<>();
        this.assignedRoomsByType = new HashMap<>();
    }

    /**
     * Confirms a booking request by assigning a unique room ID
     * and updating inventory immediately.
     */
    public void allocateRoom(Reservation reservation, RoomInventory inventory) {
        String type = reservation.getRoomType();

        if (inventory.isAvailable(type)) {
            // Generate unique ID
            String roomId = generateRoomId(type);

            // Record the ID to prevent reuse (Uniqueness Enforcement)
            allocatedRoomIds.add(roomId);

            // Map the room type to the assigned room
            assignedRoomsByType.putIfAbsent(type, new HashSet<>());
            assignedRoomsByType.get(type).add(roomId);

            // Update inventory immediately (Atomic Logical Operation)
            inventory.decrementInventory(type);

            System.out.println("Booking confirmed for Guest: " + reservation.getGuestName() +
                    ", Room ID: " + roomId);
        } else {
            System.out.println("Booking failed for Guest: " + reservation.getGuestName() +
                    " - No " + type + " rooms available.");
        }
    }

    /**
     * Generates a unique room ID for the given room type.
     * Simple logic: Type + (Current Count in that type + 1)
     */
    private String generateRoomId(String roomType) {
        int count = assignedRoomsByType.getOrDefault(roomType, new HashSet<>()).size() + 1;
        String newId = roomType + "-" + count;

        // Ensure the ID is truly unique in the global set
        while (allocatedRoomIds.contains(newId)) {
            count++;
            newId = roomType + "-" + count;
        }
        return newId;
    }
}

/**
 * MAIN CLASS - UseCase6RoomAllocation
 * Demonstrates the FIFO processing and safe allocation.
 */
public class BookMyStayAPP {

    public static void main(String[] args) {
        System.out.println("Room Allocation Processing");

        // Setup Inventory
        RoomInventory hotelInventory = new RoomInventory();
        hotelInventory.addInventory("Single", 5);
        hotelInventory.addInventory("Suite", 2);

        // Setup Services
        RoomAllocationService allocationService = new RoomAllocationService();
        Queue<Reservation> bookingQueue = new LinkedList<>();

        // Simulating dequing requests from Use Case 5 (FIFO Order)
        bookingQueue.add(new Reservation("Abhi", "Single"));
        bookingQueue.add(new Reservation("Subha", "Single"));
        bookingQueue.add(new Reservation("Vanmathi", "Suite"));

        // Process the queue
        while (!bookingQueue.isEmpty()) {
            Reservation currentRequest = bookingQueue.poll();
            allocationService.allocateRoom(currentRequest, hotelInventory);
        }
    }
}