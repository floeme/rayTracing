package fr.florian;

import static fr.florian.JavaTga.EPS;

/**
 * Triangle primitive.
 */
public class Triangle extends Geometry {
    private final Vec3f a, b, c;
    private final Vec3f normal;

    public Triangle(Vec3f a, Vec3f b, Vec3f c, Material material) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.normal = Vec3f.normalize(c.sub(a).cross(b.sub(a)));
        this.material = material;
    }

    @Override
    public float getIntersection(Vec3f P, Vec3f v) {
        Vec3f edge1 = b.sub(a);
        Vec3f edge2 = c.sub(a);
        Vec3f h = v.cross(edge2);
        float det = edge1.dot(h);
        if (Math.abs(det) < EPS){
            return -1;
        }

        float invDet = 1.0f / det;
        Vec3f s = P.sub(a);
        float u = s.dot(h) * invDet;
        if (u < 0f || u > 1f){
            return -1;
        }

        Vec3f q = s.cross(edge1);
        float vParam = v.dot(q) * invDet;
        if (vParam < 0f || u + vParam > 1f){
            return -1;
        }

        float t = edge2.dot(q) * invDet;
        return (t > EPS) ? t : -1;
    }

    @Override
    public Vec3f getNormal(Vec3f intersection) {
        return normal;
    }
}
