import org.apache.log4j.Logger;
import twitter4j.TwitterException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SimpleCollageMaker extends CollageMaker{

    public SimpleCollageMaker(String path, Logger logger) {
        this.path = path;
        this.logger = logger;
    }

    public void makeCollage(String login, int sizeConst, FormatPicture format,
            Map<Long, Count_Picture> pictureCache, HashMap<String, long[]> idsCache)
            throws IOException, TwitterException {
        File file = new File(path + "collages/" + login + "/" + format.getName() + "_" + Integer.toString(sizeConst) + EXTENSION);
        if(!file.exists()){
            List<Count_Picture> pictures = new ArrayList<Count_Picture>();
            long[] ids = getIds(login, idsCache);
            for(long id : ids)
                pictures.add(getPhoto(id, pictureCache));
            int count = calculateCount(pictures.size(), format);
            BufferedImage picture = makePicture(pictures, count, sizeConst, format);
            savePicture(file, picture);
            logger.info("Collage is made and save on server repository");
        }
        else
            logger.info("Collage is returned from server repository");
    }

    private BufferedImage makePicture(List<Count_Picture> pictures, int count, int sizeConst, FormatPicture format)
            throws IOException {
        int length = sizeConst / count;
        BufferedImage collage = new BufferedImage(format.getWeight() * sizeConst,
                format.getHeigth() * sizeConst, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = collage.getGraphics();
        Image frame = ImageIO.read(new File(path + "WEB-INF/classes/frame.png")); //"src/main/resources/frame.png"
        frame = frame.getScaledInstance(length, length, Image.SCALE_SMOOTH);
        Image image;
        for(int i = 0;!pictures.isEmpty();i++){
            image = pictures.remove(0).getImage().getScaledInstance(length, length, Image.SCALE_SMOOTH);
            int x = (i % (format.getWeight() * count)) * length;
            int y = (i / (format.getWeight() * count)) * length;
            graphics.drawImage(image, x ,y , null);
            graphics.drawImage(frame,x ,y , null);
        }
        return collage;
    }
}
