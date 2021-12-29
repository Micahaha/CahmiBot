package CahmiBot;

import CahmiBot.Commands.CheckUser;
import CahmiBot.Commands.Clear;
import CahmiBot.Commands.KickCommand;
import CahmiBot.Commands.Ping;
import CahmiBot.Events.GuildMemberJoin;
import CahmiBot.Events.GuildMemberLeave;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static String prefix = "~";
    public static JDA jda;

    public static void main(String[] args) throws LoginException, InterruptedException {

        // JDA Bot THEN add new commands!

        jda = JDABuilder.createDefault(findToken())
                .addEventListeners(new Ping(), new CheckUser(), new Clear(), new GuildMemberJoin(), new GuildMemberLeave(), new KickCommand())
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();


        jda.awaitReady();


    }

    public static String findToken()
    {
        File tokenFile = new File("C:\\Users\\Craft\\Documents\\Java\\jdabot\\src\\main\\java\\token.txt");
        String token = null;

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
