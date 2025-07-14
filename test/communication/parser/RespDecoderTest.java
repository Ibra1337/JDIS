package communication.parser;

import communication.Command;
import communication.networking.ClientConnection;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RespDecoderTest {

    @Test
    void testDecodeSimpleSetCommand() throws IOException {
        // Given
        String resp = "*3\r\n$3\r\nSET\r\n$5\r\nmykey\r\n$7\r\nmyvalue\r\n";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put(resp.getBytes());
        buf.flip();

        // Mock client connection
        SocketChannel mockChannel = mock(SocketChannel.class);
        when(mockChannel.read(any(ByteBuffer.class))).thenAnswer(invocation -> {
            ByteBuffer b = invocation.getArgument(0);
            b.put(resp.getBytes());
            return resp.getBytes().length;
        });

        ClientConnection connection = mock(ClientConnection.class);
        when(connection.getBuffer()).thenReturn(ByteBuffer.allocate(1024));
        when(connection.getChannel()).thenReturn(mockChannel);

        // When
        RespDecoder decoder = new RespDecoder();
        Command command = decoder.read(connection);

        // Then
        assertEquals("SET", command.getName());
        assertEquals(2, command.getArgs().size());
        assertEquals("mykey", command.getArgs().get(0));
        assertEquals("myvalue", command.getArgs().get(1));
    }

    @Test
    void testDecodeSimpleGetCommand() throws IOException {
        String resp = "*2\r\n$3\r\nGET\r\n$5\r\nmykey\r\n";

        SocketChannel mockChannel = mock(SocketChannel.class);
        when(mockChannel.read(any(ByteBuffer.class))).thenAnswer(invocation -> {
            ByteBuffer b = invocation.getArgument(0);
            b.put(resp.getBytes());
            return resp.getBytes().length;
        });

        ClientConnection connection = mock(ClientConnection.class);
        when(connection.getBuffer()).thenReturn(ByteBuffer.allocate(1024));
        when(connection.getChannel()).thenReturn(mockChannel);

        RespDecoder decoder = new RespDecoder();
        Command command = decoder.read(connection);

        assertEquals("GET", command.getName());
        assertEquals(1, command.getArgs().size());
        assertEquals("mykey", command.getArgs().get(0));
    }

    @Test
    void testDecodeFailsOnInvalidTopLevelType() throws IOException {
        String resp = "$3\r\nSET\r\n";

        SocketChannel mockChannel = mock(SocketChannel.class);
        when(mockChannel.read(any(ByteBuffer.class))).thenAnswer(invocation -> {
            ByteBuffer b = invocation.getArgument(0);
            b.put(resp.getBytes());
            return resp.getBytes().length;
        });

        ClientConnection connection = mock(ClientConnection.class);
        when(connection.getBuffer()).thenReturn(ByteBuffer.allocate(1024));
        when(connection.getChannel()).thenReturn(mockChannel);

        RespDecoder decoder = new RespDecoder();

        assertThrows(IllegalStateException.class, () -> decoder.read(connection));
    }

    @Test
    void testDecodeFailsOnNonBulkStringCommandName() throws IOException {
        String resp = "*1\r\n+PING\r\n";

        SocketChannel mockChannel = mock(SocketChannel.class);
        when(mockChannel.read(any(ByteBuffer.class))).thenAnswer(invocation -> {
            ByteBuffer b = invocation.getArgument(0);
            b.put(resp.getBytes());
            return resp.getBytes().length;
        });

        ClientConnection connection = mock(ClientConnection.class);
        when(connection.getBuffer()).thenReturn(ByteBuffer.allocate(1024));
        when(connection.getChannel()).thenReturn(mockChannel);

        RespDecoder decoder = new RespDecoder();

        assertThrows(IllegalArgumentException.class, () -> decoder.read(connection));
    }
}
