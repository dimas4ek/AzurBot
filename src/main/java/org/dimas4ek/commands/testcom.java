package org.dimas4ek.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.internal.requests.restaction.MessageCreateActionImpl;

public class testcom extends ListenerAdapter {
    //static Map<String, Emoji> emojiCache = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("test")) {
            EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Пример с кнопкой")
                .setDescription("Это встроенное сообщение с кнопкой.");
            
            MessageCreateAction messageAction = new MessageCreateActionImpl(event.getChannel());
            
            // Добавление кнопки
            messageAction.addActionRow(Button.primary("button_id", "Нажми меня!"));
            Message message = messageAction.complete();
            message.replyEmbeds(embed.build()).queue();
            //event.reply(MessageCreateData.fromMessage(message)).queue();
        }
    }
}
