package CahmiBot.Services.Lavaplayer;

import CahmiBot.Services.Lavaplayer.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AudioController {

    private static AudioPlayerManager playerManager = null;
    private static Map<Long, GuildMusicManager> musicManagers = null;


    public AudioController()
    {
        musicManagers = new HashMap<>();
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }


    public static void play(Guild guild, GuildMusicManager musicManager, AudioTrack audioTrack) {


        connectToFirstVoiceChannel(guild.getAudioManager());
        musicManager.scheduler.queue(audioTrack);
    }



    public static void connectToFirstVoiceChannel(AudioManager audioManager)
    {
        if(!audioManager.isConnected())
        {
            for(VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels())
            {
                audioManager.openAudioConnection(voiceChannel);
                break;
            }
        }
    }

    public static void skipTrack(TextChannel channel)
    {
        GuildMusicManager musicManager;
        musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();

        channel.sendMessage("Skipped to next Track!!").queue();
    }


    public static void pauseAndPlay(TextChannel channel)
    {
        final GuildMusicManager guildAudioPlayer = getGuildAudioPlayer(channel.getGuild());

        guildAudioPlayer.player.setPaused(!guildAudioPlayer.player.isPaused());
    }

    public static synchronized GuildMusicManager getGuildAudioPlayer(Guild guild)
    {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if(musicManager == null)
        {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId,musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }

    public static String getTimestamp(long milliseconds)
    {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

    // Checks if it is a URL, if it's not, we return this to be false.
    public static boolean isUrl(String url)
    {

        if(url.contains("https://"))
            return true;
        else
        {
            return false;
        }
    }

    public static void loadAndPlay(final TextChannel channel, final String trackUrl)
    {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {

                play(channel.getGuild(), musicManager, audioTrack);

                EmbedBuilder success = new EmbedBuilder();success.setColor(Color.GREEN);
                success.setTitle("Added: " + audioTrack.getInfo().title);
                success.addField("Author name: ",audioTrack.getInfo().author,false);
                success.setFooter("Duration: "  + getTimestamp(audioTrack.getDuration()));

                channel.sendMessageEmbeds(success.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

                AudioTrack firstTrack = audioPlaylist.getSelectedTrack();


                if(firstTrack == null)
                {
                    firstTrack = audioPlaylist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + audioPlaylist.getName() + ")").queue();

                play(channel.getGuild(), musicManager, firstTrack);

            }

            @Override
            public void noMatches() {

                channel.sendMessage("Nothing was found from: " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException e)
            {
                channel.sendMessage("Could not play: " + e.getMessage()).queue();
            }
        });
    }
}
