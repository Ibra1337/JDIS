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

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

class CommandExecutorTest {

    private DataStoreImpl mockDataStore;
    private CommandExecutorImpl executor;

    @BeforeEach
    void setUp() {
        mockDataStore = mock(DataStoreImpl.class);
        executor = new CommandExecutorImpl(mockDataStore);
    }

    @Test
    void testExecuteSetStoresValueAndReturnsOK() {
        String[] args = {"mykey", "myvalue"};

        CommandResult result = executor.executeSet(Arrays.stream(args).toList());

        verify(mockDataStore).set(eq("mykey"), any(StringEntity.class));
        assertTrue(result instanceof SimpleStringResult);
        assertEquals("OK", ((SimpleStringResult) result).getValue());
    }

    @Test
    void testExecuteSetThrowsOnWrongArgs() {
        assertThrows(IllegalArgumentException.class, () -> executor.executeSet(Arrays.stream(new String[]{"onlyOneArg"}).toList() ));
    }

    @Test
    void testExecuteGetReturnsBulkStringResult() {
        String key = "mykey";
        StoredEntity entity = new StringEntity("myvalue");

        when(mockDataStore.get(key)).thenReturn(entity);

        CommandResult result = executor.executeGet(Arrays.stream(new String[]{key}).toList());

        assertTrue(result instanceof BulkStringResult);
        assertEquals("myvalue", ((BulkStringResult) result).getValue());
    }

    @Test
    void testExecuteGetThrowsOnWrongArgs() {
        assertThrows(IllegalArgumentException.class, () -> executor.executeGet(Arrays.stream(new String[]{}).toList()));
    }

    @Test
    void testExecuteDelReturnsOneWhenDeleted() {
        when(mockDataStore.delete("mykey")).thenReturn(true);

        CommandResult result = executor.executeDel(Arrays.stream(new String[]{"mykey"}).toList());

        assertTrue(result instanceof IntegerResult);
        assertEquals(1, ((IntegerResult) result).getValue());
    }

    @Test
    void testExecuteDelReturnsZeroWhenNotDeleted() {
        when(mockDataStore.delete("mykey")).thenReturn(false);

        CommandResult result = executor.executeDel(Arrays.stream(new String[]{"mykey"}).toList());

        assertTrue(result instanceof IntegerResult);
        assertEquals(0, ((IntegerResult) result).getValue());
    }

    @Test
    void testExecuteDelThrowsOnWrongArgs() {
        assertThrows(IllegalArgumentException.class, () -> executor.executeDel(Arrays.stream(new String[]{}).toList()));
    }
}
