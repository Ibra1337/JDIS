package presistance.entry;

import dataStore.entity.*;
import lombok.ToString;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@ToString
public class EntryBuilder {

    enum Step {
        EXPIRATION_FLAG,
        EXPIRATION_VALUE,
        TYPE,
        KEY_LENGTH,
        KEY,
        VALUE_LENGTH,
        VALUE,
        COMPLETE
    }

    private Step step = Step.EXPIRATION_FLAG;

    private boolean hasExpiration = false;
    private Long expiration = null;
    private byte type;

    private int keyLen = -1;
    private byte[] keyBytes;
    private int keyBytesRead = 0;

    private int valueLen = -1;
    private byte[] valueBytes;
    private int valueBytesRead = 0;

    private boolean typeNeedsValue = false;

    public int consume(ByteBuffer buf) {
        int bytesConsumed = 0;

        while (buf.hasRemaining() && step != Step.COMPLETE) {
            switch (step) {
                case EXPIRATION_FLAG:
                    byte flag = buf.get();
                    bytesConsumed += 1;
                    if ((flag & 0xFF) == 0xFC) {
                        hasExpiration = true;
                        step = Step.EXPIRATION_VALUE;
                    } else {
                        type = flag;
                        step = Step.TYPE;
                    }
                    break;

                case EXPIRATION_VALUE:
                    if (buf.remaining() >= Long.BYTES + 1) {
                        expiration = buf.getLong();
                        type = buf.get();
                        bytesConsumed += Long.BYTES + 1;
                        step = Step.TYPE;
                    } else {
                        return bytesConsumed;
                    }
                    break;

                case TYPE:
                    typeNeedsValue = doesTypeNeedValueLen(type);
                    step = Step.KEY_LENGTH;
                    break;

                case KEY_LENGTH:
                    if (buf.remaining() >= 4) {
                        keyLen = buf.getInt();
                        bytesConsumed += 4;
                        keyBytes = new byte[keyLen];
                        keyBytesRead = 0;
                        step = Step.KEY;
                    } else {
                        return bytesConsumed;
                    }
                    break;

                case KEY:
                    int keyToRead = Math.min(buf.remaining(), keyLen - keyBytesRead);
                    buf.get(keyBytes, keyBytesRead, keyToRead);
                    keyBytesRead += keyToRead;
                    bytesConsumed += keyToRead;
                    if (keyBytesRead == keyLen) {
                        if (typeNeedsValue) {
                            step = Step.VALUE_LENGTH;
                        } else {
                            step = Step.VALUE;
                            valueLen = 0;
                            valueBytes = new byte[0];
                            valueBytesRead = 0;
                        }
                    }
                    break;

                case VALUE_LENGTH:
                    if (buf.remaining() >= 4) {
                        valueLen = buf.getInt();
                        bytesConsumed += 4;
                        valueBytes = new byte[valueLen];
                        valueBytesRead = 0;
                        step = Step.VALUE;
                    } else {
                        return bytesConsumed;
                    }
                    break;

                case VALUE:
                    int valToRead = Math.min(buf.remaining(), valueLen - valueBytesRead);
                    buf.get(valueBytes, valueBytesRead, valToRead);
                    valueBytesRead += valToRead;
                    bytesConsumed += valToRead;
                    if (valueBytesRead == valueLen) {
                        step = Step.COMPLETE;
                    }
                    break;

                default:
                    throw new IllegalStateException("Unknown step: " + step);
            }
        }

        return bytesConsumed;
    }

    public boolean isComplete() {
        return step == Step.COMPLETE;
    }

    public DecodedEntry build() {
        if (!isComplete()) {
            throw new IllegalStateException("Entry not complete yet");
        }

        String key = new String(keyBytes, StandardCharsets.UTF_8);

        StoredEntity<?> entity;
        switch (type) {
            case 0x01:
                String value = new String(valueBytes, StandardCharsets.UTF_8);
                entity = new StringEntity(value);
                break;

            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }

        return new DecodedEntry(key, entity, expiration);
    }

    public void reset() {
        step = Step.EXPIRATION_FLAG;
        hasExpiration = false;
        expiration = null;
        type = 0;
        keyLen = -1;
        keyBytes = null;
        keyBytesRead = 0;
        valueLen = -1;
        valueBytes = null;
        valueBytesRead = 0;
        typeNeedsValue = false;
    }

    private boolean doesTypeNeedValueLen(byte type) {
        return switch (type) {
            case 0x01 -> true;
            default -> false;
        };
    }
}
