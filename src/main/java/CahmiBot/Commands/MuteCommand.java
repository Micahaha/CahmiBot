package CahmiBot.Commands;

import CahmiBot.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
                        event.getChannel().sendMessage(args[1] + " has been muted").queue();
                        event.getGuild().addRoleToMember(member,role).complete();
                    }
                    else
                    {
                        //unmute
                        event.getChannel().sendMessage(args[1] + " has been unmuted").queue();
                        event.getGuild().removeRoleFromMember(member,role).complete();
                    }


                }
                else if(args.length == 3 && !mentionedMembers.get(0).hasPermission(Permission.ADMINISTRATOR))
                {
                    Member member = mentionedMembers.get(0);
                    if(!member.getRoles().contains(role))
                    {

                        event.getChannel().sendMessage("Muted " + args[1] + " for " + args[2] + " seconds").queue();
                        event.getGuild().addRoleToMember(member,role).complete();

                        //unmute when

                        new java.util.Timer().schedule(
                                new java.util.TimerTask()
                                {
                                    public void run()
                                    {
                                        event.getChannel().sendMessage(args[1] + " has been unmuted").queue();
                                        event.getGuild().removeRoleFromMember(member,role).complete();
                                    }
                                },
                                Integer.parseInt(args[2]) * 1000L
                        );
                    }
                }

                else
                {
                    try
                    {
                        if(mentionedMembers.get(0).getUser().isBot())
                        {
                            event.getChannel().sendMessage("Can't mute other bots!").queue();
                        }
                        else if(mentionedMembers.get(0).hasPermission(Permission.ADMINISTRATOR))
                        {
                            event.getChannel().sendMessage("Cannot mute role higher then bot").queue();
                        }
                    }
                    catch(Exception e)
                    {
                        event.getChannel().sendMessage("Incorrect Syntax!").queue();
                    }

                }

            }
    }
}
