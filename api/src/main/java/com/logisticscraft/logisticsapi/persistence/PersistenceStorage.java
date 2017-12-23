package com.logisticscraft.logisticsapi.persistence;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.logisticscraft.logisticsapi.block.LogisticBlock;
import com.logisticscraft.logisticsapi.persistence.adapters.DataAdapter;
import com.logisticscraft.logisticsapi.persistence.adapters.HashMapDataAdapter;
import com.logisticscraft.logisticsapi.persistence.adapters.StringDataAdapter;

import de.tr7zw.itemnbtapi.NBTCompound;
import lombok.NonNull;

public class PersistenceStorage {

	private Gson gson;
	private Objenesis objenesis; //TODO: not sure rather we should use it
	private Map<Class<?>, DataAdapter<?>> converters;

	public PersistenceStorage() {
		gson = new Gson();
		objenesis = new ObjenesisStd();
		converters = new HashMap<>();

		// Register default converters
		registerDataConverter(String.class, new StringDataAdapter(), false);
		registerDataConverter(HashMap.class, new HashMapDataAdapter(), false);
	}

	public <T> void registerDataConverter(@NonNull Class<T> clazz, @NonNull DataAdapter<? extends T> converter, boolean replace) {
		if (replace) {
			converters.put(clazz, converter);
		} else {
			converters.putIfAbsent(clazz, converter);
		}
	}

	public void saveFields(@NonNull Object object, @NonNull NBTCompound nbtCompound){
		Arrays.stream(getFieldsUpTo(object.getClass(), LogisticBlock.class).toArray(new Field[0]))
		.filter(field -> field.getAnnotation(Persistent.class) != null)
		.forEach(field -> {
			field.setAccessible(true);
			try {
				saveObject(field.get(object), nbtCompound.addCompound(field.getName()));
			} catch (IllegalAccessException e) {
				throw new IllegalStateException("Unable to save field " + object.getClass().getSimpleName()
						+ "." + field.getName(), e);
			}
		});
	}

	public void loadFields(@NonNull Object object, @NonNull NBTCompound nbtCompound){
		Arrays.stream(getFieldsUpTo(object.getClass(), LogisticBlock.class).toArray(new Field[0]))
		.filter(field -> field.getAnnotation(Persistent.class) != null)
		.forEach(field -> {
			field.setAccessible(true);
			if(nbtCompound.hasKey(field.getName())){
				try {
					field.set(object, loadObject(field.getType(), nbtCompound.addCompound(field.getName())));
				} catch (IllegalAccessException e) {
					throw new IllegalStateException("Unable to load field " + object.getClass().getSimpleName()
							+ "." + field.getName(), e);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void saveObject(@NonNull Object data, @NonNull NBTCompound nbtCompound) {
		Class<?> clazz = data.getClass();

		// Check custom converters
		if (converters.containsKey(data.getClass())) {
			((DataAdapter<Object>) converters.get(clazz)).store(this, data, nbtCompound);
			return;
		}

		// Fallback to Json
		nbtCompound.setString("json", gson.toJson(data));
	}

	@SuppressWarnings("unchecked")
	public <T> T loadObject(@NonNull Class<T> type, @NonNull NBTCompound nbtCompound) {
		if (converters.containsKey(type)) {
			return (T) converters.get(type).parse(this, nbtCompound);
		}

		// Fallback to Json
		if (nbtCompound.hasKey("json")) {
			return gson.fromJson(nbtCompound.getString("json"), type);
		}

		return null;
	}

	private ArrayList<Field> getFieldsUpTo(@NonNull Class<?> startClass, 
			Class<?> exclusiveParent) {

		ArrayList<Field> currentClassFields = Lists.newArrayList(startClass.getDeclaredFields());
		Class<?> parentClass = startClass.getSuperclass();

		if (parentClass != null && 
				(exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
			List<Field> parentClassFields = 
					(List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
			currentClassFields.addAll(parentClassFields);
		}

		return currentClassFields;
	}

}
