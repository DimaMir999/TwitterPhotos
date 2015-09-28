
import org.apache.log4j.Logger;
import twitter4j.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TwitterUtil {

    private static Twitter twitter = TwitterFactory.getSingleton();
    private Logger logger;

    public TwitterUtil(Logger logger){
        this.logger = logger;
    }

    public long[] getIdsFriends(String login) throws TwitterException {
        long[] ids = twitter.getFriendsIDs(login, -1).getIDs();
        logger.info("Ids users for " + login + " are came");
        return ids;
    }

    public Count_Picture getPhotoURL(long id) throws TwitterException, IOException {
        User user = twitter.showUser(id);
        Count_Picture cp = new Count_Picture(user.getOriginalProfileImageURL(), user.getStatusesCount());
        cp.loadImage();
        logger.info("URL of picture from twitter about " + user.getName() + " are came");
        return cp;
    }
}
