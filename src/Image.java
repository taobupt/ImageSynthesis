/**
 * Created by Tao on 1/31/2017.
 */
import project1.RasterConversion;
import project2.Camera;

public class Image {
    public static void main(String []args){
//        RasterConversion rs=new RasterConversion();
//        rs.drawImage();

        Camera ca=new Camera("sphere");
        ca.fillImage();
    }
}
