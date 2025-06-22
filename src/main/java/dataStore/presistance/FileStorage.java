package dataStore.presistance;

import java.io.IOException;

public interface FileStorage {


    public void Write();

    public void Load() throws IOException;

}
