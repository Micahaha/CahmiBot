package CahmiBot.Commands;

import CahmiBot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;

public class KickCommand extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s");
        Message message = event.getMessage();
        List<Member> mentionedMembers = message.getMentionedMembers();
        Guild guild = message.getGuild();

        if (args[0].equalsIgnoreCase(Main.prefix + "kick")) {


            if(args.length < 2 || mentionedMembers.isEmpty())
            {
                EmbedBuilder error = new EmbedBuilder();
                error.setColor(Color.red);
                error.setTitle("Arguments missing");
                error.setDescription("Usage: " + Main.prefix + "kick [Member]");

                event.getChannel().sendMessageEmbeds(error.build()).queue();

            }

            Member target = mentionedMembers.get(0);


           try
           {
               guild.kick(target).complete();
               EmbedBuilder success = new EmbedBuilder();
               success.setColor(Color.GREEN);
               success.setTitle("Member " + target.getEffectiveName() + "has been kicked from " + guild.getName());
           }
           catch(Exception e)
           {

                   EmbedBuilder error = new EmbedBuilder();
                   error.setColor(Color.red);
                   error.setTitle("You lack the permissions to kick " + target.getEffectiveName());
                   error.setDescription("You may only kick members with a role beneath your own!");

                   event.getChannel().sendMessageEmbeds(error.build()).queue();

                   if(!event.getMember().hasPermission(Permission.ADMINISTRATOR))
               {
                   error.setColor(Color.red);
                   error.setTitle("You lack the permissions to kick " + target.getEffectiveName());
                   error.setDescription("you may only kick members with a role beneath the bot!");

                   event.getChannel().sendMessageEmbeds(error.build()).queue();
               }
           }

           /* try
            {
                guild.kick(target).complete();
            }
            catch(net.dv8tion.jda.api.exceptions.HierarchyException e)
            {
                EmbedBuilder error = new EmbedBuilder();
                error.setColor(Color.red);
                error.setTitle("You lack the permissions to kick " + target.getEffectiveName());
                error.setDescription("You may only kick members with a role beneath the bot!");

                event.getChannel().sendMessageEmbeds(error.build()).queue();
            }
           */
        }
    }
}