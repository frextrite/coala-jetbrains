package io.coala.jetbrains.utils.deserializers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.coala.jetbrains.utils.AffectedCode;
import io.coala.jetbrains.utils.SourceRange;
import java.lang.reflect.Type;

public class DeserializeAffectedCodeFromJson implements JsonDeserializer<AffectedCode> {

  @Override
  public AffectedCode deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    final JsonObject jsonObject = jsonElement.getAsJsonObject();
    final String fileName = jsonObject.get("file").getAsString();
    final JsonObject start = jsonObject.get("start").getAsJsonObject();
    final JsonObject end = jsonObject.get("end").getAsJsonObject();

    final GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.serializeNulls();
    gsonBuilder.registerTypeAdapter(SourceRange.class, new DeserializeSourceRangeFromJson());

    final SourceRange startSourceRange = gsonBuilder.create().fromJson(start, SourceRange.class);
    final SourceRange endSourceRange = gsonBuilder.create().fromJson(end, SourceRange.class);

    return new AffectedCode(fileName, startSourceRange, endSourceRange);
  }
}
