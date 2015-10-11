import org.apache.log4j.Logger;
import twitter4j.TwitterException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class CollageMaker {

	public final static String EXTENSION = ".jpg";
	protected Logger logger;
	protected TwitterUtil util;

	protected String path;

	public abstract void makeCollage(String login, int sizeConst, FormatPicture format,
	        Map<Long, Count_Picture> pictureCache, HashMap<String, long[]> idsCache)
			throws IOException, TwitterException;

	protected long[] getIds(String login, Map<String, long []> cache) throws TwitterException, IOException {
		long [] ids = cache.get(login);
		if(ids == null) {
			if(this.util == null)
				this.util = new TwitterUtil(logger);
			ids = util.getIdsFriends(login);
			cache.put(login, ids);
		}
		return ids;
	}

	protected void savePicture(File file, BufferedImage picture) throws IOException {
		if(file.mkdirs())
			ImageIO.write(picture, "png", file);
		else
			logger.error("some trobles with creating directories");
	}

	protected BufferedImage imageToFoursquare(BufferedImage image){
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

	protected Count_Picture getPhoto(long id, Map<Long, Count_Picture> cache) throws TwitterException, IOException {
		Count_Picture cp = cache.get(id);
		if(cp == null) {
			if(this.util == null)
				this.util = new TwitterUtil(logger);
			cp = util.getPhotoURL(id);
			cp.loadImage();
			//savePicture(new File(path + "testPhotos/" + cp.getUrl() + EXTENSION), cp.getImage());
			cp.setImage(imageToFoursquare(cp.getImage()));
			cache.put(id, cp);
		}
		return cp;
	}

	protected int calculateCount(int realSquare, FormatPicture format){
		int count = 0, i = 0, square = format.getHeigth() * format.getWeight(), prev = 0;
		while(i < realSquare){
			count++;
			i = square * count * count;
		}
		return count;
	}
}
