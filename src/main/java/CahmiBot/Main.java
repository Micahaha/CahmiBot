package CahmiBot;

import CahmiBot.Commands.*;
import CahmiBot.Commands.Music.*;
import CahmiBot.Events.GuildMemberJoin;
import CahmiBot.Events.GuildMemberLeave;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static String prefix = "~";
    public static JDA jda;




    public static void main(String[] args) throws LoginException, InterruptedException {

        // JDA Bot THEN add new commands!


        // TODO: 1/3/2022 Optimize the process of adding new commands. Clean this code up basically


        jda = JDABuilder.createDefault(findToken())
                .addEventListeners(new Ping(), new CheckUser(), new Clear(), new GuildMemberJoin(), new GuildMemberLeave(), new KickCommand(), new MuteCommand(), new PlayCommand(), new RepeatCommand(), new SkipCommand(),new QueueCommand(), new JoinCommand(), new StopCommand(), new PauseCommand())
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES)
                .enableCache(CacheFlag.VOICE_STATE)
                .build();


        jda.awaitReady();

    }

    public static String findToken()
    {
        File tokenFile = new File("C:\\Users\\Craft\\Documents\\Java\\jdabot\\src\\main\\java\\token.txt");

        try(Scanner s = new Scanner(tokenFile))
        {
            if(s.hasNextLine())
            {
                token = s.nextLine();
                return token;
            }
            else
            {
                throw new RuntimeException("The token file is empty");
            }

        }
        catch(IOException e)
        {
            System.out.println("File not found!");
            return null;
        }
    }
}
