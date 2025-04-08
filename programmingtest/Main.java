import model.*;
import service.ChatService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Voer je naam in: ");
        Gebruiker gebruiker = new Gebruiker(scanner.nextLine());

        Gespreksruimte ruimte = new Gespreksruimte("Algemeen");
        ChatService chatService = new ChatService(ruimte);

        System.out.println("Welkom bij de CLI Chat App, typ 'exit' om te stoppen.");
        
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                break;
            }

            chatService.verstuurBericht(gebruiker, input);
            chatService.toonChat();
        }

        System.out.println("Chat afgesloten.");
        scanner.close();
    }
}