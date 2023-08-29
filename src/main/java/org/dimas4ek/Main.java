package org.dimas4ek;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.dimas4ek.commands.*;

public class Main {

    private static final String TOKEN = "***";

    public static void main(String[] args) {
        JDA api = JDABuilder.createDefault(TOKEN)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        api.addEventListener(
                new ShipInfo(),
                new BotInfo(),
                new ChapterInfo(),
                new EquipInfo(),
                new testcom()
        );

        api.updateCommands().addCommands(
                Commands.slash("info", "Get info about the bot"),
                Commands.slash("ship", "Get info about a ship")
                        .addOption(OptionType.STRING, "name", "Name of the ship", true),
                Commands.slash("equip", "Get info about a equipment"),
                Commands.slash("chapter", "Get info about a chapter")
                        .addOption(OptionType.STRING, "chapter", "Chapter (1-1, 7-4)", true),
                Commands.slash("test", "Test command")
        ).queue();

    }
}
