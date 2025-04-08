package model;

import java.util.ArrayList;
import java.util.List;

public class Gespreksruimte {
    private String naam;
    private List<Bericht> berichten;

    public Gespreksruimte(String naam) {
        this.naam = naam;
        this.berichten = new ArrayList<>();
    }

    public void voegBerichtToe(Bericht bericht) {
        berichten.add(bericht);
    }

    public void toonAlleBerichten() {
        System.out.println("=== Berichten in " + naam + " ===");
        for (Bericht bericht : berichten) {
            bericht.toonBericht();
        }
    }
}