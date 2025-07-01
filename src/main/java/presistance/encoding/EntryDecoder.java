package presistance.encoding;

import dataStore.entity.StoredEntity;
import dataStore.entity.StoredEntityType;
import dataStore.entity.StringEntity;
import lombok.val;
import presistance.entry.DecodedEntry;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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

    public DecodedEntry decode(ByteBuffer buff){


        Long exp = null;
        byte dataType = buff.get();
        if (dataType == (byte) 0xFC){
            exp = buff.getLong();
            dataType = buff.get();
        }


        switch (dataType){
            case 0x01:

                break;
            default:
                throw new IllegalArgumentException("type not known or does not ahve handler" + dataType );

        }

        return null;

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
