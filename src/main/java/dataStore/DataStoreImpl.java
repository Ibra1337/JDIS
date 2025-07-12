package dataStore;

import dataStore.entity.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class DataStoreImpl {

    private final Map<String, StoredEntity<?>> store;
    private final Map<String, Long> expriationStore;
    public DataStoreImpl() {
        this.store = new ConcurrentHashMap<>();
        this.expriationStore = new ConcurrentHashMap<>();
    }

    public DataStoreImpl(int initialCapacity, Map<String, Long> expriationStore) {
        this.store = new ConcurrentHashMap<>(initialCapacity);
        this.expriationStore = expriationStore;
    }

    public Long getExpiration(String key){
        return expriationStore.get(key);
    }
    public DataStoreImpl(int initialCapacity, float loadFactor, Map<String, Long> expriationStore) {
        this.expriationStore = expriationStore;
        this.store = new ConcurrentHashMap<>(initialCapacity, loadFactor);
    }

    public <T> StoredEntity<T> set(String key, StoredEntity<T> entity) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        @SuppressWarnings("unchecked")
        StoredEntity<T> previous = (StoredEntity<T>) store.put(key, entity);
        return previous;
    }

    public StoredEntity<?> get(String key) {
        return store.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> StoredEntity<T> get(String key, Class<T> expectedType) {
        StoredEntity<?> entity = store.get(key);
        if (entity == null) {
            return null;
        }

        if (!expectedType.isInstance(entity.getValue())) {
            throw new ClassCastException("Expected " + expectedType.getSimpleName() +
                    " but found " + entity.getValue().getClass().getSimpleName());
        }

        return (StoredEntity<T>) entity;
    }

    public StoredEntityType getType(String key) {
        StoredEntity<?> entity = store.get(key);
        return entity != null ? entity.getType() : null;
    }

    public boolean delete(String key) {
        expriationStore.remove(key);
        return store.remove(key) != null;
    }

    public boolean exists(String key) {
        return store.containsKey(key);
    }

    public Set<String> getKeys() {
        return new HashSet<>(store.keySet());
    }

    public int size() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }

    public String setString(String key, String value) {
        StoredEntity<?> previous = set(key, new StringEntity(value));
        return previous instanceof StringEntity ?
                ((StringEntity) previous).getValue() : null;
    }

    public String getString(String key) {
        StoredEntity<?> entity = get(key);
        return entity instanceof StringEntity ?
                ((StringEntity) entity).getValue() : null;
    }

    public Integer setInteger(String key, Integer value) {
        StoredEntity<?> previous = set(key, new IntegerEntity(value));
        return previous instanceof IntegerEntity ?
                ((IntegerEntity) previous).getValue() : null;
    }

    public Integer getInteger(String key) {
        StoredEntity<?> entity = get(key);
        return entity instanceof IntegerEntity ?
                ((IntegerEntity) entity).getValue() : null;
    }

    public Double setDouble(String key, Double value) {
        StoredEntity<?> previous = set(key, new DoubleEntity(value));
        return previous instanceof DoubleEntity ?
                ((DoubleEntity) previous).getValue() : null;
    }

    public Double getDouble(String key) {
        StoredEntity<?> entity = get(key);
        return entity instanceof DoubleEntity ?
                ((DoubleEntity) entity).getValue() : null;
    }

    public int listPush(String key, String... values) {
        StoredEntity<?> entity = get(key);
        ListEntity listEntity;

        if (entity instanceof ListEntity) {
            listEntity = (ListEntity) entity;
        } else if (entity == null) {
            listEntity = new ListEntity(new ArrayList<>());
            set(key, listEntity);
        } else {
            throw new IllegalArgumentException("Key exists but is not a list");
        }

        for (String value : values) {
            listEntity.add(value);
        }

        return listEntity.size();
    }

    public String listPop(String key) {
        StoredEntity<?> entity = get(key);
        if (!(entity instanceof ListEntity listEntity)) {
            return null;
        }

        if (listEntity.size() == 0) {
            return null;
        }

        return listEntity.remove(listEntity.size() - 1);
    }

    public List<String> listRange(String key, int start, int end) {
        StoredEntity<?> entity = get(key);
        if (!(entity instanceof ListEntity)) {
            return new ArrayList<>();
        }

        return ((ListEntity) entity).getRange(start, end);
    }

    // Hash operations
    public String hashSet(String key, String field, String value) {
        StoredEntity<?> entity = get(key);
        HashEntity hashEntity;

        if (entity instanceof HashEntity) {
            hashEntity = (HashEntity) entity;
        } else if (entity == null) {
            hashEntity = new HashEntity();
            set(key, hashEntity);
        } else {
            throw new IllegalArgumentException("Key exists but is not a hash");
        }

        return hashEntity.put(field, value);
    }

    public String hashGet(String key, String field) {
        StoredEntity<?> entity = get(key);
        if (!(entity instanceof HashEntity)) {
            return null;
        }

        return ((HashEntity) entity).get(field);
    }

    public boolean hashDelete(String key, String field) {
        StoredEntity<?> entity = get(key);
        if (!(entity instanceof HashEntity)) {
            return false;
        }

        return ((HashEntity) entity).remove(field) != null;
    }

    public Set<String> hashFields(String key) {
        StoredEntity<?> entity = get(key);
        if (!(entity instanceof HashEntity)) {
            return new HashSet<>();
        }

        return ((HashEntity) entity).getFields();
    }

    public List<String> getRandomExpirationKeys(int amount){
        if (expriationStore.keySet().size() < amount)
            amount = getKeys().size();

        var indexes = new HashSet<>(amount);
        for (int i =0 ; i < amount; i++ )
            indexes.add(ThreadLocalRandom.current().nextInt(expriationStore.size()));


        List<String> keys = new LinkedList<>();

        int i = 0;

        for (String k : expriationStore.keySet()) {
            if (indexes.contains(i)) {
                keys.add(k);
                if (keys.size() == amount )
                    break;
            }

            i++;
        }

        return keys;
    }

    public Map<String, StoredEntityType> getAllTypes() {
        Map<String, StoredEntityType> types = new HashMap<>();
        for (Map.Entry<String, StoredEntity<?>> entry : store.entrySet()) {
            types.put(entry.getKey(), entry.getValue().getType());
        }
        return types;
    }

    public List<String> getKeysByType(StoredEntityType type) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, StoredEntity<?>> entry : store.entrySet()) {
            if (entry.getValue().getType() == type) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }



    public void set(String key, StoredEntity storedEntity, Long expiration) {
        this.set(key,  storedEntity);
        if (expiration != null)
            this.expriationStore.put(key , expiration);
    }
}
