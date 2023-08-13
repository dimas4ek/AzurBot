package org.dimas4ek.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public class testcom extends ListenerAdapter {
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("test")) {
            event.reply("content1").addActionRow(Button.of(ButtonStyle.PRIMARY, "btn", "button")).queue();
        }
    }
    
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("btn")) {
            event.deferEdit().setEmbeds(createEmbed().build()).queue();
        }
    }
    
    private EmbedBuilder createEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("title");
        builder.setDescription("desc");
        return builder;
    }
}
