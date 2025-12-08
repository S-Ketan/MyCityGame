package io.github.myCity.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FeldTest {

    private Feld feld;
    private Geländetyp wald = Geländetyp.WALD;

    @BeforeEach
    void setUp() {
        feld = new Feld(wald);
    }

    @Test
    void testConstructor() {
        assertEquals(wald, feld.getGelände());
        assertFalse(feld.istBelegt());
        assertFalse(feld.hatFluss());
        assertTrue(feld.getStickers().isEmpty());
    }

    @Test
    void testSetGelände() {
        feld.setGelände(Geländetyp.GEBIRGE);
        assertEquals(Geländetyp.GEBIRGE, feld.getGelände());
    }

    @Test
    void testSetBelegt() {
        feld.setBelegt(true);
        assertTrue(feld.istBelegt());
        feld.setBelegt(false);
        assertFalse(feld.istBelegt());
    }

    @Test
    void testHatFluss() {
        assertFalse(feld.hatFluss());
        feld.flussOben = true;
        assertTrue(feld.hatFluss());
        feld.flussOben = false;
        feld.flussUnten = true;
        assertTrue(feld.hatFluss());
        feld.flussUnten = false;
        feld.flussLinks = true;
        assertTrue(feld.hatFluss());
        feld.flussLinks = false;
        feld.flussRechts = true;
        assertTrue(feld.hatFluss());
    }

    @Test
    void testAddAndGetStickers() {
        Sticker sticker1 = new Sticker(StickerTyp.BAUM_HINZUFÜGEN, new Position(0, 0), "Effekt 1");
        Sticker sticker2 = new Sticker(StickerTyp.BONUSFELD, new Position(1, 1), "Effekt 2");
        feld.addSticker(sticker1);
        feld.addSticker(sticker2);

        List<Sticker> stickers = feld.getStickers();
        assertEquals(2, stickers.size());
        assertTrue(stickers.contains(sticker1));
        assertTrue(stickers.contains(sticker2));
    }
}
