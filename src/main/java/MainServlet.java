import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class MainServlet extends HttpServlet {

    private final static String ERROR = "ERROR";
    private final static String PATH = "collages/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Logger logger = (Logger)getServletContext().getAttribute("log4j");
        String login = request.getParameter("login");
        String sizeConst = request.getParameter("size_const");
        String format = request.getParameter("format");
        logger.info("process MainServlet with params: login = " + login + " ; sizeConst = " + sizeConst +
        " ; format = " + format);
        HttpSession session = request.getSession();
        ArrayList<String> list;
        if(session.getAttribute("owners") == null) {
            list = new ArrayList<String>();
            session.setAttribute("owners", list);
        }
        else
            list = (ArrayList<String>) session.getAttribute("owners");
        list.add(login);

        String path = getServletContext().getResource("/").getPath();
        SimpleCollageMaker maker = new SimpleCollageMaker(path, logger);
        Object pictureCacheObj = session.getAttribute("pictureCache");
        HashMap<Long, Count_Picture> pictureCache;
        if(pictureCacheObj == null){
            pictureCache = new HashMap<Long, Count_Picture>();
            session.setAttribute("pictureCache", pictureCache);
        }
        else {
            pictureCache = (HashMap<Long, Count_Picture>)pictureCacheObj;
        }
        Object idsCacheObj = session.getAttribute("idsCache");
        HashMap<String, long[]> idsCache;
        if(pictureCacheObj == null){
            idsCache = new HashMap<String, long[]>();
            session.setAttribute("idsCache", idsCache);
        }
        else {
            idsCache = (HashMap<String, long[]>)idsCacheObj;
        }
        FormatPicture fp = null;
        boolean isMake = true;
        try{
            fp = new FormatPicture(Integer.parseInt(format));
            maker.makeCollage(login, Integer.parseInt(sizeConst), fp, pictureCache, idsCache);
        }catch (Exception e) {
            e.printStackTrace();
            isMake = false;
            logger.error(e.getMessage());
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            if (isMake) {
                out.println(PATH + "/" + login + "/" + fp.getName() + "_" + sizeConst + SimpleCollageMaker.EXTENSION);
                logger.info("Collage of " + login + " is send to client");
            } else {
                out.println(ERROR);
                logger.info("Some problems with collage of " + login);
            }
        }finally {
            out.close();
        }
    }
}