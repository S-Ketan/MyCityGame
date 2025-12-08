# My City – Technische Implementierungsdokumentation (Blueprint für Copilot)

Diese Datei beschreibt alle technischen Details, Datenstrukturen, Regeln und Klassen, die für die Implementierung des Spiels **„My City – Digital Edition“** benötigt werden.  
Sie dient als Grundlage und Referenz für die automatische Codegenerierung durch GitHub Copilot.

---

# 1. Gesamtarchitektur

Das System gliedert sich in folgende Bereiche:

## 1.1 Modell (Domain Layer)
- Spiel
- Kapitel
- Spieler
- Stadtplan
- Feld
- Plättchen
- Gebäudekarte
- Sticker (Legacy)
- PlatzierungsRegel
- KapitelBonus
- Spielstand

## 1.2 Logik (Controller / Game Engine)
- Spielablaufsteuerung
- Deckverwaltung
- Punkteberechnung
- Legacy-Verarbeitung
- Regelprüfung
- Rundenmanagement

## 1.3 UI (Presentation Layer)
- Darstellung des Stadtplans
- Drag & Drop für Plättchen
- Overlay für Regeln & Fehlermeldungen
- Punkteanzeige
- Kapitelwahl
- Speichern/Laden-Menüs

---

# 2. Datenmodell (Kern-Klassen)

## 2.1 Klasse `Spiel`
Verwaltet das gesamte Spiel und den aktuellen Zustand.

Attribute:
- aktuellesKapitel : Kapitel
- spieler : Spieler[2]
- spielstand : Spielstand

Methoden:
- neuesSpielStarten()
- kapitelLaden(n)
- kapitelSpielen()
- endpunkteBerechnen()
- spielstandSpeichern()
- spielstandLaden()

---

## 2.2 Klasse `Kapitel`
Repräsentiert ein Kapitel aus der Kampagne (insgesamt 8 Kapitel, je 3 Spiele).

Attribute:
- id : int
- deck : PlättchenDeck
- regeln : PlatzierungsRegel
- bonus : KapitelBonus
- stickerVorgaben : List<Sticker>

---

## 2.3 Klasse `PlättchenDeck`
Enthält **24 Gebäudekarten** (entsprechen 24 Plättchen).

Attribute:
- karten : List<Gebäudekarte>

Methoden:
- mischen()
- zieheKarte()

---

## 2.4 Klasse `Gebäudekarte`
Eine Karte, die ein Plättchen repräsentiert.

Attribute:
- plättchen : Plättchen

---

## 2.5 Klasse `Spieler`
Repräsentiert Spieler 1 oder Spieler 2.

Attribute:
- name
- punktzahl
- stadtplan : Stadtplan

Methoden:
- kannLegen()
- plättchenLegen()
- plättchenDrehen()
- plättchenÜberspringen()

---

## 2.6 Klasse `Stadtplan`
11x11 Felder mit Flusskanten + Sticker (Legacy).

Attribute:
- raster : Feld[11][11]
- sticker : List<Sticker>

Methoden:
- plättchenPlatzieren()
- getFeld()
- bäumeZählen()
- felsenZählen()

---

## 2.7 Klasse `Feld`
Ein einzelnes Rasterfeld.

Attribute:
- gelände : Geländetyp
- belegt : boolean
- flussOben : boolean
- flussUnten : boolean
- flussLinks : boolean
- flussRechts : boolean
- sticker : List<Sticker>

---

## 2.8 Enum `Geländetyp`
- WIESE  
- BAUM  
- FELS  
- FLUSS  
- GEBIRGE  
- WALD  

---

## 2.9 Klasse `Plättchen`
Ein Gebäudeplättchen mit Formmatrix.

Attribute:
- id
- form : boolean[][]
- rotation

Methoden:
- drehen()

---

## 2.10 Klasse `PlatzierungsRegel`
Implementiert alle Bauregeln des Spiels:

