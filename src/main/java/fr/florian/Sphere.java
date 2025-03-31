package fr.florian;

import java.awt.*;

import static fr.florian.Vec3f.normalize;

class Sphere extends Geometry {
    Vec3f center;
    float radius;

    public Sphere(Vec3f center, float radius, Vec3f color) {
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    @Override
    public double getIntersection(Vec3f P, Vec3f v) {
        Vec3f oc = new Vec3f(P.x - center.x, P.y - center.y, P.z - center.z);

        float a = Vec3f.dotProduct(v, v);
        float b = 2 * Vec3f.dotProduct(v, oc);;
        float c =  Vec3f.dotProduct(oc, oc) - (radius * radius);
        float discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            return -1.0; // pas d'intersection
        } else {
            double lambda =  (-b - Math.sqrt(discriminant)) / (2.0 * a);
            //System.out.println("Sphere Intersection Test: b=" + b + " c=" + c + " discriminant=" + discriminant);
             if (lambda > 0) {
                 System.out.println("Sphere Intersection Test: b=" + b + " c=" + c + " Lambda1 =" + lambda);
                return lambda; // point d'entrée.
             }else{
                 lambda = (-b + Math.sqrt(discriminant)) / (2.0 * a);
                 if (lambda > 0) {
                     System.out.println("Sphere Intersection Test: b=" + b + " c=" + c + " Lambda2 =" + lambda);
                     return lambda; // point d'entrée.
                 }else {
                     return -1; // donc sphere derriere.
                 }
             }
        }
    }


    @Override
    public Vec3f getNormal(Vec3f intersection) {
        // The normal is the vector from the center of the sphere to the intersection point
        Vec3f normal = new Vec3f(intersection.x - center.x, intersection.y - center.y, intersection.z - center.z);
        normalize(normal);  // Normalize the normal
        return normal;
    }
}
