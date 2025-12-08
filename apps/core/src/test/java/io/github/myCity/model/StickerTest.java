package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StickerTest {

    @Test
    void testConstructorAndGetters() {
        Position position = new Position(1, 2);
        Sticker sticker = new Sticker(StickerTyp.BONUSFELD, position, "Testeffekt");

        assertEquals(StickerTyp.BONUSFELD, sticker.getTyp());
        assertEquals(position, sticker.getPos());
        assertEquals("Testeffekt", sticker.getEffektBeschreibung());
    }
}
