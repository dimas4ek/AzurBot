package org.dimas4ek.adapters;

import com.google.gson.*;
import org.dimas4ek.entity.Ship.ObtainedFrom;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ObtainedFromAdapter implements JsonDeserializer<ObtainedFrom> {
    @Override
    public ObtainedFrom deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ObtainedFrom obtainedFrom = new ObtainedFrom();
        
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            
            if (jsonObject.has("fromMaps")) {
                JsonElement fromMapsElement = jsonObject.get("fromMaps");
                if (fromMapsElement.isJsonArray()) {
                    List<String> fromMaps = new ArrayList<>();
                    JsonArray jsonArray = fromMapsElement.getAsJsonArray();
                    for (JsonElement element : jsonArray) {
                        if (element.isJsonPrimitive()) {
                            fromMaps.add(element.getAsString());
                        }
                    }
                    obtainedFrom.setFromMaps(fromMaps);
                }
            }
            
            if (jsonObject.has("obtainedFrom")) {
                JsonElement obtainedFromElement = jsonObject.get("obtainedFrom");
                if (obtainedFromElement.isJsonPrimitive()) {
                    obtainedFrom.setObtainedFrom(obtainedFromElement.getAsString());
                }
            }
        }
        
        return obtainedFrom;
    }
}