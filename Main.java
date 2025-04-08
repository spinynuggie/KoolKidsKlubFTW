import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    private static ArrayList<User> users = new ArrayList<>();
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TrelloBoard board = new TrelloBoard("School Project");

        String ingelochtals = "";
        boolean kleinmenu = true;
        boolean kleinmenu2 = true;
         

        while (true) {
            kleinmenu = true;
            
            // Toon het menu
            System.out.println("\nWat wil je doen?");
            System.out.println("1. Taak aanmaken");
            System.out.println("2. Taak verplaatsen");
            System.out.println("3. Taak verwijderen");
            System.out.println("4. Taken tonen");
            System.out.println("5. Beschrijving van een taak opvragen");
            System.out.println("6. user menu");
            System.out.println("7. Afsluiten");
            System.out.print("Kies een optie: ");
            

            int keuze = scanner.nextInt();
            scanner.nextLine(); // Consumeer de newline

            switch (keuze) {
                case 1 -> {// Taak aanmaken
                    
                    System.out.print("Voer de naam van de taak in: ");
                    String naam = scanner.nextLine();
                    System.out.print("Voer een beschrijving in: ");
                    String beschrijving = scanner.nextLine();
                    System.out.println("Kies een status: 1. TODO, 2. IN_PROGRESS, 3. REAVIEUW, 4. DONE");
                    int statusKeuze = scanner.nextInt();
                    scanner.nextLine(); // Consumeer de newline
                    TaskStatus status = switch (statusKeuze) {
                        case 1 -> TaskStatus.TODO;
                        case 2 -> TaskStatus.IN_PROGRESS;
                        case 3 -> TaskStatus.REAVIEUW;
                        case 4 -> TaskStatus.DONE;
                        default -> {
                            System.out.println("Ongeldige keuze, standaard naar TODO.");
                            yield TaskStatus.TODO;
                        }
                    };
                    board.addTask(new Task(naam, beschrijving, status));
                    System.out.println("Taak toegevoegd!");
                }
                case 2 -> {// Taak verplaatsen
                    System.out.print("Voer het ID van de taak in die je wilt verplaatsen: ");
                    int taakId = scanner.nextInt();
                    
                    scanner.nextLine(); // Consumeer de newline
                    System.out.println("Kies een nieuwe status: 1. TODO, 2. IN_PROGRESS, 3. REAVIEUW, 4. DONE");
                    int nieuweStatusKeuze = scanner.nextInt();
                    scanner.nextLine(); // Consumeer de newline
                    TaskStatus nieuweStatus = switch (nieuweStatusKeuze) {
                        case 1 -> TaskStatus.TODO;
                        case 2 -> TaskStatus.IN_PROGRESS;
                        case 3 -> TaskStatus.REAVIEUW;
                        case 4 -> TaskStatus.DONE;
                        default -> {
                            System.out.println("Ongeldige keuze, standaard naar TODO.");
                            yield TaskStatus.TODO;
                        }
                    };
                    board.updateTaskStatus(taakId, nieuweStatus);
                    System.out.println("Taakstatus bijgewerkt!");
                }
                case 3 -> {// Taak verwijderen
                    System.out.print("Voer het ID van de taak in die je wilt verwijderen: ");
                    int taakId = scanner.nextInt();
                    scanner.nextLine(); // Consumeer de newline
                    board.removeTask(taakId);
                    System.out.println("Taak verwijderd!");
                }
                case 4 -> {// Taken tonen
                    board.showTasks();
                }
                
                case 5 -> {// Beschrijving van een taak opvragen
                    System.out.print("Voer het ID van de taak in: ");
                    int taakId = scanner.nextInt();
                    scanner.nextLine(); // Consumeer de newline
                    Task taak = board.getTaskById(taakId);
                    if (taak != null) {
                        System.out.println("Beschrijving van taak " + taakId + ": " + taak.getDescription());
                    } else {
                        System.out.println("Taak met ID " + taakId + " bestaat niet.");
                    }
                }
                case 6 -> {// Toon het menu

                    while(kleinmenu){
                        kleinmenu2 = true;
                        System.out.println("\nWat wil je doen?");
                        System.out.println("1. user aanmaken");
                        System.out.println("2. user daliten");
                        System.out.println("3. inloggen als user");
                        System.out.println("4. berichtjes bekijken");
                        System.out.println("5. berichtje versturen");
                        System.out.println("6. terug naar hoofdmenu");
                        System.out.print("Kies een optie: ");


                        int keuze2 = scanner.nextInt();
                        scanner.nextLine(); // Consumeer de newline
            
                        switch (keuze2) {
                            case 1 -> {// user aan maken
                                System.out.print("Voer de naam van de user in: ");
                                String naam = scanner.nextLine();
                                System.out.print("Voer een email in: ");
                                String email = scanner.nextLine();
                                System.out.print("Voer een wachtwoord in: ");
                                String wachtwoord = scanner.nextLine();
                                users.add(new User(naam, email, wachtwoord));
                                ingelochtals = naam;
                                
                            }
                            case 2 -> {// user daliten
                                System.out.print("Voer de naam van de user in die je wilt verwijderen: ");
                                String naam = scanner.nextLine();
                                boolean gevonden = false;
                                for (int i = 0; i < users.size(); i++) {
                                    if (users.get(i).getName().equals(naam)) {
                                        users.remove(i);
                                        gevonden = true;
                                        break;
                                    }
                                }
                                if (gevonden) {
                                    System.out.println("user verwijderd!");
                                } else {
                                    System.out.println("user niet gevonden!");
                                }

                            }
                            case 3 -> {// inloggen als user
                                System.out.print("Voer je naam in: ");
                                String naam = scanner.nextLine();
                                System.out.print("Voer je wachtwoord in: ");
                                String wachtwoord = scanner.nextLine();
                                boolean gevonden = false;
                                for (User user : users) {
                                    if (user.getName().equals(naam) && user.getPassword().equals(wachtwoord)) {
                                        System.out.println("Inloggen gelukt! Welkom " + user.getName() + "!");
                                        ingelochtals = user.getName();
                                        gevonden = true;
                                        break;
                                    }
                                }
                                if (!gevonden) {
                                    System.out.println("Ongeldige inloggegevens!");
                                }
                            }
                            case 4 -> {// Berichtjes bekijken
                                System.out.print("Voer je naam in: ");
                                System.out.println(ingelochtals);
                                boolean gevonden = false;
                                for (User user : users) {
                                    if(user.getName().equals(ingelochtals)){
                                        int maxBerichtjes = 10; // Maximum aantal berichtjes om te tonen
                                        int teller = 0;
                                        
                                        for (String berichtje : user.getBerichtjes()) {
                                            System.out.println(berichtje);
                                            teller++;
                                            if (teller >= maxBerichtjes) {
                                                break; // Stop de loop als het maximum is bereikt
                                            }
                                        }

                                        gevonden = true;
                                        break; // Stop de loop zodra de juiste gebruiker is gevonden
                                    }
                                }
                                if (!gevonden) {
                                    System.out.println("user niet gevonden!");
                                }
                            }
                            case 5 -> { // Berichtje versturen
                                ArrayList<User> ontvangers = new ArrayList<>(); // Lijst van ontvangers
                            
                                while (kleinmenu2) {
                                    System.out.println("\nWat wil je doen?");
                                    System.out.println("1. Voeg een naam toe aan de ontvangerslijst");
                                    System.out.println("2. Typ een bericht en verstuur naar alle ontvangers");
                                    System.out.println("3. Verwijder een naam uit de ontvangerslijst");
                                    System.out.println("4. Stoppen");
                                    System.out.print("Kies een optie: ");
                                    int keuze3 = scanner.nextInt();
                                    scanner.nextLine(); // Consumeer de newline
                            
                                    switch (keuze3) {
                                        case 1 -> { // Voeg een naam toe
                                            System.out.print("Voer de naam van de ontvanger in: ");
                                            String naam = scanner.nextLine();
                                            boolean gevonden = false;
                            
                                            for (User user : users) {
                                                if (user.getName().equals(naam)) {
                                                    ontvangers.add(user);
                                                    System.out.println("Gebruiker " + naam + " toegevoegd aan de ontvangerslijst.");
                                                    gevonden = true;
                                                    break;
                                                }
                                            }
                            
                                            if (!gevonden) {
                                                System.out.println("Gebruiker " + naam + " niet gevonden!");
                                            }
                                        }
                                        case 2 -> { // Typ een bericht en verstuur
                                            if (ontvangers.isEmpty()) {
                                                System.out.println("De ontvangerslijst is leeg. Voeg eerst ontvangers toe.");
                                            } else {
                                                System.out.print("Voer het bericht in: ");
                                                String berichtje = scanner.nextLine();
                            
                                                for (User ontvanger : ontvangers) {
                                                    ontvanger.ontvangbericht("Bericht van " + ingelochtals + ": " + berichtje);
                                                }
                            
                                                System.out.println("Bericht verstuurd naar alle ontvangers!");
                                            }
                                        }
                                        case 3 -> { // Verwijder een naam uit de ontvangerslijst
                                            System.out.print("Voer de naam in die je wilt verwijderen uit de ontvangerslijst: ");
                                            String naam = scanner.nextLine();
                                            boolean verwijderd = false;
                            
                                            for (int i = 0; i < ontvangers.size(); i++) {
                                                if (ontvangers.get(i).getName().equals(naam)) {
                                                    ontvangers.remove(i);
                                                    System.out.println("Gebruiker " + naam + " verwijderd uit de ontvangerslijst.");
                                                    verwijderd = true;
                                                    break;
                                                }
                                            }
                            
                                            if (!verwijderd) {
                                                System.out.println("Gebruiker " + naam + " staat niet in de ontvangerslijst.");
                                            }
                                        }
                                        case 4 -> { // Stoppen
                                            System.out.println("Terug naar het vorige menu.");
                                            kleinmenu2 = false;
                                        }
                                        default -> System.out.println("Ongeldige keuze, probeer opnieuw.");
                                    }
                            
                                    // Toon de huidige ontvangerslijst
                                    System.out.println("\nHuidige ontvangerslijst:");
                                    if (ontvangers.isEmpty()) {
                                        System.out.println("Geen ontvangers toegevoegd.");
                                    } else {
                                        for (User ontvanger : ontvangers) {
                                            System.out.println("- " + ontvanger.getName());
                                        }
                                    }
                                }
                            }
                            case 6 -> {// Terug naar hoofdmenu
                                System.out.println("Terug naar hoofdmenu.");
                                kleinmenu = false;
                            }
                            default -> System.out.println("Ongeldige keuze, probeer opnieuw.");
                        }
                    }
                }
                case 7 -> {// Afsluiten
                    
                    System.out.println("Programma afgesloten.");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Ongeldige keuze, probeer opnieuw.");
            }
        }
    }
}

