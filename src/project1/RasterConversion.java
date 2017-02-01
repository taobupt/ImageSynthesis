package project1;

/**
 * Created by Tao on 1/31/2017.
 */

import java.util.Random;
import java.awt.BorderLayout;
import java.awt.color.ColorSpace;

import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.*;
public class RasterConversion {
    public double []a;
    public double []s;
    public double []v2={0.0,0.0,1.0};//vview
    public double []vup={1.0,0.0,0.0};
    public double []pe={0.0,0.0,-1.0};
    public double []pc={0.0,0.0,0.0};
    public double []p00;
    public int Xmax=600;
    public int Ymax=600;
    public double []n0;
    public double []n1;
    public double []n2;

    //subpixel
    public final int M=5;
    public final int N=5;
    //Random
    Random rand=null;

    //Image
    public BufferedImage image=null;

    public RasterConversion(String name){
        if(name.equals("sphere")){
            a=new double[]{1.0,1.0,1.0,0.0,-1.0};
            s=new double[]{1.0,1.0,1.0};
        }else if(name.equals("plane")){
            a=new double[]{0.0,0.0,0,1,0};
            s=new double[]{0,0,0};
        }else if(name.equals("cone")){
            a=new double[]{1,1,-1,0,0};
            s=new double[]{6,6,6};
        }else{
            System.out.println("wrong choice");
            System.exit(1);
        }

        //inital Random
        rand=new Random();
        //initial image
        image=new BufferedImage(Xmax,Ymax,BufferedImage.TYPE_INT_RGB);//rgb

        n0=new double[3];
        n1=new double[3];
        n2=new double[3];
        //calculate n2
        getUnitVector(v2,n2);

        //calculate n0
        getUnitVector(getCrossProduct(v2,vup),n0);

        //calculate n1
        n1=getCrossProduct(n0,n2);

        //calculate p00
        p00=new double[3];
        for(int i=0;i<3;++i)
            p00[i]=pc[i]-s[0]*n0[i]/2.0-s[1]*n1[i]/2.0;

    }

    public void getUnitVector(double[]v,double[]n){
        double nModule=Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
        for(int i=0;i<3;++i)
            n[i]=v[i]*1.0/nModule;
    }

    public double[] getCrossProduct(double []vec1,double[]vec2){
        double []v0=new double[3];
        v0[0]=vec1[1]*vec2[2]-vec1[2]*vec2[1];
        v0[1]=vec1[2]*vec2[0]-vec1[0]*vec2[2];
        v0[2]=vec1[0]*vec2[1]-vec1[1]*vec2[0];
        return v0;
    }

    public double dotProduct(double []vec1,double []vec2){
        int n=vec1.length;
        double res=0.0;
        for(int i=0;i<n;++i)
            res+=vec1[i]*vec2[i];
        return res;
    }

    public int isInQuadrics(double x,double y){
        double []pp=new double[3];
        double []ppe=new double[3];
        double []npe=new double[3];
        double []pec=new double[3];
        for(int i=0;i<3;++i) {
            pp[i] = p00[i] + x * s[0] * n0[i] + y * s[1] * n1[i];//pp
            ppe[i]=pp[i]-pe[i];//npe vector
            pec[i]=pe[i]-pc[i];
        }
        getUnitVector(ppe,npe);

        double dotProductN0PE=dotProduct(n0,npe);
        double dotProductN1PE=dotProduct(n1,npe);
        double dotProductN2PE=dotProduct(n2,npe);
        double A=a[0]*(dotProductN0PE/s[0])*(dotProductN0PE/s[0])+a[1]*(dotProductN1PE/s[1])*(dotProductN1PE/s[1])+a[2]*(dotProductN2PE/s[2])*(dotProductN2PE/s[2]);
        double B=a[0]*2*(dotProductN0PE)*(dotProduct(n0,pec))/(s[0]*s[0])+a[1]*2*(dotProductN1PE)*(dotProduct(n1,pec))/(s[1]*s[1])+a[2]*2*(dotProductN2PE)*(dotProduct(n2,pec))/(s[2]*s[2])+a[3]*dotProduct(n2,npe)/s[2];
        double C=a[0]*(dotProduct(n0,pec)/s[0])*(dotProduct(n0,pec)/s[0])+a[1]*(dotProduct(n1,pec)/s[1])*(dotProduct(n1,pec)/s[1])+a[2]*(dotProduct(n2,pec)/s[2])*(dotProduct(n2,pec)/s[2])+a[4]+a[3]*(dotProduct(n2,pec))/s[2];
       // System.out.println(C);
        double delta=B*B-4*A*C;
        if(delta<0)
            return 0;
        double sol1=(-B+Math.sqrt(delta))/(2*A);
        double sol2=(-B-Math.sqrt(delta))/(2*A);
        System.out.println(sol1+" "+sol2);
        //return sol1>0 && sol2>0?255:0;
        return 255;
    }

    public void fillImage(){
        for(int I=0;I<Xmax;++I){
            for(int J=0;J<Ymax;++J){
                image.setRGB(I,J,0);
                int sum=0;
                for(int m=0;m<M;++m){
                    for(int n=0;n<N;++n){
                        double x=(I+m/M+rand.nextDouble()/M);
                        double y=(J+n/N+rand.nextDouble()/N);
                        sum+=isInQuadrics(x,y);
                    }
                }
                sum/=M*N;
                if(sum==0)
                    System.out.println("sum"+sum);
                int rgb=(255<<24)+(sum<<16);
                image.setRGB(I,J,rgb);
            }
        }
        writeImage(image,"K:\\sephere.jpg","jpg");
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
