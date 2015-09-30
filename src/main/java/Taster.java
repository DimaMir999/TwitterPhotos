import org.apache.log4j.Logger;
import twitter4j.*;

import java.io.IOException;

import java.util.HashMap;


public class Taster {

    private static String sashsa = "alexandra_meoww";
    private static String anton = "hammer4thesmith";
    private static String andrei = "andrewhindcrea";
    private static String path = "src/main/webapp/collages/";
    static Logger logger = Logger.getLogger("console");

    public static void main(String[] args) throws TwitterException, IOException, InterruptedException {
        CollageMaker cm = new CollageMaker(path, logger);
        cm.makeCollage2(anton, 400, new FormatPicture(FormatPicture.FORMAT_4X3), new HashMap<Long, Count_Picture>(), new HashMap<String, long[]>());
    }
}
