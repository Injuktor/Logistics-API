package com.logisticscraft.logisticsapi.block;

import java.util.HashMap;
import java.util.Optional;

import com.logisticscraft.logisticsapi.data.LogisticDataHolder;
import com.logisticscraft.logisticsapi.data.LogisticKey;
import com.logisticscraft.logisticsapi.data.SafeBlockLocation;
import com.logisticscraft.logisticsapi.persistence.Persistent;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class LogisticBlock implements LogisticDataHolder {

    @NonNull
    @Getter
    @Persistent
    private SafeBlockLocation safeLocation;

    @Persistent
    private HashMap<LogisticKey, Object> data = new HashMap<>();

    @Override
    public <T> void setLogisticData(@NonNull LogisticKey key, T value) {
        data.put(key, value);
    }

    @Override
    public void removeLogisticData(@NonNull LogisticKey key) {
        data.remove(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getLogisticData(@NonNull LogisticKey key, @NonNull Class<T> type) {
        return Optional.ofNullable((T) data.get(key));
    }

}
