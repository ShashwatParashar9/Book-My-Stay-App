import java.util.*;
import java.io.*;

/**
 * --- Book My Stay App: Use Case 12 ---
 * Filename: BookMyStayApp.java
 * This version integrates File Persistence and System Recovery.
 */

// --- Supporting Class: RoomInventory ---
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addInventory(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public Map<String, Integer> getAllInventory() {
        return inventory;
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        if (inventory.isEmpty()) {
            System.out.println("Inventory is empty.");
        } else {
            inventory.forEach((type, count) -> System.out.println(type + ": " + count));
        }
    }
}

/**
 * CLASS - FilePersistenceService
 * Handles durable storage of system state using plain text files.
 */
class FilePersistenceService {

    /**
     * Saves room inventory state to a file.
     * Format: roomType=availableCount
     */
    public void saveInventory(RoomInventory inventory, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            Map<String, Integer> data = inventory.getAllInventory();
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                writer.println(entry.getKey() + "=" + entry.getValue());
            }
            System.out.println("Inventory saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving inventory: " + e.getMessage());
        }
    }

    /**
     * Loads room inventory state from a file.
     * Reconstructs the in-memory Map from stored strings.
     */
    public void loadInventory(RoomInventory inventory, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("No valid inventory data found. Starting fresh.");
            return;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.contains("=")) {
                    String[] parts = line.split("=");
                    String type = parts[0];
                    int count = Integer.parseInt(parts[1]);
                    inventory.addInventory(type, count);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Persistence file not found.");
        } catch (Exception e) {
            System.out.println("Error loading persistence data: " + e.getMessage());
        }
    }
}

/**
 * MAIN CLASS - BookMyStayApp (UseCase12)
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("System Recovery");

        // Initialize components
        RoomInventory inventory = new RoomInventory();
        FilePersistenceService persistenceService = new FilePersistenceService();
        String storageFile = "inventory_state.txt";

        // 1. Attempt System Recovery (Startup)
        persistenceService.loadInventory(inventory, storageFile);

        // 2. If recovery yields no data, initialize default state
        if (inventory.getAllInventory().isEmpty()) {
            inventory.addInventory("Single", 5);
            inventory.addInventory("Double", 3);
            inventory.addInventory("Suite", 2);
        }

        // 3. Display Current State
        inventory.displayInventory();

        // 4. Simulate Shutdown / Save Operation
        // In a real app, this would be called during a controlled shutdown
        persistenceService.saveInventory(inventory, storageFile);
    }
}