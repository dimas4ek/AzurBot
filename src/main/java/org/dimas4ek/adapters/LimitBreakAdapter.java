package org.dimas4ek.adapters;

import com.google.gson.*;
import org.dimas4ek.entity.Ship.LimitBreak;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LimitBreakAdapter implements JsonDeserializer<List<LimitBreak>> {
    public List<LimitBreak> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<LimitBreak> limitBreaks = new ArrayList<>();
        if (json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                if (element.isJsonArray()) {
                    LimitBreak limitBreak = new LimitBreak();
                    JsonArray subArray = element.getAsJsonArray();
                    String breaks = "";
                    String bonus = "";
                    for (JsonElement subElement : subArray) {
                        if (subElement.isJsonPrimitive()) {
                            if (breaks.isEmpty()) {
                                breaks = subElement.getAsString();
                            } else {
                                bonus = subElement.getAsString();
                            }
                        }
                    }
                    limitBreak.setBreak(breaks);
                    limitBreak.setBonus(bonus);
                    limitBreaks.add(limitBreak);
                }
            }
        }
        return limitBreaks;
    }
}
