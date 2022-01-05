package CahmiBot.Commands.Music;

import CahmiBot.Main;
import CahmiBot.Services.Lavaplayer.AudioController;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static CahmiBot.Services.Lavaplayer.AudioController.getGuildAudioPlayer;
import static CahmiBot.Services.Lavaplayer.AudioController.loadAndPlay;


public class PlayCommand extends ListenerAdapter {


    public PlayCommand() {

        // Initialize audio controller
        new AudioController();
    }

    // COMMAND ARGUMENTS
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        String[] args = event.getMessage().getContentRaw().split("\\s");

        if (args[0].equalsIgnoreCase(Main.prefix + "play")) {

            getGuildAudioPlayer(event.getGuild());
            String link = args[1];
            StringBuilder sb = new StringBuilder("ytsearch:");

            if (!AudioController.isUrl(link)) {
                for (int i = 1; i < args.length; i++) {
                    sb.append(" ").append(args[i]);
                }
                loadAndPlay((TextChannel) event.getChannel(), sb.toString());
                System.out.println("passed1");
            } else {
                loadAndPlay((TextChannel) event.getChannel(), args[1]);
                System.out.println("passed2");
            }
        }
    }
}