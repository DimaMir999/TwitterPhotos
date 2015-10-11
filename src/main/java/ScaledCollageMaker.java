import org.apache.log4j.Logger;
import twitter4j.TwitterException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ScaledCollageMaker extends CollageMaker {

	private final static int GAP_COUNT = 4;

	public ScaledCollageMaker(String path, Logger logger) {
		this.path = path;
		this.logger = logger;
	}

	@Override
	public void makeCollage(String login, int sizeConst, FormatPicture format,
	        Map<Long, Count_Picture> pictureCache, HashMap<String, long[]> idsCache)
			throws IOException, TwitterException {
		File file = new File(path + "collages/" + login + "/" + format.getName() + "_" + Integer.toString(sizeConst) + EXTENSION);
		if(!file.exists()){
			List<Count_Picture> pictures = new ArrayList<Count_Picture>();
			long[] ids = getIds(login, idsCache);
			for(long id : ids)
				pictures.add(getPhoto(id, pictureCache));
			List<Count_Picture>[] arraysForGaps = makeGapList(pictures);
			int count = calculateCount(calculateSquare(arraysForGaps), format);
			BufferedImage picture = makePicture(arraysForGaps, count, sizeConst, format);
			savePicture(file, picture);
			logger.info("Collage is made and save on server repository");
		}
		else
			logger.info("Collage is returned from server repository");
	}


	protected int calculateSquare(List<Count_Picture>[] arraysForGaps) {
		int square = 0;
		for(int i = 0;i < arraysForGaps.length;i++)
			square += arraysForGaps[i].size() * Math.pow(i + 1, 2);
		return square;
	}

	protected BufferedImage makePicture(List<Count_Picture>[] arrayForGaps, int count, int sizeConst, FormatPicture format)
			throws IOException {
		int length = sizeConst / count;
		BufferedImage collage = new BufferedImage(format.getWeight() * sizeConst,
				format.getHeigth() * sizeConst, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = collage.getGraphics();
		Image frame = ImageIO.read(new File(path + "../resources/frame.png")); //"WEB-INF/classes/frame.png"
		Image[] frames = new Image[GAP_COUNT];
		for(int i = 0;i < arrayForGaps.length;i++) {
			int size = (i + 1) * length;
			frames[i] = frame.getScaledInstance(size, size, Image.SCALE_SMOOTH);
		}

		int height = format.getHeigth() * count, weight = format.getWeight() * count;
		boolean [][] canvas = new boolean[height][];
		for(int j = 0;j < canvas.length;j++) {
			canvas[j] = new boolean[weight];
			for (int i = 0; i < canvas[j].length; i++)
				canvas[j][i] = false;

		}

		for(int i = arrayForGaps.length - 1;i > -1;i--) {
			List<Count_Picture> pictures = arrayForGaps[i];
			int x = 0, y = 0;
			while (!pictures.isEmpty()) {
				if(canPut(canvas, x, y, i + 1)){
					for(int k = y;k < y + i + 1;k++)
						for(int j = x;j < x + i + 1;j++)
							canvas[k][j] = true;
					graphics.drawImage(pictures.remove(0).getImage().getScaledInstance(length * (i + 1), length * (i + 1)
							, Image.SCALE_SMOOTH), x * length, y * length, null);
					graphics.drawImage(frames[i],x * length, y * length, null);
					System.out.println("size " + (i + 1) + " x = " + x + " y = " + y);
					x += i + 1;
				} else {
					x++;
				}
				if(x > weight){
					x = 0;
					y += 1;
				}
			}
		}
		return collage;
	}

	protected boolean canPut(boolean[][] canvas, int x, int y, int size){
		if(y + size > canvas.length || x + size > canvas[0].length)
			return false;
		for(int i = y;i < y + size;i++)
			for(int j = x;j < x + size;j++)
				if(canvas[i][j])
					return false;
		return true;
	}

	protected List<Count_Picture>[] makeGapList(List<Count_Picture> pictures){
		Comparator<Count_Picture> comparator = new Comparator<Count_Picture>() {
			public int compare(Count_Picture o1, Count_Picture o2) {
				return o1.getCount() - o2.getCount();
			}
		};
		pictures.sort(comparator);
		int minCount = pictures.get(0).getCount();
		double gapLength = (double)(pictures.get(pictures.size() - 1).getCount() - minCount)/GAP_COUNT;
		List<Count_Picture>[] arraysForGaps = new List[GAP_COUNT];
		for(int i = 0;i < arraysForGaps.length;i++) {
			arraysForGaps[i] = new LinkedList<Count_Picture>();
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
		}

		for(List<Count_Picture> list : arraysForGaps) {
			list.sort(comparator);
		}
		return arraysForGaps;
	}
}
