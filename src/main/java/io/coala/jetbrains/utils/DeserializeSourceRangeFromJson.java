package io.coala.jetbrains.utils;

import gherkin.deps.com.google.gson.*;

import java.lang.reflect.Type;

public class DeserializeSourceRangeFromJson implements JsonDeserializer<SourceRange> {
    @Override
    public SourceRange deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        final String fileName = jsonObject.get("file").getAsString();
        final int line = jsonObject.get("line").getAsInt();
        final int column = jsonObject.get("column").getAsInt();

        return new SourceRange(fileName, line, column);
    }
}
