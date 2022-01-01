package CahmiBot.Commands;

import CahmiBot.Main;
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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Music extends ListenerAdapter {

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    public Music()
    {
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    // COMMAND ARGUMENTS
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentRaw().split("\\s");

        if(args[0].equalsIgnoreCase(Main.prefix + "play") && args.length == 2)
        {
            loadAndPlay((TextChannel) event.getChannel(), args[1]);
        }
        else if(args[0].equalsIgnoreCase(Main.prefix + "skip"))
        {
            skipTrack((TextChannel) event.getChannel());
        }
        else if(args[0].equalsIgnoreCase(Main.prefix + "pause") || args[0].equalsIgnoreCase(Main.prefix + "play"))
        {
            pauseAndPlay((TextChannel) event.getChannel());
        }

        //Non Functional method as of right now, Need to learn streams ):

    /*    else if(args[0].equals(Main.prefix + "queue"))
        {
            for(String song : songList((TextChannel) event.getChannel()))
            {
                event.getChannel().sendMessage(song).queue();
            }
        }
    */

    }


    //MUSIC HANDLER METHODS

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild)
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

    private void loadAndPlay(final TextChannel channel, final String trackUrl)
    {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {

                play(channel.getGuild(), musicManager, audioTrack);

                EmbedBuilder success = new EmbedBuilder();
                success.setColor(Color.GREEN);
                success.setTitle("Added: " + audioTrack.getInfo().title);
                success.addField("Author name: ",audioTrack.getInfo().author,false);
                success.setFooter("Duration: "  + (audioTrack.getDuration() / 60000) + ":" + ((audioTrack.getDuration()) / 1000) % 60);

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

   /* private List<String> songList(TextChannel channel)
    {
        List<String> songTitles = null;
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        for(AudioTrack track : musicManager.scheduler.queue)
            {
                songTitles.add(track.getInfo().title);
            }

            return songTitles;
    }
    */

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack audioTrack) {
        connectToFirstVoiceChannel(guild.getAudioManager());
        musicManager.scheduler.queue(audioTrack);
    }
    private void skipTrack(TextChannel channel)
    {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();

        channel.sendMessage("Skipped to next Track!!").queue();
    }



    private void pauseAndPlay(TextChannel channel)
    {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        if(!getGuildAudioPlayer(channel.getGuild()).player.isPaused())
        {
            getGuildAudioPlayer(channel.getGuild()).player.setPaused(true);
        }
        else
        {
            getGuildAudioPlayer(channel.getGuild()).player.setPaused(false);
        }
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager)
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
}
