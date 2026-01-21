# MyCity – Spielbedienung & Startanleitung

MyCity ist ein strategisches Aufbauspiel für zwei Spieler.  
Dieses Repository enthält das Spielprojekt sowie eine separate **Bedienungsdokumentation**, die erklärt,
wie das Spiel gestartet und gesteuert wird.

👉 **Wichtig:**  
Diese README beschreibt **nur den Projektstart und die grundlegende Nutzung**.  
Eine detaillierte Erklärung der Spielbedienung befindet sich in der Datei 
/Dokumentation/[`Spieldokumentation`](https://gitlab.hof-university.de/s-sys-e/my-city/-/blob/main/Dokumentation/Spieledokumentation.md).

---

## Projektstruktur (relevant)

/apps
└── gradlew
Spieldokumentation.md
README.md

- Das Spiel wird aus dem Verzeichnis **`/apps`** gestartet.
- Die Bedienung des Spiels ist in **`Spieldokumentation.md`** dokumentiert.

---

## Voraussetzungen

Um das Spiel auszuführen, werden folgende Komponenten benötigt:

- **Java JDK** (empfohlen: Java 17 oder höher)
- Keine separate Gradle-Installation notwendig  
  (Gradle Wrapper ist im Projekt enthalten)

---

## Spiel starten

1. Öffnen Sie ein Terminal.
2. Wechseln Sie in das `apps`-Verzeichnis:

```bash
cd apps
Starten Sie das Spiel mit dem Gradle Wrapper:
./gradlew run
Nach erfolgreichem Start öffnet sich das Spiel im Hauptmenü.