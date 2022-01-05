package CahmiBot.Commands.Music;

import CahmiBot.Main;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static CahmiBot.Services.Lavaplayer.AudioController.pauseAndPlay;
import static CahmiBot.Services.Lavaplayer.AudioController.skipTrack;

public class PauseCommand extends ListenerAdapter
{
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentRaw().split("\\s");

        if(args[0].equalsIgnoreCase(Main.prefix + "pause"))
        {
            pauseAndPlay((TextChannel) event.getChannel());
        }
    }
}
