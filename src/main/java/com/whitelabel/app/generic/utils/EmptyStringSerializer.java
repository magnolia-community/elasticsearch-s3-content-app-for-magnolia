package com.whitelabel.app.generic.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EmptyStringSerializer implements JsonSerializer<String> {
	@Override
	public JsonElement serialize(String src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
		if (src == null || src.isEmpty())
			return JsonNull.INSTANCE;
		return new JsonPrimitive(src);
	}
}
