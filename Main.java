import java.util.ArrayList;
import java.util.Scanner;



public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TrelloBoard board = new TrelloBoard("School Project");

        while (true) {
            // Toon het menu
            System.out.println("\nWat wil je doen?");
            System.out.println("1. Taak aanmaken");
            System.out.println("2. Taak verplaatsen");
            System.out.println("3. Taak verwijderen");
            System.out.println("4. Taken tonen");
            System.out.println("5. Beschrijving van een taak opvragen");
            System.out.println("6. Afsluiten");
            System.out.print("Kies een optie: ");
            

            int keuze = scanner.nextInt();
            scanner.nextLine(); // Consumeer de newline

            switch (keuze) {
                case 1 -> {
                    // Taak aanmaken
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
                case 2 -> {
                    // Taak verplaatsen
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
                case 3 -> {
                    // Taak verwijderen
                    System.out.print("Voer het ID van de taak in die je wilt verwijderen: ");
                    int taakId = scanner.nextInt();
                    scanner.nextLine(); // Consumeer de newline
                    board.removeTask(taakId);
                    System.out.println("Taak verwijderd!");
                }
                case 4 -> {
                    // Taken tonen
                    board.showTasks();
                }
                
                case 5 -> {
                    // Beschrijving van een taak opvragen
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
                case 6 -> {
                    // Afsluiten
                    System.out.println("Programma afgesloten.");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Ongeldige keuze, probeer opnieuw.");
            
            }
        }
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