package io.coala.jetbrains.utils.deserializers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import io.coala.jetbrains.utils.CodeAnalysisLog;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CodeAnalysisLogDeserializer {

  /**
   * This method deserializes raw JSON log output from coala analysis
   * and converts it to Java friendly objects.
   *
   * @param jsonString the String to be deserialized
   * @return list of {@link CodeAnalysisLog} objects
   */
  public static List<CodeAnalysisLog> getAllCodeAnalysisLogs(String jsonString) {
    final GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(List.class, new ExtractCodeAnalysisLogsFromJson());
    Type typeOfListOfCodeAnalysisLogs = new TypeToken<List<CodeAnalysisLog>>() {}.getType();
    return gsonBuilder.create().fromJson(jsonString, typeOfListOfCodeAnalysisLogs);
  }

  /**
   * This method creates a new log object from the corresponding JsonElement.
   *
   * @param jsonElement the element to be parsed
   * @return new {@link CodeAnalysisLog} object synthesized from JSON
   */
  public static CodeAnalysisLog getNewCodeAnalysisLog(JsonElement jsonElement) {
    final GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(CodeAnalysisLog.class, new DeserializeLogFromJson());
    return gsonBuilder.create().fromJson(jsonElement, CodeAnalysisLog.class);
  }

  private static class ExtractCodeAnalysisLogsFromJson implements
      JsonDeserializer<List<CodeAnalysisLog>> {

    @Override
    public List<CodeAnalysisLog> deserialize(JsonElement jsonElement, Type type,
        JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      final JsonObject jsonObject = jsonElement.getAsJsonObject();
      final JsonArray logsList = jsonObject.get("logs").getAsJsonArray();

      List<CodeAnalysisLog> codeAnalysisLogsList = new ArrayList<>();

      for (JsonElement log : logsList) {
        codeAnalysisLogsList.add(getNewCodeAnalysisLog(log));
      }

      return codeAnalysisLogsList;
    }
  }
}
