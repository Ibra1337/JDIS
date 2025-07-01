package dataStore.presistance;

import dataStore.DataStoreImpl;
import org.junit.jupiter.api.Test;
import presistance.AsyncFileStorage;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileStorageImplTest {

    @Test
    void write() throws InterruptedException {
        var dataStore = new DataStoreImpl();

        dataStore.setString("krz" , "OMG Iran");
        dataStore.setString("kwz" , "some interesting String is goning on here");

        dataStore.setString("kqz" , "Funny that both start wit I");



        var w = new AsyncFileStorage(dataStore);

        w.Write();

        Thread.sleep(100);
    }


    @Test
    void writeBig() throws InterruptedException {
        var dataStore = new DataStoreImpl();

        for (int i = 0 ; i <1000 ; i++) {
            dataStore.setString(String.valueOf(i) , UUID.randomUUID().toString());
        }

        var w = new AsyncFileStorage(dataStore);

        w.Write();

        Thread.sleep(100);
    }
    @Test
    void load() {

        var dataStore = new DataStoreImpl();

        var w = new AsyncFileStorage(dataStore);
        try {
            w.Load();
            Thread.sleep(2000);
            //54289
            int i =0;
            for (var k : dataStore.getKeys()){
                i++;
            }
            System.out.println("test ended ");
            System.out.println(i);
        } catch (IOException e) {
            System.out.println("???");

            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}