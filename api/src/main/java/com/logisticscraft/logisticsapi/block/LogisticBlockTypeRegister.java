package com.logisticscraft.logisticsapi.block;

import com.logisticscraft.logisticsapi.data.LogisticKey;
import com.logisticscraft.logisticsapi.utils.Tracer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages internally the registered LogisticBlock types.
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class LogisticBlockTypeRegister {

    @Inject
    private LogisticTickManager tickManager;

    private Map<LogisticKey, Class<? extends LogisticBlock>> blockTypes = new HashMap<>();
    private Map<LogisticKey, LogisticBlockFactory> factories = new HashMap<>();

    @Synchronized
    public void registerLogisticBlock(@NonNull Plugin plugin, @NonNull String name, @NonNull Class<? extends LogisticBlock> block, @NonNull LogisticBlockFactory factory) {
        if (blockTypes.putIfAbsent(new LogisticKey(plugin, name), block) == null) {
            factories.put(new LogisticKey(plugin, name), factory);
            tickManager.registerLogisticBlockClass(block);
            Tracer.debug("LogisticBlock Registert: " + block.getName());
        } else {
            Tracer.warn("Trying to reregister known key: " + new LogisticKey(plugin, name).getName());
        }
    }

    @Synchronized
    public boolean isBlockRegistert(@NonNull LogisticBlock block) {
        return blockTypes.containsValue(block.getClass());
    }

    @Synchronized
    public Optional<LogisticBlockFactory> getFactory(@NonNull LogisticKey logisticKey) {
        return Optional.ofNullable(factories.get(logisticKey));
    }

}
