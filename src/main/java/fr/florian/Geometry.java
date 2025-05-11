package fr.florian;

public abstract class Geometry {
    protected Material material;

    /**
     * @param P  ray origin
     * @param v  ray direction (should be normalized)
     * @return λ > 0 of first intersection P+λ v, or ≤0 if none.
     */
    public abstract float getIntersection(Vec3f P, Vec3f v);

    /**
     * @param M  a point on the surface
     * @return unit-normal at M
     */
    public abstract Vec3f getNormal(Vec3f M);
}

