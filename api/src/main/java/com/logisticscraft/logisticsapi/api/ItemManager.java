package com.logisticscraft.logisticsapi.api;

import java.util.Optional;

import javax.inject.Inject;

import com.logisticscraft.logisticsapi.data.LogisticKey;
import com.logisticscraft.logisticsapi.item.LogisticItem;
import com.logisticscraft.logisticsapi.item.LogisticItemRegister;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ItemManager {

    @Inject
    private LogisticItemRegister itemRegister;

    public void registerLogisticItem(@NonNull LogisticItem logisticItem) {
        itemRegister.registerLogisticItem(logisticItem);
    }

    public Optional<LogisticItem> getLogisticItem(@NonNull LogisticKey logisticKey) {
        return itemRegister.getLogisticItem(logisticKey);
    }

}
