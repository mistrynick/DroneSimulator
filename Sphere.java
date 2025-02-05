package drones;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Sphere extends RenderableObject {
    private Vector3f scaleFactor;
    
    public Sphere(Vector3f scale, Vector3f color, Vector3f position) {
        super(new ObjectParams(FILES.get(2), true, true));
        this.scaleFactor = new Vector3f(scale);
        this.color = new Vector3f(color);
        updateTransformation(position);
    }

    public void move(Vector3f position) {
        updateTransformation(position);
    }

    private void updateTransformation(Vector3f position) {
        transformation = new Matrix4f()
            .translate(position)
            .scale(scaleFactor);
    }
}
