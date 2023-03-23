package org.dimas4ek.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ShipInfo extends ListenerAdapter {

    String name;
    static Map<String, Emoji> emojiCache = new HashMap<>();

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
                //event.reply("Error: " + e.getMessage()).setEphemeral(true).queue();
                System.out.println("Error: " + e.getMessage());
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
                    event.getMessage().delete().queue();
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
                    event.getMessage().delete().queue();
                    event.deferReply().queue();
                    loadRetrofit(json.getJSONObject("names").getString("en") + " Retrofit", json, json.getJSONObject("names"), event.getHook());
                }
            }
        } catch (IOException | JSONException e) {
            //event.reply("Error: " + e.getMessage()).setEphemeral(true).queue();
            System.out.println("Error" + e.getMessage());
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
        EmbedBuilder builder = createBuilder(en, json, names, hook);

        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.of(ButtonStyle.PRIMARY, "normal", "Normal"));


        hook.sendMessageEmbeds(
            builder
                .setThumbnail(json.getJSONArray("skins").getJSONObject(1).getString("chibi"))
                .setImage(json.getJSONArray("skins").getJSONObject(1).getString("image"))
                .build()
        ).setActionRow(buttons).queue();
    }


    public void loadNormal(String en, JSONObject json, JSONObject names, InteractionHook hook) {
        EmbedBuilder builder = createBuilder(en, json, names, hook);

        List<Button> buttons = new ArrayList<>();
        if (json.has("retrofit")) {
            buttons.add(Button.of(ButtonStyle.PRIMARY, "retrofit", "Retrofit"));
        }

        if (!buttons.isEmpty()) {
            hook.sendMessageEmbeds(
                builder
                    .setThumbnail(json.getJSONArray("skins").getJSONObject(0).getString("chibi"))
                    .setImage(json.getString("thumbnail"))
                    .build()
            ).setActionRow(buttons).queue();
        } else {
            hook.sendMessageEmbeds(
                builder
                    .setThumbnail(json.getJSONArray("skins").getJSONObject(0).getString("chibi"))
                    .setImage(json.getString("thumbnail"))
                    .build()
            ).queue();
        }
    }

    private EmbedBuilder createBuilder(String en, JSONObject json, JSONObject names, InteractionHook hook) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(en);
        builder.addField("Name", names.getString("code"), true);
        builder.addField("Class", json.getString("class"), true);
        builder.addField("Nationality", json.getString("nationality"), true);
        builder.addField("Classification", Objects.requireNonNull(hook.getJDA().getEmojiById("1084533740421791836")).getFormatted() + " " + json.getString("hullType"), false);

        StringBuilder stars = new StringBuilder();
        stars.append("★".repeat(Math.max(0, json.getInt("stars"))));
        if (stars.length() != 6) {
            stars.append("☆");
        }
        builder.addField("Rarity", json.getString("rarity") + " " + stars, false);

        JSONArray slots = json.getJSONArray("slots");
        /*String statsBuilder = "```\n" +
            String.format("%s\t\t%s\t\t%s\n", "Slot", "Efficiency", "Equippable") +
            String.format("%s\t\t%s\t\t%s\n", "1", getEfficiency(slots, 1), slots.getJSONObject(0).getString("type")) +
            String.format("%s\t\t%s\t\t%s\n", "2", getEfficiency(slots, 2), slots.getJSONObject(1).getString("type")) +
            String.format("%s\t\t%s\t\t%s\n", "3", getEfficiency(slots, 3), slots.getJSONObject(2).getString("type")) +
            String.format("%s\t\t%s\t\t%s\n", "Augment", "N/A", getAugment(json.getString("hullType"))) +
            "```";*/

        //builder.addField("Gear", statsBuilder, false);

        //builder.addField("Gear\nSlot", String.join("\n", "1", "2", "3", "Augment"), true);
        //builder.addField(EmbedBuilder.ZERO_WIDTH_SPACE + "\nEfficiency", String.join("\n", getEfficiency(slots, 1), getEfficiency(slots, 2), getEfficiency(slots, 3), "N/A"), true);
        //builder.addField(EmbedBuilder.ZERO_WIDTH_SPACE + "\nEquippable", String.join("\n", getEquipabble(slots, 1), getEquipabble(slots, 2), getEquipabble(slots, 3), getAugment(json.getString("hullType"))), true);

        builder.addField("Gear", String.join("\n", "**Slot**", "1", "2", "3", "Augment"), true);
        builder.addField(EmbedBuilder.ZERO_WIDTH_SPACE, String.join("\n", "**Efficiency**", getEfficiency(slots, 1), getEfficiency(slots, 2), getEfficiency(slots, 3), "N/A"), true);
        builder.addField(EmbedBuilder.ZERO_WIDTH_SPACE, String.join("\n", "**Equippable**", getEquippable(slots, 1), getEquippable(slots, 2), getEquippable(slots, 3), getAugment(json.getString("hullType"))), true);

        /*List<String> availableIn = new ArrayList<>(List.of("light", "heavy", "aviation", "limited", "exchange"));
        List<String> availableInValues = new ArrayList<>();
        for (String s : availableIn) {
            if (json.getJSONObject("construction").getJSONObject("availableIn").get(s).getClass() == String.class) {
                availableInValues.add(s.substring(0, 1).toUpperCase() + s.substring(1) + ": " + json.getJSONObject("construction").getJSONObject("availableIn").getString(s));
            } else if (json.getJSONObject("construction").getJSONObject("availableIn").get(s).getClass() == Boolean.class
                && json.getJSONObject("construction").getJSONObject("availableIn").getBoolean(s)) {
                availableInValues.add(s.substring(0, 1).toUpperCase() + s.substring(1) + ": ✓");
            }
        }
        builder.addField("Construction", "Time: " + json.getJSONObject("construction").getString("constructionTime") + "\n"
            + String.join("\n", availableInValues), false);*/

        JSONObject availableIn = json.getJSONObject("construction").getJSONObject("availableIn");
        Map<String, String> availableInValues = new HashMap<>();

        for (String s : availableIn.keySet()) {
            if (availableIn.opt(s) instanceof String) {
                availableInValues.put(s, s.substring(0, 1).toUpperCase() + s.substring(1) + ": " + availableIn.getString(s));
            } else if (availableIn.opt(s) instanceof Boolean) {
                availableInValues.put(s, s.substring(0, 1).toUpperCase() + s.substring(1) + ": ✓");
            }
        }

        StringJoiner sj = new StringJoiner("\n");
        availableInValues.forEach((key, value) -> sj.add(value));
        builder.addField("Construction", json.getJSONObject("construction").optString("constructionTime") + "\n" + sj, false);

        //работает некорректно
        /*User bot = hook.getJDA().getSelfUser();
        List<RichCustomEmoji> emojisList = hook.getJDA().getEmojis();
        Objects.requireNonNull(hook.getJDA().getGuildById("975381997344145448")).retrieveEmojis().complete();
        if (!emojisList.isEmpty()) {
            for (RichCustomEmoji emoji : emojisList) {
                if (emoji.getOwner() == bot) {
                    emoji.delete().queue();
                }
            }
        }
        try {
            List<String> skills = new ArrayList<>();
            JSONArray skillsArray = json.getJSONArray("skills");
            for (int i = 0; i < skillsArray.length(); i++) {
                JSONObject skill = skillsArray.getJSONObject(i);
                Emoji emoji = createEmojiFromUrl(Objects.requireNonNull(hook.getJDA().getGuildById(Main.BOT_GUILD)),
                    skill.getString("icon"), "skill");
                skills.add(emoji.getFormatted() + " **" + skill.getJSONObject("names").getString("en") + "**\n" +
                    skill.getString("description"));
            }
            builder.addField("Skills", String.join("\n", skills), false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        List<String> skills = new ArrayList<>();
        JSONArray skillsArray = json.getJSONArray("skills");
        for (int i = 0; i < skillsArray.length(); i++) {
            JSONObject skill = skillsArray.getJSONObject(i);
            skills.add("**" + skill.getJSONObject("names").getString("en") + "**\n" +
                skill.getString("description"));
        }
        builder.addField("Skills", String.join("\n", skills), false);

        return builder;
    }

    private String getAugment(String hullType) {
        return switch (hullType) {
            case "Destroyer" -> "Hammer, Dual Swords";
            case "Light Cruiser" -> "Crossbow, Sword";
            case "Repair Ship" -> "Crossbow";
            case "Heavy Cruiser", "Large Cruiser", "Munition Ship" -> "Lance, Greatsword";
            case "Monitor" -> "Lance";
            case "Battleship", "Battlecruiser", "Aviation Battleship" -> "Bowgun, Officer's Sword";
            case "Aircraft Carrier", "Light Aircraft Carrier" -> "Scepter, Hunting Bow";
            case "Submarine", "Aviation Submarine" -> "Kunai, Dagger";
            default -> null;
        };
    }

    private String getEfficiency(JSONArray slots, int slot) {
        if (slots.getJSONObject(slot - 1).has("kaiEfficiency"))
            return slots.getJSONObject(slot - 1).getInt("minEfficiency") + " → " + slots.getJSONObject(slot - 1).getInt("maxEfficiency") + " → " + slots.getJSONObject(slot - 1).getInt("kaiEfficiency");
        else
            return slots.getJSONObject(slot - 1).getInt("minEfficiency") + " → " + slots.getJSONObject(slot - 1).getInt("maxEfficiency");
    }

    private String getEquippable(JSONArray slots, int slot) {
        return slots.getJSONObject(slot - 1).getString("type");
    }

    public static Emoji createEmojiFromUrl(Guild guild, String url, String name) throws IOException {
        BufferedImage image = ImageIO.read(new URL(url));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageData = baos.toByteArray();
        Emoji emoji = guild.createEmoji(name, Icon.from(imageData), (Role) null).complete();
        emojiCache.put(url, emoji);

        return emoji;
    }
}
