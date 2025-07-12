package expirationCleanup;

import dataStore.DataStoreImpl;

import java.util.Timer;
import java.util.TimerTask;

public class ExpirationScheduler {

    private final Timer timer = new Timer(true);
    private  Integer entriesPerCycle =20;
    private final DataStoreImpl dataStore;

    public ExpirationScheduler(DataStoreImpl dataStore) {
        this.dataStore = dataStore;
    }

    public ExpirationScheduler(DataStoreImpl dataStore, int entriesPerCycle) {
        this.dataStore = dataStore;
        this.entriesPerCycle = entriesPerCycle;
    }

    public void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                expireCycle();
            }
        }, 0, 1000);
    }

    private void expireCycle() {
        for (String key : dataStore.getRandomExpirationKeys(entriesPerCycle)) {
            Long expiration = dataStore.getExpiration(key);
            if (expired(expiration)) {
                dataStore.delete(key);
            }
        }
    }


    public void stop() {
        timer.cancel();
    }

    private boolean expired(Long expirationDate){
        return expirationDate < System.currentTimeMillis();
    }
}