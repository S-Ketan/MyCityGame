# Projekt-Spezifikation: My City (Desktop Edition)

## 1. Projektübersicht
Digitale Umsetzung des Brettspiels "My City" (Reiner Knizia) als lokale Desktop-Anwendung.
[cite_start]Das Spiel ist eine Legacy-Kampagne über 24 Kapitel, bei der zwei Spieler rundenbasiert ihre Stadt aufbauen[cite: 9, 11, 33].

## 2. Tech Stack & Architektur
* **Sprache:** Java
* [cite_start]**Framework:** libGDX [cite: 17]
* [cite_start]**Architektur:** Model-View-Controller (MVC) - Strikte Trennung erforderlich! [cite: 18, 42]
* [cite_start]**Datenhaltung:** Lokal (JSON/SQLite) für Spielstände und Kapitel-Fortschritt[cite: 15, 18].
* [cite_start]**Plattformen:** Windows, macOS, Linux[cite: 18].
* [cite_start]**Input:** Maus & Tastatur (Drag & Drop, Tasten zum Drehen)[cite: 18, 38].

## 3. Kern-Features (Functional Requirements)

### 3.1 Spielverwaltung (Game Management)
* **[F1] Spielstart:** Neues Spiel initialisieren.
* [cite_start]**[F2] Kapitelwahl:** Auswahl aus 24 Kapiteln[cite: 33].
* [cite_start]**[F3/F4] Persistenz:** Speichern und Laden des aktuellen Spielstands (inkl. Punktestände, Plättchenpositionen, Stadtpläne)[cite: 33, 52].
* **[F5] Fortschritt:** Tracking des zuletzt gespielten Kapitels.

### 3.2 Spiellogik (Game Logic - Model)
* [cite_start]**[F6] Rundenbasiert:** Spieler 1 und Spieler 2 ziehen abwechselnd[cite: 35].
* **[F7] Plättchen-Pool:** Bereitstellung des nächsten Gebäudeteils (Plättchen).
* **[F8/F9] Platzierung & Validierung:**
    * Plättchen werden auf einem Raster (Stadtplan) platziert.
    * **WICHTIG:** Vor jeder Platzierung müssen die Regeln geprüft werden.
    * Es gibt 4 Grundregeln + kapitelspezifische Regeln.
    * [cite_start]Ungültige Züge müssen eine Fehlermeldung auslösen[cite: 35].
* **[F10] Scoring:** Automatische Punkteberechnung nach jeder Aktion basierend auf Kapitelregeln.
* [cite_start]**[F11] Kapitelabschluss:** Speicherung der kumulierten Punkte nach Kapitelende[cite: 35].

### 3.3 Benutzeroberfläche (UI - View)
* [cite_start]**[F12] Stadtplan:** Visuelle Darstellung der zwei Raster (eines pro Spieler)[cite: 38].
* **[F13] Interaktion:** Drag & Drop für Plättchen. Drehen via Tastatur/Maus vor dem Ablegen.
* **[F14/F15] HUD:** Anzeige des aktuellen Spielers und der Punktestände.
* **[F16] Menüs:** Hauptmenü, Speichermenü, Kapitelwahl.

### 3.4 Erweiterbarkeit
* [cite_start]**[F17] Konfiguration:** Kapitelinhalte (Karten, Plättchen) sollten konfigurierbar sein (z. B. externe JSON-Dateien), um den Kerncode nicht ändern zu müssen[cite: 40, 52].

## 4. Datenmodell (Entities)
[cite_start]Basierend auf dem Glossar [cite: 55] sollten folgende Klassen existieren:
* **Kapitel (Chapter):** Enthält Regeln und Sets für eine Spielphase.
* **Plättchen (Tile):** Ein Gebäudeteil mit Form, Farbe und Orientierung.
* **Stadtplan (Board/Grid):** Das Raster für die Platzierung.
* **Spieler (Player):** Hält den Punktestand und den eigenen Stadtplan.
* **Spielstand (SaveState):** Serialisierbares Objekt für den gesamten Fortschritt.

## 5. Nicht-Funktionale Anforderungen (Constraints)
* [cite_start]**[N2] Performance:** Reaktionszeit < 100 ms bei Interaktionen[cite: 42].
* **[N3] Zuverlässigkeit:** Keine doppelten Züge oder Inkonsistenzen erlaubt.
* **[N4] Wartbarkeit:** MVC Pattern muss eingehalten werden.
* [cite_start]**[N5] Portabilität:** Muss ohne Code-Änderung auf allen OS laufen (Java)[cite: 42].

## 6. Out of Scope (Nicht implementieren)
* [cite_start]Keine KI / Bot[cite: 45].
* [cite_start]Kein Online-Multiplayer[cite: 46].
* [cite_start]Keine Datenbank-Server (nur lokale Files)[cite: 47].
* [cite_start]Keine Mobile-Unterstützung[cite: 48].