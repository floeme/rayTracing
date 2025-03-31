package fr.florian;

public class Triangle extends Geometry {
    private Vec3f a, b, c;

    public Triangle(Vec3f a, Vec3f b, Vec3f c) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.color = new Vec3f(0,0,1);
    }

    @Override
    public double getIntersection(Vec3f P, Vec3f v) {
        Vec3f edge1 = b.subtract(a);
        Vec3f edge2 = c.subtract(a);
        Vec3f h = Vec3f.crossProduct(v, edge2);
        float det = Vec3f.dotProduct(edge1, h);

        if (Math.abs(det) < 1e-8) return -1; // rayon parallèle au triangle

        float invDet = 1.0f / det;
        Vec3f s = P.subtract(a);
        float u = Vec3f.dotProduct(s, h) * invDet;

        if (u < 0 || u > 1) return -1; // en dehors du triangle

        Vec3f q = Vec3f.crossProduct(s, edge1);
        float vParam = Vec3f.dotProduct(v, q) * invDet;

        if (vParam < 0 || (u + vParam) > 1) return -1; // en dehors du triangle

        double t = Vec3f.dotProduct(edge2, q) * invDet;
        return (t > 0) ? t : -1; // Si t < 0, c'est derrière la caméra
    }


    @Override
    public Vec3f getNormal(Vec3f intersection) {
        return null;
    }
}
