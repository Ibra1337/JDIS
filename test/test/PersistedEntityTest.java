package test;

import presistance.PersistedEntity;
import org.junit.jupiter.api.Test;

class PersistedEntityTest {

    @Test
    void testToString() {
        var p = new PersistedEntity("key" , "zooo");
        System.out.println(p);

    }
}