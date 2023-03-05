package org.dimas4ek.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

import java.awt.*;

public class testcom extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getFullCommandName().equals("test")) {
            Button button = Button.primary("button-id", "Click me");

            StringSelectMenu selectMenu = StringSelectMenu.create("menu-id")
                .setPlaceholder("Select an option")
                .addOptions(
                    SelectOption.of("Option 1", "option-1"),
                    SelectOption.of("Option 2", "option-2"),
                    SelectOption.of("Option 3", "option-3")
                ).build();


            EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Example Embed")
                .setDescription("This is an example embed with a button and a select menu")
                .setColor(Color.BLUE)
                .setFooter("Footer text");

            MessageEmbed embed = embedBuilder.build();
            event.getChannel().sendMessageEmbeds(embed).addComponents(
                ActionRow.of(
                    Button.of(ButtonStyle.PRIMARY, "1", "1"),
                    Button.of(ButtonStyle.PRIMARY, "2", "1"),
                    Button.of(ButtonStyle.PRIMARY, "3", "1"),
                    Button.of(ButtonStyle.PRIMARY, "4", "1"),
                    Button.of(ButtonStyle.PRIMARY, "5", "1")
                )
            ).queue();
        }
    }
}
