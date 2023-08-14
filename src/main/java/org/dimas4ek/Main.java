package org.dimas4ek;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.dimas4ek.commands.BotInfo;
import org.dimas4ek.commands.EquipInfo;
import org.dimas4ek.commands.ShipInfo;
import org.dimas4ek.commands.testcom;

public class Main {

    private static final String TOKEN = "***";

    public static void main(String[] args) {
        JDA api = JDABuilder.createDefault(TOKEN)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .build();

        api.addEventListener(
            new ShipInfo(),
            new BotInfo(),
            //new ChapterInfo(),
            new EquipInfo(),
            new testcom()
        );
        api.updateCommands().addCommands(
            Commands.slash("info", "Get info about the bot"),
            Commands.slash("ship", "Get info about a ship")
                .addOption(OptionType.STRING, "name", "Name of the ship", true),
            //Commands.slash("chapter", "Get info about a chapter"),
            Commands.slash("equip", "Get info about a equipment"),
            Commands.slash("test", "Test command")
        ).queue();
    }
}
