package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StickerTest {

    @Test
    void testConstructorAndGetters() {
        Position position = new Position(1, 2);
        Sticker sticker = new Sticker(StickerType.BONUS_FIELD, position, "Test effect");

        assertEquals(StickerType.BONUS_FIELD, sticker.getType());
        assertEquals(position, sticker.getPosition());
        assertEquals("Test effect", sticker.getEffectDescription());
    }
}
