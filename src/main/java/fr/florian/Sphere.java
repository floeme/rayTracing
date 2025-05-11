package fr.florian;

/**
 * Sphere primitive.
 */
public class Sphere extends Geometry {
    public Vec3f center;
    public float radius;

    public Sphere(Vec3f center, float radius, Material mat) {
        this.center = center;
        this.radius = radius;
        this.material = mat;
    }

    @Override
    public float getIntersection(Vec3f P, Vec3f v) {
        Vec3f oc = new Vec3f(P.x - center.x, P.y - center.y, P.z - center.z);

        float a = Vec3f.dotProduct(v, v);
        float b = 2 * Vec3f.dotProduct(v, oc);;
        float c = (Vec3f.dotProduct(oc, oc) - (radius * radius));
        float discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            return  -1.0f; // pas d'intersection
        } else {
            double lambda =  (-b - Math.sqrt(discriminant)) / (2.0 * a);

             if (lambda > 0) {
                return (float) lambda; // point d'entree.
             }else{
                 lambda = (-b + Math.sqrt(discriminant)) / (2.0 * a);
                 if (lambda > 0) {
                     return (float) lambda; // point d'entree.
                 }else {
                     return -1f; // donc sphere derriere.
                 }
             }
        }
    }

    @Override
    public Vec3f getNormal(Vec3f intersection) {
        Vec3f normal = Vec3f.subtract(intersection, center);
        return Vec3f.normalize(normal);
    }
}
