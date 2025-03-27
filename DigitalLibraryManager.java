

import java.util.*;


class LiteraryItem {
    private final String itemCode;
    private String title;
    private String creator;
    private String type;
    private String availability;

   //   Item
    public LiteraryItem(String code, String title, String creator, String type, String availability) {
        this.itemCode = Objects.requireNonNull(code, "Item code cannot be null");
        this.title = validateString(title, "Title");
        this.creator = validateString(creator, "Creator");
        this.type = type != null ? type : "Uncategorized";
        setAvailability(availability);
    }

    private String validateString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return value.trim();
    }

    private void setAvailability(String status) {
        if (!"Accessible".equals(status) && !"On Loan".equals(status)) {
            throw new IllegalArgumentException("Invalid availability status");
        }
        this.availability = status;
    }

   //update
    public void updateInformation(String newTitle, String newCreator, String newType, String newStatus) {
        if (newTitle != null && !newTitle.trim().isEmpty()) {
            this.title = newTitle.trim();
        }
        if (newCreator != null && !newCreator.trim().isEmpty()) {
            this.creator = newCreator.trim();
        }
        if (newType != null) {
            this.type = newType.trim();
        }
        if (newStatus != null && ("Accessible".equals(newStatus) || "On Loan".equals(newStatus))) {
            this.availability = newStatus;
        }
    }

    //display
    @Override
    public String toString() {
        return String.format("[%s] '%s' by %s | Category: %s | %s",
                itemCode, title, creator, type, availability);
    }

    // Different getter names
    public String getCode() { return itemCode; }
    public String getTitle() { return title; }
    public String getCreator() { return creator; }
    public String getType() { return type; }
    public String getAvailability() { return availability; }
}

//library manager
public class DigitalLibraryManager {
    private final Map<String, LiteraryItem> inventory = new TreeMap<>();
    private final Scanner userInput = new Scanner(System.in);

    public static void main(String[] args) {
        DigitalLibraryManager manager = new DigitalLibraryManager();
        manager.runSystem();
    }

    private void runSystem() {
        boolean isRunning = true;
        
        while (isRunning) {
            displayMainInterface();
            
            try {
                int selection = userInput.nextInt();
                userInput.nextLine();
                
                switch (selection) {
                    case 1 -> addNewItem();
                    case 2 -> showAllItems();
                    case 3 -> locateItem();
                    case 4 -> alterItem();
                    case 5 -> deleteItem();
                    case 6 -> isRunning = false;
                    default -> System.out.println("Invalid option selected");
                }
            } catch (InputMismatchException e) {
                userInput.nextLine();
                System.out.println("Please enter a valid number");
            }
        }
        System.out.println("System terminated");
    }

    private void displayMainInterface() {
        System.out.println("\nDigital Library Management");
        System.out.println("1. Add New Literary Item");
        System.out.println("2. Display Complete Inventory");
        System.out.println("3. Search for Item");
        System.out.println("4. Update Item Details");
        System.out.println("5. Remove Item from System");
        System.out.println("6. Exit Application");
        System.out.print("Enter your choice: ");
    }

    private void addNewItem() {
        System.out.println("\nAdding New Literary Item");
        String code = getValidInput("Enter unique identifier: ", false);
        
        if (inventory.containsKey(code)) {
            System.out.println("Error: Identifier already exists");
            return;
        }

        String title = getValidInput("Enter item title: ", false);
        String creator = getValidInput("Enter creator name: ", false);
        String type = getValidInput("Enter category (optional): ", true);
        String status = getValidStatus();

        try {
            inventory.put(code, new LiteraryItem(code, title, creator, type, status));
            System.out.println("Item successfully cataloged");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String getValidInput(String prompt, boolean optional) {
        System.out.print(prompt);
        String input = userInput.nextLine().trim();
        if (!optional && input.isEmpty()) {
            throw new IllegalArgumentException("Required field cannot be empty");
        }
        return input;
    }

    private String getValidStatus() {
        while (true) {
            System.out.print("Availability (Accessible/On Loan): ");
            String status = userInput.nextLine().trim();
            if ("Accessible".equalsIgnoreCase(status)) return "Accessible";
            if ("On Loan".equalsIgnoreCase(status)) return "On Loan";
            System.out.println("Invalid status - please enter 'Accessible' or 'On Loan'");
        }
    }

    private void showAllItems() {
        if (inventory.isEmpty()) {
            System.out.println("No items currently in inventory");
            return;
        }
        System.out.println("\nCurrent Library Inventory:");
        inventory.values().forEach(System.out::println);
    }

    private void locateItem() {
        System.out.print("\nEnter search term (identifier or title): ");
        String query = userInput.nextLine().trim().toLowerCase();
        
        boolean found = false;
        for (LiteraryItem item : inventory.values()) {
            if (item.getCode().toLowerCase().contains(query) || 
                item.getTitle().toLowerCase().contains(query)) {
                System.out.println(item);
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("No matching items found");
        }
    }

    private void alterItem() {
        String code = getValidInput("\nEnter identifier of item to modify: ", false);
        LiteraryItem item = inventory.get(code);
        
        if (item == null) {
            System.out.println("Item not found in system");
            return;
        }

        System.out.println("Current details: " + item);
        System.out.println("Enter new values (leave blank to keep current):");

        String newTitle = getValidInput("New title: ", true);
        String newCreator = getValidInput("New creator: ", true);
        String newType = getValidInput("New category: ", true);
        String newStatus = getOptionalStatus();

        item.updateInformation(newTitle, newCreator, newType, newStatus);
        System.out.println("Item details successfully updated");
    }

    private String getOptionalStatus() {
        System.out.print("New status (Accessible/On Loan) or blank: ");
        String status = userInput.nextLine().trim();
        if (status.isEmpty()) return null;
        if ("Accessible".equalsIgnoreCase(status)) return "Accessible";
        if ("On Loan".equalsIgnoreCase(status)) return "On Loan";
        System.out.println("Invalid status - keeping current value");
        return null;
    }

    private void deleteItem() {
        String code = getValidInput("\nEnter identifier of item to remove: ", false);
        if (inventory.remove(code) != null) {
            System.out.println("Item successfully removed");
        } else {
            System.out.println("No item found with that identifier");
        }
    }
}
