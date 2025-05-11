package fr.florian;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static fr.florian.Vec3f.normalize;

/**
 *
 * @author P. Meseure based on a Java Adaptation of a C code by B. Debouchages (M1, 2018-2019)
 */
public class JavaTga
{

    static final Material white = new Material(
            /* ambient */       new Vec3f(1f,1f,1f),
            /* diffuse */       new Vec3f(1f,1f,1f),
            /* specular */      new Vec3f(1f,1f,1f),
            /* shininess */     32f,
            /* reflectivity */  0f
    );

    static final Material grey = new Material(
            /* ambient */       new Vec3f(0.5f,0.5f,0.5f),
            /* diffuse */       new Vec3f(0.5f,0.5f,0.5f),
            /* specular */      new Vec3f(1f,1f,1f),
            /* shininess */     32f,
            /* reflectivity */  0f
    );

    static final Material red = new Material(
            /* ambient */       new Vec3f(0.7f,0f,0f),
            /* diffuse */       new Vec3f(0.7f,0f,0f),
            /* specular */      new Vec3f(1f,1f,1f),
            /* shininess */     32f,
            /* reflectivity */  0f
    );

    static final Material glass = new Material(
            /* ambient */        new Vec3f(0.1f, 0.1f, 0.1f),
            /* diffuse */        new Vec3f(0.7f, 0.8f, 1.0f),
            /* specular */       new Vec3f(1.0f, 1.0f, 1.0f),
            /* shininess */      64f,
            /* reflectivity */   0.05f,
            /* transparency */   0.9f,
            /* ior */            1.5f,
            /* absorptionTint */ new Vec3f(0.7f, 0.8f, 1.0f)
    );

    static final Material brown = new Material(
            /* ambient */       new Vec3f(0.1f, 0.0f, 0.0f),
            /* diffuse */       new Vec3f(0.7f, 0.0f, 0.0f),
            /* specular */      new Vec3f(1.0f, 1.0f, 1.0f),
            /* shininess */     32f,
            /* reflectivity */   0.0f
    );

    static final Material greyMirror = new Material(
            /* ambient */       new Vec3f(0.05f, 0.05f, 0.05f),
            /* diffuse */       new Vec3f(0.3f, 0.3f, 0.3f),
            /* specular */      new Vec3f(0.8f, 0.8f, 0.8f),
            /* shininess */     128f,
            /* reflectivity */  0.9f
    );

    static final Material diffuseOrange = new Material(
            /* ambient */       new Vec3f(1f, 1f, 0.0f),
            /* diffuse */       new Vec3f(0.7f, 0.0f, 0.0f),
            /* specular */      new Vec3f(1.0f, 1.0f, 1.0f),
            /* shininess */     32f,
            /* reflectivity */  0.0f
    );


    static final Material diffuseBlue = new Material(
            /* ambient */       new Vec3f(0.0f, 0.0f, 0.1f),
            /* diffuse */       new Vec3f(0.0f, 0.0f, 0.7f),
            /* specular */      new Vec3f(1.0f, 1.0f, 1.0f),
            /* shininess */     32f,
            /* reflectivity */  0f
    );

    static final Material verticalGreen = new Material(
            /* ambient */       new Vec3f(0.0f, 0.1f, 0.0f),
            /* diffuse */       new Vec3f(0.0f, 0.7f, 0.0f),
            /* specular */      new Vec3f(0.3f, 0.3f, 0.3f),
            /* shininess */     100f,
            /* reflectivity */  0.0f
    );

