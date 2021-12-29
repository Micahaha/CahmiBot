package CahmiBot.Commands;

import CahmiBot.Main;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Ping extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if(event.getAuthor().isBot())
        {
            return;
        }

        if(event.isFromType(ChannelType.PRIVATE))
        {
            return;
        }

        Message msg = event.getMessage();

        if(msg.getContentRaw().equalsIgnoreCase(Main.prefix + "ping"))
        {
            MessageChannel channel = event.getChannel();

            channel.sendMessage("Pong!").queue();
        }
    }
}
