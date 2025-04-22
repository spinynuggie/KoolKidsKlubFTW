import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;

public class Main {
    private static ArrayList<User> users = new ArrayList<>();
    private static String currentUser = "gast";

    private static List<UserStory> userStories = new ArrayList<>();
    private static List<Epic> epics = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TrelloBoard board = new TrelloBoard("School Project");
        board.loadTasksFromDatabase();
        users = User.loadUsersFromDatabase();
        userStories = UserStory.loadFromDatabase();
        epics = Epic.loadFromDatabase(userStories, board);

        mainLoop:
        while (true) {
            System.out.println("\n=== Ingelogd als: " + currentUser + " ===");
            System.out.println("1. Taak aanmaken");
            System.out.println("2. Taak verplaatsen");
            System.out.println("3. Taak verwijderen");
            System.out.println("4. Taken tonen");
            System.out.println("5. Beschrijving opvragen");
            System.out.println("6. User menu");
            System.out.println("7. Epics/user stories");
            System.out.println("8. Afsluiten");
            System.out.print("Kies een optie: ");

            String keuze = scanner.nextLine().trim();
            switch (keuze) {
                case "1": {
                    // Taak aanmaken
                    String naam = readInput(scanner, "Voer de naam van de taak in");
                    if (naam == null) break;
                    String beschrijving = readInput(scanner, "Voer een beschrijving in");
                    if (beschrijving == null) break;
                    Integer statusKeuze = readIntInput(scanner,
                            "Kies een status: 1. TODO, 2. IN_PROGRESS, 3. REVIEW, 4. DONE");
                    if (statusKeuze == null) break;

                    TaskStatus status = switch (statusKeuze) {
                        case 1 -> TaskStatus.TODO;
                        case 2 -> TaskStatus.IN_PROGRESS;
                        case 3 -> TaskStatus.REVIEW;
                        case 4 -> TaskStatus.DONE;
                        default -> {
                            System.out.println("Ongeldige keuze, standaard TODO.");
                            yield TaskStatus.TODO;
                        }
                    };

                    int newTaskId = Task.getNextTaskId();
                    Task nieuweTask = new Task(newTaskId, naam, beschrijving, status);
                    board.addTask(nieuweTask);
                    nieuweTask.saveToDatabase();
                    System.out.println("Taak toegevoegd en in DB opgeslagen!");
                    break;
                }
                case "2": {
                    // Taak verplaatsen
                    board.showTasks();
                    Integer taakId = readIntInput(scanner, "Voer het ID van de taak in die je wilt verplaatsen");
                    if (taakId == null) break;
                    Integer ns = readIntInput(scanner,
                            "Kies nieuwe status: 1. TODO, 2. IN_PROGRESS, 3. REVIEW, 4. DONE");
                    if (ns == null) break;

                    TaskStatus nieuweStatus = switch (ns) {
                        case 1 -> TaskStatus.TODO;
                        case 2 -> TaskStatus.IN_PROGRESS;
                        case 3 -> TaskStatus.REVIEW;
                        case 4 -> TaskStatus.DONE;
                        default -> {
                            System.out.println("Ongeldige keuze, standaard TODO.");
                            yield TaskStatus.TODO;
                        }
                    };

                    boolean updated = board.updateTaskStatus(taakId, nieuweStatus);
                    if (updated) {
                        System.out.println("Taakstatus bijgewerkt!");
                        board.showTasks();
                    } else {
                        System.out.println("Geen taak gevonden met ID " + taakId + ".");
                    }
                    break;
                }
                case "3": {
                    // Taak verwijderen
                    board.showTasks();
                    Integer taakId = readIntInput(scanner, "Voer het ID van de taak in die je wilt verwijderen");
                    if (taakId == null) break;
                    board.removeTask(taakId);
                    board.deleteTaskFromDatabase(taakId);
                    System.out.println("Taak verwijderd!");
                    break;
                }
                case "4":
                    board.showTasks();
                    break;
                case "5": {
                    // Beschrijving opvragen
                    Integer taakId = readIntInput(scanner, "Voer het ID van de taak in");
                    if (taakId == null) break;
                    Task t = board.getTaskById(taakId);
                    if (t != null) {
                        System.out.println("Beschrijving: " + t.getDescription());
                    } else {
                        System.out.println("Taak met ID " + taakId + " bestaat niet.");
                    }
                    break;
                }
                case "6": {
                    // User menu
                    boolean inUserMenu = true;
                    while (inUserMenu) {
                        System.out.println("\n=== User-menu (ingelogd als: " + currentUser + ") ===");
                        System.out.println("1. User aanmaken");
                        System.out.println("2. User verwijderen");
                        System.out.println("3. Inloggen als user");
                        System.out.println("4. Berichtjes bekijken");
                        System.out.println("5. Berichtje versturen");
                        System.out.print("Kies een optie (of typ 'exit' om terug te gaan naar het hoofdmenu): ");

                        String uchoice = scanner.nextLine().trim();
                        if ("exit".equalsIgnoreCase(uchoice)) {
                            System.out.println("Terug naar hoofdmenu.");
                            break;
                        }
                        switch (uchoice) {
                            case "1": {
                                // User aanmaken
                                String uNaam = readInput(scanner, "Naam");
                                if (uNaam == null) break;
                                String uEmail = readInput(scanner, "Email");
                                if (uEmail == null) break;
                                String uWacht = readInput(scanner, "Wachtwoord");
                                if (uWacht == null) break;
                                User newUser = new User(uNaam, uEmail, uWacht);
                                newUser.insertUserIntoDatabase();
                                users.add(newUser);
                                currentUser = uNaam;
                                System.out.println("User toegevoegd en ingelogd!");
                                break;
                            }
                            case "2": {
                                // User verwijderen
                                String delNaam = readInput(scanner, "Naam user te verwijderen");
                                if (delNaam == null) break;
                                boolean verwijderd = users.removeIf(u -> u.getName().equals(delNaam));
                                System.out.println(verwijderd
                                        ? "User \"" + delNaam + "\" verwijderd."
                                        : "User niet gevonden.");
                                break;
                            }
                            case "3": {
                                // Inloggen
                                String loginNaam = readInput(scanner, "Naam");
                                if (loginNaam == null) break;
                                String loginWacht = readInput(scanner, "Wachtwoord");
                                if (loginWacht == null) break;
                                boolean found = false;
                                for (User u : users) {
                                    if (u.getName().equals(loginNaam) && u.getPassword().equals(loginWacht)) {
                                        currentUser = loginNaam;
                                        System.out.println("Inloggen gelukt! Welkom " + loginNaam);
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) System.out.println("Ongeldige credentials.");
                                break;
                            }
                            case "4":
                                // Berichtjes bekijken
                                System.out.println("Laatste 10 berichtjes voor " + currentUser + ":");
                                for (User u : users) {
                                    if (u.getName().equals(currentUser)) {
                                        int teller = 0;
                                        for (int bid : u.getBerichtjesid()) {
                                            toonBerichtById(bid);
                                            if (++teller >= 10) break;
                                        }
                                        break;
                                    }
                                }
                                break;
                            case "5": {
                                // Berichtje versturen
                                List<User> ontvangers = new ArrayList<>();
                                boolean inRecipients = true;
                                while (inRecipients) {
                                    System.out.println("\n1. Ontvanger toevoegen  2. Verstuur  3. Verwijder ontvanger  4. Stop");
                                    System.out.print("Kies (of typ 'exit'): ");
                                    String rc = scanner.nextLine().trim();
                                    if ("exit".equalsIgnoreCase(rc)) {
                                        System.out.println("Bericht versturen geannuleerd.");
                                        break;
                                    }
                                    switch (rc) {
                                        case "1": {
                                            String onm = readInput(scanner, "Naam ontvanger");
                                            if (onm == null) break;
                                            users.stream()
                                                    .filter(u -> u.getName().equals(onm))
                                                    .findFirst()
                                                    .ifPresentOrElse(u -> {
                                                        ontvangers.add(u);
                                                        System.out.println(onm + " toegevoegd.");
                                                    }, () -> System.out.println("Niet gevonden."));
                                            break;
                                        }
                                        case "2": {
                                            if (ontvangers.isEmpty()) {
                                                System.out.println("Geen ontvangers.");
                                            } else {
                                                String msg = readInput(scanner, "Bericht");
                                                if (msg == null) break;
                                                System.out.print("Taak koppelen? (ja/nee): ");
                                                String ans = scanner.nextLine().trim();
                                                Integer koppeltaak = null;
                                                if (ans.equalsIgnoreCase("ja")) {
                                                    board.showTasks();
                                                    koppeltaak = readIntInput(scanner, "Voer taak-ID in");
                                                    if (koppeltaak == null) break;
                                                }
                                                int newMsgId = getNextBerichtId();
                                                berichtje nb = new berichtje(newMsgId, currentUser, msg, koppeltaak);
                                                nb.zetBerichtInDatabase();
                                                ontvangers.forEach(u -> u.ontvangberichtid(newMsgId));
                                                System.out.println("Bericht verstuurd!");
                                            }
                                            break;
                                        }
                                        case "3": {
                                            String rem = readInput(scanner, "Naam ontvanger weghalen");
                                            if (rem == null) break;
                                            boolean removed = ontvangers.removeIf(u -> u.getName().equals(rem));
                                            System.out.println(removed ? rem + " verwijderd." : rem + " niet in lijst.");
                                            break;
                                        }
                                        case "4":
                                            inRecipients = false;
                                            break;
                                        default:
                                            System.out.println("Onbekende keuze.");
                                    }
                                    if (!ontvangers.isEmpty() && inRecipients) {
                                        System.out.print("Ontvangers: ");
                                        ontvangers.forEach(u -> System.out.print(u.getName() + " "));
                                        System.out.println();
                                    }
                                }
                                break;
                            }
                            default:
                                System.out.println("Ongeldige keuze.");
                        }
                    }
                    break;
                }
                case "8":
                    System.out.println("Programma afgesloten.");
                    break mainLoop;
                case "7": {
                    while (true) {
                        System.out.println("\nWat wil je doen?");
                        System.out.println("1. User Story aanmaken");
                        System.out.println("2. Epic aanmaken (en user stories koppelen)");
                        System.out.println("3. Taak toevoegen aan een epic");
                        System.out.println("4. Epics tonen (met gelinkte user stories/taken)");
                        System.out.println("5. Toon alle User Stories");
                        System.out.println("6. Verwijder een User Story");
                        System.out.println("7. Verwijder een Epic");
                        System.out.println("0. Stoppen");
                        System.out.print("Maak een keuze: ");
                        int keuzesub = scanner.nextInt();
                        scanner.nextLine();  // Consume newline

                        if (keuzesub == 0) break;

                        switch (keuzesub) {
                            case 1:
                                userStoryAanmaken(scanner);
                                break;
                            case 2:
                                epicAanmaken(scanner);
                                break;
                            case 3:
                                taakToevoegen(scanner, board);
                                break;
                            case 4:
                                toonAlles();
                                break;
                            case 5:
                                System.out.println("\n-- Alle User Stories --");
                                if (userStories.isEmpty()) {
                                    System.out.println("Geen user stories gevonden.");
                                } else {
                                    for (UserStory us : userStories) {
                                        System.out.println(" - " + us);
                                    }
                                }
                                break;
                            case 6:
                                verwijderUserStory(scanner);
                                break;
                            case 7:
                                verwijderEpic(scanner);
                                break;
                            default:
                                System.out.println("Ongeldige keuze.");
                        }
                    }
                    break;
                }
                default:
                    System.out.println("Ongeldige keuze, probeer opnieuw.");
            }
        }

        scanner.close();
    }

