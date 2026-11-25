import java.io.*;
import java.util.*;

public class EventRegistrationSystem {
    static final int MAX_SEATS = 50;
    static final String FILE_NAME = "events.txt";
    static int idCounter = 101;

    static HashMap<String, ArrayList<String>> confirmedMap = new HashMap<>();
    static HashMap<String, Queue<String>> waitingMap = new HashMap<>();

    public static void main(String[] args) {
        String[] events = {"Tech Fest", "Music Night", "Sports Day"};
        for (String event : events) {
            confirmedMap.put(event, new ArrayList<>());
            waitingMap.put(event, new LinkedList<>());
        }

        loadFromFile();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- SIMPLE EVENT REGISTRATION SYSTEM ---");
            System.out.println("1. Register Participant");
            System.out.println("2. Cancel Registration");
            System.out.println("3. Display Confirmed Registrations");
            System.out.println("4. Show Waiting List");
            System.out.println("5. Exit & Save");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    registerParticipant(sc);
                    break;
                case 2:
                    cancelRegistration(sc);
                    break;
                case 3:
                    displayConfirmed();
                    break;
                case 4:
                    showWaitingList();
                    break;
                case 5:
                    saveToFile();
                    System.out.println("Records saved to " + FILE_NAME + ". Exiting...");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice! Please select 1 to 5.");
            }
        }
    }

    static void registerParticipant(Scanner sc) {
        System.out.print("Enter Participant Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Event (Tech Fest / Music Night / Sports Day): ");
        String event = sc.nextLine();

        if (!confirmedMap.containsKey(event)) {
            System.out.println("Invalid Event! Available events: Tech Fest, Music Night, Sports Day");
            return;
        }

        String regId = "REG" + idCounter++;
        String record = regId + " - " + name;

        ArrayList<String> confirmedList = confirmedMap.get(event);
        Queue<String> waitingQueue = waitingMap.get(event);

        if (confirmedList.size() < MAX_SEATS) {
            confirmedList.add(record);
            System.out.println("Registration Confirmed! ID: " + regId);
        } else {
            waitingQueue.add(record);
            System.out.println("Event Full (50/50)! Added to Waiting List. ID: " + regId);
        }
    }

    static void cancelRegistration(Scanner sc) {
        System.out.print("Enter Registration ID to cancel (e.g., REG101): ");
        String cancelId = sc.nextLine().toUpperCase();

        for (String event : confirmedMap.keySet()) {
            ArrayList<String> confirmedList = confirmedMap.get(event);
            Queue<String> waitingQueue = waitingMap.get(event);

            for (int i = 0; i < confirmedList.size(); i++) {
                if (confirmedList.get(i).startsWith(cancelId + " ")) {
                    String removed = confirmedList.remove(i);
                    System.out.println("Cancelled: " + removed + " from " + event);

                    if (!waitingQueue.isEmpty()) {
                        String promoted = waitingQueue.poll();
                        confirmedList.add(promoted);
                        System.out.println("Auto-Promoted from Waiting List: " + promoted);
                    }
                    return;
                }
            }
        }
        System.out.println("Registration ID not found in Confirmed list!");
    }

    static void displayConfirmed() {
        System.out.println("\n--- CONFIRMED REGISTRATIONS ---");
        for (String event : confirmedMap.keySet()) {
            System.out.println(event + " (" + confirmedMap.get(event).size() + "/" + MAX_SEATS + "):");
            for (String record : confirmedMap.get(event)) {
                System.out.println("  -> " + record + " [Confirmed]");
            }
        }
    }

    static void showWaitingList() {
        System.out.println("\n--- WAITING LIST (FIFO QUEUE) ---");
        for (String event : waitingMap.keySet()) {
            System.out.println(event + " Waiting (" + waitingMap.get(event).size() + "):");
            for (String record : waitingMap.get(event)) {
                System.out.println("  -> " + record + " [Waiting]");
            }
        }
    }

    static void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            writer.println(idCounter);
            for (String event : confirmedMap.keySet()) {
                for (String rec : confirmedMap.get(event)) {
                    writer.println("CONFIRMED|" + event + "|" + rec);
                }
                for (String rec : waitingMap.get(event)) {
                    writer.println("WAITING|" + event + "|" + rec);
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String firstLine = reader.readLine();
            if (firstLine != null) idCounter = Integer.parseInt(firstLine);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String status = parts[0];
                    String event = parts[1];
                    String record = parts[2];

                    if (status.equals("CONFIRMED") && confirmedMap.containsKey(event)) {
                        confirmedMap.get(event).add(record);
                    } else if (status.equals("WAITING") && waitingMap.containsKey(event)) {
                        waitingMap.get(event).add(record);
                    }
                }
            }
            System.out.println("Previous records loaded from " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }
}