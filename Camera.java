package drones;

import org.joml.*;
import java.lang.Math;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class Camera {
    private float cameraDistance = 20f;
    private Vector3f lookAtPoint = new Vector3f(0, 0, 0);
    private Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);
    private Vector3f cameraStartDirection = new Vector3f(0, 10, 20);
    public Vector3f eyePoint;
    private float near = 1f;
    private float far = 1000f;
    private float fov = 60f;
    
    protected static final float MOVEMENT_SPEED = 0.1f;
    protected static final float ROTATION_SPEED = 2.0f;
    protected static final float ACCELERATION = 100f;
    protected static final float DECELERATION = 0.0f;
    protected static final float MAX_SPEED = 15.0f;
    
    protected static float deltaTime = 0.1f;
    
    protected Vector3f velocity = new Vector3f();
    protected Vector3f rotationVelocity = new Vector3f();
    
    public Vector3f deg_angles;
    
    public Vector3f origin;
    
    public Camera() {
    	origin = new Vector3f(0,0,0);
    	deg_angles = new Vector3f(0,0,0);
    }

    public Matrix4f getCameraMatrix() {

        float radX = (float) Math.toRadians(deg_angles.x);
        float radY = (float) Math.toRadians(deg_angles.y);
        float radZ = (float) Math.toRadians(deg_angles.z);

        Matrix4f rotationX = new Matrix4f().rotateX(radX);
        Matrix4f rotationY = new Matrix4f().rotateY(radY);
        Matrix4f rotationZ = new Matrix4f().rotateZ(radZ);

        Matrix4f rotationMatrix = rotationZ.mul(rotationY).mul(rotationX);

        Vector3f viewDirection = cameraStartDirection.normalize(new Vector3f());
        rotationMatrix.transformDirection(viewDirection);
        eyePoint = new Vector3f(viewDirection).mul(cameraDistance).add(lookAtPoint);
        

        Matrix4f viewMatrix = new Matrix4f().lookAt(eyePoint, lookAtPoint, upVector);
        viewMatrix = translate(viewMatrix);
        return viewMatrix;
    }
    
    public Matrix4f translate(Matrix4f viewMatrix) {
    	viewMatrix.m30(viewMatrix.m30() + origin.x);
        viewMatrix.m31(viewMatrix.m31() + origin.y);
        viewMatrix.m32(viewMatrix.m32() + origin.z);

        return viewMatrix;
    }
    
    
    public Matrix4f rotateCamera(Matrix4f oldMatrix) {
 
        lookAtPoint.add(oldMatrix.m30(), oldMatrix.m31(), oldMatrix.m32());

        Matrix4f viewMatrix = getCameraMatrix();

        return viewMatrix;
    }

    public Matrix4f getPerspectiveMatrix(float aspectRatio) {
        return new Matrix4f().perspective((float) Math.toRadians(fov), aspectRatio, near, far);
    }
    
    public void update(float deltaTime) {
       
        origin.add(new Vector3f(velocity).mul(deltaTime));
        
        deg_angles.add(new Vector3f(rotationVelocity).mul(deltaTime));
        
        velocity.mul((float)Math.pow(1.0f - DECELERATION * deltaTime, deltaTime));
        rotationVelocity.mul((float)Math.pow(1.0f - DECELERATION * deltaTime, deltaTime));
    }
    
    
    
    
    
    
}
