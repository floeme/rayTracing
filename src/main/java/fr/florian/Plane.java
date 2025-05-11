package fr.florian;

import java.awt.*;

public class Plane extends Geometry {
    public Vec3f P0, N;

    public Plane(Vec3f P0, Vec3f N, Material mat) {
        this.P0 = P0;
        this.N  = Vec3f.normalize(N);
        this.material = mat;
    }

    @Override
    public float getIntersection(Vec3f P, Vec3f v) {
        float denom = N.x * v.x + N.y * v.y + N.z * v.z;
        if (denom == 0) return -1;

        float t = ((P0.x - P.x) * N.x + (P0.y - P.y) * N.y + (P0.z - P.z) * N.z) / denom;
        return (t >= 0) ? t : -1;
    }

    @Override
    public Vec3f getNormal(Vec3f intersection) {
        return N;
    }
}