    static final int MAX_DEPTH = 15;
    static final float EPS = 1e-4f;

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
    public static void saveTGA(String filename, byte buffer[], int width, int height) throws IOException, UnsupportedEncodingException {

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
     * Main entry point. Renders many separate scenes.
     *
     * @param args no command line arguments
     */
    public static void main(String[] args) throws IOException {
        final int w = 1024, h = 768;
        final int blockSize = 32;

        renderScene("scene_simple.tga", w, h, blockSize, JavaTga::drawSimpleScene);
        renderScene("palais_glace.tga", w, h, blockSize, JavaTga::drawMirrorPalaceScene);
        renderScene("refraction.tga", w, h, blockSize, JavaTga::drawRefractionScene);
        renderScene("reflection.tga", w, h, blockSize, JavaTga::drawReflexionScene);
        renderScene("reflectionDamier.tga", w, h, blockSize, JavaTga::drawDamierReflectionScene);
        renderScene("refractionDamier.tga", w, h, blockSize, JavaTga::drawDamierRefractionScene);
    }

    /**
     * Renders a scene with ray tracing and saves to file.*
     *
     * @param filename name of output TGA
     * @param sceneBuilder function to build the scene (geometries + lights)
     */
    public static void renderScene(String filename, int w, int h, int blockSize,
                                   BiConsumer<List<Geometry>, List<Light>> sceneBuilder) throws IOException {
        byte[] buffer = new byte[3 * w * h];
        List<Geometry> scene = new ArrayList<>();
        List<Light> lights = new ArrayList<>();

        sceneBuilder.accept(scene, lights);

        // initialisation du pool de threads
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        // parallelisation par blocs
        for (int blockY = 0; blockY < h; blockY += blockSize) {
            for (int blockX = 0; blockX < w; blockX += blockSize) {
                final int startX = blockX;
                final int startY = blockY;

                executor.submit(() -> {
                    int endX = Math.min(startX + blockSize, w);
                    int endY = Math.min(startY + blockSize, h);

                    for (int row = startY; row < endY; row++) {
                        for (int col = startX; col < endX; col++) {
                            int index = 3 * ((row * w) + col);

                            // calcul direction rayon
                            float x = ((col - w / 2f) / (float) h);
                            float y = ((row - h / 2f) / (float) h);

                            Vec3f rayDir = new Vec3f(x, y - 0.2f, -1);
                            rayDir = normalize(rayDir);

                            // obtention de la couleur par lancer de rayon
                            Vec3f pixelColor = findColor(new Vec3f(0, 2, 0), rayDir, scene, lights, 0);

                            // clamp et ecriture dans le buffer
                            pixelColor.x = Math.min(1, pixelColor.x);
                            pixelColor.y = Math.min(1, pixelColor.y);
                            pixelColor.z = Math.min(1, pixelColor.z);

                            buffer[index] = (byte) (pixelColor.z * 255);
                            buffer[index + 1] = (byte) (pixelColor.y * 255);
                            buffer[index + 2] = (byte) (pixelColor.x * 255);
                        }
                    }
                });
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
            saveTGA(filename, buffer, w, h);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Computes the color returned by a ray in the scene using the Phong model
     * (ambient, diffuse, specular), with support for shadows, reflections, and refractions.
     *
     * @param P the origin point of the ray
     * @param v the normalized direction of the ray
     * @param scene the list of all geometric objects in the scene
     * @param lights the list of light sources in the scene
     * @param depth the current recursion depth
     * @return a Vec3f containing the RGB color returned by this ray
     */
    public static Vec3f findColor(Vec3f P, Vec3f v,
                                  List<Geometry> scene, List<Light> lights,
                                  int depth) {
        float tMin = Float.MAX_VALUE;
        Geometry hitObj = null;

        // intersection la plus proche
        for (Geometry obj : scene) {
            float t = obj.getIntersection(P, v);
            if (t > EPS && t < tMin) {
                tMin = t;
                hitObj = obj;
            }
        }
        if (hitObj == null) {
            // fond noir
            return new Vec3f(0,0,0);
        }

        // point d'intersection
        Vec3f M = P.add(v.scale(tMin));
        // normale
        Vec3f N = hitObj.getNormal(M);
        Material mat = hitObj.material;

        // calcul de l'eclairage local (Phong)
        Vec3f color = new Vec3f(0, 0, 0);
        for (Light L : lights) {
            color = color.add(mat.ambient.mul(L.ambient));
            Vec3f toL = normalize(L.position.sub(M));

            // test d'ombre
            boolean inShadow = false;
            float distToLight = L.position.sub(M).length();
            for (Geometry o2 : scene) {
                float ts = o2.getIntersection(M.add(N.scale(EPS)), toL);
                if (ts > EPS && ts < distToLight) {
                    inShadow = true;
                    break;
                }
            }
            if (!inShadow) {
                // diffus
                float diff = Math.max(0, N.dot(toL));
                Vec3f diffuse = mat.diffuse.mul(L.diffuse).scale(diff);
                color = color.add(diffuse);

                //speculaire
                Vec3f toV = v.negate();  // vers la camera
                Vec3f refl = normalize(toL.negate().sub(N.scale(2 * toL.negate().dot(N))));
                float spec = (float)Math.pow(Math.max(0, toV.dot(refl)), mat.shininess);
                Vec3f specular = mat.specular.mul(L.specular).scale(spec);
                color = color.add(specular);
            }
        }

        // gestion des reflexions
        Vec3f reflCol = new Vec3f(0,0,0);
        if (mat.reflectivity > 0 && depth < MAX_DEPTH) {
            Vec3f R = normalize(v.sub(N.scale(2 * v.dot(N))));
            reflCol = findColor( M.add(N.scale(EPS)), R, scene, lights, depth + 1).scale(mat.reflectivity);
        }

        // gestion des refractions
        Vec3f refrCol = new Vec3f(0,0,0);
        if (mat.transparency > 0 && depth < MAX_DEPTH) {
            float n1 = 1f;  // air
            float n2 = mat.ior;
            Vec3f N2 = N;
            float cosI = v.negate().dot(N);
            if (cosI < 0) { // on sort du materiau
                cosI = -cosI;
                N2 = N.negate();
                float tmp = n1; n1 = n2; n2 = tmp;
            }
            float eta = n1 / n2;
            float k = 1 - eta*eta*(1 - cosI*cosI);
            if (k >= 0) {
                Vec3f T = normalize(v.scale(eta).add(N2.scale(eta * cosI - (float)Math.sqrt(k)))
                );
                refrCol = findColor( M.add(T.scale(EPS)), T, scene, lights, depth + 1)
                        .scale(mat.transparency);
            }
        }

        // composition finale des couleurs
        float Rf = mat.reflectivity, Tr = mat.transparency;
        Vec3f local = color.scale(1 - Rf - Tr);
        return local.add(reflCol).add(refrCol);
    }


    private static void drawSimpleScene(List<Geometry> scene, List<Light> lights) {
        scene.add(new Sphere(new Vec3f( -2.0f, 0.5f, -7f), 0.75f, greyMirror));
        scene.add(new Sphere(new Vec3f(-1.0f, 0.5f, -6), 0.5f, red));
        scene.add(new Sphere(new Vec3f(0f, 0.5f, -5f), 0.5f, diffuseBlue));
        scene.add(new Sphere(new Vec3f(1f, 0.5f, -6f), 0.5f, red));
        scene.add(new Sphere(new Vec3f( 2f, 0.5f, -7f), 0.6f, glass));

        scene.add(new Sphere(new Vec3f(0, 0.2f, 2), 0.6f, diffuseOrange));

        Vec3f A = new Vec3f(-0.5f, -0.5f, -4.0f);
        Vec3f B = new Vec3f( 0.5f, -0.5f, -4.0f);
        Vec3f C = new Vec3f( 0.0f,  0.5f, -4.0f);
        //scene.add(new Triangle(A, B, C, diffuseBlue));

        // ——————————————————————————————————————————————————————————————

        scene.add(new Plane(
                new Vec3f(0f, -1f, 0f),
                new Vec3f(0f, 1f, 0f),
                new Material(
                        new Vec3f(0.1f,0.1f,0.1f),
                        new Vec3f(0.6f,0.6f,0.6f),
                        new Vec3f(1f,1f,1f),
                        32f,
                        0.0f
                )
        ));

        scene.add(new Plane(
                new Vec3f(0f, 0f, -15f),
                new Vec3f(0f, 0f, 1f),
                verticalGreen
        ));

        scene.add(new Plane(
                new Vec3f(5f, 0f, 0f),
                new Vec3f(-1f, 0f, 0f),
                verticalGreen
        ));

        scene.add(new Plane(
                new Vec3f(-5f, 0f, 0f),
                new Vec3f(1f, 0f, 0f),
                verticalGreen
        ));

        // ——————————————————————————————————————————————————————————————

        lights.add(new Light(
                new Vec3f(-4,3,0),
                new Vec3f(0.1f,0.1f,0.1f),
                new Vec3f(0.8f,0.8f,0.8f),
                new Vec3f(1,1,1)
        ));
    }

    private static void drawMirrorPalaceScene(List<Geometry> scene, List<Light> lights) {
        // sol
        scene.add(new Plane(new Vec3f(0f, -1f, 0f), new Vec3f(0f, 1f, 0f), brown));
        // plafond
        scene.add(new Plane(new Vec3f(0f, 5f, 0f), new Vec3f(0f, -1f, 0f), brown));
        // mur fond
        scene.add(new Plane(new Vec3f(0f, 0f, -10f), new Vec3f(0f, 0f, 1f), greyMirror));
        // mur gauche
        scene.add(new Plane(new Vec3f(-5f, 0f, 0f), new Vec3f(1f, 0f, 0f), greyMirror));
        // mur droit
        scene.add(new Plane(new Vec3f(5f, 0f, 0f), new Vec3f(-1f, 0f, 0f), greyMirror));
        // mur derriere
        scene.add(new Plane(new Vec3f(0f, 0f, 10f), new Vec3f(0f, 0f, -1f), greyMirror));

        // ——————————————————————————————————————————————————————————————
        //sphere rouge
        scene.add(new Sphere(new Vec3f(-1.5f, 0f, -6f), 0.7f, red));


        // ——————————————————————————————————————————————————————————————
        lights.add(new Light(
                new Vec3f(0f, 4.5f, -2f),
                new Vec3f(0.6f, 0.4f, 0.6f),
                new Vec3f(1f, 1f, 1f),
                new Vec3f(1f, 1f, 1f)
        ));
    }

    private static void drawRefractionScene(List<Geometry> scene, List<Light> lights) {
        Material checker = new Material(
                new Vec3f(0.2f, 0.2f, 0.2f),
                new Vec3f(0.8f, 0.8f, 0.8f),
                new Vec3f(0.2f, 0.2f, 0.2f),
                32f,
                0f
        );

        scene.add(new Plane(new Vec3f(0f, -1f, 0f), new Vec3f(0f, 1f, 0f), checker));
        scene.add(new Plane(new Vec3f(0f, 0f, -15f), new Vec3f(0f, 0f, 1f), checker));
        scene.add(new Sphere(new Vec3f(-1f, 0f, -7f), 1f, glass));
        scene.add(new Sphere(new Vec3f(0f, 0f, -10f), 0.6f,
                new Material(
                        new Vec3f(0.1f, 0.1f, 0.1f),
                        new Vec3f(0.9f, 0.0f, 0.0f),
                        new Vec3f(1.0f, 1.0f, 1.0f),
                        32f,
                        0.0f
                )));

        lights.add(new Light(
                new Vec3f(-3f, 5f, -4f),
                new Vec3f(0.2f, 0.2f, 0.2f),
                new Vec3f(0.9f, 0.9f, 0.9f),
                new Vec3f(1f, 1f, 1f)
        ));
    }
    private static void drawReflexionScene(List<Geometry> scene, List<Light> lights){
        //sol
        scene.add(new Plane(new Vec3f(0f, 0f, 0f), new Vec3f(0f, 1f, 0f), white));

        // mur fond
        scene.add(new Plane(new Vec3f(0f, 0f, -10f), new Vec3f(0f, 0f, 1f), brown));
        // mur gauche
        scene.add(new Plane(new Vec3f(-5f, 0f, 0f), new Vec3f(1f, 0f, 0f), red));
        // mur droit
        scene.add(new Plane(new Vec3f(5f, 0f, 0f), new Vec3f(-1f, 0f, 0f), red));
        // mur derriere
        scene.add(new Plane(new Vec3f(0f, 0f, 10f), new Vec3f(0f, 0f, -1f), brown));

        // ——————————————————————————————————————————————————————————————

        scene.add(new Sphere(new Vec3f(0f, 1f, -6f), 1f, greyMirror));
        scene.add(new Sphere(new Vec3f(2f, 1f, -6f), 1f, greyMirror));
        scene.add(new Sphere(new Vec3f(-2f, 1f, -6f), 1f, greyMirror));
        // ——————————————————————————————————————————————————————————————

        lights.add(new Light(
                new Vec3f(1f, 3f, -9f),
                new Vec3f(0.6f, 0.6f, 0.6f),
                new Vec3f(0.9f, 0.9f, 0.9f),
                new Vec3f(1f, 1f, 1f)
        ));
    }

    private static void drawDamierRefractionScene(List<Geometry> scene, List<Light> lights) {
        for (int i = -6; i < 6; i++) {
            for (int j = -30; j < 1; j++) {
                Material mat = ((i + j) % 2 == 0) ? white : grey;
                Vec3f p0 = new Vec3f(i,   0f, j);
                Vec3f p1 = new Vec3f(i+1, 0f, j);
                Vec3f p2 = new Vec3f(i+1, 0, j+1);
                Vec3f p3 = new Vec3f(i,   -0, j+1);
                scene.add(new Triangle(p0, p1, p2, mat));
                scene.add(new Triangle(p0, p2, p3, mat));
            }
        }

//        //sol
//        scene.add(new Plane(new Vec3f(0f, 0f, 0f), new Vec3f(0f, 1f, 0f), white));

        scene.add(new Sphere(new Vec3f(0f, 1f, -8f), 1f, glass));
        addCubeAsTriangles(scene, new Vec3f(-4f,1f,-15f), 2f, diffuseOrange);
        addCubeAsTriangles(scene, new Vec3f( 4f,1f,-15f), 2f, diffuseOrange);

        scene.add(new Plane(
                new Vec3f(0f, 0f, -20f), new Vec3f(0f, 0f, 1f), red
        ));
        scene.add(new Plane(
                new Vec3f(-6f, 0f, 0f), new Vec3f(1f, 0f, 0f), red
        ));
        scene.add(new Plane(
                new Vec3f( 6f, 0f, 0f), new Vec3f(-1f, 0f, 0f), red
        ));

        // ——————————————————————————————————————————————————————————————

        lights.add(new Light(
                new Vec3f(0f, 4f, -2f),
                new Vec3f(0f,0f,0f),
                new Vec3f(0.8f,0.8f,0.8f),
                new Vec3f(1f,1f,1f)
        ));

        lights.add(new Light(
                new Vec3f(0f,0f,0f),
                new Vec3f(0.5f,0.5f,0.5f),
                new Vec3f(0f,0f,0f),
                new Vec3f(0f,0f,0f)
        ));
    }

    private static void drawDamierReflectionScene(List<Geometry> scene, List<Light> lights) {

        for (int i = -6; i < 6; i++) {
            for (int j = -30; j < 1; j++) {
                Material mat = ((i + j) % 2 == 0) ? white : grey;
                Vec3f p0 = new Vec3f(i,   0f, j);
                Vec3f p1 = new Vec3f(i+1, 0f, j);
                Vec3f p2 = new Vec3f(i+1, 0f, j+1);
                Vec3f p3 = new Vec3f(i,   0f, j+1);
                scene.add(new Triangle(p0, p1, p2, mat));
                scene.add(new Triangle(p0, p2, p3, mat));
            }
        }

//        //sol
//        scene.add(new Plane(new Vec3f(0f, 0f, 0f), new Vec3f(0f, 1f, 0f), white));

        scene.add(new Sphere(new Vec3f(0f, 1f, -8f), 1f, greyMirror));
        addCubeAsTriangles(scene, new Vec3f(-4f,1f,-15f), 2f, diffuseOrange);
        addCubeAsTriangles(scene, new Vec3f( 4f,1f,-15f), 2f, diffuseOrange);


        scene.add(new Plane(
                new Vec3f(0f, 0f, -20f), new Vec3f(0f, 0f, 1f), red
        ));

        scene.add(new Plane(
                new Vec3f(-6f, 0f, 0f), new Vec3f(1f, 0f, 0f), red
        ));
        scene.add(new Plane(
                new Vec3f( 6f, 0f, 0f), new Vec3f(-1f, 0f, 0f), red
        ));

        // ——————————————————————————————————————————————————————————————

        lights.add(new Light(
                new Vec3f(0f, 4f, -2f),
                new Vec3f(0f,0f,0f),
                new Vec3f(0.8f,0.8f,0.8f),
                new Vec3f(1f,1f,1f)
        ));

        lights.add(new Light(
                new Vec3f(0f,0f,0f),
                new Vec3f(0.5f,0.5f,0.5f),
                new Vec3f(0f,0f,0f),
                new Vec3f(0f,0f,0f)
        ));
    }

    private static void addCubeAsTriangles(
            List<Geometry> scene,
            Vec3f center,
            float size,
            Material mat
    ) {
        float h = size/2f;
        Vec3f p000 = new Vec3f(center.x - h, center.y - h, center.z - h);
        Vec3f p001 = new Vec3f(center.x - h, center.y - h, center.z + h);
        Vec3f p010 = new Vec3f(center.x - h, center.y + h, center.z - h);
        Vec3f p011 = new Vec3f(center.x - h, center.y + h, center.z + h);
        Vec3f p100 = new Vec3f(center.x + h, center.y - h, center.z - h);
        Vec3f p101 = new Vec3f(center.x + h, center.y - h, center.z + h);
        Vec3f p110 = new Vec3f(center.x + h, center.y + h, center.z - h);
        Vec3f p111 = new Vec3f(center.x + h, center.y + h, center.z + h);

        scene.add(new Triangle(p101, p001, p011, mat));
        scene.add(new Triangle(p101, p011, p111, mat));

        scene.add(new Triangle(p000, p100, p110, mat));
        scene.add(new Triangle(p000, p110, p010, mat));

        scene.add(new Triangle(p001, p000, p010, mat));
        scene.add(new Triangle(p001, p010, p011, mat));

        scene.add(new Triangle(p100, p101, p111, mat));
        scene.add(new Triangle(p100, p111, p110, mat));

        scene.add(new Triangle(p010, p110, p111, mat));
        scene.add(new Triangle(p010, p111, p011, mat));

        scene.add(new Triangle(p000, p001, p101, mat));
        scene.add(new Triangle(p000, p101, p100, mat));
    }
}