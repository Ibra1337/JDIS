package dataStore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StringDataStore implements DataStore<String,String> {

    private Map<String , String> store = new HashMap<>();


    @Override
    public String set(String string, String string2) {
        store.put(string, string2);
        return string;
    }

    @Override
    public String get(String string) {
        return store.get(string);
    }

    @Override
    public Set<String> getKeys() {
        return store.keySet();
    }

    @Override

    public int size(){
        return store.size();
    }


}
