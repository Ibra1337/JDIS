package command;

import static org.junit.jupiter.api.Assertions.*;


import command.commandRes.BulkStringResult;
import command.commandRes.CommandResult;
import command.commandRes.IntegerResult;
import command.commandRes.SimpleStringResult;
import dataStore.DataStoreImpl;
import dataStore.entity.StoredEntity;
import dataStore.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandExecutorTest {

    private DataStoreImpl mockDataStore;
    private CommandExecutor executor;

    @BeforeEach
    void setUp() {
        mockDataStore = mock(DataStoreImpl.class);
        executor = new CommandExecutor(mockDataStore);
    }

    @Test
    void testExecuteSetStoresValueAndReturnsOK() {
        String[] args = {"mykey", "myvalue"};

        CommandResult result = executor.executeSet(args);

        verify(mockDataStore).set(eq("mykey"), any(StringEntity.class));
        assertTrue(result instanceof SimpleStringResult);
        assertEquals("OK", ((SimpleStringResult) result).getValue());
    }

    @Test
    void testExecuteSetThrowsOnWrongArgs() {
        assertThrows(IllegalArgumentException.class, () -> executor.executeSet(new String[]{"onlyOneArg"}));
    }

    @Test
    void testExecuteGetReturnsBulkStringResult() {
        String key = "mykey";
        StoredEntity entity = new StringEntity("myvalue");

        when(mockDataStore.get(key)).thenReturn(entity);

        CommandResult result = executor.executeGet(new String[]{key});

        assertTrue(result instanceof BulkStringResult);
        assertEquals("myvalue", ((BulkStringResult) result).getValue());
    }

    @Test
    void testExecuteGetThrowsOnWrongArgs() {
        assertThrows(IllegalArgumentException.class, () -> executor.executeGet(new String[]{}));
    }

    @Test
    void testExecuteDelReturnsOneWhenDeleted() {
        when(mockDataStore.delete("mykey")).thenReturn(true);

        CommandResult result = executor.executeDel(new String[]{"mykey"});

        assertTrue(result instanceof IntegerResult);
        assertEquals(1, ((IntegerResult) result).getValue());
    }

    @Test
    void testExecuteDelReturnsZeroWhenNotDeleted() {
        when(mockDataStore.delete("mykey")).thenReturn(false);

        CommandResult result = executor.executeDel(new String[]{"mykey"});

        assertTrue(result instanceof IntegerResult);
        assertEquals(0, ((IntegerResult) result).getValue());
    }

    @Test
    void testExecuteDelThrowsOnWrongArgs() {
        assertThrows(IllegalArgumentException.class, () -> executor.executeDel(new String[]{}));
    }
}
