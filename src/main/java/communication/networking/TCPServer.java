package communication.networking;

import communication.parser.RespDecoder;
import communication.parser.RequestReader;
import dataStore.DataStoreImpl;

import java.io.*;
import java.net.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;


public class TCPServer implements Server {

    private String host;
    private int port;

    private final RequestReader requestReader;

    private final DataStoreImpl dataStore = new DataStoreImpl();
    private Selector selector;
    public TCPServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.requestReader = new RespDecoder();

    }



    @Override
    public void listen() {
        System.out.println("Listening on port " + this.port);
        try {
            selector = Selector.open();

            ServerSocketChannel socket = ServerSocketChannel.open();
            ServerSocket serverSocket = socket.socket();
            serverSocket.bind(new InetSocketAddress("localhost", this.port));

            socket.configureBlocking(false);
            socket.register(selector, socket.validOps(), null);

            while (true) {
                selector.select();

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();

                while (i.hasNext()) {
                    SelectionKey key = i.next();
                    i.remove();

                    try {
                        if (key.isAcceptable()) {
                            handleAccept(socket, key);
                        } else if (key.isReadable()) {
                            handleRead(key);
                        }
                    } catch (IOException e) {
                        System.err.println("Client error: " + e.getMessage());
                        key.cancel();
                        try {
                            key.channel().close();
                        } catch (IOException ce) {
                            ce.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


        void handleAccept(ServerSocketChannel mySocket,
                SelectionKey key) throws IOException {

            System.out.println("Connection Accepted...");

            SocketChannel client = mySocket.accept();
            client.configureBlocking(false);

            client.register(selector, SelectionKey.OP_READ);
        }

          void handleRead(SelectionKey key)
            throws IOException {
            System.out.println("Reading...");
            SocketChannel chanel = (SocketChannel) key.channel();
            ClientConnection clientConnection = new ClientConnection(chanel);
            var com  = this.requestReader.read(clientConnection);
            //TODO: add request handler
        }
    }



