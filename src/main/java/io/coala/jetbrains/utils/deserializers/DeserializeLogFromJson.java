package io.coala.jetbrains.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.coala.jetbrains.utils.CodeAnalysisLog;
import java.lang.reflect.Type;

public class DeserializeLogFromJson implements JsonDeserializer<CodeAnalysisLog> {

  @Override
  public CodeAnalysisLog deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    final JsonObject jsonObject = jsonElement.getAsJsonObject();
    final String level = jsonObject.get("level").getAsString();
    final String message = jsonObject.get("message").getAsString();
    final String timestamp = jsonObject.get("timestamp").getAsString();

    return new CodeAnalysisLog(message, timestamp, level);
  }
}
