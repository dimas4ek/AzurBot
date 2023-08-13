package org.dimas4ek.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.dimas4ek.entity.Equip;
import org.dimas4ek.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class EquipInfo extends ListenerAdapter {
    Collection<SelectOption> types = new ArrayList<>();
    Collection<SelectOption> nationalities = new ArrayList<>();
    Collection<SelectOption> rarities = new ArrayList<>();
    
    String selectedValue;
    StringSelectMenu equipSelectMenu;
    Equip selectedEquip;
    List<Button> tierButtons;
    List<LayoutComponent> components;
    String selectedCategory;
    List<LayoutComponent> categoryComponents;
    
    List<List<SelectOption>> pages;
    List<Button> pageButtons;
    List<Button> pageButtons2;
    int pagesCount;
    
    public EquipInfo() {
        Equip[] equips = JsonUtils.getEquipData();
        
        Set<String> catOptions = new HashSet<>();
        Set<String> natOptions = new HashSet<>();
        
        for (Equip equip : equips) {
            String category = equip.getCategory();
            String nationality = equip.getNationality();
            
            catOptions.add(category);
            natOptions.add(nationality);
        }
        
        catOptions = catOptions.stream().parallel().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        natOptions = natOptions.stream().parallel().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        
        List<String> rarOptions = Arrays.asList(
            "Normal ★", "Normal ★★", "Rare ★★★",
            "Elite ★★★★", "Super Rare ★★★★★", "Ultra Rare ★★★★★★"
        );
        
        for (String option : catOptions) {
            types.add(SelectOption.of(option, option));
        }
        for (String option : natOptions) {
            nationalities.add(SelectOption.of(option, option));
        }
        for (String option : rarOptions) {
            rarities.add(SelectOption.of(option, option));
        }
    }
    
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getFullCommandName().equals("equipment")) {
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
                )
                .queue();
        }
    }
    
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getUser().isBot()) return;
        
        switch (event.getComponentId()) {
            case "back" -> handleBackButton(event);
            
            case "type" -> handleCategoryButton(event, "type");
            case "nationality" -> handleCategoryButton(event, "nationality");
            case "rarity" -> handleCategoryButton(event, "rarity");
            
            case "tierType1" -> handleTierButton(event, 1);
            case "tierType2" -> handleTierButton(event, 2);
            case "tierType3" -> handleTierButton(event, 3);
        }
        
        handlePageButtons(event);
    }
    
    @Override
    public void onGenericSelectMenuInteraction(GenericSelectMenuInteractionEvent event) {
        selectedValue = event.getValues().get(0).toString();
        Equip[] equipData = JsonUtils.getEquipData();
        
        switch (event.getComponentId()) {
            case "Type", "Nationality", "Rarity" -> {
                Set<SelectOption> options = filteredOptions(event.getComponentId(), equipData);
                createPages(event.getInteraction(), options);
            }
        }
        
        for (Equip equip : equipData) {
            if (selectedValue.equals(equip.getName())) {
                selectedEquip = equip;
                
                event.deferEdit()
                    .setSuppressEmbeds(false)
                    .setEmbeds(createEmbed(equip, 1).build())
                    .setComponents(components)
                    .queue();
                
                break;
            }
        }
    }
    
    private void createCategoryComponents(String category, List<LayoutComponent> categoryComponents) {
        Button typeButton = Button.of(ButtonStyle.PRIMARY, "type", "Type");
        Button nationalityButton = Button.of(ButtonStyle.PRIMARY, "nationality", "Nationality");
        Button rarityButton = Button.of(ButtonStyle.PRIMARY, "rarity", "Rarity");
        
        switch (category) {
            case "type" -> {
                categoryComponents.add(
                    ActionRow.of(
                        typeButton.asDisabled(),
                        nationalityButton,
                        rarityButton
                    )
                );
                categoryComponents.add(
                    ActionRow.of(
                        StringSelectMenu.create("Type")
                            .addOptions(types)
                            .build()
                    )
                );
            }
            case "nationality" -> {
                categoryComponents.add(
                    ActionRow.of(
                        typeButton,
                        nationalityButton.asDisabled(),
                        rarityButton
                    )
                );
                categoryComponents.add(
                    ActionRow.of(
                        StringSelectMenu.create("Nationality")
                            .addOptions(nationalities)
                            .build()
                    )
                );
            }
            case "rarity" -> {
                categoryComponents.add(
                    ActionRow.of(
                        typeButton,
                        nationalityButton,
                        rarityButton.asDisabled()
                    )
                );
                categoryComponents.add(
                    ActionRow.of(
                        StringSelectMenu.create("Rarity")
                            .addOptions(rarities)
                            .build()
                    )
                );
            }
        }
    }
    
    private void createPages(SelectMenuInteraction<?, ?> interaction, Set<SelectOption> options) {
        int pageSize = 25;
        pages = new ArrayList<>();
        pageButtons = new ArrayList<>();
        List<SelectOption> optionsList = new ArrayList<>(options);
        optionsList.sort(Comparator.comparing(SelectOption::getValue));
        pagesCount = (int) Math.ceil(optionsList.size() / (double) pageSize);
        for (int i = 0; i < optionsList.size(); i += pageSize) {
            pages.add(optionsList.subList(i, Math.min(i + pageSize, optionsList.size())));
        }
        
        equipSelectMenu = StringSelectMenu.create(selectedValue).addOptions(pages.get(0)).build();
        
        if (pages.size() > 1) {
            if (pages.size() > 5) {
                for (int i = 1; i <= 5; i++) {
                    Button button = Button.of(ButtonStyle.PRIMARY, "page_" + i, "Page " + i);
                    if (i == 1) {
                        button = button.asDisabled();
                    }
                    pageButtons.add(button);
                }
                pageButtons2 = new ArrayList<>();
                for (int i = 6; i <= pagesCount; i++) {
                    Button button = Button.of(ButtonStyle.PRIMARY, "page_" + i, "Page " + i);
                    pageButtons2.add(button);
                }
                interaction.editMessage(selectedValue)
                    .setSuppressEmbeds(true)
                    .setComponents(
                        ActionRow.of(pageButtons),
                        ActionRow.of(pageButtons2),
                        ActionRow.of(Button.of(ButtonStyle.PRIMARY, "back", "Back")),
                        ActionRow.of(equipSelectMenu)
                    )
                    .queue();
            } else {
                for (int i = 1; i <= pagesCount; i++) {
                    Button button = Button.of(ButtonStyle.PRIMARY, "page_" + i, "Page " + i);
                    if (i == 1) {
                        button = button.asDisabled();
                    }
                    pageButtons.add(button);
                }
                interaction.editMessage(selectedValue)
                    .setSuppressEmbeds(true)
                    .setComponents(
                        ActionRow.of(pageButtons),
                        ActionRow.of(Button.of(ButtonStyle.PRIMARY, "back", "Back")),
                        ActionRow.of(equipSelectMenu)
                    )
                    .queue();
            }
        } else {
            interaction.editMessage(selectedValue)
                .setSuppressEmbeds(true)
                .setComponents(
                    ActionRow.of(Button.of(ButtonStyle.PRIMARY, "back", "Back")),
                    ActionRow.of(equipSelectMenu)
                )
                .queue();
        }
    }
    
    private EmbedBuilder createEmbed(Equip equip, int tier) {
        List<Equip.Tier> tiers = equip.getTiers();
        
        String rarity;
        String stars;
        Equip.Tier.Stats stats;
        
        if (equip.getTiers().size() == 2 && tier == 3) {
            rarity = tiers.get(tier - 2).getRarity();
            stars = tiers.get(tier - 2).getStars().getStars();
            stats = tiers.get(tier - 2).getStats();
        } else {
            rarity = tiers.get(tier - 1).getRarity();
            stars = tiers.get(tier - 1).getStars().getStars();
            stats = tiers.get(tier - 1).getStats();
        }
        
        List<String> usedBy = new ArrayList<>();
        for (Map.Entry<String, String> fit : equip.getFits().getAll().entrySet()) {
            if (fit.getKey() != null) {
                if (fit.getKey().equals("primary")) {
                    usedBy.add(fit.getValue() + ": ✔");
                } else {
                    usedBy.add(fit.getValue() + ": ○");
                }
            }
        }
        
        List<String> misc = new ArrayList<>();
        
        if (!equip.getMisc().getNotes().isEmpty()) {
            misc.add("*Notes*\n" + equip.getMisc().getNotes());
        }
        
        if (!equip.getMisc().getObtainedFrom().isEmpty()) {
            misc.add("*Obtained from*\n" + equip.getMisc().getObtainedFrom());
        }
        
        Set<String> madeFromList = equip.getMisc().getMadeFrom();
        if (!madeFromList.isEmpty()) {
            misc.add("*Made from*\n" + generateFormattedString(madeFromList));
        }
        
        Set<String> usedForList = equip.getMisc().getUsedFor();
        if (!usedForList.isEmpty()) {
            misc.add("*Used for*\n" + generateFormattedString(usedForList));
        }
        
        if (!equip.getMisc().getBlueprints().isEmpty()) {
            misc.add("*Blueprints*\n" + equip.getMisc().getBlueprints());
        }
        
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(equip.getName())
            .addField("Type", equip.getCategory(), false)
            .addField("Rarity", String.join("\n", rarity, stars), false)
            .addField("Nation", equip.getNationality(), false)
            .addField("Stats", createStatsString(stats), true);
        
        if (!usedBy.isEmpty()) {
            builder.addField("Used by", String.join("\n", usedBy), true);
        }
        
        builder.addField("Misc", String.join("\n\n", misc), false);
        
        if (!equip.getMisc().getAnimationUrl().isEmpty()) {
            builder.setImage(equip.getMisc().getAnimationUrl());
        }
        builder.setThumbnail(equip.getImageUrl());
        
        components = new ArrayList<>();
        
        tierButtons = new ArrayList<>();
        if (tiers.size() > 1) {
            for (Equip.Tier value : tiers) {
                int equipTier = value.getTier();
                Button button = Button.of(ButtonStyle.PRIMARY, "tierType" + equipTier, "Type " + equipTier);
                if (equipTier == tier) {
                    button = button.asDisabled();
                }
                tierButtons.add(button);
            }
            components.add(ActionRow.of(tierButtons));
        }
        
        if (pages.size() > 1) {
            components.add(ActionRow.of(pageButtons));
            if (pages.size() > 5) {
                components.add(ActionRow.of(pageButtons2));
            }
        }
        
        components.add(ActionRow.of(Button.of(ButtonStyle.PRIMARY, "back", "Back")));
        components.add(ActionRow.of(equipSelectMenu));
        
        return builder;
    }
    
    private void handleBackButton(ButtonInteractionEvent event) {
        selectedEquip = null;
        
        categoryComponents = new ArrayList<>();
        createCategoryComponents(selectedCategory, categoryComponents);
        
        event.editMessage("")
            .setSuppressEmbeds(true)
            .setComponents(categoryComponents)
            .queue();
    }
    
    private void handleCategoryButton(ButtonInteractionEvent event, String category) {
        selectedCategory = category;
        
        categoryComponents = new ArrayList<>();
        createCategoryComponents(category, categoryComponents);
        
        event.editMessage("")
            .setSuppressEmbeds(true)
            .setComponents(categoryComponents)
            .queue();
    }
    
    private void handleTierButton(ButtonInteractionEvent event, int tier) {
        MessageEmbed embed = null;
        switch (tier) {
            case 1 -> embed = createEmbed(selectedEquip, 1).build();
            case 2 -> embed = createEmbed(selectedEquip, 2).build();
            case 3 -> embed = createEmbed(selectedEquip, 3).build();
        }
        event.deferEdit()
            .setSuppressEmbeds(false)
            .setEmbeds(embed)
            .setComponents(components)
            .queue();
    }
    
    private void handlePageButtons(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        if (buttonId.startsWith("page_")) {
            List<LayoutComponent> components = new ArrayList<>();
            if (selectedEquip != null) {
                if (tierButtons != null && !tierButtons.isEmpty()) {
                    components.add(ActionRow.of(tierButtons));
                }
            }
            
            int pageNumber = Integer.parseInt(buttonId.substring(5));
            System.out.println(pageNumber);
            equipSelectMenu = StringSelectMenu.create(selectedValue).addOptions(pages.get(pageNumber - 1)).build();
            
            if (pageButtons != null && !pageButtons.isEmpty()) {
                pageButtons.clear();
            }
            if (pageButtons2 != null && !pageButtons2.isEmpty()) {
                pageButtons2.clear();
            }
            
            if (pages.size() > 5) {
                for (int i = 1; i <= 5; i++) {
                    Button button = Button.of(ButtonStyle.PRIMARY, "page_" + i, "Page " + i);
                    if (i == pageNumber) {
                        button = button.asDisabled();
                    }
                    pageButtons.add(button);
                    System.out.println(button);
                }
                System.out.println("===");
                for (int i = 6; i <= pagesCount; i++) {
                    Button button = Button.of(ButtonStyle.PRIMARY, "page_" + i, "Page " + i);
                    if (i == pageNumber) {
                        button = button.asDisabled();
                    }
                    pageButtons2.add(button);
                    System.out.println(button);
                }
                components.add(ActionRow.of(pageButtons));
                components.add(ActionRow.of(pageButtons2));
            } else {
                for (int i = 1; i <= pagesCount; i++) {
                    Button button = Button.of(ButtonStyle.PRIMARY, "page_" + i, "Page " + i);
                    if (i == pageNumber) {
                        button = button.asDisabled();
                    }
                    pageButtons.add(button);
                }
                if (pageButtons != null) {
                    components.add(ActionRow.of(pageButtons));
                }
            }
            components.add(ActionRow.of(Button.of(ButtonStyle.PRIMARY, "back", "Back")));
            components.add(ActionRow.of(equipSelectMenu));
            
            event.editMessage(selectedValue)
                .setSuppressEmbeds(selectedEquip == null)
                .setComponents(components)
                .queue();
        }
    }
    
    private Set<SelectOption> filteredOptions(String componentId, Equip[] equipData) {
        Set<SelectOption> options = new LinkedHashSet<>();
        
        for (Equip equip : equipData) {
            if (componentId.equals("Type") && equip.getCategory().equals(selectedValue) ||
                componentId.equals("Nationality") && equip.getNationality().equals(selectedValue) ||
                componentId.equals("Rarity") && matchingRarity(equip)) {
                
                options.add(SelectOption.of(equip.getName(), equip.getName()));
            }
        }
        return options;
    }
    
    private boolean matchingRarity(Equip equip) {
        for (Equip.Tier tier : equip.getTiers()) {
            String stars;
            boolean twoTerms = selectedValue.split(" ").length == 2;
            stars = selectedValue.split(" ")[twoTerms ? 1 : 2];
            if (tier.getStars().getStars().equals(stars)) {
                return true;
            }
        }
        return false;
    }
    
    private String generateFormattedString(Set<String> stringSet) {
        StringBuilder formattedString = new StringBuilder();
        List<String> nonEmptyStrings = stringSet.stream()
            .filter(str -> !str.isEmpty() && !str.equals(" "))
            .collect(Collectors.toList());
        
        for (int i = 0; i < nonEmptyStrings.size(); i++) {
            formattedString.append(nonEmptyStrings.get(i));
            if (i < nonEmptyStrings.size() - 1) {
                formattedString.append("\n");
            }
        }
        return formattedString.toString();
    }
    
    private String createStatsString(Equip.Tier.Stats stats) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Equip.Tier.Stats.StatsInfo, String> entry : stats.getAll().entrySet()) {
            if (entry.getKey() != null) {
                sb.append(entry.getValue()).append(": ").append(entry.getKey().getFormatted()).append("\n");
            }
        }
        
        return sb.toString();
    }
}
