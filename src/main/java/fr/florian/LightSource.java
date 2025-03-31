package fr.florian;

class LightSource {
    Vec3f position;
    float ambient, diffuse, specular;

    public LightSource(Vec3f position, float ambient, float diffuse, float specular) {
        this.position = position;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
    }
}
