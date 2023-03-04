package org.dimas4ek.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShipInfo extends ListenerAdapter {

    String name;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getFullCommandName().equals("ship")) {
            name = Objects.requireNonNull(event.getOption("name")).getAsString();
            try {
                JSONArray jsonArray = getJsonArrayFromUrl();
                JSONObject json = findJsonWithName(jsonArray, name);

                if (json == null) {
                    event.reply("Could not find ship with name " + name).setEphemeral(true).queue();
                    return;
                }

                event.deferReply().queue();
                loadNormal(json.getJSONObject("names").getString("en"), json, json.getJSONObject("names"), event.getHook());
            } catch (IOException | JSONException e) {
                event.reply("Error: " + e.getMessage()).setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        try {
            switch (event.getComponentId()) {
                case "normal" -> {
                    JSONArray jsonArray = getJsonArrayFromUrl();
                    JSONObject json = findJsonWithName(jsonArray, name);

                    if (json == null) {
                        event.reply("Could not find ship").setEphemeral(true).queue();
                        return;
                    }
                    event.deferReply().queue();
                    loadNormal(json.getJSONObject("names").getString("en"), json, json.getJSONObject("names"), event.getHook());
                }
                case "retrofit" -> {
                    JSONArray jsonArray = getJsonArrayFromUrl();
                    JSONObject json = findJsonWithName(jsonArray, name);

                    if (json == null) {
                        event.reply("Could not find ship").setEphemeral(true).queue();
                        return;
                    }
                    event.deferReply().queue();
                    loadRetrofit(json.getJSONObject("names").getString("en") + " Retrofit", json, json.getJSONObject("names"), event.getHook());
                }
            }
        } catch (IOException | JSONException e) {
            event.reply("Error: " + e.getMessage()).setEphemeral(true).queue();
        }
    }

    private JSONArray getJsonArrayFromUrl() throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();
        String url = "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/ships.json";
        Request request = new Request.Builder()
            .url(url)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String jsonString = Objects.requireNonNull(response.body()).string();
            return new JSONArray(jsonString);
        }
    }

    private JSONObject findJsonWithName(JSONArray jsonArray, String name) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            String enName = json.getJSONObject("names").getString("en");

            if (enName.equals(name)) {
                return json;
            }
        }

        return null;
    }

    public void loadRetrofit(String en, JSONObject json, JSONObject names, InteractionHook hook) {
        EmbedBuilder builder = createBuilder(en, json, names);

        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.of(ButtonStyle.PRIMARY, "normal", "Normal"));
        hook.editOriginalEmbeds(builder.build()).setActionRow(buttons).queue();
    }

    public void loadNormal(String en, JSONObject json, JSONObject names, InteractionHook hook) {
        EmbedBuilder builder = createBuilder(en, json, names);

        List<Button> buttons = new ArrayList<>();
        if (json.has("retrofit")) {
            buttons.add(Button.of(ButtonStyle.PRIMARY, "retrofit", "Retrofit"));
        }

        if (!buttons.isEmpty()) {
            hook.editOriginalEmbeds(builder.build()).setActionRow(buttons).queue();
        } else {
            hook.editOriginalEmbeds(builder.build()).queue();
        }
    }

    private EmbedBuilder createBuilder(String en, JSONObject json, JSONObject names) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(en);
        builder.addField("Name", names.getString("code"), true);
        builder.addField("Class", json.getString("class"), true);
        builder.addField("Nationality", json.getString("nationality"), true);
        builder.addField("Type", json.getString("hullType"), false);
        builder.addField("Rarity", json.getString("rarity"), true);

        StringBuilder stars = new StringBuilder();
        stars.append("★".repeat(Math.max(0, json.getInt("stars"))));
        if (stars.length() != 6) {
            stars.append("☆");
        }
        builder.addField("Stars", stars.toString(), true);

        builder.setImage(json.getString("thumbnail"));

        return builder;
    }
}
