package org.dimas4ek.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public class CommandList extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getFullCommandName().equals("start")) {
            event.replyEmbeds(
                    new EmbedBuilder()
                        .setTitle("Welcome to Azur Lane Bot!")
                        .setDescription("This bot is made by Dimas4ek#0001")
                        .addField("Commands", "```\n" +
                            "/info - Get info about the bot\n" +
                            "/ship - Get info about a ship\n" +
                            "/chapter - Get info about a chapter\n" +
                            "/equipment - Get info about a equipment\n" +
                            "```", false)
                        .build()
                )
                .addActionRow(
                    Button.of(ButtonStyle.LINK, "https://discord.com/api/oauth2/authorize?client_id=1078632658931810304&permissions=8&scope=bot%20applications.commands", "Invite me")
                ).queue();
        }
    }
}