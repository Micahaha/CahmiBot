package CahmiBot.Commands;

import CahmiBot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;


public class Clear extends ListenerAdapter {

    @Override
    public void onMessageReceived( MessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentRaw().split("\\s");

        if(args[0].equalsIgnoreCase(Main.prefix + "clear"))
        {
            if(args.length < 2)
            {
                EmbedBuilder error = new EmbedBuilder();
                error.setColor(Color.red);
                error.setTitle("Please specify the amount of messages to delete");
                error.setDescription("Usage: `" + Main.prefix + "clear [# of messages]");

                event.getChannel().sendMessageEmbeds(error.build()).queue();

            }
            else
            {
                try
                {
                    List<Message> messageList = event.getChannel().getHistory().retrievePast(Integer.parseInt(args[1])).complete();
                    event.getChannel().purgeMessages(messageList);

                    EmbedBuilder success = new EmbedBuilder();
                    success.setColor(Color.green);
                    success.setTitle("Successfully deleted [" + args[1] + "] messages");

                    event.getChannel().sendMessageEmbeds(success.build()).queue();
                }
                catch(IllegalArgumentException e)
                {
                    if(e.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval"))
                    {
                        EmbedBuilder error = new EmbedBuilder();
                        error.setColor(Color.red);
                        error.setTitle("Too many messages selected!");
                        error.setDescription("only 1-100 messages may be deleted at a time");

                        event.getChannel().sendMessageEmbeds(error.build()).queue();
                    }
                    else
                    {
                        EmbedBuilder error = new EmbedBuilder();
                        error.setColor(Color.red);
                        error.setTitle("Messages are too old!");
                        error.setDescription("only messages between one week to two weeks may be deleted");

                        event.getChannel().sendMessageEmbeds(error.build()).queue();
                    }
                }
            }
        }
    }
}
