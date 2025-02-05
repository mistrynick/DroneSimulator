package drones;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Flag extends RenderableObject {
    private float x = 100, y = 1, z = 100;
    
    public Flag() {
        super(new ObjectParams(FILES.get(3), true, true));
        this.color = new Vector3f(1.0f, 0.098f, 0.0f);  // 255, 25, 0 normalized
        updateTransformation();
    }

    public void setPos(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        updateTransformation();
    }

    public void move(float dir, String axis) {
        switch (axis.toLowerCase()) {
            case "x" -> x += dir;
            case "y" -> y += dir;
            case "z" -> z += dir;
        }
        updateTransformation();
    }

    private void updateTransformation() {
        transformation = new Matrix4f()
            .translate(x, y, z)
            .scale(2.5f);
    }
}


