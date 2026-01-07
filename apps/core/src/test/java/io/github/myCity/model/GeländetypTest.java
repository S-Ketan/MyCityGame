package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GeländetypTest {

    @Test
    void testEnumValues() {
        // Rivers are implemented as boolean flags on Feld, not as a terrain type
        assertNotNull(Geländetyp.valueOf("WIESE"));
        assertNotNull(Geländetyp.valueOf("BAUM"));
        assertNotNull(Geländetyp.valueOf("FELS"));
        assertNotNull(Geländetyp.valueOf("GEBIRGE"));
        assertNotNull(Geländetyp.valueOf("WALD"));
    }

    @Test
    void testEnumLength() {
        assertEquals(5, Geländetyp.values().length);
    }
}
