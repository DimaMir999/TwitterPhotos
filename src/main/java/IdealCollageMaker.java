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
import java.util.List;
import java.util.Map;

public class IdealCollageMaker extends ScaledCollageMaker{

	private final static int GAP_COUNT = 4;

	public IdealCollageMaker(String path, Logger logger) {
		super(path, logger);
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
			int square = calculateSquare(arraysForGaps);
			int count = calculateCount(square, format);
			normalizeData(arraysForGaps, count, square, format);
			BufferedImage picture = makePicture(arraysForGaps, count, sizeConst, format);
			savePicture(file, picture);
			logger.info("Collage is made and save on server repository");
		}
		else
			logger.info("Collage is returned from server repository");
	}

	@Override
	protected int calculateCount(int realSquare, FormatPicture format){
		int count = 0, i = 0, square = format.getHeigth() * format.getWeight();
		while(i < realSquare){
			count++;
			i = square * count * count;
		}
		if(i - realSquare < realSquare - (square * (count - 1)*(count - 1)))
			return count;
		else
			return count - 1;
	}

	private void normalizeData(List<Count_Picture>[] arraysForGaps, int count, int realSquare, FormatPicture format){
		System.out.println(realSquare);
		int [] avChanges = new int[GAP_COUNT - 1];
		for(int i = 0;i < avChanges.length;i++) {
			avChanges[i] = (i + 2)*(i + 2) - (i + 1) * (i + 1);
		}
		int dif = (format.getHeigth() * count) * (format.getWeight() * count) - realSquare;
		// перевіряти чи взагалі треба міняти категорії тут або перед викликом методу normalizeData
		if(dif < 0) {
			for (int i = avChanges.length - 1; i >= 0; i--) {
				List<Count_Picture> listUp = arraysForGaps[i + 1];
				List<Count_Picture> listDown = arraysForGaps[i];
				while (!listUp.isEmpty() && dif >= avChanges[i]) {
					listDown.add(listDown.size() - 1, listUp.remove(0));
					dif += avChanges[i];
				}
			}
			if (dif != 0) {
				int tryDif = dif;
				boolean flag = false;
				//додати цикл кількості додавань і відповідно додати ше break
				for (int j = 0; j < avChanges.length; j++) {
					tryDif -= avChanges[j];
					for (int k = 0; k < avChanges.length; k++) {
						if (k != j) {
							if (tryDif % avChanges[k] == 0 && Math.abs(tryDif / avChanges[k]) <= arraysForGaps[k + 1].size() &&
									!arraysForGaps[j+1].isEmpty()) {
								arraysForGaps[j + 1].add(0, arraysForGaps[j].remove(arraysForGaps[j].size() - 1));
								for (int l = 0; l < Math.abs(tryDif / avChanges[k]); l++) {
									arraysForGaps[k].add(arraysForGaps[k].size() - 1, arraysForGaps[k + 1].remove(0));
								}
								flag = true;
								break;
							}
						}
					}
					if (flag)
						break;
					tryDif = dif;
				}
			}
		} else {
			for (int i = avChanges.length - 1; i >= 0; i--) {
				List<Count_Picture> listUp = arraysForGaps[i + 1];
				List<Count_Picture> listDown = arraysForGaps[i];
				while (!listDown.isEmpty() && dif >= avChanges[i]) {
					listUp.add(0, listDown.remove(listDown.size() - 1));
					dif -= avChanges[i];
				}
			}
			if (dif != 0) {
				int tryDif = dif;
				boolean flag = false;
				//додати цикл кількості додавань і відповідно додати ше break
				for (int j = 0; j < avChanges.length; j++) {
					tryDif += avChanges[j];
					for (int k = 0; k < avChanges.length; k++) {
						if (k != j) {
							if (tryDif % avChanges[k] == 0 && Math.abs(tryDif / avChanges[k]) <= arraysForGaps[k + 1].size() &&
									!arraysForGaps[j+1].isEmpty()) {
								arraysForGaps[j].add(arraysForGaps[j].size() - 1, arraysForGaps[j + 1].remove(0));
								for (int l = 0; l < Math.abs(tryDif / avChanges[k]); l++) {
									arraysForGaps[k + 1].add(0, arraysForGaps[k].remove(arraysForGaps[k].size() - 1));
								}
								flag = true;
								break;
							}
						}
					}
					if (flag)
						break;
					tryDif = dif;
				}
			}
		}
		System.out.println(calculateSquare(arraysForGaps));
	}
}
