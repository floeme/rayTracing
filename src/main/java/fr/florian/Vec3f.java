package fr.florian;

public class Vec3f {
    public float x, y, z;

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vec3f normalize(Vec3f v) {
        float length = (float) Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        if (length != 0) {
            return new Vec3f(v.x / length, v.y / length, v.z / length);
        }
        return new Vec3f(0, 0, 0);
    }

    public static float dotProduct(Vec3f v1, Vec3f v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public static Vec3f crossProduct(Vec3f v1, Vec3f v2) {
        float x = v1.y * v2.z - v1.z * v2.y;
        float y = v1.z * v2.x - v1.x * v2.z;
        float z = v1.x * v2.y - v1.y * v2.x;
        return new Vec3f(x, y, z);
    }

    public static Vec3f add(Vec3f v1, Vec3f v2) {
        return new Vec3f(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }

    public static Vec3f subtract(Vec3f v1, Vec3f v2) {
        return new Vec3f(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    public static Vec3f multiply(Vec3f v, float scalar) {
        return new Vec3f(v.x * scalar, v.y * scalar, v.z * scalar);
    }

    public static Vec3f negate(Vec3f v) {
        return new Vec3f(-v.x, -v.y, -v.z);
    }

    public static Vec3f multiplyVect(Vec3f v1, Vec3f v2) {
        return new Vec3f(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
    }


    public static Vec3f reflect(Vec3f incident, Vec3f normal) {
        float dot = dotProduct(incident, normal);
        return subtract(incident, multiply(normal, 2 * dot));
    }

    @Override
    public String toString() {
        return "Vec3f(" + x + ", " + y + ", " + z + ")";
    }

    public Vec3f add(Vec3f v) {
        return new Vec3f(x + v.x, y + v.y, z + v.z);
    }

    public Vec3f sub(Vec3f v) {
        return new Vec3f(x - v.x, y - v.y, z - v.z);
    }

    public Vec3f scale(float s) {
        return new Vec3f(x * s, y * s, z * s);
    }

    public Vec3f mul(Vec3f v) {
        return new Vec3f(x * v.x, y * v.y, z * v.z);
    }

    public Vec3f negate() {
        return new Vec3f(-x, -y, -z);
    }

    public float dot(Vec3f v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vec3f cross(Vec3f v) {
        return new Vec3f(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
        );
    }

    public float length() {
        return (float)Math.sqrt(dot(this));
    }

}
