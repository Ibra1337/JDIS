package dataStore.presistance;

import dataStore.DataStore;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class FileStorageImpl implements FileStorage {

    private DataStore<String , String> dataStore;
    private String path = "data.bin";

    public FileStorageImpl(DataStore<String, String> dataStore) {
        this.dataStore = dataStore;
    }

    //data format
    //[key_length (int)][key_bytes][value_length (int)][value_bytes]
    @Override
    public void Write() {
        var keys = dataStore.getKeys();
        try (FileOutputStream fos = new FileOutputStream("data.bin");

             FileChannel channel = fos.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            for (var k : keys){
                buffer.clear();

                var val = dataStore.get(k);

                buffer.putInt(k.length());
                buffer.put( k.getBytes("UTF-8") );
                buffer.putInt( val.length());
                buffer.put( val.getBytes("UTF-8"));
                buffer.put("\n".getBytes());
                buffer.flip();
                channel.write(buffer);
                System.out.println("writing " + k + " " + dataStore.get(k));

            }

            System.out.println("Data written in binary format.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    @Override
    public void Load() throws IOException {


        var w = new FileStorageImpl(dataStore);


        AsynchronousFileChannel fc = AsynchronousFileChannel.open(Paths.get(this.path));
            var comlitionHandler = new FileReadHandler(fc);

            var buffer = ByteBuffer.allocate(1024);
            Integer pos = 0;
            fc.read(buffer, pos, buffer, comlitionHandler);


    }


    private class FileReadHandler implements CompletionHandler<Integer, ByteBuffer> {
        private final AsynchronousFileChannel fc;
        private final AtomicLong filePosition = new AtomicLong(0);
        private final ByteBuffer buffer = ByteBuffer.allocate(1024);

        public FileReadHandler(AsynchronousFileChannel fc) {
            this.fc = fc;
        }

        private int readInt(ByteBuffer buff, AtomicBoolean executed) {
            if (buff.remaining() >= 4) {
                executed.set(true);
                filePosition.addAndGet(4);
                return buff.getInt();
            }
            executed.set(false);
            return -1;
        }

        private String readString(ByteBuffer buff, int len, AtomicBoolean executed) {
            if (buff.remaining() >= len) {
                byte[] bytes = new byte[len];
                buff.get(bytes);
                executed.set(true);
                filePosition.addAndGet(len);
                return new String(bytes, StandardCharsets.UTF_8);
            }
            executed.set(false);
            return null;
        }

        @Override
        public void completed(Integer bytesRead, ByteBuffer buf) {

            System.out.println("=============LOADING BUFFOR==================");
            System.out.println(bytesRead + " : " + buf.position() );
            if (bytesRead == -1) {
                System.out.println("Reached end of file.");
                System.out.println(   dataStore.size() + " entries loaded" );
                return;
            }

            buf.flip();
            AtomicBoolean executed = new AtomicBoolean(true);
            int read = 0;

            try {

                while (buf.remaining() > 0) {
                    read = 0;
                    int keyLen = readInt(buf, executed);
                    if (!executed.get()) {
                        System.out.println();
                        filePosition.set( filePosition.get() - read);
                        break;
                    }
                    read +=4;

                    String key = readString(buf, keyLen, executed);
                    read += keyLen;
                    if (!executed.get()) {
                        System.out.println();
                        filePosition.set( filePosition.get() - read);

                        break;
                    }

                    int valLen = readInt(buf, executed);
                    if (!executed.get()) {
                        System.out.println();
                        break;
                    }
                    read += 4;

                    String val = readString(buf, valLen, executed);
                    if (!executed.get()) {
                        System.out.println();
                        filePosition.set( filePosition.get() - read);

                        break;
                    }
                    read += valLen;

                    filePosition.addAndGet(1);
                    if (buf.remaining() > 0) buf.get();

                    System.out.println("key: " + key + ", val: " + val + " "+ filePosition.get() + " " + buf.position() );
                    dataStore.set(key,val);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(read);
            buf.position( buf.position() );
            System.out.println("pos before reading" + buf.position());
            System.out.println(filePosition.get());
            buf.clear();
            fc.read(buf, filePosition.get(), buf, this);
        }

        @Override
        public void failed(Throwable exc, ByteBuffer buf) {
            exc.printStackTrace();
        }
    }


}