    private static String readInput(Scanner scanner, String prompt) {
        System.out.print(prompt + " (of typ 'exit' om te annuleren): ");
        String input = scanner.nextLine().trim();
        return "exit".equalsIgnoreCase(input) ? null : input;
    }

    private static Integer readIntInput(Scanner scanner, String prompt) {
        String s = readInput(scanner, prompt);
        if (s == null) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Ongeldige invoer, probeer opnieuw.");
            return readIntInput(scanner, prompt);
        }
    }

    public static void toonBerichtById(int berichtId) {
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "SELECT afzender, berichtje, taskid, timestamp FROM berichtjes WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, berichtId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    String afz = rs.getString("afzender");
                    String bericht = rs.getString("berichtje");
                    Timestamp ts = rs.getTimestamp("timestamp");
                    int taskid = rs.getInt("taskid");
                    Task t = rs.wasNull() ? null : getTaskByIdFromDB(taskid);
                    System.out.print("Afzender: " + afz + " | " + bericht);
                    if (t != null) {
                        System.out.print(" | taak: " + t.getName() + " (status:" + t.getStatus() + ")");
                    }
                    System.out.println(" | op: " + ts);
                }
            }
        } catch (SQLException e) {
            System.out.println("Fout bij selectie bericht: " + e.getMessage());
        }
    }

    public static Task getTaskByIdFromDB(int taskId) {
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "SELECT id, naam, beschrijving, status FROM tasks WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, taskId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return new Task(
                            rs.getInt("id"),
                            rs.getString("naam"),
                            rs.getString("beschrijving"),
                            TaskStatus.valueOf(rs.getString("status"))
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Fout bij ophalen taak: " + e.getMessage());
        }
        return null;
    }

    public static int getNextBerichtId() {
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "SELECT MAX(id) AS maxId FROM berichtjes";
        try (Connection conn = DriverManager.getConnection(url);
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            int next = 1;
            if (rs.next()) next = rs.getInt("maxId") + 1;
            return next;
        } catch (SQLException e) {
            System.out.println("Fout bij bericht-id: " + e.getMessage());
            return -1;
        }
    }

    private static void userStoryAanmaken(Scanner scanner) {
        System.out.print("Naam van de user story: ");
        String naam = scanner.nextLine();
        System.out.print("Beschrijving: ");
        String beschrijving = scanner.nextLine();
        UserStory story = new UserStory(naam, beschrijving);
        story.saveToDatabase();
        userStories.add(story);
        System.out.println("User story toegevoegd en in DB opgeslagen!");
    }

    private static void epicAanmaken(Scanner scanner) {
        System.out.print("Titel van de epic: ");
        String titel = scanner.nextLine();
        Epic epic = new Epic(titel);
        epic.saveToDatabase();
        if (userStories.isEmpty()) {
            System.out.println("Geen user stories beschikbaar om te koppelen.");
        } else {
            System.out.println("Kies de nummers van de user stories om toe te voegen aan deze epic (gescheiden door komma's):");
            for (int i = 0; i < userStories.size(); i++) {
                UserStory us = userStories.get(i);
                System.out.println(i + ": " + us.getNaam() + " – " + us.getBeschrijving());
            }
            System.out.print("Voer nummers in: ");
            String input = scanner.nextLine();
            String[] indices = input.split(",");
            for (String indexStr : indices) {
                try {
                    int index = Integer.parseInt(indexStr.trim());
                    if (index >= 0 && index < userStories.size()) {
                        epic.voegUserStoryToe(userStories.get(index));
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        epics.add(epic);
        System.out.println("Epic toegevoegd!");
    }

    private static void verwijderUserStory(Scanner scanner) {
        if (userStories.isEmpty()) {
            System.out.println("Geen user stories om te verwijderen.");
            return;
        }
        System.out.println("\nKies de index van de te verwijderen User Story:");
        for (int i = 0; i < userStories.size(); i++) {
            UserStory us = userStories.get(i);
            // toon alleen naam en beschrijving
            System.out.println(i + ": " + us.getNaam() + " - " + us.getBeschrijving());
        }
        System.out.print("Voer het nummer in (of typ 'exit'): ");
        String in = scanner.nextLine().trim();
        if ("exit".equalsIgnoreCase(in)) return;

        try {
            int idx = Integer.parseInt(in);
            if (idx < 0 || idx >= userStories.size()) throw new NumberFormatException();
            int usId = userStories.get(idx).getId();

            // Verwijder uit DB
            String url = "jdbc:sqlite:teamflow.db";
            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement p = conn.prepareStatement(
                         "DELETE FROM userstories WHERE id = ?")) {
                p.setInt(1, usId);
                p.executeUpdate();
            }

            // Verwijder uit lijst
            userStories.remove(idx);
            System.out.println("User Story verwijderd!");
        } catch (NumberFormatException e) {
            System.out.println("Ongeldige index.");
        } catch (SQLException e) {
            System.out.println("Fout bij verwijderen uit DB: " + e.getMessage());
        }
    }


    private static void verwijderEpic(Scanner scanner) {
        if (epics.isEmpty()) {
            System.out.println("Geen epics om te verwijderen.");
            return;
        }
        System.out.println("\nKies de nummer van de te verwijderen Epic:");
        for (int i = 0; i < epics.size(); i++) {
            System.out.println(i + ": " + epics.get(i).getTitel());
        }
        System.out.print("Voer het nummer in (of typ 'exit'): ");
        String in = scanner.nextLine().trim();
        if ("exit".equalsIgnoreCase(in)) return;
        try {
            int idx = Integer.parseInt(in);
            if (idx < 0 || idx >= epics.size()) throw new NumberFormatException();
            int epicId = epics.get(idx).getId();
            // 1) Verwijder uit DB (cascade in epic_user_stories/epic_tasks)
            String url = "jdbc:sqlite:teamflow.db";
            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement p = conn.prepareStatement(
                         "DELETE FROM epics WHERE id = ?")) {
                p.setInt(1, epicId);
                p.executeUpdate();
            }
            // 2) Verwijder uit lijst
            epics.remove(idx);
            System.out.println("Epic verwijderd!");
        } catch (NumberFormatException e) {
            System.out.println("Ongeldige index.");
        } catch (SQLException e) {
            System.out.println("Fout bij verwijderen uit DB: " + e.getMessage());
        }
    }

    private static void taakToevoegen(Scanner scanner, TrelloBoard board) {
        if (epics.isEmpty()) {
            System.out.println("Geen epics beschikbaar.");
            return;
        }
        // 1) Laat de beschikbare epics zien
        System.out.println("Kies een epic om een taak aan toe te voegen:");
        for (int i = 0; i < epics.size(); i++) {
            System.out.println(i + ": " + epics.get(i).getTitel());
        }
        System.out.print("Voer het nummer in: ");
        int index = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        if (index >= 0 && index < epics.size()) {
            // 2) Print alle taken voordat je om de taak‑ID vraagt
            System.out.println("\nBeschikbare taken:");
            board.showTasks();

            // 3) Vraag de taak‑ID
            Integer taakId = readIntInput(scanner, "Voer het ID van de taak in");
            if (taakId == null) {
                System.out.println("Koppeling geannuleerd.");
                return;
            }

            Task t = board.getTaskById(taakId);
            if (t != null) {
                epics.get(index).voegTaakToe(t);
                System.out.println("Taak toegevoegd!");
            } else {
                System.out.println("Taak niet gevonden.");
            }
        } else {
            System.out.println("Ongeldige keuze.");
        }
    }
    private static void toonAlles() {
        if (epics.isEmpty()) {
            System.out.println("Nog geen epics aangemaakt.");
        } else {
            for (Epic epic : epics) {
                System.out.println("\n" + epic);
            }
        }
    }
}

enum TaskStatus {
    TODO, IN_PROGRESS, REVIEW, DONE
}

class Task {
    private int id;
    private String name, description;
    private TaskStatus status;

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id; this.name = name; this.description = description; this.status = status;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus s) { this.status = s; }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }

    public static int getNextTaskId() {
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "SELECT MAX(id) AS maxId FROM tasks";
        try (Connection conn = DriverManager.getConnection(url);
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            int next = 1;
            if (rs.next()) next = rs.getInt("maxId") + 1;
            return next;
        } catch (SQLException e) {
            System.out.println("Fout bij task-id: " + e.getMessage());
            return -1;
        }
    }

    public void saveToDatabase() {
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "INSERT INTO tasks (id, naam, beschrijving, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, id);
            p.setString(2, name);
            p.setString(3, description);
            p.setString(4, status.name());
            p.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fout bij opslaan taak: " + e.getMessage());
        }
    }
}

