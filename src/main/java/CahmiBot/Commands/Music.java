package CahmiBot.Commands;

import CahmiBot.Main;
import CahmiBot.Services.Lavaplayer.GuildMusicManager;
import CahmiBot.Services.Lavaplayer.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sun.jndi.toolkit.url.Uri;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;



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


    // TODO: 1/3/2022 Develop a command handler

    // COMMAND ARGUMENTS
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {


        String[] args = event.getMessage().getContentRaw().split("\\s");

        if(args[0].equalsIgnoreCase(Main.prefix + "play"))
        {

            GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
            String link = args[1];
            StringBuilder sb = new StringBuilder("ytsearch:");

                if(!link.contains("https://"))
                {

                   /*
                    // TODO: 1/2/2022
                    Implement soundcloud usage in command
                   */

                    for(int i = 1; i < args.length; i++)
                    {
                        sb.append(" " + args[i]);
                    }
                    loadAndPlay((TextChannel) event.getChannel(), sb.toString());
                    System.out.println("hi");
                    System.out.println(sb.toString());
                }
                else
                {
                    loadAndPlay((TextChannel) event.getChannel(), args[1]);
                    System.out.println("I passed");
                }
        }
        else if(args[0].equalsIgnoreCase(Main.prefix + "skip"))
        {
            skipTrack((TextChannel) event.getChannel());
        }
        else if(args[0].equalsIgnoreCase(Main.prefix + "pause"))
        {
            pauseAndPlay((TextChannel) event.getChannel());
        }
        else if(args[0].equalsIgnoreCase(Main.prefix + "join"))
        {
            // Don't leave the voice channel it's currently in

            final TextChannel channel = event.getTextChannel();
            final Member self = event.getGuild().getSelfMember();
            final GuildVoiceState selfVoiceState = self.getVoiceState();
            if(selfVoiceState.inAudioChannel())
            {
                event.getChannel().sendMessage("Can't join! I'm currently in another voice channel right now!").queue();
                return;
            }
                // Member who called command

                final Member member = event.getMember();
                final GuildVoiceState memberVoiceState  = member.getVoiceState();


                final AudioManager audioManager = event.getGuild().getAudioManager();
                final AudioChannel memberChannel = memberVoiceState.getChannel();

                audioManager.openAudioConnection(memberChannel);
                channel.sendMessage("Connecting to " + memberChannel.getName()).queue();
        }

        else if(args[0].equalsIgnoreCase(Main.prefix + "leave"))
        {
            final Member self = event.getGuild().getSelfMember();
            final GuildVoiceState selfVoiceState = self.getVoiceState();
            final AudioManager audioManager = event.getGuild().getAudioManager();
            GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());

            if(selfVoiceState.inAudioChannel())
            {
                event.getChannel().sendMessage("Leaving Channel").queue();
                musicManager.player.stopTrack();
                audioManager.closeAudioConnection();
            }
            else
            {
                event.getChannel().sendMessage("Not in a voice channel.").queue();
            }

        }


        else if(args[0].equalsIgnoreCase(Main.prefix + "queue"))
        {
           Queue<AudioTrack> queue = getGuildAudioPlayer(event.getGuild()).scheduler.queue;
           synchronized (queue)
           {
               if(queue.isEmpty() && getGuildAudioPlayer(event.getGuild()).player.getPlayingTrack() == null)
               {
                   event.getChannel().sendMessage("The queue is empty").queue();
               }
               else if(getGuildAudioPlayer(event.getGuild()).player.getPlayingTrack() != null && queue.isEmpty())
               {
                   AudioTrack audioTrack = getGuildAudioPlayer(event.getGuild()).player.getPlayingTrack();

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
                   AudioTrack audioTrack = getGuildAudioPlayer(event.getGuild()).player.getPlayingTrack();
                   EmbedBuilder success = new EmbedBuilder();

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

             /*   AudioTrack firstTrack = audioPlaylist.getSelectedTrack();

                if(firstTrack == null)
                {
                    firstTrack = audioPlaylist.getTracks().get(0);
                }
                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + audioPlaylist.getName() + ")").queue();


                play(channel.getGuild(), musicManager, firstTrack);

            */
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
    private static String getTimestamp(long milliseconds)
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
    private boolean isUrl(String url)
    {
        try
        {
            new URI(url);
            return true;
        }
        catch (URISyntaxException e)
        {
            return false;
        }
    }
}
