package io.github.myCity.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GeländetypTest {

    @Test
    void testEnumValues() {
        assertNotNull(Geländetyp.valueOf("WIESE"));
        assertNotNull(Geländetyp.valueOf("BAUM"));
        assertNotNull(Geländetyp.valueOf("FELS"));
        assertNotNull(Geländetyp.valueOf("FLUSS"));
        assertNotNull(Geländetyp.valueOf("GEBIRGE"));
        assertNotNull(Geländetyp.valueOf("WALD"));
    }

    @Test
    void testEnumLength() {
        assertEquals(6, Geländetyp.values().length);
    }
}