class TrelloBoard {
    private String name;
    private ArrayList<Task> taskList = new ArrayList<>();

    public TrelloBoard(String name) { this.name = name; }

    public void loadTasksFromDatabase() {
        taskList.clear();
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "SELECT id, naam, beschrijving, status FROM tasks";
        try (Connection conn = DriverManager.getConnection(url);
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                taskList.add(new Task(
                        rs.getInt("id"),
                        rs.getString("naam"),
                        rs.getString("beschrijving"),
                        TaskStatus.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            System.out.println("Fout bij laden taken: " + e.getMessage());
        }
    }

    public void addTask(Task t) { taskList.add(t); }

    public boolean updateTaskStatus(int taskId, TaskStatus newStatus) {
        for (Task t : taskList) {
            if (t.getId() == taskId) {
                t.setStatus(newStatus);
                updateTaskInDatabase(taskId, newStatus);
                return true;
            }
        }
        return false;
    }

    private void updateTaskInDatabase(int taskId, TaskStatus s) {
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, s.name());
            p.setInt(2, taskId);
            p.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fout bij update taak: " + e.getMessage());
        }
    }

    public void deleteTaskFromDatabase(int taskId) {
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sqlUnlink = "UPDATE berichtjes SET taskid = NULL WHERE taskid = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pu = conn.prepareStatement(sqlUnlink)) {
            pu.setInt(1, taskId);
            pu.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fout bij ontkoppelen berichtjes: " + e.getMessage());
        }
        String sqlDelete = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pd = conn.prepareStatement(sqlDelete)) {
            pd.setInt(1, taskId);
            pd.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fout bij verwijderen taak: " + e.getMessage());
        }
    }

    public void removeTask(int taskId) {
        taskList.removeIf(t -> t.getId() == taskId);
    }

    public Task getTaskById(int taskId) {
        return taskList.stream().filter(t -> t.getId() == taskId).findFirst().orElse(null);
    }

    public void showTasks() {
        System.out.println("Trello-bord: " + name);
        String sep = "+" + "-".repeat(18) + "+" + "-".repeat(18) + "+" + "-".repeat(18) + "+" + "-".repeat(18) + "+";
        System.out.println(sep);
        System.out.printf("| %-16s | %-16s | %-16s | %-16s |%n",
                "TODO","IN_PROGRESS","REVIEW","DONE");
        System.out.println(sep);
        @SuppressWarnings("unchecked")
        List<Task>[] cols = new List[4];
        for (int i = 0; i < 4; i++) cols[i] = new ArrayList<>();
        for (Task t : taskList) cols[t.getStatus().ordinal()].add(t);
        int max = 0; for (var c : cols) max = Math.max(max, c.size());
        for (int row = 0; row < max; row++) {
            System.out.print("| ");
            for (int col = 0; col < 4; col++) {
                if (row < cols[col].size()) {
                    Task t = cols[col].get(row);
                    System.out.printf("%-16s | ", t.getName() + "("+t.getId()+")");
                } else {
                    System.out.print(" ".repeat(16) + " | ");
                }
            }
            System.out.println();
        }
        System.out.println(sep);
    }
}

