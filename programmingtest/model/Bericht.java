package model;
import java.time.LocalDateTime;

public class Bericht {
    private String inhoud;
    private Gebruiker afzender;
    private LocalDateTime tijdstip;

    public Bericht(String inhoud, Gebruiker afzender) {
        this.inhoud = inhoud;
        this.afzender = afzender;
        this.tijdstip = LocalDateTime.now();
    }

    public String getInhoud() {
        return inhoud;
    }

    public Gebruiker getAfzender() {
        return afzender;
    }

    public LocalDateTime getTijdstip() {
        return tijdstip;
    }

    public void toonBericht() {
        System.out.println("[" + tijdstip + "] " + afzender.getNaam() + ": " + inhoud);
    }
}