package drones;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glBindSampler;





public class Main {
	
	private long window;
    private int width = 1000;
    private int height = 800;
    private float aspectRatio;
    
    private List<RenderableObject> renderableObjectsQueue;
    private Floor floor;
    private Drone drone;
    private Flag flag;
    private GrassTexture grassTexture;
    private QuadRenderer QR;
   
    private int mainShaderProgram;
    private int depthShaderProgram;
    private int quadShaderProgram;
    

    private FrameBuffer FB;

    
    private Camera camera;
    private Light light;
    
    final private String[] vertex_shader_paths = {"./src/drones/vertexShader.glsl", "./src/drones/quadCodeVertex.glsl", "./src/drones/depthShader.glsl"};
    final private String[] fragment_shader_paths = {"./src/drones/fragmentShader.glsl", "./src/drones/quadCodeFragment.glsl", "./src/drones/depthShaderFragment.glsl"};
    //final private String[] vertex_shader_paths = {"./src/drones/quadCodeVertex.glsl"};
    //final private String[] fragment_shader_paths = {"./src/drones/quadCodeFragment.glsl"};
    final private String[] texturePaths = {"./src/drones/tex.png"};
    
    
    private void compileShaders() {
    	try {
    		mainShaderProgram = ShaderUtils.createProgramFromFiles(vertex_shader_paths[0], fragment_shader_paths[0]);
    		quadShaderProgram = ShaderUtils.createProgramFromFiles(vertex_shader_paths[1], fragment_shader_paths[1]);
    		depthShaderProgram = ShaderUtils.createProgramFromFiles(vertex_shader_paths[2], fragment_shader_paths[2]);
    	} catch (java.io.IOException e) {
    		System.err.println("ERROR OCCURRED " + e.toString());
    	}
        
    }
    
    
    private void setup_renderables() {
    	floor.createRenderable();
    	drone.createRenderable();
    	flag.createRenderable();
    	for (RenderableObject o: renderableObjectsQueue) {
    		o.createRenderable();
    	}
    }
    
    public void pushToStack(RenderableObject object) {
        if (!object.wasRenderCreated) {
            object.createRenderable();
        }
        renderableObjectsQueue.add(object);
    }
    
    
    
	public void run() {
		init();
		ShaderUtils.checkGLError("after init");
		
		renderableObjectsQueue = new ArrayList<>();
		
		floor = new Floor();
        drone = new Drone();
        flag = new Flag();
        camera = new Camera();
        
        
		
        light = new Light();
        grassTexture = new GrassTexture(texturePaths[0]);
        
        setup_renderables(); 
        FB = new FrameBuffer();
        
        compileShaders();
        
        QR = new QuadRenderer(quadShaderProgram);
        
       
		loop();
        //hello_world();
		
		
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	

		
	private void init() {
		
		GLFWErrorCallback.createPrint(System.err).set();

		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		
		glfwDefaultWindowHints(); 
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); 
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); 
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 1);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

		
		window = glfwCreateWindow(width, height, "Game", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		aspectRatio = (float) width / height;

		glfwSetFramebufferSizeCallback(window, (win, w, h) -> {
	        width = w;
	        height = h;
	        glViewport(0, 0, width, height);
	    });
		
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
			UserInput.setCameraMovements(camera, window, action, key);
			UserInput.setCameraMovementRotationMovements(camera, window, action, key);
					
		});
		
		
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); 
			IntBuffer pHeight = stack.mallocInt(1); 
			
			glfwGetWindowSize(window, pWidth, pHeight);
			
			

			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
			width = pWidth.get(0);
			height = pHeight.get(0);
		} 
		aspectRatio = width / height;
		glfwMakeContextCurrent(window);
		glfwSwapInterval(0);

				
		GL.createCapabilities();
		
	    glfwShowWindow(window);
	    System.out.println("Created Context");
	    
	    
		
	}
	
	
	private void renderShadowMap(Vector4f light, Vector3f eye, Matrix4f view, Matrix4f perspective) {
		aspectRatio = (float) width / height;
		Matrix4f lightProjectionMatrix = Light.getProjectionMatrix(aspectRatio);
        glEnable(GL_DEPTH_TEST);
        //glViewport(0,0,4000,4000);
        
        //FB.bindTexture();
		//ShaderUtils.setUniform(depthShaderProgram, "depthMap", 0);
        floor.render(depthShaderProgram, eye, light, view, lightProjectionMatrix, grassTexture);
        drone.render(depthShaderProgram, eye, light, view, lightProjectionMatrix);
        flag.render(depthShaderProgram, eye, light, view, lightProjectionMatrix);
        for (RenderableObject obj : renderableObjectsQueue) {
            obj.render(depthShaderProgram, eye, light, view, lightProjectionMatrix);
        }
        
	}
	
	private void renderScene(Vector4f light, Vector3f eye, Matrix4f view, Matrix4f perspective) {
        
		FB.bindTexture();
		
		ShaderUtils.setUniform(mainShaderProgram, "depthMap", 1);
        drone.render(mainShaderProgram, eye, light, view, perspective);
        flag.render(mainShaderProgram, eye, light, view, perspective);
        for (RenderableObject obj : renderableObjectsQueue) {
            obj.render(mainShaderProgram, eye, light, view, perspective);
        }
        floor.render(mainShaderProgram, eye, light, view, perspective, grassTexture, FB);
        glBindTexture(GL_TEXTURE_2D, 0);
        
	}
	
	private void renderDepth() {
		//glViewport(0,0,4000,4000);
		FB.bindTexture();
		ShaderUtils.setUniform(quadShaderProgram, "shadowMapSampler", 1);
		QR.render();
        glBindTexture(GL_TEXTURE_2D, 0);
        
		
	}
	
	
	private void loop() {
		
		
		Matrix4f perspectiveMatrix = camera.getPerspectiveMatrix(aspectRatio);
		
		float angle = 0f;
		boolean pointSourceFlag = true;
		
		Matrix4f viewMatrix = camera.getCameraMatrix();
		Vector3f eyePoint = camera.eyePoint;
		double lastTime = glfwGetTime();
		
		
		while ( !glfwWindowShouldClose(window) ) {
			glfwPollEvents();
			
			glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
	        
			
			Vector3f lightVector = new Vector3f();
            Vector3f lightPosition = new Vector3f();
            Vector3f[] light_details = light.getLightVector(angle);
            lightVector = light_details[0];
            lightPosition = light_details[1];
            
            
            Matrix4f lightViewMatrix = new Matrix4f();
            lightViewMatrix = light.getCameraMatrixLight(lightPosition);
            viewMatrix = camera.getCameraMatrix();
            eyePoint = camera.eyePoint;
            
            Vector4f lightVec = pointSourceFlag ? 
                new Vector4f(lightPosition, 1.0f) : 
                new Vector4f(lightVector, 0.0f);
            
            FB.bind();
            FB.clear(); 
            
            glClearDepth(1.0);
            glEnable(GL_DEPTH_TEST);
            
            renderShadowMap(lightVec, eyePoint, lightViewMatrix, perspectiveMatrix); 
            
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            //renderDepth();
            renderScene(lightVec, eyePoint, viewMatrix, perspectiveMatrix); 
            //angle += 0.1f;
            glfwSwapBuffers(window);
            
			
		}
	}


	public static void main(String[] args) {
		new Main().run();

	}

}
