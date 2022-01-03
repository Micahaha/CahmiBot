package CahmiBot.Services.Lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    // Instance variables for TrackScheduler

    private final AudioPlayer player;
    public final LinkedList<AudioTrack> queue;


    // Constructor for TrackScheduler

    public TrackScheduler(AudioPlayer player)
    {
        this.player = player;
        this.queue = new LinkedList<>();
    }

    // if not currently playing a track, start a new one.
    public void queue(AudioTrack track)
    {
        if(!this.player.startTrack(track, true))
        {
            queue.offer(track);
        }
    }


    // Start next track
    public void nextTrack()
    {
        player.startTrack(queue.poll(), false);
    }



    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {

        if(endReason.mayStartNext){
            nextTrack();
        }
    }
}
