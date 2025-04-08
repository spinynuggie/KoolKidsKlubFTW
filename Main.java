import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Maak een Trello-bord aan
        TrelloBoard board = new TrelloBoard("School Project");

        // Voeg taken toe
        board.addTask(new Task("Taak 1", "Maak de presentatie af", TaskStatus.TODO));
        board.addTask(new Task("Taak 2", "Schrijf de documentatie", TaskStatus.IN_PROGRESS));

        // Toon alle taken
        board.showTasks();

        // Verander de status van een taak
        board.updateTaskStatus(0, TaskStatus.DONE);

        // Toon de taken opnieuw
        board.showTasks();
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
    
            System.out.printf("%-20s %-20s %-20s %-20s\n", todo, inProgress, review, done);
        }
    }

    public void oldshowTasks() {
        System.out.println("Trello-bord: " + name);
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            System.out.println(i + ": " + task);
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