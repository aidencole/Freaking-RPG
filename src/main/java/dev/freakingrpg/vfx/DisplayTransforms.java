package dev.freakingrpg.vfx;

import org.bukkit.Material;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class DisplayTransforms {

    private DisplayTransforms() {
    }

    public static Transformation rockChunk(float scale) {
        Quaternionf rotation = new Quaternionf().rotateXYZ(
            (float) (Math.random() * Math.PI),
            (float) (Math.random() * Math.PI),
            (float) (Math.random() * Math.PI)
        );
        return new Transformation(
            new Vector3f(0, 0.05f, 0),
            rotation,
            new Vector3f(scale, scale, scale),
            new Quaternionf()
        );
    }

    public static Transformation groundCrack(float scale) {
        Quaternionf rotation = new Quaternionf().rotateX((float) Math.toRadians(90));
        return new Transformation(
            new Vector3f(0, 0.02f, 0),
            rotation,
            new Vector3f(scale, 0.08f, scale),
            new Quaternionf()
        );
    }

    public static Transformation translated(Transformation base, float x, float y, float z) {
        return new Transformation(
            new Vector3f(base.getTranslation()).add(x, y, z),
            base.getLeftRotation(),
            base.getScale(),
            base.getRightRotation()
        );
    }

    public static Material randomRockMaterial() {
        Material[] materials = {
            Material.COBBLESTONE,
            Material.STONE,
            Material.DEEPSLATE,
            Material.TUFF,
            Material.GRAVEL,
            Material.ANDESITE
        };
        return materials[(int) (Math.random() * materials.length)];
    }
}
