package CahmiBot.Commands.Music;

import CahmiBot.Main;
import CahmiBot.Services.Lavaplayer.AudioController;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Queue;

import static CahmiBot.Services.Lavaplayer.AudioController.getTimestamp;

public class QueueCommand extends ListenerAdapter
{
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentRaw().split("\\s");
        if(args[0].equalsIgnoreCase(Main.prefix + "queue"))
        {
            Queue<AudioTrack> queue = AudioController.getGuildAudioPlayer(event.getGuild()).scheduler.queue;
            synchronized (queue)
            {
                if(queue.isEmpty() && AudioController.getGuildAudioPlayer(event.getGuild()).player.getPlayingTrack() == null)
                {
                    event.getChannel().sendMessage("The queue is empty").queue();
                }
                else if(AudioController.getGuildAudioPlayer(event.getGuild()).player.getPlayingTrack() != null && queue.isEmpty())
                {
                    AudioTrack audioTrack = AudioController.getGuildAudioPlayer(event.getGuild()).player.getPlayingTrack();

                    EmbedBuilder success = new EmbedBuilder();
                    success.setColor(Color.GREEN);
                    success.addField("Playing Track: ",audioTrack.getInfo().title,false);
                    success.addField("Author name: ",audioTrack.getInfo().author,false);
                    success.setFooter("Duration: "  + getTimestamp(audioTrack.getDuration()));

                    event.getChannel().sendMessageEmbeds(success.build()).queue();
                }
                else
                {
                    int trackCount = 0;
                    long queueLength = 0;
                    AudioTrack audioTrack = AudioController.getGuildAudioPlayer(event.getGuild()).player.getPlayingTrack();
                    EmbedBuilder success = new EmbedBuilder();

                    success.setColor(Color.GREEN);
                    success.addField("Current song playing: " + audioTrack.getInfo().title + "\n","Duration: " + getTimestamp(audioTrack.getDuration()),true);
                    success.addField("Current queue ", "entries: " + queue.size(), false);
                    for(AudioTrack track : queue)
                    {
                        queueLength += track.getDuration();
                        if(trackCount < 10)
                        {
                            success.addField(track.getInfo().title,"`[" + getTimestamp(track.getDuration()) + "]` ",false);
                            trackCount++;
                        }
                    }
                    success.addField("Total queue length: ",(getTimestamp(queueLength)),false);

                    event.getChannel().sendMessageEmbeds(success.build()).queue();

                }
            }
        }
    }
}
