package drones;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import org.joml.Vector3f;

public class UserInput {
	long window;
	static final float SPEED = 0.1f;
	static final float accfactor = 1.1f;
	static Vector3f acceleration = new Vector3f(0,0,0);
	
    
	public UserInput(long window, int key, int scancode, int anction, int mods) {
		this.window = window;
	}
	
	
	
	public static void setCameraMovements(Camera camera, long window, int action, int key) {
		
		
		
		if ( key == org.lwjgl.glfw.GLFW.GLFW_KEY_W) {
			camera.origin.z += camera.velocity.z + Camera.ACCELERATION * Camera.deltaTime;
			
		} else if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_W && action == GLFW_RELEASE){
			camera.velocity.z = 0; 
		}
			
		
		if ( key == org.lwjgl.glfw.GLFW.GLFW_KEY_S) {
			camera.origin.z -= camera.velocity.z + Camera.ACCELERATION * Camera.deltaTime;
			
			
		} else if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_S && action == GLFW_RELEASE) {
			camera.velocity.z = 0; 
		}
			
		if ( key == org.lwjgl.glfw.GLFW.GLFW_KEY_A) {
			camera.origin.x += camera.velocity.x + Camera.ACCELERATION * Camera.deltaTime;
			
			
		} else if ( key == org.lwjgl.glfw.GLFW.GLFW_KEY_A && action == GLFW_RELEASE){
			camera.velocity.x = 0;
		}
		
		if ( key == org.lwjgl.glfw.GLFW.GLFW_KEY_D) {
			camera.origin.x -= camera.velocity.x + Camera.ACCELERATION * Camera.deltaTime;
			
			
		} else if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_D && action == GLFW_RELEASE){
			camera.velocity.x = 0;
		}
			
		
	}
	
	public static void setCameraMovementRotationMovements(Camera camera, long window, int action, int key) {
		if ( key == org.lwjgl.glfw.GLFW.GLFW_KEY_UP)
			camera.deg_angles.x -= SPEED + 5;
		
		if ( key == org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN)
			camera.deg_angles.x += SPEED + 5;
		
		if ( key == org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT)
			camera.deg_angles.y += SPEED + 5;
		
		if ( key == org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT)
			camera.deg_angles.y -= SPEED + 5;
		
	}
	
	public boolean setKeyCallBacks() {
		
		return true;
	}
	
	
}
