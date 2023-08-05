package org.dimas4ek.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class testcom extends ListenerAdapter {
    //static Map<String, Emoji> emojiCache = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("test")) {


            /*User bot = event.getJDA().getSelfUser();
            List<RichCustomEmoji> emojisList = event.getGuild().getEmojis();
            event.getGuild().retrieveEmojis().complete();
            if (!emojisList.isEmpty()) {
                for (RichCustomEmoji emoji : emojisList) {
                    if (emoji.getOwner() == bot) {
                        emoji.delete().queue();
                    }
                }
            }

            try {
                Emoji emoji = createEmojiFromUrl(event.getGuild(), "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/images/skills/106/full_firepower.png", "asddd");
                Emoji emoji2 = createEmojiFromUrl(event.getGuild(), "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/images/skills/106/giant_hunterkai.png", "asdeqweqe");
                event.reply(emoji.getFormatted() + " " + emoji2.getFormatted()).queue();

                System.out.println(emojiCache.size());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }*/

        }
    }

    /*public static Emoji createEmojiFromUrl(Guild guild, String url, String name) throws IOException {
        if (emojiCache.containsKey(url)) {
            return emojiCache.get(url);
        } else {
            BufferedImage image = ImageIO.read(new URL(url));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageData = baos.toByteArray();
            Emoji emoji = guild.createEmoji(name, Icon.from(imageData), (Role) null).complete();
            emojiCache.put(url, emoji);

            return emoji;
        }
    }

    private String getEmojiById(String id, InteractionHook hook) {
        return hook.getJDA().getEmojiById(id).getFormatted();
    }*/
}
