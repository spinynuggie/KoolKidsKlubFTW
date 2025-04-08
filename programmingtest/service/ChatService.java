package service;
import model.*;

public class ChatService {
    private Gespreksruimte ruimte;

    public ChatService(Gespreksruimte ruimte) {
        this.ruimte = ruimte;
    }

    public void verstuurBericht(Gebruiker gebruiker, String inhoud) {
        Bericht bericht = new Bericht(inhoud, gebruiker);
        ruimte.voegBerichtToe(bericht);
    }

    public void toonChat() {
        ruimte.toonAlleBerichten();
    }
}