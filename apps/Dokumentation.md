

# Spieledokumentation

## Einleitung
Willkommen bei My City! In diesem Spiel bauen die Spieler ihre eigenen Städte über eine Reihe von Kapiteln. In jedem Kapitel werden neue Gebäude und Regeln eingeführt, die die Entwicklung der Stadt beeinflussen.

**Hinweis:** Derzeit ist nur das erste Kapitel (Episoden 1-3) implementiert. Zukünftige Updates können weitere Kapitel hinzufügen.

## Spielstart
- Um ein neues Spiel zu beginnen, geben Sie die Namen von zwei Spielern ein und klicken Sie auf die Schaltfläche "Spiel starten".
- Das Spiel beginnt mit dem ersten Kapitel, und die Spieler ziehen abwechselnd Gebäudekarten und platzieren die entsprechenden Plättchen auf ihren Stadtplänen.

## Spielablauf
- **Plättchen platzieren:** Um ein Plättchen zu platzieren, klicken und ziehen Sie es von der rechten Seite des Bildschirms auf ein beliebiges Feld auf dem Raster.
- **Regeln für die Platzierung:** Das Plättchen wird grün, wenn die Platzierung gemäß den aktuellen Spielregeln gültig ist, und rot, wenn sie ungültig ist.
- **Plättchen drehen:** Klicken Sie auf die Schaltfläche "Drehen", um das Plättchen um 90 Grad im Uhrzeigersinn zu drehen.
- **Plättchen überspringen:** Wenn Sie ein Plättchen nicht platzieren möchten, können Sie auf die Schaltfläche "Überspringen" klicken, um es zu verwerfen und zum nächsten überzugehen.

## Punkteberechnung (Live-Punktestand)
Der während des Spiels angezeigte Punktestand wird wie folgt berechnet:
- **Startpunkte:** Jeder Spieler beginnt mit 10 Punkten.
- **Bäume:** +1 Punkt für jeden sichtbaren Baum auf einer Wiese.
- **Felsen:** -1 Punkt für jeden sichtbaren Felsen auf einer Wiese.
- **Leere Wiesen:** -1 Punkt für jedes unbebaute Wiesenfeld.
- **Übersprungene Plättchen:** -1 Punkt für jedes übersprungene Plättchen.
- Der Punktestand ist auf einen Bereich von 0 bis 50 begrenzt.
- Am Ende jedes Kapitels werden zusätzliche Bonuspunkte basierend auf den Episoden-spezifischen Regeln vergeben.

## Sticker-Platzierung
Nach bestimmten Episoden platzieren die Spieler Sticker auf ihren Spielplänen, die dauerhafte Auswirkungen haben. Die Platzierung erfolgt durch **Klicken auf ein hervorgehobenes Feld** auf dem eigenen Spielplan.

### Felsen- und Baum-Sticker (Nach Episoden 1 & 2)
- **Wer platziert:** Nach den Episoden 1 und 2 platziert der Gewinner der Episode einen **Felsen-Sticker** (negativ) und der Verlierer einen **Baum-Sticker** (positiv).
- **Platzierungsregeln:**
  - Sticker können nur auf bestimmten, hervorgehobenen Wiesenfeldern platziert werden.
  - Ein Feld darf nicht bereits einen Sticker haben.
- **Effekt:**
  - **Felsen:** Zieht 1 Punkt ab, wenn er am Ende eines Kapitels sichtbar ist.
  - **Baum:** Gibt 1 Punkt, wenn er am Ende eines Kapitels sichtbar ist.

### Brunnen-Sticker (Vor Episode 3)
- **Wer platziert:** Vor Beginn von Episode 3 platziert **jeder Spieler** einen **Brunnen-Sticker**.
- **Platzierungsregeln:**
  - Die Platzierung folgt den gleichen Regeln wie bei Felsen- und Baum-Stickern.
  - Zusätzlich darf ein Brunnen **nicht direkt an einen Fluss angrenzen**.
- **Effekt:**
  - Ein vollständig mit Gebäuden umschlossener Brunnen bringt am Ende von Episode 3 **+4 Bonuspunkte**.

## Kapitelende-Bildschirm
Nach Abschluss eines Kapitels wird ein Zusammenfassungsbildschirm angezeigt, der folgende Informationen enthält:
- **Punkte-Aufschlüsselung:** Eine detaillierte Tabelle, die zeigt, wie die Endpunktzahl für jeden Spieler berechnet wurde (Basispunkte, Bäume, Felsen, leere Felder, übersprungene Plättchen und Boni).
- **Episodensieger:** Eine Bekanntgabe des Gewinners der Episode.
- **Fortschrittspunkte:** Zeigt die in dieser Runde verdienten Fortschrittspunkte und den Gesamtfortschritt jedes Spielers auf dem Weg zum Sieg (100 Punkte).
- **Sticker-Anweisungen (nur Episoden 1-3):** Informiert darüber, welcher Spieler welchen Sticker als Nächstes platzieren wird.
- **Schaltflächen:**
  - **"Sticker platzieren" / "Weiter":** Fährt mit der nächsten Phase fort (Sticker-Platzierung oder nächstes Kapitel).
  - **"Speichern & Beenden":** Speichert den aktuellen Spielstand und kehrt zum Hauptmenü zurück.

## Spielregeln für Kapitel 1: Das neue Land (Episoden 1-3)

### Allgemeine Platzierungsregeln
- Das erste Plättchen muss an den Fluss angrenzen.
- Jedes weitere Plättchen muss an ein bereits gelegtes Plättchen angrenzen.
- Plättchen dürfen nicht auf Berge oder Wälder gebaut werden.
- Das Bauen auf Felsen und Bäumen ist erlaubt, wirkt sich aber auf die Punktzahl aus.
- Plättchen dürfen nicht über den Rand des Spielplans hinausragen oder sich mit anderen Plättchen überschneiden.

### Episoden-spezifische Regeln
- **Episode 1:**
  - **Wertung:** +1 Punkt für jeden sichtbaren Baum, -1 Punkt für jeden sichtbaren Felsen.
- **Episode 2:**
  - **Wertung:** Die größte zusammenhängende Gruppe von Gebäuden der gleichen Farbe erhält +1 Punkt pro Plättchen in dieser Gruppe.
- **Episode 3:**
  - **Wertung:** +4 Punkte, wenn der Brunnen vollständig von Gebäuden umgeben ist. Bäume und Felsen werden wie in Episode 1 gewertet.

## Spielziel
Das Ziel des Spiels ist es, über 24 Kapitel hinweg die erfolgreichste Stadt zu errichten. Die Spieler erhalten Punkte basierend auf der Platzierung von Gebäuden, der Einhaltung von Kapitelzielen und anderen Errungenschaften. Der Spieler mit den meisten Punkten am Ende des Spiels gewinnt.
