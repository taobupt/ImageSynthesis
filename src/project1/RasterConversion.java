package project1;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Tao on 1/31/2017.
 */
public class RasterConversion{

    public final int width=600;
    public final int height=600;
    public final int M=3;
    public final int N=3;

//    public Random random=null;
//
//    public RasterConversion(){
//        random=new Random();
//    }


    public int check(int x,int y){
        //the image plane is z=0;
        //the sphere is red, and its function (x-600)^2+y^2+(z-100)^2=300^2;
        //the plane is green and its function  x-2y+600=0;
        //the cone is blue and its function is x^2-y^2+z^2=100^2;
        int red=0,blue=0,green=0;
        if((x-300)*(x-300)+y*y+(0-100)*(0-100)<200*200)//sphere
            red=255;
        if(x-3*y+100<0)//plane
            green=255;
        if((x-50)*(x-50)-(y-50)*(y-50)<100)//cone
            blue=255;
        return (red<<16)+(green<<8)+blue;

    }
    public void drawImage(){
        BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);//rgb
        for(int I=0;I<height;++I){
            for(int J=0;J<width;++J){
                image.setRGB(I,J,0);//black

                int rgb=(255<<24)+check(I,J);
                image.setRGB(I,J,rgb);
            }
        }
        display(image);
        //writeImage(image,"K:\\sephere.jpg","jpg");
    }


    /**
     * This method display an image from the input buffered image
     * @return void --> display the image in the frame
     */
    public void display(BufferedImage img){
        JFrame frame = new JFrame("Title of the window :)");
        JLabel label = new JLabel(new ImageIcon(img));
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * This method reads an image from the file
     * @param fileLocation -- > eg. "C:/testImage.jpg"
     * @return BufferedImage of the file read
     */
    public BufferedImage readImage(String fileLocation) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(fileLocation));
            System.out.println(img.getType());
            for(int i=0;i<img.getHeight();++i){
                for(int j=0;j<img.getWidth();++j)
                    System.out.println(img.getRGB(i,j));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    /**
     * This method writes a buffered image to a file
     * @param img -- > BufferedImage
     * @param fileLocation --> e.g. "C:/testImage.jpg"
     * @param extension --> e.g. "jpg","gif","png"
     */
    public void writeImage(BufferedImage img, String fileLocation,
                           String extension) {
        try {
            BufferedImage bi = img;
            File outputfile = new File(fileLocation);
            ImageIO.write(bi, extension, outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
