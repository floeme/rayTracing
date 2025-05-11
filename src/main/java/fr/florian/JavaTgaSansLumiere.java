package fr.florian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fr.florian.JavaTga.saveTGA;

public class JavaTgaSansLumiere {

    /**
     * @param args no command line arguments
     * */
    public static void main(String[] args) {
        int w = 1024;
        int h = 768;
        byte[] buffer = new byte[3 * w * h];

        List<Geometry> scene = new ArrayList<>();
        Material red = new Material(
                new Vec3f(1f,0f,0f),
                new Vec3f(0.7f,0,0),
                new Vec3f(1,1,1),
                64f, 0.2f
        );
        scene.add(new Sphere(new Vec3f(0,0,-5), 1f, red));
        scene.add(new Plane(
                new Vec3f(0,-1,0), new Vec3f(0,1,0),
                new Material(
                        new Vec3f(1f,1f,1f),
                        new Vec3f(0.6f,0.6f,0.6f),
                        new Vec3f(1,1,1),
                        32f, 0f
                )
        ));

        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                int index = 3 * ((row * w) + col);

                float x = (col - w / 2f) / (float) h;
                float y = (row - h / 2f) / (float) h;
                Vec3f rayDir = new Vec3f(x, y, -1);

                Vec3f.normalize(rayDir);
                Vec3f pixelColor = findColorSansLumiere(new Vec3f(0,0,0), rayDir, scene);

                pixelColor.x = Math.min(1, pixelColor.x);
                pixelColor.y = Math.min(1, pixelColor.y);
                pixelColor.z = Math.min(1, pixelColor.z);

                buffer[index] = (byte) (pixelColor.z * 255);
                buffer[index + 1] = (byte) (pixelColor.y * 255);
                buffer[index + 2] = (byte) (pixelColor.x * 255);
            }
        }

        try {
            saveTGA("image_sans_lumiere.tga", buffer, w, h);
        } catch (IOException e) {
            System.err.println("TGA file not created :"+e);
        }
    }

    public static  Vec3f findColorSansLumiere(Vec3f plan, Vec3f rayDir, List<Geometry> scene){
        double minLambda = Double.MAX_VALUE;
        Geometry closestObject = null;

        for (Geometry obj : scene) {
            double lambda = obj.getIntersection(plan, rayDir);

            if (lambda > 0 && lambda < minLambda) {
                minLambda = lambda;
                closestObject = obj;
            }
        }

        if (closestObject == null){
            return new Vec3f(0,0,0);
        }

        Vec3f colorObjectPixel = closestObject.material.ambient;
        return colorObjectPixel;
    }

}
