package org.dimas4ek.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class testcom extends ListenerAdapter {
    //static Map<String, Emoji> emojiCache = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("test")) {
            MessageChannel channel = event.getChannel(); // = reference of a MessageChannel
            EmbedBuilder embed = new EmbedBuilder();
            InputStream file = null;
            try {
                file = new URL("https://http.cat/500").openStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            embed.setImage("attachment://cat.png") // we specify this in sendFile as "cat.png"
                .setDescription("This is a cute cat :3");
            channel.sendFiles(FileUpload.fromData(file, "cat.png")).setEmbeds(embed.build()).queue();
            
            /*Guild guild = event.getGuild();
            TextChannel textChannel = guild.getTextChannelById("976000224239706152");
            Message message = textChannel.retrieveMessageById("1137343905105457252").complete();
            
            File file = new File("output1.png");
            
            System.out.println(message.getContentRaw());*/
            
            /*System.out.println(message.getAttachments().get(0).getFileName());
            System.out.println(message.getAttachments().get(0).getProxy().downloadToFile(file, 300, 300).complete(file));
            System.out.println(message.getAttachments().get(0).getUrl());
            
            event.getChannel().sendFiles(FileUpload.fromData(new File("output1.png"))).queue();*/


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
