
public class FormatPicture {

    public final static int FORMAT_1X1 = 0;
    public final static int FORMAT_1X2 = 1;
    public final static int FORMAT_2X1= 2;
    public final static int FORMAT_2X3 = 3;
    public final static int FORMAT_3X2 = 4;
    public final static int FORMAT_3X4 = 5;
    public final static int FORMAT_4X3= 6;

    private String name;
    private int heigth;
    private int weight;

    public FormatPicture(int format){
        if(format > 6 || format < 0)
            throw new IllegalArgumentException();
        if(format == FORMAT_1X1) {
            name = "1X1";
            heigth = 1;
            weight = 1;
        }else if(format == FORMAT_1X2){
            name = "1X2";
            heigth = 1;
            weight = 2;
        }else if(format == FORMAT_2X1){
            name = "2X1";
            heigth = 2;
            weight = 1;
        }else if(format == FORMAT_2X3){
            name = "2X3";
            heigth = 2;
            weight = 3;
        }else if(format == FORMAT_3X2){
            name = "3X2";
            heigth = 3;
            weight = 2;
        }else if(format == FORMAT_3X4){
            name = "3X4";
            heigth = 3;
            weight = 4;
        }else{
            name = "4X3";
            heigth = 4;
            weight = 3;
        }
    }

    public String getName() {
        return name;
    }

    public int getHeigth() {
        return heigth;
    }

    public int getWeight() {
        return weight;
    }

}
