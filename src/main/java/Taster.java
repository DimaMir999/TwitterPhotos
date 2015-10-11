import org.apache.log4j.Logger;
import twitter4j.*;

import java.io.IOException;

import java.util.HashMap;


public class Taster {

    private static String sashsa = "alexandra_meoww";
    private static String anton = "hammer4thesmith";
    private static String andrei = "andrewhindcrea";
	private static String erik = "eko24ive";
    private static String path = "src/main/webapp/";
    static Logger logger = Logger.getLogger("console");

    public static void main(String[] args) throws TwitterException, IOException, InterruptedException {
        IdealCollageMaker cm = new IdealCollageMaker(path, logger);
        cm.makeCollage(anton, 400, new FormatPicture(FormatPicture.FORMAT_3X2), new HashMap<Long, Count_Picture>(),
        new HashMap<String, long[]>());
    }
}
