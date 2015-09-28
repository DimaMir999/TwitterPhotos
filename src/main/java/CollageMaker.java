import org.apache.log4j.Logger;
import twitter4j.TwitterException;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class CollageMaker {

    private final static int GAP_COUNT = 4;
    public final static String EXTENSION = ".jpg";
    private Logger logger;
    private TwitterUtil util;

    private final String path;

    public CollageMaker(String path, Logger logger) {
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

    private Count_Picture getPhoto(long id, Map<Long, Count_Picture> cache) throws TwitterException, IOException {
        Count_Picture cp = cache.get(id);
        if(cp == null) {
            if(this.util == null)
                this.util = new TwitterUtil(logger);
            cp = util.getPhotoURL(id);
            cp.loadImage();
            cp.setImage(imageToFoursquare(cp.getImage()));
            cache.put(id, cp);
        }
        return cp;
    }

    private long[] getIds(String login, Map<String, long []> cache) throws TwitterException, IOException {
        long [] ids = cache.get(login);
        if(ids == null) {
            if(this.util == null)
                this.util = new TwitterUtil(logger);
            ids = util.getIdsFriends(login);
            cache.put(login, ids);
        }
        return ids;
    }

    private int calculateCount(int realSquare, FormatPicture format){
        int i = 0, count = 0, square = format.getHeigth() * format.getWeight();
        while(count < realSquare){
            i++;
            count = square * i * i;

        }
        return i;
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

    private void savePicture(File file, BufferedImage picture) throws IOException {
        if(file.mkdirs())
            ImageIO.write(picture, "png", file);
        else
            logger.error("some trobles with creating directories");
    }

    public BufferedImage imageToFoursquare(BufferedImage image){
        if(image.getWidth() != image.getHeight()) {
            if(image.getHeight() > image.getWidth()) {
                image = image.getSubimage(0, image.getHeight() / 2 - image.getWidth() / 2,
                        image.getWidth(), image.getWidth());
            }
            else{
                image = image.getSubimage(image.getWidth() / 2 - image.getHeight() / 2, 0,
                        image.getHeight(), image.getHeight());
            }
        }
        return image;
    }


    // second algorithm




    /*public void makeCollage2(String login, int sizeConst, FormatPicture format) throws IOException, TwitterException {
        File file = new File(path + "collages/" + login + "/" + format.getName() + EXTENSION);
        if (!file.exists()) {
            TwitterUtil util = new TwitterUtil(logger);
            List<Count_Picture> pictures = util.getPhotoURLs(login);
            for (Count_Picture cp : pictures)
                cp.loadImage();
            List<Count_Picture>[] arraysForGaps = makeGapList(pictures);
            int count = calculateCount(calculateSquare(arraysForGaps), format);
            BufferedImage picture = makePicture2(arraysForGaps, count, sizeConst, format);
            savePicture(file, picture);
            System.out.println("makePicture2");
        } else
            System.out.println("getReadyPicture");
    }


    private int calculateSquare(List<Count_Picture>[] arraysForGaps) {
        int square = 0;
        for(int i = 0;i < arraysForGaps.length;i++)
            square += arraysForGaps[i].size() * Math.pow(i + 1, 2);
        return square;
    }

    private BufferedImage makePicture2(List<Count_Picture>[] arrayForGaps, int count, int sizeConst, FormatPicture format)
            throws IOException {
        int length = sizeConst / count;
        BufferedImage collage = new BufferedImage(format.getWeight() * sizeConst,
                format.getHeigth() * sizeConst, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = collage.getGraphics();
        Image frame = ImageIO.read(new File(path + "WEB-INF/classes/frame.png"));
        Image[] frames = new Image[GAP_COUNT];
        BufferedImage image;
        for(int i = 0;i < arrayForGaps.length;i++) {
            int size = (i + 1) * length;
            frames[i] = frame.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            for (Count_Picture cp : arrayForGaps[i]) {
                image = (BufferedImage) cp.getImage();
                imageToFoursquare(image);
                cp.setImage(image.getScaledInstance(size, size, Image.SCALE_SMOOTH));
            }
        }
        int height = format.getWeight() * count, weight = format.getWeight() * count;
        for(int i = 0;i < arrayForGaps.length;i++) {
            List<Count_Picture> pictures = arrayForGaps[i];
            while (!pictures.isEmpty()) {

                int x, y;
                graphics.drawImage(pictures.remove(0).getImage(),x ,y , null);
                graphics.drawImage(frame,x ,y , null);
            }
        }
        return collage;
    }

    private List<Count_Picture>[] makeGapList(List<Count_Picture> pictures){
        Comparator<Count_Picture> comparator = new Comparator<Count_Picture>() {
            public int compare(Count_Picture o1, Count_Picture o2) {
                return o1.getCount() - o2.getCount();
            }
        };
        pictures.sort(comparator);
        int minCount = pictures.get(0).getCount();
        double gapLength = (double)(pictures.get(pictures.size() - 1).getCount() - minCount)/GAP_COUNT;
        List<Count_Picture>[] arraysForGaps = new List[GAP_COUNT];
        for(List<Count_Picture> list : arraysForGaps) {
            list = new ArrayList<Count_Picture>();
        }
        for(Count_Picture cp : pictures){
            int count = cp.getCount();
            if(count < minCount + gapLength) {
                arraysForGaps[0].add(cp);
            }else if(count < minCount + 2 * gapLength) {
                arraysForGaps[1].add(cp);
            }else if(count < minCount + 3 * gapLength) {
                arraysForGaps[2].add(cp);
            }else {
                arraysForGaps[3].add(cp);
            }
            return arraysForGaps;
        }

        for(List<Count_Picture> list : arraysForGaps) {
            list.sort(comparator);
        }
        return arraysForGaps;
    }

    private void normalizeData(List<Count_Picture>[] arraysForGaps, int square, FormatPicture format){
        int dif = (int) Math.round((double)square / format.getWeight() / format.getHeigth());
    }*/
}
