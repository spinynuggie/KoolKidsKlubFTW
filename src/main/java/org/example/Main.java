import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;

public class Main {
    private static ArrayList<User> users = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TrelloBoard board = new TrelloBoard("School Project");

        // 1) Laad alle taken uit de database
        board.loadTasksFromDatabase();

        // 2) Laad alle users uit de database
        users = User.loadUsersFromDatabase();

        String ingelochtals = "gast";
        boolean kleinmenu, kleinmenu2;

        while (true) {
            // Toon wie er ingelogd is
            System.out.println("\n=== Ingelogd als: " + ingelochtals + " ===");

            kleinmenu = true;
            System.out.println("Wat wil je doen?");
            System.out.println("1. Taak aanmaken");
            System.out.println("2. Taak verplaatsen");
            System.out.println("3. Taak verwijderen");
            System.out.println("4. Taken tonen");
            System.out.println("5. Beschrijving van een taak opvragen");
            System.out.println("6. User menu");
            System.out.println("7. Afsluiten");
            System.out.print("Kies een optie: ");

            int keuze = scanner.nextInt();
            scanner.nextLine();

            switch (keuze) {
                case 1: // Taak aanmaken
                    System.out.print("Voer de naam van de taak in: ");
                    String naam = scanner.nextLine();
                    System.out.print("Voer een beschrijving in: ");
                    String beschrijving = scanner.nextLine();
                    System.out.println("Kies een status: 1. TODO, 2. IN_PROGRESS, 3. REAVIEUW, 4. DONE");
                    int statusKeuze = scanner.nextInt();
                    scanner.nextLine();
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
                    int newTaskId = Task.getNextTaskId();
                    Task nieuweTask = new Task(newTaskId, naam, beschrijving, status);
                    board.addTask(nieuweTask);
                    nieuweTask.saveToDatabase();
                    System.out.println("Taak toegevoegd en in DB opgeslagen!");
                    break;

                case 2: // Taak verplaatsen
                    System.out.print("Voer het ID van de taak in die je wilt verplaatsen: ");
                    int taakIdVerplaats = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Kies nieuwe status: 1. TODO, 2. IN_PROGRESS, 3. REAVIEUW, 4. DONE");
                    int nieuweStatusKeuze = scanner.nextInt();
                    scanner.nextLine();
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
                    boolean updated = board.updateTaskStatus(taakIdVerplaats, nieuweStatus);
                    if (updated) {
                        System.out.println("Taakstatus bijgewerkt!");
                        board.showTasks();
                    } else {
                        System.out.println("Geen taak gevonden met ID " + taakIdVerplaats + ".");
                    }
                    break;

                case 3: // Taak verwijderen
                    System.out.print("Voer het ID van de taak in die je wilt verwijderen: ");
                    int taakIdDelete = scanner.nextInt();
                    scanner.nextLine();
                    board.removeTask(taakIdDelete);
                    board.deleteTaskFromDatabase(taakIdDelete);
                    System.out.println("Taak verwijderd!");
                    break;

                case 4: // Taken tonen
                    board.showTasks();
                    break;

                case 5: // Beschrijving opvragen
                    System.out.print("Voer het ID van de taak in: ");
                    int taakIdInfo = scanner.nextInt();
                    scanner.nextLine();
                    Task taak = board.getTaskById(taakIdInfo);
                    if (taak != null) {
                        System.out.println("Beschrijving: " + taak.getDescription());
                    } else {
                        System.out.println("Taak met ID " + taakIdInfo + " bestaat niet.");
                    }
                    break;

                case 6: // User menu
                    while (kleinmenu) {
                        kleinmenu2 = true;
                        // Toon wie er in het user-menu is ingelogd
                        System.out.println("\n=== User-menu (ingelogd als: " + ingelochtals + ") ===");
                        System.out.println("1. User aanmaken");
                        System.out.println("2. User verwijderen");
                        System.out.println("3. Inloggen als user");
                        System.out.println("4. Berichtjes bekijken");
                        System.out.println("5. Berichtje versturen");
                        System.out.println("6. Terug naar hoofdmenu");
                        System.out.print("Kies een optie: ");

                        int k2 = scanner.nextInt();
                        scanner.nextLine();
                        switch (k2) {
                            case 1: // User aanmaken
                                System.out.print("Naam: ");
                                String uNaam = scanner.nextLine();
                                System.out.print("Email: ");
                                String uEmail = scanner.nextLine();
                                System.out.print("Wachtwoord: ");
                                String uWacht = scanner.nextLine();
                                User newUser = new User(uNaam, uEmail, uWacht);
                                newUser.insertUserIntoDatabase();
                                users.add(newUser);
                                ingelochtals = uNaam;
                                System.out.println("User toegevoegd en in DB opgeslagen!");
                                break;

                            case 2: // User verwijderen
                                System.out.print("Naam user te verwijderen: ");
                                String delNaam = scanner.nextLine();
                                boolean verwijderd = users.removeIf(u -> u.getName().equals(delNaam));
                                if (verwijderd) {
                                    System.out.println("User \"" + delNaam + "\" verwijderd uit geheugen.");
                                } else {
                                    System.out.println("User niet gevonden.");
                                }
                                break;

                            case 3: // Inloggen
                                System.out.print("Naam: ");
                                String loginNaam = scanner.nextLine();
                                System.out.print("Wachtwoord: ");
                                String loginWacht = scanner.nextLine();
                                boolean found = false;
                                for (User u : users) {
                                    if (u.getName().equals(loginNaam) && u.getPassword().equals(loginWacht)) {
                                        ingelochtals = loginNaam;
                                        System.out.println("Inloggen gelukt! Welkom " + loginNaam);
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    System.out.println("Ongeldige credentials.");
                                }
                                break;

                            case 4: // Berichtjes bekijken
                                System.out.println("Laatste 10 berichtjes voor " + ingelochtals + ":");
                                for (User u : users) {
                                    if (u.getName().equals(ingelochtals)) {
                                        int teller = 0;
                                        for (int bid : u.getBerichtjesid()) {
                                            toonBerichtById(bid);
                                            if (++teller >= 10) break;
                                        }
                                        break;
                                    }
                                }
                                break;

                            case 5: // Berichtje versturen
                                ArrayList<User> ontvangers = new ArrayList<>();
                                while (kleinmenu2) {
                                    System.out.println("\n1. Ontvanger toevoegen  2. Verstuur  3. Verwijder ontvanger  4. Stop");
                                    System.out.print("Kies: ");
                                    int k3 = scanner.nextInt();
                                    scanner.nextLine();
                                    switch (k3) {
                                        case 1:
                                            System.out.print("Naam ontvanger: ");
                                            String onm = scanner.nextLine();
                                            users.stream()
                                                    .filter(u -> u.getName().equals(onm))
                                                    .findFirst()
                                                    .ifPresentOrElse(u -> {
                                                        ontvangers.add(u);
                                                        System.out.println(onm + " toegevoegd.");
                                                    }, () -> System.out.println("Niet gevonden."));
                                            break;
                                        case 2:
                                            if (ontvangers.isEmpty()) {
                                                System.out.println("Geen ontvangers.");
                                            } else {
                                                System.out.print("Bericht: ");
                                                String msg = scanner.nextLine();
                                                System.out.print("Taak koppelen? (ja/nee): ");
                                                String ans = scanner.nextLine();
                                                Integer koppeltaak = null;
                                                if (ans.equalsIgnoreCase("ja")) {
                                                    board.showTasks();
                                                    System.out.print("Taak ID: ");
                                                    koppeltaak = scanner.nextInt();
                                                    scanner.nextLine();
                                                }
                                                int newMsgId = getNextBerichtId();
                                                berichtje nb = new berichtje(newMsgId, ingelochtals, msg, koppeltaak);
                                                nb.zetBerichtInDatabase();
                                                ontvangers.forEach(u -> u.ontvangberichtid(newMsgId));
                                                System.out.println("Bericht verstuurd!");
                                            }
                                            break;
                                        case 3:
                                            System.out.print("Naam ontvanger weghalen: ");
                                            String rem = scanner.nextLine();
                                            if (ontvangers.removeIf(u -> u.getName().equals(rem))) {
                                                System.out.println(rem + " verwijderd.");
                                            } else {
                                                System.out.println(rem + " niet in lijst.");
                                            }
                                            break;
                                        case 4:
                                            kleinmenu2 = false;
                                            break;
                                        default:
                                            System.out.println("Onbekende keuze.");
                                    }
                                    if (!ontvangers.isEmpty() && kleinmenu2) {
                                        System.out.print("Ontvangers: ");
                                        ontvangers.forEach(u -> System.out.print(u.getName() + " "));
                                        System.out.println();
                                    }
                                }
                                break;

                            case 6:
                                kleinmenu = false;
                                break;

                            default:
                                System.out.println("Ongeldige keuze.");
                        }
                    }
                    break;

                case 7:
                    System.out.println("Programma afgesloten.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Ongeldige keuze, probeer opnieuw.");
            }
        }
    }

