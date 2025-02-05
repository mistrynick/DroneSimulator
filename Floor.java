package drones;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

public class Floor extends RenderableObject {
	
	private final int TEXTURE_UNIT = 2;
    public Floor() {
        super(new ObjectParams(FILES.get(0), true, true));
        this.color = new Vector3f(0.11f, 0.62f, 0.13f);  // 28, 158, 33 normalized
        this.usesTexture = true;
        updateTransformation();
       
    }

    private void updateTransformation() {
        transformation = new Matrix4f()
            .translate(0, -1, 0)
            .scale(500, 1, 500);
    }
    
    public void render(int shaderProgram, Vector3f eye, Vector4f light, Matrix4f view, Matrix4f perspective, GrassTexture grasstexture) {
    	if (!wasRenderCreated) {
            throw new IllegalStateException("Must create renderable first");
        }

        GL33.glUseProgram(shaderProgram);
        grasstexture.bind(TEXTURE_UNIT);
        

        FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(16);

        int eyeLocation = GL33.glGetUniformLocation(shaderProgram, "eye");
        GL33.glUniform3f(eyeLocation, eye.x, eye.y, eye.z);
        
        int mapLocation = GL33.glGetUniformLocation(shaderProgram, "map");
        GL33.glUniform1i(mapLocation, TEXTURE_UNIT);

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
        GL33.glUniform1i(metalLocation, int32_False);
        
        int hasTexture = GL33.glGetUniformLocation(shaderProgram, "hasTexture");
        GL33.glUniform1i(hasTexture, int32_True);
        
        int customColor = GL33.glGetUniformLocation(shaderProgram, "customColor");
        GL33.glUniform3f(customColor, this.color.x, this.color.y, this.color.z);
        

        int modelLocation = GL33.glGetUniformLocation(shaderProgram, "model");
        transformation.get(matrixBuffer);
        GL33.glUniformMatrix4fv(modelLocation, false, matrixBuffer);
        
        

        GL33.glBindVertexArray(vao);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, vertices.length / 8);

        MemoryUtil.memFree(matrixBuffer);
    }
    
    public void render(int shaderProgram, Vector3f eye, Vector4f light, Matrix4f view, Matrix4f perspective, GrassTexture grasstexture, FrameBuffer FB) {
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
        GL33.glUniform1i(metalLocation, int32_False);
        
        int hasTexture = GL33.glGetUniformLocation(shaderProgram, "hasTexture");
        GL33.glUniform1i(hasTexture, int32_True);
        
        

        int modelLocation = GL33.glGetUniformLocation(shaderProgram, "model");
        transformation.get(matrixBuffer);
        GL33.glUniformMatrix4fv(modelLocation, false, matrixBuffer);
        
        FB.bindTexture();
        ShaderUtils.setUniform(shaderProgram, "depthMap", 1);
        
        
        grasstexture.bind(TEXTURE_UNIT);
        int mapLocation = GL33.glGetUniformLocation(shaderProgram, "map");
        GL33.glUniform1i(mapLocation, TEXTURE_UNIT);
        
        

        GL33.glBindVertexArray(vao);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, vertices.length / 8);

        MemoryUtil.memFree(matrixBuffer);
    }
}
