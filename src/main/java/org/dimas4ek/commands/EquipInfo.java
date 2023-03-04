package org.dimas4ek.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class EquipInfo extends ListenerAdapter {
    Collection<SelectOption> types = new ArrayList<>();
    Collection<SelectOption> nationalities = new ArrayList<>();
    Collection<SelectOption> rarities = new ArrayList<>();
    private final String url = "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/equipments.json";
    String selectedValue;
    List<SelectOption> names;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getFullCommandName().equals("equipment")) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                .url(url)
                .build();

            try (Response response = client.newCall(request).execute()) {
                String jsonString = Objects.requireNonNull(response.body()).string();
                JSONArray jsonArray = new JSONArray(jsonString);
                JSONObject json;

                Set<String> catOptions = new HashSet<>();
                Set<String> natOptions = new HashSet<>();
                List<String> rarOptions = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    json = jsonArray.getJSONObject(i);
                    String category = json.getString("category");
                    String nationality = json.getString("nationality");
                    JSONArray tiers = json.getJSONArray("tiers");
                    JSONObject jsonTiers;
                    /*for (int j = 0; j < tiers.length(); j++) {
                        jsonTiers = tiers.getJSONObject(j);
                        String rarity = *//*jsonTiers.getString("rarity") + " " + *//*jsonTiers.getJSONObject("stars").getString("stars");
                        rarOptions.add(rarity);
                    }*/
                    catOptions.add(category);
                    natOptions.add(nationality);
                }
                catOptions = catOptions.stream().parallel().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
                natOptions = natOptions.stream().parallel().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
                {
                    rarOptions.add("Normal ★");
                    rarOptions.add("Normal ★★");
                    rarOptions.add("Rare ★★★");
                    rarOptions.add("Elite ★★★★");
                    rarOptions.add("Super Rare ★★★★★");
                    rarOptions.add("Ultra Rare ★★★★★★");
                }
                //rarOptions = rarOptions.stream().parallel().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
                for (String option : catOptions) {
                    types.add(SelectOption.of(option, option));
                }
                for (String option : natOptions) {
                    nationalities.add(SelectOption.of(option, option));
                }
                for (String option : rarOptions) {
                    rarities.add(SelectOption.of(option, option));
                }
                ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                service.execute(() -> {
                    if (event.getInteraction().isAcknowledged()) {
                        types.clear();
                        nationalities.clear();
                        rarities.clear();
                    }
                });
                event.reply("Choose category")
                    .addComponents(
                        ActionRow.of(
                            Button.of(ButtonStyle.PRIMARY, "type", "Type").asDisabled(),
                            Button.of(ButtonStyle.PRIMARY, "nationality", "Nationality"),
                            Button.of(ButtonStyle.PRIMARY, "rarity", "Rarity")
                        ),
                        ActionRow.of(
                            StringSelectMenu.create("Type")
                                .addOptions(types)
                                .build()
                        )
                    ).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getUser().isBot()) return;

        switch (event.getComponentId()) {
            case "type", "back" -> event.editMessage("")
                .setComponents(
                    ActionRow.of(
                        Button.of(ButtonStyle.PRIMARY, "type", "Type").asDisabled(),
                        Button.of(ButtonStyle.PRIMARY, "nationality", "Nationality"),
                        Button.of(ButtonStyle.PRIMARY, "rarity", "Rarity")
                    ),
                    ActionRow.of(
                        StringSelectMenu.create("Type")
                            .addOptions(types)
                            .build()
                    )
                ).queue();

            case "nationality" -> event.editMessage("")
                .setComponents(
                    ActionRow.of(
                        Button.of(ButtonStyle.PRIMARY, "type", "Type"),
                        Button.of(ButtonStyle.PRIMARY, "nationality", "Nationality").asDisabled(),
                        Button.of(ButtonStyle.PRIMARY, "rarity", "Rarity")
                    ),
                    ActionRow.of(
                        StringSelectMenu.create("Nationality")
                            .addOptions(nationalities)
                            .build()
                    )
                ).queue();

            case "rarity" -> event.editMessage("")
                .setComponents(
                    ActionRow.of(
                        Button.of(ButtonStyle.PRIMARY, "type", "Type"),
                        Button.of(ButtonStyle.PRIMARY, "nationality", "Nationality"),
                        Button.of(ButtonStyle.PRIMARY, "rarity", "Rarity").asDisabled()
                    ),
                    ActionRow.of(
                        StringSelectMenu.create("Rarity")
                            .addOptions(rarities)
                            .build()
                    )
                ).queue();


            case "page_1" -> event.editMessage(selectedValue)
                .setComponents(
                    ActionRow.of(
                        Button.of(ButtonStyle.PRIMARY, "page_1", "Page 1").asDisabled(),
                        Button.of(ButtonStyle.PRIMARY, "page_2", "Page 2"),
                        Button.of(ButtonStyle.PRIMARY, "back", "Back")
                    ),
                    ActionRow.of(StringSelectMenu.create(selectedValue).addOptions(names.subList(0, names.size() / 2)).build())
                ).queue();

            case "page_2" -> event.editMessage(selectedValue)
                .setComponents(
                    ActionRow.of(
                        Button.of(ButtonStyle.PRIMARY, "page_1", "Page 1"),
                        Button.of(ButtonStyle.PRIMARY, "page_2", "Page 2").asDisabled(),
                        Button.of(ButtonStyle.PRIMARY, "back", "Back")
                    ),
                    ActionRow.of(StringSelectMenu.create(selectedValue).addOptions(names.subList(names.size() / 2, names.size())).build())
                ).queue();

            //default -> event.reply("Unknown button").setEphemeral(true).queue();
        }
    }


    @Override
    public void onGenericSelectMenuInteraction(GenericSelectMenuInteractionEvent event) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(url)
            .build();


        selectedValue = event.getValues().get(0).toString();
        names = new ArrayList<>();

        try (Response response = client.newCall(request).execute()) {
            String jsonString = Objects.requireNonNull(response.body()).string();
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject json;
            for (int i = 0; i < jsonArray.length(); i++) {
                json = jsonArray.getJSONObject(i);
                String category = json.getString("category");
                String nationality = json.getString("nationality");
                //доработать
                JSONArray tiers = json.getJSONArray("tiers");
                String fullRarity = null;
                String stars = null;
                String rarity = null;
                for (int j = 0; j < tiers.length(); j++) {
                    JSONObject jsonTiers = tiers.getJSONObject(j);
                    rarity = jsonTiers.getString("rarity");
                    stars = jsonTiers.getJSONObject("stars").getString("stars");
                    fullRarity = rarity + " " + stars;
                }
                String name = json.getJSONObject("names").getString("en");

                if (event.getComponentId().equals("Type")) {
                    if (Objects.equals(selectedValue, category)) {
                        names.add(SelectOption.of(name, name));
                    }
                }
                if (event.getComponentId().equals("Nationality")) {
                    if (Objects.equals(selectedValue, nationality)) {
                        names.add(SelectOption.of(name, name));
                    }
                }

                if (event.getComponentId().equals(event.getComponentId())) {
                    String selectedEquip = event.getValues().get(0).toString();
                    JSONObject misc = json.getJSONObject("misc");
                    String notes = misc.getString("notes");
                    JSONObject stats = null;
                    for (int j = 0; j < tiers.length(); j++) {
                        stats = tiers.getJSONObject(j).getJSONObject("stats");
                    }
                    if (Objects.equals(name, selectedEquip)) {
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setTitle(selectedEquip)
                            .addField("Type", category, false)
                            .addField("Rarity", String.join("\n", capitalizeFirstLetter(rarity), stars), false)
                            .addField("Nation", capitalizeFirstLetter(nationality), false)
                            .addField("Stats", createStatsString(Objects.requireNonNull(stats)), true);

                        List<String> usedBy = new ArrayList<>();
                        String[] shipTypes = {
                            "destroyer", "lightCruiser", "heavyCruiser", "monitor", "largeCruiser",
                            "battleship", "battlecruiser", "aviationBattleship", "aircraftCarrier",
                            "lightCarrier", "repairShip", "munitionShip", "submarine", "submarineCarrier", "sailingFrigate"};

                        for (String shipType : shipTypes) {
                            String equipped = equippedBy(json, shipType);
                            if (equipped != null) {
                                String[] substrings = equipped.split("(?<=\\p{Lower})(?=\\p{Upper})");
                                usedBy.add(Arrays.stream(substrings)
                                    .map(str -> Character.toUpperCase(str.charAt(0)) + str.substring(1))
                                    .collect(Collectors.joining(" ")));
                            }
                        }
                        builder.addField("Used by", String.join("\n", usedBy), true);

                        List<String> miscList = new ArrayList<>();

                        if (!Objects.equals(notes, "")) {
                            miscList.add("*Notes:* " + notes);
                        }

                        String[] substrings = misc.getString("obtainedFrom").split("(?<=[a-z])(?=[A-Z]\\s*)");
                        if (substrings.length == 1) {
                            miscList.add("*Obtained from:* " + misc.getString("obtainedFrom"));
                        } else {
                            StringBuilder result = new StringBuilder("*Obtained from:*\n");
                            for (String substring : substrings) {
                                result.append(substring).append("\n");
                            }
                            miscList.add(result.toString());
                        }

                        if (!misc.getJSONArray("madeFrom").isEmpty()) {
                            miscList.add("*Made from:* " + misc.getJSONArray("madeFrom").toString());
                        }
                        if (!misc.getJSONArray("usedFor").isEmpty()) {
                            miscList.add("*Used for:* " + misc.getJSONArray("usedFor").toString());
                        }

                        builder.addField("Misc", miscList.stream().filter(Objects::nonNull).collect(Collectors.joining("\n")), false);

                        if (!Objects.equals(misc.getString("animation"), "")) {
                            builder.setImage(misc.getString("animation"));
                        }
                        builder.setThumbnail(json.getString("image"));

                        event.editMessage("").setEmbeds(builder.build()).queue();
                    }
                }
                //доработать
                /*if (event.getComponentId().equals("Rarity")) {
                    if (Objects.equals(selectedValue, rarity)) {
                        names.add(SelectOption.of(name, name));
                    }
                }*/
            }
            if (names.size() > 25) {
                event.editMessage(selectedValue)
                    .setComponents(
                        ActionRow.of(
                            Button.of(ButtonStyle.PRIMARY, "page_1", "Page 1").asDisabled(),
                            Button.of(ButtonStyle.PRIMARY, "page_2", "Page 2"),
                            Button.of(ButtonStyle.PRIMARY, "back", "Back")
                        ),
                        ActionRow.of(StringSelectMenu.create(selectedValue).addOptions(names.subList(0, names.size() / 2)).build())
                    ).queue();
            } else {
                event.editMessage(selectedValue)
                    .setComponents(
                        ActionRow.of(Button.of(ButtonStyle.PRIMARY, "back", "Back")),
                        ActionRow.of(StringSelectMenu.create(selectedValue).addOptions(names).build())
                    ).queue();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

   /* private String createStatsString(JSONObject stats) {
        StringBuilder sb = new StringBuilder();
        Set<String> stat = stats.keySet();
        for (int i = 0; i < stat.size(); i++) {
            sb.append(new ArrayList<>(stat).get(i)).append(": ").append(stats.getJSONObject(new ArrayList<>(stat).get(i)).getString("formatted")).append("\n");
        }

        return sb.toString();
    }*/

    private String createStatsString(JSONObject stats) {
        StringBuilder sb = new StringBuilder();
        for (String key : stats.keySet()) {
            String formattedKey = key.replaceAll("(?<=[a-z])(?=[A-Z]\\s*)", " ");
            String formattedValue = stats.getJSONObject(key).getString("formatted");
            sb.append(formattedKey).append(": ").append(formattedValue).append("\n");
        }

        return sb.toString();
    }


    public String equippedBy(JSONObject json, String shipType) {
        if (equals(json, shipType, "primary")) return shipType + ": ✔";
        if (equals(json, shipType, "secondary")) return shipType + ": ⚪";
        return null;
    }

    public boolean equals(JSONObject json, String optString, String check) {
        return (Objects.equals(json.getJSONObject("fits").optString(optString), check));
    }
}
