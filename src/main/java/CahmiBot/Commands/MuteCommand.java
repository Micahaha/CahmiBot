package CahmiBot.Commands;

import CahmiBot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;

public class MuteCommand  extends ListenerAdapter {
    public void onMessageReceived( MessageReceivedEvent event)
    {
            String[] args = event.getMessage().getContentRaw().split("\\s");
            Message message = event.getMessage();
            List<Member> mentionedMembers = message.getMentionedMembers();
            Role role = event.getGuild().getRoleById("925938793360531476");

            if(args[0].equalsIgnoreCase(Main.prefix + "mute"))
            {
                if(args.length == 2 && !mentionedMembers.get(0).hasPermission(Permission.ADMINISTRATOR))
                {

                    Member member = mentionedMembers.get(0);
                    if(!member.getRoles().contains(role))
                    {
                        // Mute the user
                        EmbedBuilder success = new EmbedBuilder();
                        success.setColor(Color.GREEN);
                        success.setTitle("Member " + member.getEffectiveName() + " has been muted ");
                        event.getChannel().sendMessageEmbeds(success.build()).queue();
                        event.getGuild().addRoleToMember(member,role).complete();
                    }
                    else
                    {
                        //unmute the user
                        EmbedBuilder success = new EmbedBuilder();
                        success.setColor(Color.GREEN);
                        success.setTitle("Member " + member.getEffectiveName() + " has been unmuted");
                        event.getChannel().sendMessageEmbeds(success.build()).queue();

                        event.getGuild().removeRoleFromMember(member,role).complete();
                    }


                }
                else if(args.length == 3 && !mentionedMembers.get(0).hasPermission(Permission.ADMINISTRATOR))
                {
                    Member member = mentionedMembers.get(0);
                    if(!member.getRoles().contains(role))
                    {

                        // event.getChannel().sendMessage("Muted " + args[1] + " for " + args[2] + " seconds").queue();
                        EmbedBuilder success = new EmbedBuilder();
                        success.setColor(Color.GREEN);
                        success.setTitle("Member " + member.getEffectiveName() + " has been muted for " + args[2] + " seconds");
                        event.getChannel().sendMessageEmbeds(success.build()).queue();
                        event.getGuild().addRoleToMember(member,role).complete();

                        //unmute when

                        new java.util.Timer().schedule(
                                new java.util.TimerTask()
                                {
                                    public void run()
                                    {
                                        EmbedBuilder success = new EmbedBuilder();
                                        success.setColor(Color.GREEN);
                                        success.setTitle("Member " + member.getEffectiveName() + " has been unmuted!");
                                        event.getChannel().sendMessageEmbeds(success.build()).queue();

                                        event.getGuild().removeRoleFromMember(member,role).complete();
                                    }
                                },
                                Integer.parseInt(args[2]) * 1000L
                        );
                    }
                }

                // Error Messages
                else
                {
                    try
                    {
                        if(mentionedMembers.get(0).getUser().isBot())
                        {
                            EmbedBuilder error = new EmbedBuilder();
                            error.setColor(Color.red);
                            error.setTitle("Can't mute other bots!");
                            error.setDescription("usage ~mute [Member] or ~mute [Member] [amount of seconds]");

                            event.getChannel().sendMessageEmbeds(error.build()).queue();
                        }
                        else if(mentionedMembers.get(0).hasPermission(Permission.ADMINISTRATOR))
                        {
                            EmbedBuilder error = new EmbedBuilder();
                            error.setColor(Color.red);
                            error.setTitle("Can't mute roles higher than bot!");
                            error.setDescription("usage ~mute [Member] or ~mute [Member] [amount of seconds]");

                            event.getChannel().sendMessageEmbeds(error.build()).queue();
                        }
                    }
                    catch(Exception e)
                    {
                        EmbedBuilder error = new EmbedBuilder();
                        error.setColor(Color.red);
                        error.setTitle("Incorrect syntax for command!");
                        error.setDescription("usage ~mute [Member] or ~mute [Member] [amount of seconds]");

                        event.getChannel().sendMessageEmbeds(error.build()).queue();
                    }

                }

            }
    }
}
