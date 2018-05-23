import com.emoji_er.Logger;
import com.emoji_er.MyListener;
import com.emoji_er.Output;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.fusesource.jansi.AnsiConsole;
import sun.misc.Signal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static org.fusesource.jansi.Ansi.ansi;
public class BOT
{
    public static void main(String[] arguments) throws Exception
    {
        System.out.print((char)27+"[?25l");
        Connection conn=null;
        AnsiConsole.systemInstall();

        testEnv();

        Logger.tlogger.setPriority(Thread.NORM_PRIORITY - 1);
        Logger.tlogger.start();

        Logger.logger.logInit();
        Logger.logger.logGeneral("-----------SYSTEM STARTED------------");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("\r"+"Missing posgresql JDBC Driver!");
            e.printStackTrace();
            return;
        }
        try {
            String url= System.getenv("DATABASE_URL");
            String username = System.getenv("DATABASE_USER");
            String password = System.getenv("DATABASE_PASSWORD");
            Logger.logger.logGeneral("Connecting to: "+ url);
            conn = DriverManager.getConnection("jdbc:"+url,username,password);
            conn.setAutoCommit(false);
            Logger.logger.logGeneral("SQL INITIALIZZATED");
        } catch (SQLException ex) {
            Logger.logger.logGeneral("SQLException: " + ex.getMessage());
            Logger.logger.logGeneral("SQLState: " + ex.getSQLState());
            Logger.logger.logGeneral("VendorError: " + ex.getErrorCode());
            System.exit(-1);
        }

        JDA api = new JDABuilder(AccountType.BOT).setToken(System.getenv("BOT_TOKEN")).buildAsync();

        MyListener listener = new MyListener(conn);

        Signal.handle(new Signal("INT"), sig -> {
            Logger.started = false;
            System.out.println((char)27+"[?25h");
            System.err.println(ansi().fgRed().a("Received SIGINT").reset());
            api.shutdown();
            listener.close();
            Logger.tlogger.interrupt();
            try {
                Logger.tlogger.join();
            }catch (Exception ignore){}
            Logger.logger.closeFiles();
            System.exit(sig.getNumber());
        });

        api.addEventListener(listener);
        api.getPresence().setGame(Game.playing("v1.7.11 - em prj"));

        while (!Logger.started && !Thread.interrupted()) ;

        Output.run();
    }

    private static void testEnv() throws Exception {
        Map<String, String> env = System.getenv();

        String var = env.get("BOT_TOKEN");
        if (var == null || var.isEmpty())
            throw new Exception("Missing environement variable: BOT_TOKEN");

        var = env.get("DATABASE_URL");
        if (var == null || var.isEmpty())
            throw new Exception("Missing environement variable: DATABASE_URL");

        var = env.get("DATABASE_USER");
        if (var == null || var.isEmpty())
            throw new Exception("Missing environement variable: DATABASE_USER");

        var = env.get("DATABASE_PASSWORD");
        if (var == null || var.isEmpty())
            throw new Exception("Missing environement variable: DATABASE_PASSWORD");

        var = env.get("DEFAULT_EMOJI_PREFIX");
        if (var == null || var.isEmpty())
            throw new Exception("Missing environement variable: DEFAULT_EMOJI_PREFIX");

        var = env.get("DISCORDBOTS_KEY");
        if (var == null)
            throw new Exception("Missing environement variable: DISCORDBOTS_KEY (can be empty)");

        var = env.get("SUPPORT_GUILD_ID");
        if (var == null || var.isEmpty())
            throw new Exception("Missing environement variable: SUPPORT_GUILD_ID");
        else
            try {
                Long.parseLong(var);
            } catch (NumberFormatException ex) {
                throw new Exception("Environement variable ( SUPPORT_GUILD_ID ) is not valid");
            }

        var = env.get("OWNER_ID");
        if (var == null || var.isEmpty() || Long.parseLong(var) == 0)
            throw new Exception("Missing environement variable: OWNER_ID");
        else
            try {
                Long.parseLong(var);
            } catch (NumberFormatException ex) {
                throw new Exception("Environement variable ( OWNER_ID ) is not valid");
            }

    }

}
