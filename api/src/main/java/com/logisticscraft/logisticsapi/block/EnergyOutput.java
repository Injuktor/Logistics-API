package com.logisticscraft.logisticsapi.block;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface EnergyOutput extends EnergyStorage {

    default long extractEnergy(long maxExtract, boolean simulate){
        long energyExtracted = Math.min(getStoredEnergy(), Math.min(getMaxExtract(), maxExtract));
        if(!simulate)setStoredEnergy(getStoredEnergy()-energyExtracted);
        return energyExtracted;
    }
    
    default long getMaxExtract() {
        return AnnotationUtils.getAnnotation(this, EnergyOutputData.class).maxExtract();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface EnergyOutputData {

        int maxExtract();

    }

}
