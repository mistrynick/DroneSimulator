package drones;

import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Matrix4f;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

public class RenderableObject {
    protected static final List<String> FILES = Arrays.asList(
        "./src/drones/objects/cube.obj",
        "./src/drones/objects/model.obj",
        "./src/drones/objects/sphere.obj",
        "./src/drones/objects/flag.obj"
    );

    protected int vao;
    protected int vbo;
    protected int quadVao;
    protected boolean wasRenderCreated;
    protected Vector3f color;
    protected boolean usesTexture;
    protected Matrix4f transformation;
    protected float[] vertices;
    protected static int int32_False = 0;
    protected static int int32_True = 1;
    
    

    public RenderableObject(ObjectParams params) {
    	//object = new ObjectLoader();
    	//File file = new File(params.filename);
    	//Obj obj = object.loadModel(file);
        this.vertices = ObjectLoader.getObjectData(params.filename, params.hasNormal, params.hasTexture);
        //debug();
        this.color = new Vector3f(1, 0, 0);
        this.usesTexture = false;
        createBuffers();
    }
    public void debug() {
    	for(int i = 0; i < Math.min(24, vertices.length); i += 8) {
    	    System.out.println("Vertex " + (i/8) + ": " + 
    	        vertices[i] + ", " + vertices[i+1] + ", " + vertices[i+2]);
    	}
    }

    protected void createBuffers() {
        // Create and bind VAO
        vao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(vao);

        // Create and bind VBO
        vbo = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);

        // Upload data to VBO
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertexBuffer, GL33.GL_STATIC_DRAW);
        MemoryUtil.memFree(vertexBuffer);
    }

    public void createRenderable() {
        GL33.glBindVertexArray(vao);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);

        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 8 * Float.BYTES, 0);
        GL33.glEnableVertexAttribArray(0);

        GL33.glVertexAttribPointer(1, 3, GL33.GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        GL33.glEnableVertexAttribArray(1);

        GL33.glVertexAttribPointer(2, 2, GL33.GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        GL33.glEnableVertexAttribArray(2);
        
        wasRenderCreated = true;
        
    }
    
    public void renderDepth(int depthProgram) {
    	if (!wasRenderCreated) {
            throw new IllegalStateException("Must create renderable first");
        }
        GL33.glEnable(GL33.GL_DEPTH_TEST);

        GL33.glUseProgram(depthProgram);
        
        FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(16);
        int modelLocation = GL33.glGetUniformLocation(depthProgram, "model");
        transformation.get(matrixBuffer);
        GL33.glUniformMatrix4fv(modelLocation, false, matrixBuffer);
        GL33.glBindVertexArray(vao);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, vertices.length / 8);
        
        MemoryUtil.memFree(matrixBuffer);
        int error;
        while ((error = GL33.glGetError()) != GL33.GL_NO_ERROR) {
            System.err.println("OpenGL Error: " + error);
        }
        
        
    }
    
    
    
    

    public void render(int shaderProgram, Vector3f eye, Vector4f light, Matrix4f view, Matrix4f perspective) {
        if (!wasRenderCreated) {
            throw new IllegalStateException("Must create renderable first");
        }
        GL33.glEnable(GL33.GL_DEPTH_TEST);

        GL33.glUseProgram(shaderProgram);
        
        FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(16);

        int eyeLocation = GL33.glGetUniformLocation(shaderProgram, "eye");
        GL33.glUniform3f(eyeLocation, eye.x, eye.y, eye.z);

        int lightLocation = GL33.glGetUniformLocation(shaderProgram, "light");
        GL33.glUniform4f(lightLocation, light.x, light.y, light.z, light.w);

        int viewLocation = GL33.glGetUniformLocation(shaderProgram, "view");
        view.get(matrixBuffer);
        GL33.glUniformMatrix4fv(viewLocation, false, matrixBuffer);

        int perspLocation = GL33.glGetUniformLocation(shaderProgram, "perspective");
        perspective.get(matrixBuffer);
        GL33.glUniformMatrix4fv(perspLocation, false, matrixBuffer);

        int colorLocation = GL33.glGetUniformLocation(shaderProgram, "customColor");
        GL33.glUniform3f(colorLocation, color.x, color.y, color.z);

        int metalLocation = GL33.glGetUniformLocation(shaderProgram, "metal");
        GL33.glUniform1i(metalLocation, 0);
        
        int hasTexture = GL33.glGetUniformLocation(shaderProgram, "hasTexture");
        GL33.glUniform1i(hasTexture, 0);

        int modelLocation = GL33.glGetUniformLocation(shaderProgram, "model");
        transformation.get(matrixBuffer);
        GL33.glUniformMatrix4fv(modelLocation, false, matrixBuffer);

        GL33.glBindVertexArray(vao);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, vertices.length / 8);

        MemoryUtil.memFree(matrixBuffer);
        int error;
        while ((error = GL33.glGetError()) != GL33.GL_NO_ERROR) {
            System.err.println("OpenGL PError: " + error);
        }
        
    }

    public void cleanup() {
        GL33.glDeleteBuffers(vbo);
        GL33.glDeleteVertexArrays(vao);
        if (quadVao != 0) GL33.glDeleteVertexArrays(quadVao);
    }
}





class ObjectParams {
    public final String filename;
    public final boolean hasNormal;
    public final boolean hasTexture;

    public ObjectParams(String filename, boolean hasNormal, boolean hasTexture) {
        this.filename = filename;
        this.hasNormal = hasNormal;
        this.hasTexture = hasTexture;
    }
}