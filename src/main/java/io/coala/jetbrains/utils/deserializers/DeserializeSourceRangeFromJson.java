package io.coala.jetbrains.utils.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.coala.jetbrains.utils.SourceRange;
import java.lang.reflect.Type;

public class DeserializeSourceRangeFromJson implements JsonDeserializer<SourceRange> {

  @Override
  public SourceRange deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    final JsonObject jsonObject = jsonElement.getAsJsonObject();
    final String fileName = jsonObject.get("file").getAsString();
    final int line = jsonObject.get("line").getAsInt();
    final JsonElement jsonElementColumn = jsonObject.get("column");
    final int column = jsonElementColumn instanceof JsonNull ? -1 : jsonElementColumn.getAsInt();

    return new SourceRange(fileName, line, column);
  }
}
