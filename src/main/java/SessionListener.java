import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class SessionListener implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent se) {  }

    public void sessionDestroyed (HttpSessionEvent se){
        Logger logger = (Logger) se.getSession().getServletContext().getAttribute("log4j");
        Object ownersObj = se.getSession().getAttribute("owners");
        if(ownersObj != null){
            ArrayList<String> owners = (ArrayList<String>) ownersObj;
            String path = null;
            try {
                path = se.getSession().getServletContext().getResource("/").getPath();
                for(String owner : owners)
                    try {
                        File file = new File(path + "/collages/" + owner);
                        FileUtils.deleteDirectory(file);
                        logger.info("Collages of " + owner + " was deleted");
                    }catch (IOException e) {
                            e.printStackTrace();
                            logger.info("Collages of " + owner + " was not deleted");
                    }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}
