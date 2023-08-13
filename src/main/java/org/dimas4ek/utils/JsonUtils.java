package org.dimas4ek.utils;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.dimas4ek.entity.Equip;
import org.dimas4ek.entity.Ship;

import java.io.IOException;
import java.util.Objects;

public class JsonUtils {
    public static Ship[] getShipData() {
        return getJsonData("https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/ships.json", Ship[].class);
    }
    
    public static Equip[] getEquipData() {
        return getJsonData("https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/equipments.json", Equip[].class);
    }
    
    private static <T> T getJsonData(String url, Class<T> type) {
        T result = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                .url(url)
                .build();
            
            try (Response response = client.newCall(request).execute()) {
                String jsonString = Objects.requireNonNull(response.body()).string();
                Gson gson = new Gson();
                result = gson.fromJson(jsonString, type);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
