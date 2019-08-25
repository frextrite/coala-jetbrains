package io.coala.jetbrains.utils.deserializers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.coala.jetbrains.utils.AffectedCode;
import io.coala.jetbrains.utils.CodeAnalysisIssue;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DeserializeIssueFromJson implements JsonDeserializer<CodeAnalysisIssue> {

  @Override
  public CodeAnalysisIssue deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    final JsonObject jsonObject = jsonElement.getAsJsonObject();
    final String origin = jsonObject.get("origin").getAsString();
    final String message = jsonObject.get("message").getAsString();
    final int severity = jsonObject.get("severity").getAsInt();
    final JsonArray affectedCodeJsonArray = jsonObject.get("affected_code").getAsJsonArray();

    List<AffectedCode> affectedCodeList = new ArrayList<>();

    final GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(AffectedCode.class, new DeserializeAffectedCodeFromJson());

    for (JsonElement affectedCodeJsonElement : affectedCodeJsonArray) {
      final AffectedCode affectedCode = gsonBuilder.create()
          .fromJson(affectedCodeJsonElement, AffectedCode.class);
      affectedCodeList.add(affectedCode);
    }

    return new CodeAnalysisIssue(origin, message, affectedCodeList, severity);
  }
}
