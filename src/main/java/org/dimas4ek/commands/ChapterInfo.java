package org.dimas4ek.commands;

import com.google.gson.internal.LinkedTreeMap;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.dimas4ek.entity.Chapter;
import org.dimas4ek.entity.Chapter.ChapterMap;
import org.dimas4ek.entity.Chapter.ChapterMap.Difficulty;
import org.dimas4ek.entity.Chapter.ChapterMap.Difficulty.*;
import org.dimas4ek.entity.Chapter.ChapterMap.Difficulty.FleetRestrictions.Fleet;
import org.dimas4ek.utils.JsonUtils;

import java.util.*;

public class ChapterInfo extends ListenerAdapter {
    String chapterId;
    String chapterCode;
    private Map<String, String> emojis;
    private Map<String, String> shipTypeEmojis;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getFullCommandName().equals("chapter")) {
            OptionMapping chapterOption = event.getOption("chapter");

            chapterId = Objects.requireNonNull(chapterOption).getAsString().substring(0, chapterOption.getAsString().indexOf("-"));
            chapterCode = Objects.requireNonNull(chapterOption).getAsString().substring(chapterOption.getAsString().indexOf("-") + 1);

            Chapter chapter = findChapterWithCode(chapterId);

            if (chapter == null) {
                event.reply("Could not find chapter " + chapterOption).setEphemeral(true).queue();
                return;
            }

            setEmojis(event.getGuild());
            setShipTypeEmojis(event.getGuild());

            EmbedBuilder builder = loadChapter(chapter, false);
            event.replyEmbeds(builder.build())
                    .setActionRow(
                            Button.of(ButtonStyle.PRIMARY, "ch_normal", "Normal").asDisabled(),
                            Button.of(ButtonStyle.PRIMARY, "ch_hard", "Hard")
                    )
                    .queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getUser().isBot()) return;

        Chapter chapter = findChapterWithCode(chapterId);

        switch (event.getComponentId()) {
            case "ch_normal" -> event.getInteraction().deferEdit()
                    .setEmbeds(loadChapter(chapter, false).build())
                    .setActionRow(
                            Button.of(ButtonStyle.PRIMARY, "ch_normal", "Normal").asDisabled(),
                            Button.of(ButtonStyle.PRIMARY, "ch_hard", "Hard")
                    )
                    .queue();
            case "ch_hard" -> event.getInteraction().deferEdit()
                    .setEmbeds(loadChapter(chapter, true).build())
                    .setActionRow(
                            Button.of(ButtonStyle.PRIMARY, "ch_normal", "Normal"),
                            Button.of(ButtonStyle.PRIMARY, "ch_hard", "Hard").asDisabled()
                    )
                    .queue();
        }
    }

    private EmbedBuilder loadChapter(Chapter chapter, boolean isHard) {
        ChapterMap chapterMap = null;
        switch (chapterCode) {
            case "1" -> chapterMap = chapter.getMap1();
            case "2" -> chapterMap = chapter.getMap2();
            case "3" -> chapterMap = chapter.getMap3();
            case "4" -> chapterMap = chapter.getMap4();
        }

        Difficulty mode = isHard ? chapterMap.getHard() : chapterMap.getNormal();

        String unlockRequirements = mode.getUnlockRequirements().getText();
        String threeStarRewards = String.join("\n", getThreeStarRewards(mode.getThreeStarRewards()));
        String enemyLevel = String.join("\n", getEnemyLevel(mode.getEnemyLevel()));
        String baseXp = getBaseXp(mode.getBaseXP());
        int requiredBattles = mode.getRequiredBattles();
        int bossKills = mode.getBossKillsToClear();
        String starConditions = String.join("\n", mode.getStarConditions());
        String airSpaceControl = String.join("\n", getAirSpaceControl(mode.getAirSupremacy()));
        String mapDrops = String.join("\n", getMapDrops(mode.getMapDrops()));
        String equipBpDrops = String.join("\n", getEquipBpDrops(mode.getEquipmentBlueprintDrops()));
        String shipDrops = String.join(", ", getShipDrops(mode.getShipDrops()));

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(String.join("\n", mode.getCode(), chapterMap.getName()));
        builder.setDescription(mode.getIntroduction());

        builder.addField("Unlock Requirements", unlockRequirements, false);
        if (!isHard) {
            String clearRewards = String.join("\n", getClearRewards(mode.getClearRewards()));
            builder.addField("Clear Rewards", clearRewards, true);
        }
        builder.addField("3 Star Rewards", threeStarRewards, true);
        builder.addField("Enemy Level", enemyLevel, false);
        builder.addField("Base XP", baseXp, false);
        builder.addField("Required Battles", requiredBattles + " battles before boss appears", true);
        builder.addField("Boss Kills to Clear", bossKills + " boss battles for 100%", true);
        builder.addField("Star Conditions", starConditions, false);
        if (isHard) {
            String fleetRestrictions1 = "Fleet 1: " + String.join(", ", setFleetRestrictionEmojis(mode.getFleetRestrictions().getFleet1()));
            String fleetRestrictions2 = "Fleet 2: " + String.join(", ", setFleetRestrictionEmojis(mode.getFleetRestrictions().getFleet2()));
            String fleetRestrictionsAll = String.join("\n", fleetRestrictions1, fleetRestrictions2);

            String statRestrictions = String.join("\n", getStatRestrictions(mode.getStatRestrictions().getAll()));
            builder.addField("Fleet restrictions", fleetRestrictionsAll, true);
            builder.addField("Stat restrictions", statRestrictions, true);
        }
        builder.addField("Airspace Control", airSpaceControl, false);
        builder.addField("Map Drops", mapDrops, true);
        builder.addField("Blueprints Drops", equipBpDrops, true);
        builder.addField("Ship Drops", shipDrops, false);

        return builder;
    }

    private List<String> getAirSpaceControl(AirSupremacy airSupremacy) {
        List<String> airSpaceControl = new ArrayList<>();
        airSpaceControl.add("Actual: " + airSupremacy.getActual());
        if (airSupremacy.getActual() < 150) {
            airSpaceControl.add("Suggested: Empty < 150, " +
                    emojis.get("air_superiority") + " " + airSupremacy.getSuperiority() + ", " +
                    emojis.get("air_supremacy") + " " + airSupremacy.getSupremacy());
        } else {
            airSpaceControl.add("Suggested: " +
                    emojis.get("air_superiority") + " " + airSupremacy.getSuperiority() + ", " +
                    emojis.get("air_supremacy") + " " + airSupremacy.getSupremacy());
        }

        return airSpaceControl;
    }

    private String getBaseXp(BaseXP baseXP) {
        StringBuilder sb = new StringBuilder();
        sb.append(emojis.get("small_fleet")).append(" ").append(baseXP.getSmallFleet()).append(", ");
        sb.append(emojis.get("medium_fleet")).append(" ").append(baseXP.getMediumFleet()).append(", ");
        sb.append(emojis.get("large_fleet")).append(" ").append(baseXP.getLargeFleet()).append(", ");
        sb.append(emojis.get("boss_fleet")).append(" ").append(baseXP.getBossFleet());

        return sb.toString();
    }

    private List<String> getEnemyLevel(EnemyLevel enemyLevel) {
        List<String> enemyLevelString = new ArrayList<>();
        List<String> bosses = getBosses(enemyLevel);

        enemyLevelString.add("**Mob**: " + enemyLevel.getMobLevel());
        if (bosses.size() == 1) {
            String bossString = " ([" + bosses.get(0) + "](https://azurlane.koumakan.jp/wiki/" + bosses.get(0) + "))";
            enemyLevelString.add("**Boss**: " + enemyLevel.getBossLevel() + bossString);
        } else {
            List<String> bossesStringList = new ArrayList<>();
            for (String boss : bosses) {
                bossesStringList.add("[" + boss + "](https://azurlane.koumakan.jp/wiki/" + boss + ")");
            }
            String bossesString = "(" + String.join(" and ", bossesStringList) + ")";
            enemyLevelString.add("**Boss**: " + enemyLevel.getBossLevel() + " " + bossesString);
        }

        return enemyLevelString;
    }

    private List<String> getClearRewards(ClearRewards clearRewards) {
        List<String> clearRewardsString = new ArrayList<>();
        if (clearRewards.getCube() > 0) {
            clearRewardsString.add(clearRewards.getCube() + " " + emojis.get("cube"));
        }
        if (clearRewards.getCoin() > 0) {
            clearRewardsString.add(clearRewards.getCoin() + " " + emojis.get("Coins"));
        }
        if (clearRewards.getOil() > 0) {
            clearRewardsString.add(clearRewards.getOil() + " " + emojis.get("oil"));
        }
        if (clearRewards.getShip() != null) {
            clearRewardsString.add("[" + clearRewards.getShip() + "](" + "https://azurlane.koumakan.jp/wiki/" + clearRewards.getShip() + ")");
        }

        return clearRewardsString;
    }

    private List<String> getStatRestrictions(Map<String, Integer> statRestrictions) {
        List<String> statRestrictionsString = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : statRestrictions.entrySet()) {
            statRestrictionsString.add(entry.getKey() + entry.getValue());
        }

        return statRestrictionsString;
    }

    private List<String> setFleetRestrictionEmojis(Fleet fleet) {
        List<String> shipTypeEmojisList = new ArrayList<>();
        for (String str : fleet.getAll()) {
            for (int i = 0; i < shipTypeEmojis.size(); i++) {
                if (str.equals(new ArrayList<>(shipTypeEmojis.keySet()).get(i))) {
                    shipTypeEmojisList.add(fleet.get(str) + " " + new ArrayList<>(shipTypeEmojis.values()).get(i));
                }
            }
        }

        return shipTypeEmojisList;
    }

    private static List<String> getShipDrops(Object shipDrops) {
        List<String> shipDropsString = new ArrayList<>();
        if (shipDrops instanceof ArrayList<?> arrayList) {
            for (Object obj : arrayList) {
                if (obj instanceof LinkedTreeMap<?, ?> linkedTreeMap) {
                    shipDropsString.add(linkedTreeMap.get("name") + " (" + linkedTreeMap.get("note") + ")");
                } else {
                    shipDropsString.add((String) obj);
                }
            }
        }
        return shipDropsString;
    }

    private List<String> getBosses(EnemyLevel enemyLevel) {
        List<String> bosses = new ArrayList<>();
        if (enemyLevel.getBoss() instanceof ArrayList<?> bossList) {
            for (Object boss : bossList) {
                bosses.add((String) boss);
            }
        } else {
            bosses.add((String) enemyLevel.getBoss());
        }
        return bosses;
    }

    private List<String> getEquipBpDrops(List<EquipmentBlueprintDrop> equipBpDrops) {
        if (equipBpDrops.isEmpty()) return Collections.singletonList("None");

        List<String> equipBpDropsString = new ArrayList<>();
        for (EquipmentBlueprintDrop drop : equipBpDrops) {
            equipBpDropsString.add(drop.getTier() + " " + drop.getName());
        }
        return equipBpDropsString;
    }

    private List<String> getMapDrops(List<String> mapDrops) {
        List<String> mapDropsString = new ArrayList<>();
        for (String mapDrop : mapDrops) {
            if (mapDrop.contains("Core Data")) {
                mapDropsString.add(emojis.get("Core Data") + " " + mapDrop);
            } else {
                mapDropsString.add(emojis.get(mapDrop) + " " + mapDrop);
            }
        }
        if (chapterId.equals("13") && chapterCode.equals("4")) {
            mapDropsString.add("*Clear:* 7-9x " + emojis.get("T1-T2 Upgrade Parts") + "/" + emojis.get("T1-T3 Upgrade Parts") + ",  2650–2900x " + emojis.get("Coins"));
            mapDropsString.add("*Chance of getting:* \n20-30 " + emojis.get("Cognitive chip") + ", 3-5x " + emojis.get("T1 Stone") + ", 1x " + emojis.get("T2 Stone") + ", 1x" + emojis.get("Cola"));
        }
        return mapDropsString;
    }

    private List<String> getThreeStarRewards(List<ThreeStarReward> threeStarRewards) {
        List<String> threeStarRewardsString = new ArrayList<>();
        for (ThreeStarReward threeStarReward : threeStarRewards) {
            if (threeStarReward.getCount() != null) {
                threeStarRewardsString.add(threeStarReward.getCount() + " " + emojis.get(threeStarReward.getItem()));
            } else if (threeStarReward.getItem().equals("Universal Bullin")) {
                threeStarRewardsString.add("Universal Bullin" + " " + emojis.get("Universal Bullin"));
            } else if (threeStarReward.getItem().equals("Trial Bullin MKII")) {
                threeStarRewardsString.add("Prototype Bulin MKII" + " " + emojis.get("Prototype Bulin MKII"));
            } else {
                threeStarRewardsString.add(threeStarReward.getItem() + " " + emojis.get(threeStarReward.getItem()));
            }
        }
        if (chapterId.equals("6") && chapterCode.equals("3")) {
            threeStarRewardsString.add("T5 Equipment Box " + " " + emojis.get("T5 Equipment Box"));
        }

        return threeStarRewardsString;
    }

    private Chapter findChapterWithCode(String chapterCode) {
        for (Chapter chapter : JsonUtils.getChapterData()) {
            if (chapter.getId().equals(chapterCode)) {
                return chapter;
            }
        }
        return null;
    }

    public void setEmojis(Guild guild) {
        emojis = new HashMap<>();

        emojis.put("cube", getEmojiById("1084082083363827832", guild));
        emojis.put("Coins", getEmojiById("1084082140918071336", guild));
        emojis.put("Gem", getEmojiById("1084082197050437672", guild));
        emojis.put("oil", getEmojiById("1084084710248022027", guild));

        emojis.put("Core Data", getEmojiById("1146098145990819862", guild));
        emojis.put("Cognitive chip", getEmojiById("1084413000766722108", guild));
        emojis.put("Universal Bullin", getEmojiById("1144320174971965440", guild));
        emojis.put("Prototype Bulin MKII", getEmojiById("1084413982909141022", guild));
        emojis.put("T1 Stone", getEmojiById("1084416675149009017", guild));
        emojis.put("T2 Stone", getEmojiById("1084416672363987006", guild));
        emojis.put("Cola", getEmojiById("1084416676839305236", guild));

        emojis.put("small_fleet", getEmojiById("1084085454590181448", guild));
        emojis.put("medium_fleet", getEmojiById("1084085451750645800", guild));
        emojis.put("large_fleet", getEmojiById("1084085450207133706", guild));
        emojis.put("boss_fleet", getEmojiById("1084085447396970516", guild));

        emojis.put("air_denial", getEmojiById("1084405494002745344", guild));
        emojis.put("air_parity", getEmojiById("1084405491104481290", guild));
        emojis.put("air_superiority", getEmojiById("1084092555332943952", guild));
        emojis.put("air_supremacy", getEmojiById("1084092552040423524", guild));

        emojis.put("T1 Upgrade Parts", getEmojiById("1084392415022035014", guild));
        emojis.put("T1-T2 Upgrade Parts", getEmojiById("1084392419010818078", guild));
        emojis.put("T1-T3 Upgrade Parts", getEmojiById("1084392416469061672", guild));
        emojis.put("T2-T3 Upgrade Parts", getEmojiById("1084392416469061672", guild));
        emojis.put("T1 Tech Boxes", getEmojiById("1084390367165685800", guild));
        emojis.put("T1-T2 Tech Boxes", getEmojiById("1084390368205869087", guild));
        emojis.put("T1-T3 Tech Boxes", getEmojiById("1084390364661678090", guild));
        emojis.put("T1-T4 Tech Boxes", getEmojiById("1084391273739333662", guild));
        emojis.put("T1 Equipment Box", getEmojiById("1084390367165685800", guild));
        emojis.put("T2 Equipment Box", getEmojiById("1084390368205869087", guild));
        emojis.put("T3 Equipment Box", getEmojiById("1084390364661678090", guild));
        emojis.put("T4 Equipment Box", getEmojiById("1084391273739333662", guild));
        emojis.put("T5 Equipment Box", getEmojiById("1084391272107757568", guild));
        emojis.put("T1-T3 Battleship Retrofit Blueprint", getEmojiById("1146098149346246739", guild));
        emojis.put("T3 Battleship Retrofit Blueprint", getEmojiById("1146098149346246739", guild));
    }

    public void setShipTypeEmojis(Guild guild) {
        shipTypeEmojis = new HashMap<>();

        shipTypeEmojis.put("destroyer", getEmojiById("1084533740421791836", guild) + " DD");
        shipTypeEmojis.put("lightCruiser", getEmojiById("1084533743475228672", guild) + " CL");
        shipTypeEmojis.put("heavyCruiser", getEmojiById("1084798467110608968", guild) + " CA");
        shipTypeEmojis.put("aircraftCarrier", getEmojiById("1084533745723392010", guild) + " CV");
        shipTypeEmojis.put("lightAircraftCarrier", getEmojiById("1084798460072558642", guild) + " CVL");
        shipTypeEmojis.put("battleship", getEmojiById("1084798469002240032", guild) + " BB");
        shipTypeEmojis.put("battleCruiser", getEmojiById("1084798464526925824", guild) + " BC");
    }

    private String getEmojiById(String id, Guild guild) {
        return Objects.requireNonNull(guild.getEmojiById(id)).getFormatted();
    }
}