- Erstes Plättchen muss am Fluss angrenzen
- Danach jedes Plättchen muss angrenzen
- Keine Überbauung von Gebirge/Wald/Gebäude
- Keine Platzierung auf dem Fluss
- Bäume/Felsen dürfen überbaut werden

Methoden:
- prüfen()
- regelErstesGebäude()
- regelAnlegenAnGebäude()
- regelKeineÜberbauung()
- regelNichtAufFluss()

---

## 2.11 Klasse `KapitelBonus`
Kapitelabhängige Bonusregeln (z. B. +1 Baum, -1 Fels).

Methoden:
- berechneBonus(stadtplan)

---

## 2.12 Legacy: Sticker

Sticker können Felder dauerhaft verändern.

Attribute:
- typ : StickerTyp
- position : Position
- effektBeschreibung

Enum StickerTyp:
- FELS_ENTFERNEN
- BAUM_HINZUFÜGEN
- BONUSFELD
- STRUKTURSTICKER

---

## 2.13 Klasse `Spielstand`
Wird gespeichert und geladen.

Attribute:
- kapitelNummer
- spieler1
- spieler2

---

# 3. Gameplay-Regeln (Algorithmisch)

## 3.1 Bauregel-Algorithmus: Platzierung prüfen

### Pseudocode:

function prüfen(tile, city, pos, isFirst):
projectedCells = berechnePosition(tile, pos)

for cell in projectedCells:
    if außerhalbBrett(cell):
        return false
    if cell.terrain in {GEBIRGE, WALD}:
        return false
    if cell.belegt:
        return false
    if cell.terrain == FLUSS:
        return false

if isFirst:
    if keine angrenzende Flusskante:
        return false
else:
    if nicht angrenzend an bestehendes Gebäude:
        return false

return true


---

## 3.2 Punkteberechnung

### Grundregeln:
- Jede freie Wiese: -1
- Jeder Baum: +1
- Jeder Fels: -1
- Kapitelbonus addieren

---

# 4. Ablauf eines Spiels

## 4.1 Start
- Kapitel auswählen
- Deck mischen
- Ausgangsstadtpläne laden

## 4.2 Spieler 1 Phase
- 24 Karten nacheinander ziehen
- Plättchen drehen + platzieren
- Fehlversuch = -1 Punkt

## 4.3 Spieler 2 Phase
- benutzt exakt dasselbe Deck
- führt dieselben 24 Züge nacheinander aus

## 4.4 Ende
- Punkte berechnen
- Bonus anwenden
- Sticker vergeben
- Kapitel speichern

---

# 5. JSON-Datenformate

## 5.1 Plättchen
```json
{
  "id": 1,
  "form": [
    [true, true],
    [false, true]
  ]
}

{
  "id": 1,
  "tiles": [1,2,3,...,24],
  "bonusRules": ["+1 BAUM", "-1 FELS"],
  "stickers": ["FELS_ENTFERNEN"]
}

{
  "chapter": 3,
  "players": [
    { "score": 12, "city": [...] },
    { "score": 9, "city": [...] }
  ]
}

Jede Klasse sollte in einer eigenen Datei liegen.
Modell-Klassen niemals Logik enthalten lassen.
PlatzierungsRegel ist zentral: diese zuerst implementieren.
Nutze Interfaces für UI und Rendering.
Stadtplan immer als 2D-Array modellieren.
Sticker immer vor der Plättchenprüfung berücksichtigen.


---

# ✔ Datei ist jetzt fertig.

Wenn du möchtest, erstelle ich dir daraus:

### 📄 eine **echte downloadbare `.md` Datei**  
(ich kann es erneut versuchen – jetzt, da der Inhalt final ist)

oder

### 🧩 eine Version speziell für Java, C#, oder libGDX.

Sag mir einfach Bescheid! ​:contentReference[oaicite:0]{index=0}​
