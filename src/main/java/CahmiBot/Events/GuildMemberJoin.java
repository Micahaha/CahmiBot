package CahmiBot.Events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Random;

public class GuildMemberJoin extends ListenerAdapter {

    String[] messages = {
            "Hi [member] Welcome to official Fanclub Discord Server!",
            "Enjoy your stay at Fanclub Cord [member]!",
            "A wild [member] appeared! please message a moderator to grant you any additional roles!."
    };


    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        Random rand = new Random();
        int randNumber = rand.nextInt(messages.length);

        EmbedBuilder join = new EmbedBuilder();

        join.setColor(Color.ORANGE);
        join.setDescription(messages[randNumber].replace("[member]",event.getMember().getAsMention()));

        event.getGuild().getSystemChannel().sendMessageEmbeds(join.build()).queue();


        // Add role on user join

        if((event.getGuild().getRoleById("925074304918499328") != null))
        {
            event.getGuild().addRoleToMember(event.getMember(),event.getGuild().getRoleById("925074304918499328")).queue();
        }
        else
        {
            event.getGuild().getSystemChannel().sendMessage("Default role does not exist! cannot add!").queue();
        }




    }




}
