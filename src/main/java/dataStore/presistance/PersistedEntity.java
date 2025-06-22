package dataStore.presistance;


import java.util.Arrays;

public class PersistedEntity {

    private Integer keySize;

    private byte[] key;

    private Integer valueSize;

    private byte[] value;

    public PersistedEntity(String key , String value){
        this.key = key.getBytes();
        this.keySize = this.key.length;

        this.value = value.getBytes();
        this.valueSize = this.value.length;

    }

    @Override
    public String toString() {
        return "PersistedEntity{" +
                "keySize=" + keySize.byteValue() +
                ", key=" + Arrays.toString(key) +
                ", valueSize=" + valueSize.byteValue() +
                ", value=" + Arrays.toString(value) +
                '}';
    }



}