class User {
    private String name;
    private String email;
    private String password;
    private ArrayList<String> berichtjes = new ArrayList<>();

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String[] getBerichtjes() {
        for (int i = 0; i < berichtjes.size(); i++) {
            System.out.println(i + ": " + berichtjes.get(i));
        }
        return berichtjes.toArray(new String[0]);
        
    }

    public void ontvangbericht(String berichtje) {
        berichtjes.add(berichtje);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

class TrelloBoard {
    private String name;
    private ArrayList<Task> taskList;

    public TrelloBoard(String name) {
        this.name = name;
        this.taskList = new ArrayList<>();
    }

    public void addTask(Task task) {
        taskList.add(task);
    }

    public void updateTaskStatus(int taskIndex, TaskStatus newStatus) {
        if (taskIndex >= 0 && taskIndex < taskList.size()) {
            taskList.get(taskIndex).setStatus(newStatus);
        } else {
            System.out.println("Ongeldig taaknummer.");
        }
    }

    public void showTasks() {
        System.out.println("Trello-bord: " + name);
        System.out.println();
    
        // Maak een lijst voor elke status
        ArrayList<Task> todoTasks = new ArrayList<>();
        ArrayList<Task> inProgressTasks = new ArrayList<>();
        ArrayList<Task> reviewTasks = new ArrayList<>();
        ArrayList<Task> doneTasks = new ArrayList<>();
    
        // Verdeel de taken op basis van hun status
        for (Task task : taskList) {
            switch (task.getStatus()) {
                case TODO -> todoTasks.add(task);
                case IN_PROGRESS -> inProgressTasks.add(task);
                case REAVIEUW -> reviewTasks.add(task);
                case DONE -> doneTasks.add(task);
            }
        }
    
        // Print de kolomkoppen
        System.out.printf("%-20s %-20s %-20s %-20s\n", "TODO", "IN_PROGRESS", "REAVIEUW", "DONE");
        System.out.println("-------------------------------------------------------------------------------");
    
        // Bepaal het maximale aantal rijen
        int maxRows = Math.max(
            Math.max(todoTasks.size(), inProgressTasks.size()),
            Math.max(reviewTasks.size(), doneTasks.size())
        );
    
        // Print de taken rij voor rij
        for (int i = 0; i < maxRows; i++) {
            String todo = i < todoTasks.size() ? todoTasks.get(i).getName() : "";
            String inProgress = i < inProgressTasks.size() ? inProgressTasks.get(i).getName() : "";
            String review = i < reviewTasks.size() ? reviewTasks.get(i).getName() : "";
            String done = i < doneTasks.size() ? doneTasks.get(i).getName() : "";

            String todoid = i < todoTasks.size() ? "id:" + taskList.indexOf(todoTasks.get(i)) : "";
            String inProgressid = i < inProgressTasks.size() ? "id:" + taskList.indexOf(inProgressTasks.get(i)) : "";
            String reviewid = i < reviewTasks.size() ? "id:" + taskList.indexOf(reviewTasks.get(i)) : "";
            String doneid = i < doneTasks.size() ? "id:" + taskList.indexOf(doneTasks.get(i)) : "";

            System.out.printf("%-20s %-20s %-20s %-20s\n", 
                todo.isEmpty() ? "" : todo + " " + todoid,
                inProgress.isEmpty() ? "" : inProgress + " " + inProgressid,
                review.isEmpty() ? "" : review + " " + reviewid,
                done.isEmpty() ? "" : done + " " + doneid
            );
        }
        System.out.println("-------------------------------------------------------------------------------");
    }

    public void oldshowTasks() {
        System.out.println("Trello-bord: " + name);
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            System.out.println(i + ": " + task);
        }
    }

    public void removeTask(int taskIndex) {
        if (taskIndex >= 0 && taskIndex < taskList.size()) {
            taskList.remove(taskIndex);
        } else {
            System.out.println("Ongeldig taaknummer.");
        }
    }


    public Task getTaskById(int taskIndex) {
        if (taskIndex >= 0 && taskIndex < taskList.size()) {
            return taskList.get(taskIndex);
        } else {
            System.out.println("Ongeldig taaknummer.");
            return null;
        }
    }
   
}

class Task {
    private String name;
    private String description;
    private TaskStatus status;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Taak: " + name + ", Beschrijving: " + description + ", Status: " + status;
    }
}

enum TaskStatus {
    TODO,
    IN_PROGRESS,
    REAVIEUW,
    DONE
}
//dit is totaal niet door chat gpt gemaakt enz ik weet niet waar je het over hebt dus ik ga het ook niet zeggen
//dit is totaal niet door chat gpt gemaakt enz ik weet niet waar je het over hebt dus ik ga het ook niet zeggen
//dit is totaal niet door chat gpt gemaakt enz ik weet niet waar je het over hebt dus ik ga het ook niet zeggen
//dit is totaal niet door chat gpt gemaakt enz ik weet niet waar je het over hebt dus ik ga het ook niet zeggen