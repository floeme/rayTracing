package fr.florian;

import java.awt.*;

class Plane extends Geometry {
    Vec3f point;
    Vec3f normal;

    public Plane(Vec3f point, Vec3f normal) {
        this.point = point;
        this.normal = normal;
        this.color = new Vec3f(1, 0,0);
    }

    @Override
    public double getIntersection(Vec3f P, Vec3f v) {
        float denom = normal.x * v.x + normal.y * v.y + normal.z * v.z;
        if (denom == 0) return -1;

        float t = ((point.x - P.x) * normal.x + (point.y - P.y) * normal.y + (point.z - P.z) * normal.z) / denom;
        return (t >= 0) ? t : -1;
    }

    @Override
    public Vec3f getNormal(Vec3f intersection) {
        return normal;
    }
}
