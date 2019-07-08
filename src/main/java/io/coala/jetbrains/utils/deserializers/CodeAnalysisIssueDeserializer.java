package io.coala.jetbrains.utils.deserializers;

import gherkin.deps.com.google.gson.GsonBuilder;
import gherkin.deps.com.google.gson.JsonArray;
import gherkin.deps.com.google.gson.JsonDeserializationContext;
import gherkin.deps.com.google.gson.JsonDeserializer;
import gherkin.deps.com.google.gson.JsonElement;
import gherkin.deps.com.google.gson.JsonObject;
import gherkin.deps.com.google.gson.JsonParseException;
import gherkin.deps.com.google.gson.reflect.TypeToken;
import io.coala.jetbrains.utils.CodeAnalysisIssue;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CodeAnalysisIssueDeserializer {

  /**
   * This method deserializes raw JSON output from coala analysis
   * and converts it to Java friendly objects.
   *
   * @param jsonString the String to be deserialized
   * @return list of {@link CodeAnalysisIssue} objects
   */
  public static List<CodeAnalysisIssue> getAllCodeAnalysisIssues(String jsonString) {
    final GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(List.class, new ExtractCodeAnalysisIssuesFromJson());
    Type typeOfListOfCodeAnalysisIssues = new TypeToken<List<CodeAnalysisIssue>>() {}.getType();
    return gsonBuilder.create().fromJson(jsonString, typeOfListOfCodeAnalysisIssues);
  }

  /**
   * This method creates a new issue object from the corresponding JsonElement.
   *
   * @param jsonElement the element to be parsed
   * @return new {@link CodeAnalysisIssue} object synthesized from JSON
   */
  public static CodeAnalysisIssue getNewCodeAnalysisIssue(JsonElement jsonElement) {
    final GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(CodeAnalysisIssue.class, new DeserializeIssueFromJson());
    return gsonBuilder.create().fromJson(jsonElement, CodeAnalysisIssue.class);
  }

  /**
   * Custom deserializer class for deserializing JSON output from coala analysis.
   *
   * <p>Currently, a single list of issue objects is compiled from
   * all the different sections in .coafile
   */
  private static class ExtractCodeAnalysisIssuesFromJson implements
      JsonDeserializer<List<CodeAnalysisIssue>> {

    @Override
    public List<CodeAnalysisIssue> deserialize(JsonElement jsonElement, Type type,
        JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      final JsonObject jsonObject = jsonElement.getAsJsonObject();
      final JsonObject jsonObjectResults = jsonObject.get("results").getAsJsonObject();

      List<CodeAnalysisIssue> codeAnalysisIssueList = new ArrayList<>();

      for (Map.Entry<String, JsonElement> section : jsonObjectResults.entrySet()) {
        final JsonElement sectionValue = section.getValue();
        final JsonArray sectionIssueList = sectionValue.getAsJsonArray();
        for (JsonElement sectionIssue : sectionIssueList) {
          codeAnalysisIssueList.add(getNewCodeAnalysisIssue(sectionIssue));
        }
      }

      return codeAnalysisIssueList;
    }
  }
}
