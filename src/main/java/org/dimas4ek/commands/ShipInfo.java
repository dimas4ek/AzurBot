package org.dimas4ek.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.FileUpload;
import org.dimas4ek.entity.Ship;
import org.dimas4ek.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.*;

public class ShipInfo extends ListenerAdapter {
    String name;
    static Map<String, Emoji> emojiCache = new HashMap<>();
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getFullCommandName().equals("ship")) {
            name = Objects.requireNonNull(event.getOption("name")).getAsString();
            
            Ship ship = findShipWithName(JsonUtils.getShipData(), name);
            
            if (ship == null) {
                event.reply("Could not find ship with name " + name).setEphemeral(true).queue();
                return;
            }
            
            event.deferReply().queue();
            loadNormal(ship, event.getHook());
        }
    }
    
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
            case "normal" -> {
                Ship ship = findShipWithName(JsonUtils.getShipData(), name);
                
                if (ship == null) {
                    event.reply("Could not find ship").setEphemeral(true).queue();
                    return;
                }
                
                event.getMessage().delete().queue();
                event.deferReply().queue();
                loadNormal(ship, event.getHook());
            }
            case "retrofit" -> {
                Ship ship = findShipWithName(JsonUtils.getShipData(), name);
                
                if (ship == null) {
                    event.reply("Could not find ship").setEphemeral(true).queue();
                    return;
                }
                
                event.getMessage().delete().queue();
                loadRetrofit(ship, event.getHook());
            }
            case "skills" -> {
                Ship ship = findShipWithName(JsonUtils.getShipData(), name);
                
                if (ship == null) {
                    event.reply("Could not find ship").setEphemeral(true).queue();
                    return;
                }
                
                event.getMessage().delete().queue();
                event.deferReply().queue();
                loadSkills(ship, event.getHook());
            }
        }
    }
    
    private void loadSkills(Ship ship, InteractionHook hook) {
        List<Ship.Skill> skills = ship.getSkills();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(ship.getName() + " Skills");
        for (Ship.Skill skill : skills) {
            builder.addField(skill.getName(), skill.getDescription(), false);
        }
        builder.setThumbnail(ship.getSkins().get(0).getChibiUrl());
        builder.setImage(ship.getThumbnailUrl());
        
        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.of(ButtonStyle.PRIMARY, "normal", "Normal"));
        if (ship.isRetrofit()) {
            buttons.add(Button.of(ButtonStyle.PRIMARY, "retrofit", "Retrofit"));
        }
        
        hook
            .sendMessageEmbeds(builder.build())
            .setActionRow(buttons)
            .queue();
    }
    
    private Ship findShipWithName(Ship[] ships, String name) {
        for (Ship ship : ships) {
            if (ship.getName().equals(name)) {
                return ship;
            }
        }
        return null;
    }
    
    public void loadRetrofit(Ship ship, InteractionHook hook) {
        EmbedBuilder builder = createBuilder(ship, hook);
        
        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.of(ButtonStyle.PRIMARY, "normal", "Normal"));
        buttons.add(Button.of(ButtonStyle.PRIMARY, "skills", "Skills"));
        
        sendRetrofit(ship, hook, builder, buttons);
    }
    
    public void loadNormal(Ship ship, InteractionHook hook) {
        EmbedBuilder builder = createBuilder(ship, hook);
        
        List<Button> buttons = new ArrayList<>();
        if (ship.isRetrofit()) {
            buttons.add(Button.of(ButtonStyle.PRIMARY, "retrofit", "Retrofit"));
        }
        buttons.add(Button.of(ButtonStyle.PRIMARY, "skills", "Skills"));
        
        sendNormal(ship, hook, builder, buttons);
    }
    
    private void sendRetrofit(Ship ship, InteractionHook hook, EmbedBuilder builder, List<Button> buttons) {
        File tempFile = null;
        try {
            BufferedImage resultImage = resizeImage(ship);
            
            tempFile = createTempFile(resultImage);
            
            MessageChannel channel = hook.getInteraction().getMessageChannel();
            
            channel.sendFiles(FileUpload.fromData(tempFile, "retrofit.png"))
                .setEmbeds(
                    builder
                        .setThumbnail(ship.getSkins().get(1).getChibiUrl())
                        .setImage("attachment://retrofit.png")
                        .build()
                )
                .setActionRow(buttons)
                .queue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
    
    private static void sendNormal(Ship ship, InteractionHook hook, EmbedBuilder builder, List<Button> buttons) {
        if (!buttons.isEmpty()) {
            hook.sendMessageEmbeds(
                builder
                    .setThumbnail(ship.getSkins().get(0).getChibiUrl())
                    .setImage(ship.getThumbnailUrl())
                    .build()
            ).setActionRow(buttons).queue();
        } else {
            hook.sendMessageEmbeds(
                builder
                    .setThumbnail(ship.getSkins().get(0).getChibiUrl())
                    .setImage(ship.getThumbnailUrl())
                    .build()
            ).queue();
        }
    }
    
    private EmbedBuilder createBuilder(Ship ship, InteractionHook hook) {
        EmbedBuilder builder = new EmbedBuilder();
        if (ship.isRetrofit()) {
            builder.setTitle(ship.getName() + " Retrofit");
        } else {
            builder.setTitle(ship.getName());
        }
        builder.addField("Name", ship.getNames().get("code"), true);
        builder.addField("Class", ship.getShipClass(), true);
        builder.addField("Nationality", ship.getNationality(), true);
        builder.addField("Classification",
                         getHullTypeEmoji(ship, hook) + " " + ship.getHullType(),
                         false
        );
        
        StringBuilder stars = new StringBuilder();
        stars.append("★".repeat(Math.max(0, ship.getStars())));
        if (stars.length() != 6) {
            stars.append("☆");
        }
        builder.addField("Rarity", ship.getRarity() + " " + stars, false);
        
        List<Ship.Slot> slots = ship.getSlots();

        builder.addField("Gear", String.join("\n", "**Slot**", "1", "2", "3", "Augment"), true);
        builder.addField(EmbedBuilder.ZERO_WIDTH_SPACE,
                         String.join("\n", "**Efficiency**", getEfficiency(slots, 1), getEfficiency(slots, 2), getEfficiency(slots, 3), "N/A"), true
        );
        builder.addField(EmbedBuilder.ZERO_WIDTH_SPACE,
                         String.join("\n", "**Equippable**", getEquippable(slots, 1), getEquippable(slots, 2), getEquippable(slots, 3),
                                     getAugment(ship.getHullType())
                         ), true
        );
        
        StringJoiner construction = getConstruction(ship);
        builder.addField("Construction",
                         ship.getConstruction().getConstructionTime() + "\n" + construction,
                         false
        );
        
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
        
        return builder;
    }
    
    @NotNull
    private static StringJoiner getConstruction(Ship ship) {
        Ship.Construction.Available availableIn = ship.getConstruction().getAvailableIn();
        Map<String, String> availableInValues = new HashMap<>();
        for (Map.Entry<String, Object> entry : availableIn.getAll().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof String) {
                availableInValues.put(key, key + ": " + value);
            } else if (value instanceof Boolean) {
                availableInValues.put(key, key + ": ✓");
            }
        }
        
        StringJoiner sj = new StringJoiner("\n");
        availableInValues.forEach((key, value) -> sj.add(value));
        return sj;
    }
    
    private String getHullTypeEmoji(Ship ship, InteractionHook hook) {
        if (ship.getRetrofitHullType() != null && ship.getRetrofitHullType().equals("DDG")) {
            return hook.getJDA().getEmojisByName("ddg", true).get(0).getFormatted();
        }
        
        String emojiName = getEmojiNameForHullType(ship.getHullType());
        return hook.getJDA().getEmojisByName(emojiName, true).get(0).getFormatted();
    }
    
    private String getEmojiNameForHullType(String hullType) {
        return switch (hullType) {
            case "Destroyer" -> "destroyer";
            case "Light Cruiser" -> "lightCruiser";
            case "Repair Ship" -> "repairShip";
            case "Heavy Cruiser" -> "heavyCruiser";
            case "Large Cruiser" -> "largeCruiser";
            case "Munition Ship" -> "munitionShip";
            case "Monitor" -> "monitor";
            case "Battleship" -> "battleship";
            case "Battlecruiser" -> "battleCruiser";
            case "Aviation Battleship" -> "aviationBattleship";
            case "Aircraft Carrier" -> "aircraftCarrier";
            case "Light Aircraft Carrier" -> "lightAircraftCarrier";
            case "Submarine" -> "submarine";
            case "Submarine Carrier" -> "submarineCarrier";
            case "Sailing Frigate" -> "sailingFrigate";
            default -> null;
        };
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
            case "Submarine", "Submarine Carrier" -> "Kunai, Dagger";
            default -> null;
        };
    }
    
    private String getEfficiency(List<Ship.Slot> slots, int slot) {
        if (slots.get(slot - 1).getKaiEfficiency() != 0)
            return slots.get(slot - 1).getMinEfficiency() + " → " + slots.get(slot - 1).getMaxEfficiency()
                + " → " + slots.get(slot - 1).getKaiEfficiency();
        else
            return slots.get(slot - 1).getMinEfficiency() + " → " + slots.get(slot - 1).getMaxEfficiency();
    }
    
    private String getEquippable(List<Ship.Slot> slots, int slot) {
        return slots.get(slot - 1).getType();
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
    
    private BufferedImage resizeImage(Ship ship) throws IOException {
        InputStream file = new URL(ship.getSkins().get(1).getImageUrl()).openStream();
        BufferedImage originalImage = ImageIO.read(file);
        
        Image resizedImage = originalImage.getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        
        BufferedImage resultImage = new BufferedImage(250, 250, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resultImage.createGraphics();
        g2d.drawImage(resizedImage, 0, 0, null);
        g2d.dispose();
        
        return resultImage;
    }
    
    private File createTempFile(BufferedImage image) throws IOException {
        File tempFile = File.createTempFile("temp", ".png");
        ImageIO.write(image, "png", tempFile);
        return tempFile;
    }
}
