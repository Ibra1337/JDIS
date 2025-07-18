package presistance.encoding;

import dataStore.entity.StreamEntity;
import dataStore.entity.StoredEntity;
import dataStore.entity.StoredEntityType;
import dataStore.entity.StringEntity;
import lombok.val;
import presistance.entry.DecodedEntry;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class EntryDecoder {

    //[time][key][entry]
    //[1 time byte + 8 long ]
    //[4 keyLen bytes + k bytes]
    //[4 entry key bytes + e bytes ]

    // over all 6 elements that are needed to load 1 entity

    // solution ?
    // separate class that will be representing the currently built entry
    // something like builder pattern with completed flag () might be useful with implementing lists or other big data types

    public DecodedEntry decode(ByteBuffer buff) {


        Long exp = null;
        byte dataType = buff.get();
        if (dataType == (byte) 0xFC) {
            exp = buff.getLong();
            dataType = buff.get();
        }

        StoredEntityType type = StoredEntityType.fromMarker(dataType);
        int keyLen = buff.getInt();
        String key = readString(buff, keyLen);

        StoredEntity<?> entity;
        switch (type) {
            case STRING: {
                int valLen = buff.getInt();
                String value = readString(buff, valLen);
                entity = new StringEntity(value);
                break;
            }
            case STREAM: {
                int numEntries = buff.getInt();
                TreeMap<String, Map<String, String>> stream = new TreeMap<>();
                for (int i = 0; i < numEntries; i++) {
                    int idLen = buff.getInt();
                    String id = readString(buff, idLen);
                    int numPairs = buff.getInt();
                    Map<String, String> pairs = new HashMap<>();
                    for (int j = 0; j < numPairs; j++) {
                        int pairKeyLen = buff.getInt();
                        String pairKey = readString(buff, pairKeyLen);
                        int pairValLen = buff.getInt();
                        String pairValue = readString(buff, pairValLen);
                        pairs.put(pairKey, pairValue);
                    }
                    stream.put(id, pairs);
                }
                entity = new StreamEntity(stream);
                break;
            }
            default:
                throw new IllegalArgumentException("type not known or does not have handler" + type);

        }

        return new DecodedEntry(key, entity, exp);

    }

    private String readString(ByteBuffer buf, int len) {
        if (buf.remaining() >= len) {
            byte[] bytes = new byte[len];
            buf.get(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return null;
    }

}
