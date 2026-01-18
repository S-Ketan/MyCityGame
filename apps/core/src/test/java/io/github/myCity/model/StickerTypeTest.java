package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StickerTypeTest {

    @Test
    void testEnumValues() {
        assertNotNull(StickerType.valueOf("ADD_ROCK"));
        assertNotNull(StickerType.valueOf("ADD_TREE"));
        assertNotNull(StickerType.valueOf("BONUS_FIELD"));
        assertNotNull(StickerType.valueOf("STRUCTURE_STICKER"));
        assertNotNull(StickerType.valueOf("WELL"));
    }

    @Test
    void testEnumLength() {
        assertEquals(5, StickerType.values().length);
    }
}
