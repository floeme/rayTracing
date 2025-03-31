package fr.florian;

public abstract class Geometry {
    public Vec3f color;
    public abstract double getIntersection(Vec3f P, Vec3f v);

    public abstract Vec3f getNormal(Vec3f intersection);
}