class berichtje {
    private int id;
    private String afzender, berichtje;
    private Integer taskid;

    public berichtje(int id, String afzender, String berichtje, Integer taskid) {
        this.id = id; this.afzender = afzender; this.berichtje = berichtje; this.taskid = taskid;
    }

    public void zetBerichtInDatabase() {
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "INSERT INTO berichtjes (id, afzender, berichtje, taskid) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, id);
            p.setString(2, afzender);
            p.setString(3, berichtje);
            if (taskid == null) p.setNull(4, Types.INTEGER);
            else p.setInt(4, taskid);
            p.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fout bij opslaan bericht: " + e.getMessage());
        }
    }
}

class User {
    Integer id;
    private String name, email, password;
    private ArrayList<Integer> berichtjesids = new ArrayList<>();

    public User(Integer id, String name, String email, String password) {
        this.id = id; this.name = name; this.email = email; this.password = password;
    }
    public User(String name, String email, String password) {
        this(null, name, email, password);
    }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public int[] getBerichtjesid() {
        return berichtjesids.stream().mapToInt(i -> i).toArray();
    }
    public void ontvangberichtid(int berichtId) {
        berichtjesids.add(berichtId);
        insertUserBerichtLink(id, berichtId);
    }
    public static void insertUserBerichtLink(Integer userId, int berichtId) {
        if (userId == null) return;
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "INSERT INTO user_berichtjes (user_id, bericht_id) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, userId);
            p.setInt(2, berichtId);
            p.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fout bij koppeling user-bericht: " + e.getMessage());
        }
    }

    public void insertUserIntoDatabase() {
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, name);
            p.setString(2, email);
            p.setString(3, password);
            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next()) id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Fout bij opslaan user: " + e.getMessage());
        }
    }

    public static ArrayList<User> loadUsersFromDatabase() {
        ArrayList<User> list = new ArrayList<>();
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "SELECT id,name,email,password FROM users";
        try (Connection conn = DriverManager.getConnection(url);
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                User u = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                try (PreparedStatement p2 = conn.prepareStatement(
                        "SELECT bericht_id FROM user_berichtjes WHERE user_id = ?")) {
                    p2.setInt(1, u.id);
                    try (ResultSet rs2 = p2.executeQuery()) {
                        while (rs2.next()) {
                            u.berichtjesids.add(rs2.getInt("bericht_id"));
                        }
                    }
                }
                list.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Fout bij laden users: " + e.getMessage());
        }
        return list;
    }
}

