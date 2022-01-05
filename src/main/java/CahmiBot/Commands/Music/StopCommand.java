package CahmiBot.Commands.Music;

import CahmiBot.Main;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static CahmiBot.Services.Lavaplayer.AudioController.*;
public class StopCommand extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        final Member self = event.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        String[] args = event.getMessage().getContentRaw().split("\\s");

        if (args[0].equalsIgnoreCase(Main.prefix + "stop")) {
            if (selfVoiceState.inAudioChannel()) {
                event.getChannel().sendMessage("Stopping track").queue();
                getGuildAudioPlayer(event.getGuild()).player.stopTrack();
            } else {
                event.getChannel().sendMessage("Not in a voice channel.").queue();
            }

        }
    }
}

