package fr.florian;

public class Light {
    public Vec3f position;
    public Vec3f ambient, diffuse, specular;

    public Light(Vec3f pos, Vec3f a, Vec3f d, Vec3f s) {
        this.position = pos;
        this.ambient  = a;
        this.diffuse  = d;
        this.specular = s;
    }
}
