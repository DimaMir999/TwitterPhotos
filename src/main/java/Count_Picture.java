import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Count_Picture {

    private String url;
    private int count;
    private BufferedImage image;

    public Count_Picture(String url, int count) {
        this.url = url;
        this.count = count;
    }

    public Count_Picture(BufferedImage image, int count) {
        this.image = image;
        this.count = count;
    }

    public BufferedImage loadImage() throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new URL(this.url));
        this.image =  bufferedImage;
        return bufferedImage;
    }

    public String getUrl() {
        return url;
    }

    public int getCount() {
        return count;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
