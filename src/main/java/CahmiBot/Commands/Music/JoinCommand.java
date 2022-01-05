package CahmiBot.Commands.Music;

import CahmiBot.Main;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static CahmiBot.Services.Lavaplayer.AudioController.*;

public class JoinCommand extends ListenerAdapter
{
    public void onMessageReceived(MessageReceivedEvent event)
    {
            final Member self = event.getGuild().getSelfMember();
            final GuildVoiceState selfVoiceState = self.getVoiceState();
            final Member member = event.getMember();
            final GuildVoiceState memberVoiceState  = member.getVoiceState();
            final AudioChannel memberChannel = memberVoiceState.getChannel();

        String[] args = event.getMessage().getContentRaw().split("\\s");

        if(args[0].equalsIgnoreCase(Main.prefix + "join"))
        {
            // Don't leave the voice channel it's currently in

            if(selfVoiceState.inAudioChannel())
            {
                event.getChannel().sendMessage("Can't join! I'm currently in another voice channel right now!").queue();
            }

            event.getChannel().sendMessage("Connecting to " + memberChannel.getName()).queue();
        }
    }
}
