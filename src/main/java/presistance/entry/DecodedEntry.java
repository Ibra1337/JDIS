package presistance.entry;

import dataStore.entity.StoredEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public  class DecodedEntry {


    @NonNull
    private String key;

    @SuppressWarnings("rawtypes")
    @NonNull
    private StoredEntity storedEntity;


    private Long expiration;

}
