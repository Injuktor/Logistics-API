package com.logisticscraft.logisticsapi.block.energy;

import com.logisticscraft.logisticsapi.block.utils.AnnotationUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface EnergyInput extends EnergyStorage {

    default long receiveEnergy(final long maxEnergy, final boolean simulate) {
        long energyReceived = Math.min(getMaxEnergyStored() - getStoredEnergy(), Math.min(getMaxReceive(), maxEnergy));
        if (!simulate) {
            setStoredEnergy(getStoredEnergy() + energyReceived);
        }
        return energyReceived;
    }

    default long getMaxReceive() {
        return AnnotationUtils.getAnnotation(this, EnergyInputData.class).maxReceive();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface EnergyInputData {

        int maxReceive();

    }

}