    public static void toonBerichtById(int berichtId) {
        String url = "jdbc:sqlite:sqlite3/teamflow.db";
        String sql = "SELECT * FROM berichtjes WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, berichtId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String afz = rs.getString("afzender");
                    String bericht = rs.getString("berichtje");
                    int taskid = rs.getInt("taskid");
                    Task t = rs.wasNull() ? null : getTaskByIdFromDB(taskid);
                    System.out.print("id:" + id + ", afzender:" + afz + ", berichtje:" + bericht);
                    if (t != null) {
                        System.out.println(", taak:" + t.getName() +
                                " (status:" + t.getStatus() +
                                ", beschrijving:" + t.getDescription() + ")");
                    } else {
                        System.out.println(", geen gekoppelde taak.");
                    }
                } else {
                    System.out.println("Geen bericht met id " + berichtId);
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
}

// ---- Overige klassen ----

enum TaskStatus {
    TODO, IN_PROGRESS, REAVIEUW, DONE
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

    public TrelloBoard(String name) {
        this.name = name;
    }

    public void loadTasksFromDatabase() {
        taskList.clear(); // eerst leegmaken
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

    public void addTask(Task t) {
        taskList.add(t);
    }

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

        // 1) Ontkoppel eerst alle berichtjes die naar deze taak wijzen
        String sqlUnlink = "UPDATE berichtjes SET taskid = NULL WHERE taskid = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmtUnlink = conn.prepareStatement(sqlUnlink)) {
            pstmtUnlink.setInt(1, taskId);
            int updatedCount = pstmtUnlink.executeUpdate();
            System.out.println("Ontkoppeld van " + updatedCount + " bericht(en).");
        } catch (SQLException e) {
            System.out.println("Fout bij ontkoppelen berichtjes: " + e.getMessage());
        }

