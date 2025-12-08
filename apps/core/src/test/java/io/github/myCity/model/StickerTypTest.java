package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StickerTypTest {

    @Test
    void testEnumValues() {
        assertNotNull(StickerTyp.valueOf("FELS_HINZUFÜGEN"));
        assertNotNull(StickerTyp.valueOf("BAUM_HINZUFÜGEN"));
        assertNotNull(StickerTyp.valueOf("BONUSFELD"));
        assertNotNull(StickerTyp.valueOf("STRUKTURSTICKER"));
    }

    @Test
    void testEnumLength() {
        assertEquals(4, StickerTyp.values().length);
    }
}
