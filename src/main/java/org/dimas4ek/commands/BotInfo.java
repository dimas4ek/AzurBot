package org.dimas4ek.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.Objects;

public class BotInfo extends ListenerAdapter {
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getFullCommandName().equals("info")) {
            event.replyEmbeds(
                    new EmbedBuilder()
                        .setTitle(null)
                        .setDescription("Привет, Адмирал! Я - Azur Bot, твой верный помощник в мире игры Azur Lane. " +
                                            "Я предоставлю тебе информацию о кораблях и снаряжении, чтобы помочь в твоих морских приключениях.")
                        .setAuthor("Azur Bot", null,
                                   "https://cdn.discordapp.com/attachments/976000224239706152/1140315648279445677/essex.jpg"
                        )
                        .addField("Creator", Objects.requireNonNull(event.getJDA().getUserById("305240921019121664")).getAsMention(), false)
                        .addField("Version", "1.0.0", false)
                        .addField("Library", "[JDA](https://github.com/discord-jda/JDA)", false)
                        .addField("Как использовать:",
                                  "Используй команду `/ship search:[название]`, чтобы узнать характеристики конкретного корабля.\n" +
                                      "Для получения информации о снаряжении, воспользуйся командой `/equip search:[название]`.", false
                        )
                        .addField("Примеры использования:",
                                  "`/ship search:Enterprise` - узнать о корабле \"Enterprise\".\n" +
                                      "`/equip search:Twin 410mm (BL 13.5\" Mk I)` - получить информацию о снаряжении \"Twin 410mm (BL 13.5\" Mk I)\".",
                                  false
                        )
                        .addField("Список доступных команд:",
                                  "`/info` - информация о боте.\n" +
                                      "`/ship list` - список всех доступных кораблей в игре.\n" +
                                      "`/ship search:[название] - информаия о конкретном корабле" +
                                      "`/equip list` - список всех доступных снаряжений в игре.\n" +
                                      "`/equip search:[название] - информаия о конкретном снаряжении", false
                        )
                        .setFooter("Bot is under construction")
                        .build()
                )
                .addActionRow(
                    Button.of(
                        ButtonStyle.LINK,
                        event.getJDA().getInviteUrl(
                            Permission.MESSAGE_SEND,
                            Permission.MESSAGE_MANAGE,
                            Permission.MESSAGE_EMBED_LINKS,
                            Permission.MESSAGE_ATTACH_FILES,
                            Permission.USE_APPLICATION_COMMANDS
                        ),
                        "Invite me"
                    )
                ).queue();
        }
    }
}