package presistance;

import dataStore.DataStoreImpl;
import dataStore.entity.StoredEntity;
import presistance.encoding.Encoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

public class AsyncFileWriteHandler implements FileWriterHandler {

    private final DataStoreImpl dataStore;
    private final String path;
    private final AtomicLong filePosition = new AtomicLong(0);


    public AsyncFileWriteHandler(DataStoreImpl dataStore, String path) {
        this.dataStore = dataStore;
        this.path = path;
    }

    @Override
    public void write() {
        try {
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                    Paths.get(path),
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            ByteBuffer infoBuffer = ByteBuffer.allocate(1024);
            writeInfoSection(infoBuffer);

            channel.write(infoBuffer, 0, infoBuffer, new CompletionHandler<>() {
                @Override
                public void completed(Integer result, ByteBuffer buffer) {
                    filePosition.addAndGet(result);
                    System.out.println("Header written");

                    channel.write(
                            ByteBuffer.allocate(0),
                            filePosition.get(),
                            new WriteDatastoreContext(channel, dataStore, filePosition),
                            new WriteDatastoreHandler()
                    );
                }

                @Override
                public void failed(Throwable exc, ByteBuffer buffer) {
                    exc.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeInfoSection(ByteBuffer buffer) {
        buffer.clear();
        buffer.put((byte) 0xFB);
        buffer.putLong(dataStore.size());
        buffer.putLong(System.currentTimeMillis());
        buffer.flip();
    }

    private static class WriteDatastoreContext {
        final AsynchronousFileChannel channel;
        final Iterator<String> iterator;
        final AtomicLong filePosition;

        final DataStoreImpl dataStore;

        WriteDatastoreContext(AsynchronousFileChannel channel, DataStoreImpl data, AtomicLong filePosition) {
            this.channel = channel;
            this.iterator = data.getKeys().iterator();
            this.filePosition = filePosition;
            this.dataStore = data;
        }
    }

    // loading data there are 3 types of expected value
    // -> (optional) (time byte) + 8 time bytes
    // -> 1 type byte
    // -> key len + key
    // -> val len + val
    // basic idea generate bytes untill buff full -> dup bytes into file

    private static class WriteDatastoreHandler implements CompletionHandler<Integer, WriteDatastoreContext> {
        private final Encoder entryEncoder = new Encoder();
        private ByteBuffer carryOver;

        @Override
        public void completed(Integer result, WriteDatastoreContext ctx) {
            ctx.filePosition.addAndGet(result);

            if (!ctx.iterator.hasNext() && (carryOver == null || !carryOver.hasRemaining())) {
                System.out.println("[INFO] Finished writing all entries.");
                try {
                    ctx.channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            ByteBuffer buffer = ByteBuffer.allocate(1024);


            if (carryOver != null) {
                carryOver.flip();

                int toWrite = Math.min(buffer.remaining(), carryOver.remaining());
                byte[] chunk = new byte[toWrite];
                carryOver.get(chunk);
                buffer.put(chunk);

                if (carryOver.hasRemaining()) {
                    carryOver.compact();
                    buffer.flip();
                    ctx.channel.write(buffer, ctx.filePosition.get(), ctx, this);
                    return;
                } else {
                    carryOver = null;
                }
            }

            while (ctx.iterator.hasNext()) {


                String key = ctx.iterator.next();
                StoredEntity<?> val = ctx.dataStore.get(key);
                Long exp = ctx.dataStore.getExpiration(key);
                if (exp != null && System.currentTimeMillis() <= exp)
                    continue;
                byte[] entryBytes = entryEncoder.encodeEntity(key, val, exp).array();

                if (entryBytes.length <= buffer.remaining()) {
                    buffer.put(entryBytes);
                    System.out.println("[INFO] Buffered entry: " + key);
                } else {
                    int toWrite = buffer.remaining();
                    int remaining  = entryBytes.length - toWrite;
                    byte[] chunk = Arrays.copyOf(entryBytes , toWrite);

                    buffer.put(chunk);

                    ByteBuffer leftover = ByteBuffer.allocate(remaining);
                    leftover.put(Arrays.copyOfRange(entryBytes, toWrite, entryBytes.length));
                    carryOver = leftover;
                    break;
                }
            }

            buffer.flip();
            ctx.channel.write(buffer, ctx.filePosition.get(), ctx, this);
        }

        @Override
        public void failed(Throwable exc, WriteDatastoreContext ctx) {
            System.err.println("[ERROR] Write failed:");
            exc.printStackTrace();
            try {
                ctx.channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}