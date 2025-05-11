package fr.florian;

public class Material {
    public Vec3f ambient, diffuse, specular;
    public float shininess;
    public float reflectivity;
    public float transparency;
    public float ior;
    public Vec3f absorption;

    public Material(Vec3f a, Vec3f d, Vec3f s, float shininess, float refl) {
        this(a, d, s, shininess, refl, 0f, 1f, new Vec3f(0.1f,0.1f,0.1f));
    }

    public Material(Vec3f a, Vec3f d, Vec3f s,
                    float shininess,
                    float reflectivity,
                    float transparency,
                    float ior,
                    Vec3f absorption)
    {
        this.ambient      = a;
        this.diffuse      = d;
        this.specular     = s;
        this.shininess    = shininess;
        this.reflectivity = reflectivity;
        this.transparency = transparency;
        this.ior          = ior;
        this.absorption = absorption;
    }
}
