import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;

import javax.servlet.http.HttpServlet;
import java.net.MalformedURLException;

public class Log4jInit extends HttpServlet {

    @Override
    public void init() {
        try {
            System.setProperty("rootPath", getServletContext().getResource("/").getPath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String logfilename = getInitParameter("logfile");
        String pref;
        pref = getServletContext().getRealPath("/");
        PropertyConfigurator.configure(pref + logfilename);
        Logger globallog = Logger.getRootLogger();
        getServletContext().setAttribute("log4j", globallog);
        globallog.info("Load-onstart-up Servlet");

    }
}
