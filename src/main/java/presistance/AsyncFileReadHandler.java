package presistance;

import dataStore.DataStoreImpl;
import dataStore.entity.StoredEntity;
import dataStore.entity.StringEntity;
import lombok.extern.slf4j.Slf4j;
import presistance.entry.EntryBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


public class AsyncFileReadHandler implements FileReadHandler {
    private final DataStoreImpl dataStore;
    private final String path;

    public AsyncFileReadHandler(DataStoreImpl dataStore, String path) {
        this.dataStore = dataStore;
        this.path = path;
    }

    public void read() throws IOException {
        AsynchronousFileChannel fc = AsynchronousFileChannel.open(Paths.get(path));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        fc.read(buffer, 0, buffer, new ReadHandler(fc));
    }

    private class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {
        private final AsynchronousFileChannel fc;
        private final AtomicLong filePosition = new AtomicLong(0);
        private final AtomicBoolean headerLoaded = new AtomicBoolean(false);

        private final AtomicLong loadCounter = new AtomicLong(0);


        private EntryBuilder entryBuilder = new EntryBuilder();

        public ReadHandler(AsynchronousFileChannel fc) {
            this.fc = fc;
        }

        private void loadHeader(ByteBuffer buf) {
            System.out.println("lh");
            if ((buf.get(0) & 0xFF) == 0xFB) {
                buf.position(1);
                long dbSize = buf.getLong();
                long creationTime = buf.getLong();
                headerLoaded.set(true);
                filePosition.addAndGet(1 + 8 + 8);
                System.out.println("[INFO] Loaded header. DB size hint: " + dbSize + ", Created: " + creationTime + " " + buf.position());

            }
        }

        @Override
        public void completed(Integer bytesRead, ByteBuffer buf) {
            loadCounter.addAndGet(1);
            System.out.println("loadn nr " + loadCounter.get());
            if (bytesRead == -1) {
                System.out.println("[INFO] Reached EOF. Loaded " + dataStore.size() + " entries.");
                try {
                    fc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            buf.flip();

            if (!headerLoaded.get()) {
                loadHeader(buf);
            }


            while (buf.remaining() >=0){

                filePosition.addAndGet(entryBuilder.consume(buf));
                if (!entryBuilder.isComplete()) {
                    System.out.println("[info] partial load");
                    System.out.println(entryBuilder);
                    System.out.println("============");
                    break;
                }
                var entry = entryBuilder.build();
                dataStore.set(entry.getKey() , entry.getStoredEntity() , entry.getExpiration());
                entryBuilder.reset();
                System.out.println("[info] entry loaded " + dataStore.size());
            }
            System.out.println(" remainig() " + buf.remaining()  + " " + bytesRead);

            buf.clear();
            fc.read(buf, filePosition.get(), buf, this);
        }

        @Override
        public void failed(Throwable exc, ByteBuffer buf) {
            System.err.println("[ERROR] Read failed:");
            exc.printStackTrace();
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }






}
