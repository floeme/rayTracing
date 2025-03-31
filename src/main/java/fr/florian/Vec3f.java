package fr.florian;

class Vec3f {
    float x, y, z;

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void normalize(Vec3f v) {
        float length = (float) Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        if (length != 0) {
            v.x /= length;
            v.y /= length;
            v.z /= length;
        }
    }

    public static float dotProduct(Vec3f v1, Vec3f v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public static Vec3f mult(Vec3f v, float f) {
        return new Vec3f(v.x * f, v.y * f, v.z * f);
    }

    public static Vec3f crossProduct(Vec3f v1, Vec3f v2) {
        return new Vec3f(
                v1.y * v2.z - v1.z * v2.y,
                v1.z * v2.x - v1.x * v2.z,
                v1.x * v2.y - v1.y * v2.x
        );
    }


    public Vec3f subtract(Vec3f other) {
        return new Vec3f(this.x - other.x, this.y - other.y, this.z - other.z);
    }


    public static Vec3f reflect(Vec3f lightDir, Vec3f normal) {
        float dotProduct = lightDir.x * normal.x + lightDir.y * normal.y + lightDir.z * normal.z;

        Vec3f reflectDir = new Vec3f(
                lightDir.x - 2 * dotProduct * normal.x,
                lightDir.y - 2 * dotProduct * normal.y,
                lightDir.z - 2 * dotProduct * normal.z
        );

        return reflectDir;
    }


}
