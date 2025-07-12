package dataStore.entity;

import java.io.Serializable;

public abstract class StoredEntity<T> implements Serializable {
    protected final T value;

    protected StoredEntity(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public abstract StoredEntityType getType();

    public abstract byte[] serialize();

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StoredEntity<?> that = (StoredEntity<?>) obj;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }


}