class UserStory {
    private int id;
    private String naam;
    private String beschrijving;

    public UserStory(int id, String naam, String beschrijving) {
        this.id = id; this.naam = naam; this.beschrijving = beschrijving;
    }
    public UserStory(String naam, String beschrijving) {
        this(-1, naam, beschrijving);
    }
    public int getId() { return id; }
    public String getNaam() { return naam; }
    public String getBeschrijving() { return beschrijving; }

    public void saveToDatabase() {
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "INSERT INTO userstories (naam, beschrijving) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, naam);
            p.setString(2, beschrijving);
            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next()) id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Fout bij opslaan user story: " + e.getMessage());
        }
    }

    public static List<UserStory> loadFromDatabase() {
        List<UserStory> list = new ArrayList<>();
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "SELECT id, naam, beschrijving FROM userstories";
        try (Connection conn = DriverManager.getConnection(url);
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new UserStory(
                        rs.getInt("id"),
                        rs.getString("naam"),
                        rs.getString("beschrijving")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Fout bij laden user stories: " + e.getMessage());
        }
        return list;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + naam + " - " + beschrijving;
    }
}

class Epic {
    private int id;
    private String titel;
    private List<UserStory> userStories = new ArrayList<>();
    private List<Task> taken = new ArrayList<>();

