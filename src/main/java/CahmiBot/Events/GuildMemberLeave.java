package CahmiBot.Events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Random;

public class GuildMemberLeave extends ListenerAdapter {

        String[] messages = {
                "Unfortunate to see you go [member]",
                "Bye[member]!",
                "See ya [member]!"
        };

        public void onGuildMemberRemove(GuildMemberRemoveEvent event){

                Random rand = new Random();
                int randNumber = rand.nextInt(messages.length);

                EmbedBuilder leave = new EmbedBuilder();

                leave.setColor(Color.ORANGE);
                leave.setDescription(messages[randNumber].replace("[member]",event.getUser().getAsMention()));
                event.getGuild().getSystemChannel().sendMessageEmbeds(leave.build()).queue();
        }
}
