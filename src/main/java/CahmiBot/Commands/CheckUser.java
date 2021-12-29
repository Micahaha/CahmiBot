package CahmiBot.Commands;

import CahmiBot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.awt.*;
import java.util.List;

public class CheckUser extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s");
        Message message = event.getMessage();
        List<Member> mentionedMembers = message.getMentionedMembers();
        Guild guild = message.getGuild();

         if(args[0].equalsIgnoreCase(Main.prefix + "checkuser"))
         {
             if(args.length < 2)
             {
                 EmbedBuilder error = new EmbedBuilder();
                 error.setColor(Color.red);
                 error.setTitle("Please mention a user when performing this command!");
                 error.setDescription("Usage: `" + Main.prefix + "checkuser @[Member]");

                 event.getChannel().sendMessageEmbeds(error.build()).queue();

             }


            if(!mentionedMembers.isEmpty())
            {
                Member member = mentionedMembers.get(0);
                EmbedBuilder embed = new EmbedBuilder();

                embed.setTitle("information for user: " + member.getUser().getAsTag());
                embed.setImage(member.getEffectiveAvatarUrl());
                embed.setDescription("");
                embed.setFooter( "<" + member.getId() + "@>", member.getEffectiveAvatarUrl());
                embed.addField("Account created: ", member.getTimeCreated().getYear() + "/" + member.getTimeCreated().getMonth() +
                        "/" + member.getTimeCreated().getDayOfMonth() + " " + member.getTimeCreated().getHour() + ":" + member.getTimeCreated().getMinute() + ":" + member.getTimeCreated().getSecond(),false);
                embed.addField("Discord Server: ", guild.getName() + "\n joined: " + member.getTimeJoined(),false);
                embed.addField("Roles added: ", member.getRoles().toString(), false);


                event.getChannel().sendMessageEmbeds(embed.build()).queue();

            }
            else
            {
                EmbedBuilder error = new EmbedBuilder();
                error.setColor(Color.red);
                error.setTitle("Please make sure the user is present within the discord!");
                error.setDescription("Usage: ~checkuser @[NameOfUser]");

                event.getChannel().sendMessageEmbeds(error.build()).queue();
            }
         }
    }
}
