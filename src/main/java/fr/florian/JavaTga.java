package fr.florian;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author P. Meseure based on a Java Adaptation of a C code by B. Debouchages (M1, 2018-2019)
 */
public class JavaTga
{
    /**
     * 
     * @param fout : output file stream
     * @param n : short to write to disc in little endian
     */
    private static void writeShort(FileOutputStream fout,int n) throws IOException
    {
        fout.write(n&255);
        fout.write((n>>8)&255);
    }

    /**
     * 
     * @param filename name of final TGA file
     * @param buffer buffer that contains the image. 3 bytes per pixel ordered this way : Blue, Green, Red
     * @param width Width of the image
     * @param height Height of the image
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws IOException 
     */
    private static void saveTGA(String filename, byte buffer[], int width, int height) throws IOException, UnsupportedEncodingException {

        FileOutputStream fout = new FileOutputStream(new File(filename));

        fout.write(0); // Comment size, no comment
        fout.write(0); // Colormap type: No colormap
        fout.write(2); // Image type
        writeShort(fout,0); // Origin
        writeShort(fout,0); // Length
        fout.write(0); // Depth
        writeShort(fout,0); // X origin
        writeShort(fout,0); // Y origin
        writeShort(fout,width); // Width of the image
        writeShort(fout,height); // Height of the image
        fout.write(24); // Pixel size in bits (24bpp)
        fout.write(0); // Descriptor

        /* Write the buffer */
        fout.write(buffer);

        fout.close();
    }

    

    /**
     * @param args no command line arguments
     * */
    public static void main(String[] args) {
        int w = 1024;
        int h = 768;
        byte[] buffer = new byte[3 * w * h];

        Plane plane = new Plane(new Vec3f(0, -1, 0), new Vec3f(0, 1, 0));
        Sphere sphere1 = new Sphere(new Vec3f(0, 0.5f, -5), 1, new Vec3f(0.2f, 0.1f, 0.7f));
        Sphere sphere2 = new Sphere(new Vec3f(4, 0.5f, -10), 1, new Vec3f(0.8f, 0.1f, 0.3f));
        Sphere sphere3 = new Sphere(new Vec3f(-4, 0.5f, -10), 1, new Vec3f(0.1f, 0.7f, 0.2f));
        Sphere sphere4 = new Sphere(new Vec3f(2, 0.5f, -7), 0.8f, new Vec3f(0.6f, 0.6f, 0.1f));
        Sphere sphere5 = new Sphere(new Vec3f(-2, 0.5f, -7), 0.8f, new Vec3f(0.9f, 0.2f, 0.9f));

        Triangle triangle = new Triangle(
                new Vec3f(-0.9f, 0, -2),
                new Vec3f(0.2f, 0, -2),
                new Vec3f(0f, 1, -2)
        );
        List<Geometry> scene = new ArrayList<Geometry>();
        scene.add(plane);
        scene.add(sphere1);
        scene.add(sphere2);
        scene.add(sphere3);
        scene.add(sphere4);
        scene.add(sphere5);
        scene.add(triangle);


        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                int index = 3 * ((row * w) + col);

                float x = (col - w / 2f) / (float) h;
                float y = (row - h / 2f) / (float) h;
                Vec3f rayDir = new Vec3f(x, y, -1);

                Vec3f.normalize(rayDir);
                Vec3f pixelColor = findColor(new Vec3f(0,0,0), rayDir, scene);

                buffer[index] = (byte) (pixelColor.z * 255);
                buffer[index + 1] = (byte) (pixelColor.y * 255);
                buffer[index + 2] = (byte) (pixelColor.x * 255);
            }
        }

        try {
            saveTGA("imagetest.tga", buffer, w, h);
        } catch (IOException e) {
            System.err.println("TGA file not created :"+e);
        }
    }


    public static  Vec3f findColor(Vec3f plan, Vec3f rayDir, List<Geometry> scene){
        double minLambda = Double.MAX_VALUE;
        Geometry closestObject = null;

        for (Geometry obj : scene) {
            double lambda = obj.getIntersection(plan, rayDir);
            if(lambda != -1){
                //System.out.println("Intersection avec " + obj + " à t=" + lambda);
            }

            if (lambda > 0 && lambda < minLambda) {
                minLambda = lambda;
                closestObject = obj;
            }
        }

        if (closestObject == null){
            //System.out.print("OBJ NULL");
            return new Vec3f(0,0,0);
        }

        Vec3f colorObjectPixel = closestObject.color;
        //System.out.println("colorObjectPixel = " + colorObjectPixel);
        return colorObjectPixel;
    }


}