    public Epic(int id, String titel) {
        this.id = id; this.titel = titel;
    }
    public Epic(String titel) {
        this(-1, titel);
    }
    public int getId() { return id; }
    public String getTitel() { return titel; }

    public void saveToDatabase() {
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "INSERT INTO epics (titel) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, titel);
            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next()) id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Fout bij opslaan epic: " + e.getMessage());
        }
    }

    public void voegUserStoryToe(UserStory story) {
        userStories.add(story);
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "INSERT INTO epic_user_stories (epic_id, userstory_id) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, id);
            p.setInt(2, story.getId());
            p.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fout bij koppelen user story aan epic: " + e.getMessage());
        }
    }

    public void voegTaakToe(Task taak) {
        taken.add(taak);
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "INSERT INTO epic_tasks (epic_id, task_id) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, id);
            p.setInt(2, taak.getId());
            p.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fout bij koppelen taak aan epic: " + e.getMessage());
        }
    }

    public static List<Epic> loadFromDatabase(List<UserStory> allUserStories, TrelloBoard board) {
        List<Epic> list = new ArrayList<>();
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sqlEpics = "SELECT id, titel FROM epics";
        try (Connection conn = DriverManager.getConnection(url);
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery(sqlEpics)) {
            while (rs.next()) {
                int epicId = rs.getInt("id");
                Epic epic = new Epic(epicId, rs.getString("titel"));
                // load linked user stories
                String sqlUS = "SELECT userstory_id FROM epic_user_stories WHERE epic_id = ?";
                try (PreparedStatement p2 = conn.prepareStatement(sqlUS)) {
                    p2.setInt(1, epicId);
                    try (ResultSet rs2 = p2.executeQuery()) {
                        while (rs2.next()) {
                            int usId = rs2.getInt("userstory_id");
                            allUserStories.stream()
                                    .filter(us -> us.getId() == usId)
                                    .findFirst()
                                    .ifPresent(epic.userStories::add);
                        }
                    }
                }
                // load linked tasks
                String sqlTasks = "SELECT task_id FROM epic_tasks WHERE epic_id = ?";
                try (PreparedStatement p3 = conn.prepareStatement(sqlTasks)) {
                    p3.setInt(1, epicId);
                    try (ResultSet rs3 = p3.executeQuery()) {
                        while (rs3.next()) {
                            Task t = board.getTaskById(rs3.getInt("task_id"));
                            if (t != null) epic.taken.add(t);
                        }
                    }
                }
                list.add(epic);
            }
        } catch (SQLException e) {
            System.out.println("Fout bij laden epics: " + e.getMessage());
        }
        return list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Epic [" + id + "] " + titel + "\n");
        sb.append("User Stories:\n");
        for (UserStory us : userStories) {
            sb.append(" - ").append(us).append("\n");
        }
        sb.append("Taken:\n");
        for (Task t : taken) {
            sb.append(" - ").append(t).append("\n");
        }
        return sb.toString();
    }
}