        // 2) Verwijder nu de taak zelf
        String sqlDelete = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete)) {
            pstmtDelete.setInt(1, taskId);
            int deleted = pstmtDelete.executeUpdate();
            if (deleted > 0) {
                System.out.println("Taak " + taskId + " succesvol verwijderd uit DB.");
            } else {
                System.out.println("Geen taak gevonden met id: " + taskId);
            }
        } catch (SQLException e) {
            System.out.println("Fout bij verwijderen taak uit DB: " + e.getMessage());
        }
    }

    public void removeTask(int taskId) {
        taskList.removeIf(t -> t.getId() == taskId);
    }

    public Task getTaskById(int taskId) {
        return taskList.stream()
                .filter(t -> t.getId() == taskId)
                .findFirst()
                .orElse(null);
    }

    public void showTasks() {
        final int width = 30;
        String sep = "+" +
                "-".repeat(width + 2) + "+" +
                "-".repeat(width + 2) + "+" +
                "-".repeat(width + 2) + "+" +
                "-".repeat(width + 2) + "+";

        // Header
        System.out.println("Trello-bord: " + name);
        System.out.println(sep);
        System.out.printf("| %-" + width + "s | %-" + width + "s | %-" + width + "s | %-" + width + "s |%n",
                "TODO", "IN_PROGRESS", "REAVIEUW", "DONE");
        System.out.println(sep);

        @SuppressWarnings("unchecked")
        ArrayList<Task>[] cols = new ArrayList[4];
        for (int i = 0; i < 4; i++) cols[i] = new ArrayList<>();
        for (Task t : taskList) cols[t.getStatus().ordinal()].add(t);
        int max = 0;
        for (var c : cols) max = Math.max(max, c.size());

        // Per rij: wrap en multi-line print
        for (int row = 0; row < max; row++) {
            @SuppressWarnings("unchecked")
            List<String>[] cellLines = new List[4];
            int rowHeight = 0;

            // wrap elke kolomcel
            for (int col = 0; col < 4; col++) {
                String text = "";
                if (row < cols[col].size()) {
                    Task t = cols[col].get(row);
                    text = t.getName() + " (id:" + t.getId() + ")";
                }
                List<String> lines = wrapText(text, width);
                cellLines[col] = lines;
                rowHeight = Math.max(rowHeight, lines.size());
            }

            // print alle lines voor deze rij
            for (int li = 0; li < rowHeight; li++) {
                System.out.print("| ");
                for (int col = 0; col < 4; col++) {
                    String part = li < cellLines[col].size() ? cellLines[col].get(li) : "";
                    System.out.printf("%-" + width + "s", part);
                    System.out.print(" | ");
                }
                System.out.println();
            }
            System.out.println(sep);
        }
    }

    /** Splits lange tekst in meerdere regels van max 'width' tekens, bij voorkeur op spaties. */
    private static List<String> wrapText(String text, int width) {
        List<String> lines = new ArrayList<>();
        if (text == null) text = "";
        while (text.length() > width) {
            int split = text.lastIndexOf(' ', width);
            if (split <= 0) split = width;
            lines.add(text.substring(0, split));
            text = text.substring(split).trim();
        }
        lines.add(text);
        return lines;
    }
}

class berichtje {
    private int id;
    private String afzender, berichtje;
    private Integer taskid;

    public berichtje(int id, String afzender, String berichtje, Integer taskid) {
        this.id = id;
        this.afzender = afzender;
        this.berichtje = berichtje;
        this.taskid = taskid;
    }

    public berichtje(int id, String afzender, String berichtje) {
        this(id, afzender, berichtje, null);
    }

    public int getId() { return id; }

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
    private Integer id;
    private String name, email, password;
    private ArrayList<Integer> berichtjesids = new ArrayList<>();

    public User(Integer id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
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
        String sql = "SELECT id, name, email, password FROM users";
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
