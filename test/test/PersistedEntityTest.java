package test;

import dataStore.presistance.PersistedEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersistedEntityTest {

    @Test
    void testToString() {
        var p = new PersistedEntity("key" , "zooo");
        System.out.println(p);

    }
}