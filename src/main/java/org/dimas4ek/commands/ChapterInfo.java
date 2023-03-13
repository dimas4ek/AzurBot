package org.dimas4ek.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class ChapterInfo extends ListenerAdapter {
    String chapterNumber;
    public void setChapterNumber(String chapterNumber) {
        this.chapterNumber = chapterNumber;
    }
    String chapterCode;
    public String getChapterCode() {
        return chapterCode;
    }
    public void setChapterCode(String chapterCode) {
        this.chapterCode = chapterCode;
    }
    private Map<String, String> emojis;
    private Map<String, String> shipTypeEmojis;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getFullCommandName().equals("chapter")) {
            Collection<SelectOption> options = new ArrayList<>();
            for (int i = 0; i < 14; i++) {
                options.add(SelectOption.of("Chapter " + (i + 1), "Chapter " + (i + 1)));
            }
            event.reply("Select chapter")
                .addActionRow(
                    StringSelectMenu.create("Chapters")
                        .addOptions(options)
                        .build()
                ).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
            case "ch_normal" -> getJsonArray(event, "normal");
            case "ch_hard" -> getJsonArray(event, "hard");
            case "ch_back" -> {
                Collection<SelectOption> options = new ArrayList<>();
                for (int i = 0; i < 14; i++) {
                    options.add(SelectOption.of("Chapter " + (i + 1), "Chapter " + (i + 1)));
                }
                event.getMessage().delete().queue();
                event.reply("Select chapter")
                    .setEmbeds()
                    .setActionRow(
                        StringSelectMenu.create("Chapters")
                            .addOptions(options)
                            .build()
                    ).queue();
            }
        }
    }

    private void getJsonArray(ButtonInteractionEvent event, String mode) {
        try {
            chapterCode = getChapterCode();
            setChapterCode(chapterCode);
            JSONArray jsonArray = getJsonArrayFromUrl();
            JSONObject json = findJsonWithName(jsonArray, chapterCode);
            List<String> codes = List.of("1", "2", "3", "4");
            System.out.println("length: " + chapterCode.length());
            for (String code : codes) {
                if (chapterCode.length() == 3 && code.equals(chapterCode.substring(2))
                    || chapterCode.length() == 4 && code.equals(chapterCode.substring(3))) {
                    event.getMessage().delete().queue();
                    event.deferReply().complete();
                    assert json != null;
                    getChapter(event.getHook(), json, code, mode);
                    return;
                }
            }
        } catch (IOException | JSONException e) {
            event.reply("Error: " + e.getMessage()).setEphemeral(true).queue();
        }
    }

    @Override
    public void onGenericSelectMenuInteraction(GenericSelectMenuInteractionEvent event) {
        if (event.getComponentId().equals("Chapters")) {
            chapterNumber = event.getValues().get(0).toString().substring(8);
            setChapterNumber(chapterNumber);
            List<SelectOption> options = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                options.add(SelectOption.of(chapterNumber + "-" + (i + 1), chapterNumber + "-" + (i + 1)));
            }
            event.editMessage("Chapter").setComponents(
                ActionRow.of(
                    Button.of(ButtonStyle.PRIMARY, "ch_back", "Back")
                ),
                ActionRow.of(StringSelectMenu.create("chapter")
                    .addOptions(options)
                    .build())
            ).queue();
        }
        if (event.getComponentId().equals(event.getComponentId())) {
            try {
                chapterCode = event.getInteraction().getValues().get(0).toString();
                setChapterCode(chapterCode);
                JSONArray jsonArray = getJsonArrayFromUrl();
                JSONObject json = findJsonWithName(jsonArray, chapterCode);
                List<String> codes = List.of("1", "2", "3", "4");
                System.out.println("length: " + chapterCode.length());
                for (String code : codes) {
                    if (chapterCode.length() == 3 && code.equals(chapterCode.substring(2))
                        || chapterCode.length() == 4 && code.equals(chapterCode.substring(3))) {
                        event.getMessage().delete().queue();
                        event.deferReply().complete();
                        assert json != null;
                        getChapter(event.getHook(), json, code, "normal");
                        return;
                    }
                }
            } catch (IOException | JSONException e) {
                event.reply("Error: " + e.getMessage()).setEphemeral(true).queue();
            }
        }
    }

    private void getChapter(InteractionHook hook, JSONObject json, String code, String mode) {
        JSONObject normal = json.getJSONObject(code).getJSONObject("normal");
        JSONObject hard = json.getJSONObject(code).getJSONObject("hard");

        setEmojis(hook);

        List<String> threeStarRewards = new ArrayList<>();
        for (int i = 0; i < normal.getJSONArray("threeStarRewards").length(); i++) {
            if (normal.getJSONArray("threeStarRewards").getJSONObject(i).has("count")) {
                threeStarRewards.add(normal.getJSONArray("threeStarRewards").getJSONObject(i).getInt("count") + " " + emojis.get(normal.getJSONArray("threeStarRewards").getJSONObject(i).getString("item")));
            } else if (Objects.equals(normal.getJSONArray("threeStarRewards").getJSONObject(i).getString("item"), "Universal Bullin")) {
                threeStarRewards.add("[" + "Universal Bullin" + "](https://azurlane.koumakan.jp/wiki/" + "Universal_Bullin" + ")");
            } else if (Objects.equals(normal.getJSONArray("threeStarRewards").getJSONObject(i).getString("item"), "Trial Bullin MKII")) {
                threeStarRewards.add("[" + "Prototype Bulin MKII" + "](https://azurlane.koumakan.jp/wiki/" + "Prototype_Bulin_MKII" + ") " + emojis.get("Prototype Bulin MKII"));
            } else {
                threeStarRewards.add(normal.getJSONArray("threeStarRewards").getJSONObject(i).getString("item") + " " + emojis.get(normal.getJSONArray("threeStarRewards").getJSONObject(i).getString("item")));
            }
        }
        if (Objects.equals(normal.getString("code"), "6-3")) {
            threeStarRewards.add("T5 Equipment Box " + emojis.get("T5 Equipment Box"));
        }

        List<String> starConditions = new ArrayList<>();
        if (normal.has("starConditions")) {
            for (int i = 0; i < normal.getJSONArray("starConditions").length(); i++) {
                starConditions.add(normal.getJSONArray("starConditions").getString(i));
            }
        }

        List<String> mapDrops = new ArrayList<>();
        for (int i = 0; i < normal.getJSONArray("mapDrops").length(); i++) {
            mapDrops.add(emojis.get(normal.getJSONArray("mapDrops").getString(i)) + " " + normal.getJSONArray("mapDrops").getString(i));
        }
        if (Objects.equals(normal.getString("code"), "13-4")) {
            mapDrops.add("*Clear:* 7-9x " + emojis.get("T1-T2 Upgrade Parts") + "/" + emojis.get("T1-T3 Upgrade Parts") + ",  2650–2900x " + emojis.get("Coins"));
            mapDrops.add("*Chance of getting:* \n20-30 " + emojis.get("Cognitive chip") + ", 3-5x " + emojis.get("T1 Stone") + ", 1x " + emojis.get("T2 Stone") + ", 1x" + emojis.get("Cola"));
        }

        List<String> equipmentBlueprintDrops = new ArrayList<>();
        for (int i = 0; i < normal.getJSONArray("equipmentBlueprintDrops").length(); i++) {
            equipmentBlueprintDrops.add(normal.getJSONArray("equipmentBlueprintDrops").getJSONObject(i).getString("tier") + " " + normal.getJSONArray("equipmentBlueprintDrops").getJSONObject(i).getString("name"));
        }

        List<String> bosses = new ArrayList<>();
        if (normal.getJSONObject("enemyLevel").get("boss").getClass().equals(JSONArray.class)) {
            for (int i = 0; i < normal.getJSONObject("enemyLevel").getJSONArray("boss").length(); i++) {
                bosses.add("[" + normal.getJSONObject("enemyLevel").getJSONArray("boss").getString(i) + "](https://azurlane.koumakan.jp/wiki/" + normal.getJSONObject("enemyLevel").getJSONArray("boss").getString(i) + ")");
            }
        }

        List<String> shipDrops = new ArrayList<>();
        for (int i = 0; i < normal.getJSONArray("shipDrops").length(); i++) {
            if (normal.getJSONArray("shipDrops").get(i).getClass().equals(JSONObject.class)) {
                shipDrops.add(normal.getJSONArray("shipDrops").getJSONObject(i).getString("name") + " (" + normal.getJSONArray("shipDrops").getJSONObject(i).getString("note") + ")");
            } else {
                shipDrops.add(normal.getJSONArray("shipDrops").getString(i));
            }
        }
        Set<String> fleetRestrictions1 = new HashSet<>(hard.getJSONObject("fleetRestrictions").getJSONObject("fleet1").keySet());
        Set<String> shipTypeEmojiSet1 = new HashSet<>();
        Set<String> fleetRestrictions2 = new HashSet<>(hard.getJSONObject("fleetRestrictions").getJSONObject("fleet2").keySet());
        Set<String> shipTypeEmojiSet2 = new HashSet<>();
        setShipTypeEmojis(hook);
        for (String str : fleetRestrictions1) {
            for (int i = 0; i < shipTypeEmojis.size(); i++) {
                if (Objects.equals(str, new ArrayList<>(shipTypeEmojis.keySet()).get(i))) {
                    shipTypeEmojiSet1.add(hard.getJSONObject("fleetRestrictions").getJSONObject("fleet1").getInt(str) + " " + new ArrayList<>(shipTypeEmojis.values()).get(i));
                }
            }
        }
        for (String str : fleetRestrictions2) {
            for (int i = 0; i < shipTypeEmojis.size(); i++) {
                if (Objects.equals(str, new ArrayList<>(shipTypeEmojis.keySet()).get(i))) {
                    shipTypeEmojiSet2.add(hard.getJSONObject("fleetRestrictions").getJSONObject("fleet2").getInt(str) + " " + new ArrayList<>(shipTypeEmojis.values()).get(i));
                }
            }
        }

        Set<String> statRestrictionsFromJson = new HashSet<>(hard.getJSONObject("statRestrictions").keySet());
        Map<String, String> statRestrictionsMap = new HashMap<>();
        statRestrictionsMap.put("averageLevel", "Average level > ");
        statRestrictionsMap.put("firepower", " Total Firepower > ");
        statRestrictionsMap.put("torpedo", "Total Torpedo > ");
        statRestrictionsMap.put("evasion", "Total Evasion > ");
        statRestrictionsMap.put("anti-Air", "Total Anti-Air stat > ");
        statRestrictionsMap.put("aviation", "Total Aviation stat > ");
        Set<String> statRestrictions = new HashSet<>();
        for (String str : statRestrictionsFromJson) {
            for (int i = 0; i < statRestrictionsMap.size(); i++) {
                if (Objects.equals(str, new ArrayList<>(statRestrictionsMap.keySet()).get(i))) {
                    statRestrictions.add(new ArrayList<>(statRestrictionsMap.values()).get(i) + hard.getJSONObject("statRestrictions").getInt(str));
                }
            }
        }

        EmbedBuilder builder = createBuilder(mode, normal, threeStarRewards, bosses, starConditions, mapDrops, equipmentBlueprintDrops, shipDrops, shipTypeEmojiSet1, shipTypeEmojiSet2, statRestrictions);

        if (Objects.equals(mode, "normal")) {
            hook.editOriginal("")
                .setEmbeds(builder.build())
                .setComponents(
                    ActionRow.of(
                        Button.of(ButtonStyle.PRIMARY, "ch_normal", "Normal").asDisabled(),
                        Button.of(ButtonStyle.PRIMARY, "ch_hard", "Hard"),
                        Button.of(ButtonStyle.PRIMARY, "ch_back", "Back")
                    )
                ).queue();
        } else if (Objects.equals(mode, "hard")) {
            hook.editOriginal("")
                .setEmbeds(builder.build())
                .setComponents(
                    ActionRow.of(
                        Button.of(ButtonStyle.PRIMARY, "ch_normal", "Normal"),
                        Button.of(ButtonStyle.PRIMARY, "ch_hard", "Hard").asDisabled(),
                        Button.of(ButtonStyle.PRIMARY, "ch_back", "Back")
                    )
                ).queue();
        }
    }

    private EmbedBuilder createBuilder(String mode, JSONObject normal, List<String> threeStarRewards, List<String> bosses, List<String> starConditions, List<String> mapDrops, List<String> equipmentBlueprintDrops, List<String> shipDrops, Set<String> shipTypeEmojiSet1, Set<String> shipTypeEmojiSet2, Set<String> statRestrictions) {
        EmbedBuilder builder = new EmbedBuilder();

        builder
            .setTitle(String.join("\n", Objects.equals(mode, "normal") ? normal.getString("code") : normal.getString("code") + " Hard", normal.getString("title")))
            .setDescription(normal.getString("introduction"))
            .addField("Unlock Requirements", normal.getJSONObject("unlockRequirements").getString("text"), false);

        if (Objects.equals(mode, "normal")) {
            builder
                .addField("Clear Rewards",
                    String.join("\n",
                        normal.getJSONObject("clearRewards").getInt("cube") + " " + emojis.get("cube"),
                        normal.getJSONObject("clearRewards").getInt("coin") + " " + emojis.get("Coins"),
                        normal.getJSONObject("clearRewards").has("ship")
                            ? "Ship: " + normal.getJSONObject("clearRewards").getString("ship")
                            : normal.getJSONObject("clearRewards").getInt("oil") + " " + emojis.get("oil")
                    ), true);
        }

        builder
            .addField("3 Star Rewards",
                String.join("\n", threeStarRewards), true)
            .addField("Enemy Level",
                String.join("\n",
                    "**Mob**: " + normal.getJSONObject("enemyLevel").getInt("mobLevel"),
                    "**Boss**: " + normal.getJSONObject("enemyLevel").getInt("bossLevel") +
                        (normal.getJSONObject("enemyLevel").get("boss").getClass().equals(JSONArray.class)
                            ? (" (" + String.join(" and ", bosses) + ")")
                            : (" ([" + normal.getJSONObject("enemyLevel").get("boss") + "](https://azurlane.koumakan.jp/wiki/" + normal.getJSONObject("enemyLevel").get("boss") + "))"))
                ), false)
            .addField("Base XP",
                emojis.get("small_fleet") + " " + normal.getJSONObject("baseXP").getInt("smallFleet") + ", " +
                    emojis.get("medium_fleet") + " " + normal.getJSONObject("baseXP").getInt("mediumFleet") + ", " +
                    emojis.get("large_fleet") + " " + normal.getJSONObject("baseXP").getInt("largeFleet") + ", " +
                    emojis.get("boss_fleet") + " " + normal.getJSONObject("baseXP").getInt("bossFleet"), false)
            .addField("Required Battles", normal.getInt("requiredBattles") + " battles before boss appears", true)
            .addField("Boss Kills to Clear", normal.getInt("bossKillsToClear") + " boss battles for 100%", true);

        if (normal.has("starConditions")) {
            builder.addField("Star Conditions", String.join("\n", starConditions), false);
        }

        if (Objects.equals(mode, "hard")) {
            builder
                .addField("Fleet Restrictions",
                    "**Fleet 1:** " + String.join(", ", shipTypeEmojiSet1) +
                        "\n**Fleet 2:** " + String.join(", ", shipTypeEmojiSet2), true)
                .addField("Stat Restriction", String.join("\n", statRestrictions), true);
        }

        if (normal.getJSONObject("airSupremacy").getInt("actual") < 125) {
            builder
                .addField("Airspace Control",
                    String.join("\n",
                        "**Actual**: " + normal.getJSONObject("airSupremacy").getInt("actual"),
                        "**Suggested**: Empty <" + normal.getJSONObject("airSupremacy").getInt("superiority") + ", " +
                            emojis.get("air_superiority") + " " + normal.getJSONObject("airSupremacy").getInt("superiority") + ", " +
                            emojis.get("air_supremacy") + " " + normal.getJSONObject("airSupremacy").getInt("supremacy")), false);
        } else if (normal.getJSONObject("airSupremacy").getInt("actual") < 175 && !Objects.equals(normal.getString("code"), "4-3")) {
            builder
                .addField("Airspace Control",
                    String.join("\n",
                        "**Actual**: " + normal.getJSONObject("airSupremacy").getInt("actual"),
                        "**Suggested**: Empty <" + normal.getJSONObject("airSupremacy").getInt("superiority") + ", " +
                            emojis.get("air_parity") + " " + normal.getJSONObject("airSupremacy").getInt("superiority") + ", " +
                            emojis.get("air_superiority") + " " + normal.getJSONObject("airSupremacy").getInt("supremacy")), false);
        } else if (normal.getJSONObject("airSupremacy").getInt("actual") >= 175 || Objects.equals(normal.getString("code"), "4-3")) {
            builder
                .addField("Airspace Control",
                    String.join("\n",
                        "**Actual**: " + normal.getJSONObject("airSupremacy").getInt("actual"),
                        "**Suggested**: " +
                            emojis.get("air_denial") + " " + normal.getJSONObject("airSupremacy").getInt("superiority") + ", " +
                            emojis.get("air_parity") + " " + normal.getJSONObject("airSupremacy").getInt("supremacy")), false);
        }

        builder
            .addField("Map Drops", String.join("\n", mapDrops), true)
            .addField("Blueprint Drops", normal.getJSONArray("equipmentBlueprintDrops").isEmpty()
                ? "None"
                : String.join("\n", equipmentBlueprintDrops), true)
            .addField("Ship Drops", String.join(", ", shipDrops), false);

        return builder;
    }

    private JSONArray getJsonArrayFromUrl() throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();
        String url = "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/chapters.json";
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
            for (int j = 1; j <= 4; j++) {
                String enName = json.getJSONObject(String.valueOf(j)).getJSONObject("normal").getString("code");
                if (enName.equals(name)) {
                    return json;
                }
            }
        }

        return null;
    }

    public void setEmojis(InteractionHook hook) {
        emojis = new HashMap<>();

        emojis.put("cube", getEmojiById("1084082083363827832", hook));
        emojis.put("Coins", getEmojiById("1084082140918071336", hook));
        emojis.put("Gem", getEmojiById("1084082197050437672", hook));
        emojis.put("oil", getEmojiById("1084084710248022027", hook));

        emojis.put("Cognitive chip", getEmojiById("1084413000766722108", hook));
        emojis.put("Prototype Bulin MKII", getEmojiById("1084413982909141022", hook));
        emojis.put("T1 Stone", getEmojiById("1084416675149009017", hook));
        emojis.put("T2 Stone", getEmojiById("1084416672363987006", hook));
        emojis.put("Cola", getEmojiById("1084416676839305236", hook));

        emojis.put("small_fleet", getEmojiById("1084085454590181448", hook));
        emojis.put("medium_fleet", getEmojiById("1084085451750645800", hook));
        emojis.put("large_fleet", getEmojiById("1084085450207133706", hook));
        emojis.put("boss_fleet", getEmojiById("1084085447396970516", hook));

        emojis.put("air_denial", getEmojiById("1084405494002745344", hook));
        emojis.put("air_parity", getEmojiById("1084405491104481290", hook));
        emojis.put("air_superiority", getEmojiById("1084092555332943952", hook));
        emojis.put("air_supremacy", getEmojiById("1084092552040423524", hook));

        emojis.put("T1 Upgrade Parts", getEmojiById("1084392415022035014", hook));
        emojis.put("T1-T2 Upgrade Parts", getEmojiById("1084392419010818078", hook));
        emojis.put("T1-T3 Upgrade Parts", getEmojiById("1084392416469061672", hook));
        emojis.put("T2-T3 Upgrade Parts", getEmojiById("1084392416469061672", hook));
        emojis.put("T1 Tech Boxes", getEmojiById("1084390367165685800", hook));
        emojis.put("T1-T2 Tech Boxes", getEmojiById("1084390368205869087", hook));
        emojis.put("T1-T3 Tech Boxes", getEmojiById("1084390364661678090", hook));
        emojis.put("T1-T4 Tech Boxes", getEmojiById("1084391273739333662", hook));
        emojis.put("T1 Equipment Box", getEmojiById("1084390367165685800", hook));
        emojis.put("T2 Equipment Box", getEmojiById("1084390368205869087", hook));
        emojis.put("T3 Equipment Box", getEmojiById("1084390364661678090", hook));
        emojis.put("T4 Equipment Box", getEmojiById("1084391273739333662", hook));
        emojis.put("T5 Equipment Box", getEmojiById("1084391272107757568", hook));
    }

    public void setShipTypeEmojis(InteractionHook hook) {
        shipTypeEmojis = new HashMap<>();

        shipTypeEmojis.put("destroyer", getEmojiById("1084533740421791836", hook) + " DD");
        shipTypeEmojis.put("lightCruiser", getEmojiById("1084533743475228672", hook) + " CL");
        shipTypeEmojis.put("heavyCruiser", getEmojiById("1084798467110608968", hook) + " CA");
        shipTypeEmojis.put("aircraftCarrier", getEmojiById("1084533745723392010", hook) + " CV");
        shipTypeEmojis.put("lightAircraftCarrier", getEmojiById("1084798460072558642", hook) + " CVL");
        shipTypeEmojis.put("battleship", getEmojiById("1084798469002240032", hook) + " BB");
        shipTypeEmojis.put("battleCruiser", getEmojiById("1084798464526925824", hook) + " BC");
    }

    private String getEmojiById(String id, InteractionHook hook) {
        return Objects.requireNonNull(hook.getJDA().getEmojiById(id)).getFormatted();
    }
}
