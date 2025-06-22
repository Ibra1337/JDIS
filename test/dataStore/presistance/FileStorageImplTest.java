package dataStore.presistance;

import dataStore.DataStore;
import dataStore.StringDataStore;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileStorageImplTest {

    @Test
    void write() {
        var dataStore = new StringDataStore();

        dataStore.set("krz" , "OMG Iran");
        dataStore.set("kwz" , "Is being bombed by Israel");
        dataStore.set("kqz" , "Funny that both start wit I");



        var w = new FileStorageImpl(dataStore);

        w.Write();
    }


    @Test
    void writeBig() {
        var dataStore = new StringDataStore();

        for (int i = 0 ; i <1000 ; i++) {
            dataStore.set(String.valueOf(i) , UUID.randomUUID().toString());
        }


        var w = new FileStorageImpl(dataStore);

        w.Write();
    }
    @Test
    void load() {

        var dataStore = new StringDataStore();

        var w = new FileStorageImpl(dataStore);
        try {
            w.Load();
            Thread.sleep(1000);

            int i =0;
            for (var k : dataStore.getKeys()){
                i++;
            }
            System.out.println("kupson");
            System.out.println(i);
        } catch (IOException e) {
            System.out.println("???kupson");

            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}