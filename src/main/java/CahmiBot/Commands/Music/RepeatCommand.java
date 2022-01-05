package CahmiBot.Commands.Music;

import CahmiBot.Main;
import CahmiBot.Services.Lavaplayer.AudioController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static CahmiBot.Services.Lavaplayer.AudioController.getGuildAudioPlayer;

public class RepeatCommand extends ListenerAdapter {


    public void onMessageReceived(MessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentRaw().split("\\s");

        if(args[0].equalsIgnoreCase(Main.prefix + "repeat"))
        {
            final Member self = event.getGuild().getSelfMember();
            final GuildVoiceState selfVoiceState = self.getVoiceState();

            if(!selfVoiceState.inAudioChannel())
            {
                EmbedBuilder error = new EmbedBuilder();
                error.setTitle("No longer in a voice channel");
                event.getChannel().sendMessageEmbeds(error.build()).queue();
            }
            else
            {
                Member member = event.getMember();
                GuildVoiceState memberVoiceState = member.getVoiceState();
                if(!memberVoiceState.inAudioChannel())
                {
                    EmbedBuilder error = new EmbedBuilder();
                    error.setTitle("Member is not in a voice channel");
                    event.getChannel().sendMessageEmbeds(error.build()).queue();
                }
            }

            getGuildAudioPlayer(self.getGuild()).scheduler.isRepeating = !getGuildAudioPlayer(self.getGuild()).scheduler.isRepeating;

            if(getGuildAudioPlayer(self.getGuild()).scheduler.isRepeating)
            {
                EmbedBuilder success = new EmbedBuilder();
                success.setTitle("Now repeating " + getGuildAudioPlayer(event.getGuild()).player.getPlayingTrack().getInfo().title);
                event.getChannel().sendMessageEmbeds(success.build()).queue();
            }
            else
            {
                EmbedBuilder success = new EmbedBuilder();
                success.setTitle("No longer repeating " + getGuildAudioPlayer(event.getGuild()).player.getPlayingTrack().getInfo().title);
                event.getChannel().sendMessageEmbeds(success.build()).queue();
            }

        }
    }
}
