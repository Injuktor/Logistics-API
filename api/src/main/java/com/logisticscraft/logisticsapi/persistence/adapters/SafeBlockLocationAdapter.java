package com.logisticscraft.logisticsapi.persistence.adapters;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.logisticscraft.logisticsapi.data.SafeBlockLocation;
import com.logisticscraft.logisticsapi.persistence.PersistenceStorage;

import de.tr7zw.changeme.nbtapi.NBTCompound;

public class SafeBlockLocationAdapter implements DataAdapter<SafeBlockLocation> {

    @Override
    public void store(PersistenceStorage persistenceStorage, SafeBlockLocation value, NBTCompound nbtCompound) {
        if (!value.getLocation().isPresent()) {
            return;
        }
        Location location = value.getLocation().get();
        nbtCompound.setString("world", location.getWorld().getName());
        nbtCompound.setInteger("x", location.getBlockX());
        nbtCompound.setInteger("y", location.getBlockY());
        nbtCompound.setInteger("z", location.getBlockZ());
    }

    @Override
    public SafeBlockLocation parse(PersistenceStorage persistenceStorage, Object parentObject,
                                   NBTCompound nbtCompound) {
        World world = Bukkit.getWorld(nbtCompound.getString("world"));
        if (world == null) {
            return null;
        }
        return new SafeBlockLocation(new Location(world, nbtCompound.getInteger("x"), nbtCompound.getInteger("y"),
                nbtCompound.getInteger("z")));
    }
}
