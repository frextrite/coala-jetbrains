package io.coala.jetbrains.utils.deserializers;

import gherkin.deps.com.google.gson.JsonDeserializationContext;
import gherkin.deps.com.google.gson.JsonDeserializer;
import gherkin.deps.com.google.gson.JsonElement;
import gherkin.deps.com.google.gson.JsonObject;
import gherkin.deps.com.google.gson.JsonParseException;
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
