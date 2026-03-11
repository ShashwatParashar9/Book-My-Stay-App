import java.util.*;

/**
 * --- Book My Stay App: Use Case 7 ---
 * This file implements the Add-On Service Selection logic.
 */

// 1. CLASS - Service
// Represents an individual optional offering like Breakfast or Spa.
class Service {
    private String serviceName;
    private double cost;

    public Service(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }
}

// 2. CLASS - AddOnServiceManager
// Manages the association between Reservation IDs and their selected services.
class AddOnServiceManager {
    /**
     * Maps reservation ID to selected services.
     * Key -> Reservation ID (e.g., "Single-1")
     * Value -> List of selected services
     */
    private Map<String, List<Service>> servicesByReservation;

    public AddOnServiceManager() {
        this.servicesByReservation = new HashMap<>();
    }

    /**
     * Attaches a service to a reservation.
     * Uses computeIfAbsent to initialize the list if it doesn't exist.
     */
    public void addService(String reservationId, Service service) {
        servicesByReservation
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    /**
     * Calculates total add-on cost for a reservation.
     * Iterates through the list of services mapped to the ID.
     */
    public double calculateTotalServiceCost(String reservationId) {
        List<Service> services = servicesByReservation.get(reservationId);
        if (services == null || services.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Service s : services) {
            total += s.getCost();
        }
        return total;
    }
}

/**
 * MAIN CLASS - UseCase7AddOnServiceSelection
 * Demonstrates attaching services to a confirmed booking.
 */
public class BookMyStayAPP {

    public static void main(String[] args) {
        System.out.println("Add-On Service Selection");

        // 1. Initialize the Manager
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // 2. Define a Reservation ID (obtained from Use Case 6)
        String reservationId = "Single-1";

        // 3. Create Add-On Services
        Service breakfast = new Service("Breakfast", 500.0);
        Service spa = new Service("Spa", 1000.0);

        // 4. Attach services to the reservation
        serviceManager.addService(reservationId, breakfast);
        serviceManager.addService(reservationId, spa);

        // 5. Output results
        double totalCost = serviceManager.calculateTotalServiceCost(reservationId);

        System.out.println("Reservation ID: " + reservationId);
        System.out.println("Total Add-On Cost: " + totalCost);
    }
}