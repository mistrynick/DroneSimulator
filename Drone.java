package drones;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

public class Drone extends RenderableObject {
    private float x = 0, y = 5, z = 0;
    private float scale = 2.3f;
    
    public Drone() {
        super(new ObjectParams(FILES.get(1), true, true));
        this.color = new Vector3f(0.255f, 0.145f, 0.612f);  // 65, 37, 156 normalized
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
            .scale(scale);
    }
    
    @Override 
    public void render(int shaderProgram, Vector3f eye, Vector4f light, Matrix4f view, Matrix4f perspective) {
    	if (!wasRenderCreated) {
            throw new IllegalStateException("Must create renderable first");
        }

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
        GL33.glUniform1i(metalLocation, int32_True);
        
        int hasTexture = GL33.glGetUniformLocation(shaderProgram, "hasTexture");
        GL33.glUniform1i(hasTexture, int32_False);
        
        int customColor = GL33.glGetUniformLocation(shaderProgram, "customColor");
        GL33.glUniform3f(customColor, this.color.x, this.color.y, this.color.z);
        

        int modelLocation = GL33.glGetUniformLocation(shaderProgram, "model");
        transformation.get(matrixBuffer);
        GL33.glUniformMatrix4fv(modelLocation, false, matrixBuffer);

        GL33.glBindVertexArray(vao);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, vertices.length / 8);

        MemoryUtil.memFree(matrixBuffer);
    }
}
