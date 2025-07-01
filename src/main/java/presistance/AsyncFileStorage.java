package presistance;

import dataStore.DataStore;
import dataStore.DataStoreImpl;

import java.io.IOException;

public class AsyncFileStorage implements FileStorage {
    private final DataStoreImpl dataStore;
    private final String path = "data.bin";

    public AsyncFileStorage(DataStoreImpl dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void Write() {
        new AsyncFileWriteHandler(dataStore, path).write();
    }

    @Override
    public void Load() throws IOException {
        new AsyncFileReadHandler(dataStore, path).read();
    }


}
