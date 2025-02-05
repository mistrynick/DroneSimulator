package drones;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL33.glBindSampler;
import static org.lwjgl.opengl.GL33.glGenSamplers;
import static org.lwjgl.opengl.GL33.glSamplerParameteri;
import static org.lwjgl.opengl.GL33.GL_TEXTURE_BORDER_COLOR;
import static org.lwjgl.opengl.GL33.GL_CLAMP_TO_BORDER;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL45;

import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;


import java.nio.ByteBuffer;

public class FrameBuffer {
    public int depthTexture;
    private int fbo;
    private int colorTexture;
    private int depthSampler;
    private int TEXTURE_UNIT = 1;
    private static final int shadowMapSize = 1024;

    public FrameBuffer() {
    	
    	createDepthTexture();
        
        fbo = GL45.glGenFramebuffers();
        GL45.glBindFramebuffer(GL45.GL_FRAMEBUFFER, fbo);
        GL45.glFramebufferTexture2D(GL45.GL_FRAMEBUFFER, GL45.GL_COLOR_ATTACHMENT0, GL45.GL_TEXTURE_2D, colorTexture, 0);
        GL45.glFramebufferTexture2D(GL45.GL_FRAMEBUFFER, GL45.GL_DEPTH_ATTACHMENT, GL45.GL_TEXTURE_2D, depthTexture, 0);

        if (GL45.glCheckFramebufferStatus(GL45.GL_FRAMEBUFFER) != GL45.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer is not complete");
        }

        depthSampler = GL45.glGenSamplers();
        GL45.glSamplerParameteri(depthSampler, GL45.GL_TEXTURE_MIN_FILTER, GL45.GL_LINEAR);
        GL45.glSamplerParameteri(depthSampler, GL45.GL_TEXTURE_MAG_FILTER, GL45.GL_LINEAR);
        GL45.glSamplerParameteri(depthSampler, GL45.GL_TEXTURE_WRAP_S, GL45.GL_CLAMP_TO_EDGE);
        GL45.glSamplerParameteri(depthSampler, GL45.GL_TEXTURE_WRAP_T, GL45.GL_CLAMP_TO_EDGE);
        GL45.glSamplerParameteri(depthSampler, GL45.GL_TEXTURE_COMPARE_MODE, GL45.GL_COMPARE_REF_TO_TEXTURE);
        GL45.glSamplerParameteri(depthSampler, GL45.GL_TEXTURE_COMPARE_FUNC, GL45.GL_LESS);

        GL45.glBindSampler(0, depthSampler);
        
    }
    
    
    void createDepthTexture() {
    	depthTexture = GL45.glGenTextures();
        GL45.glBindTexture(GL45.GL_TEXTURE_2D, depthTexture);
        GL45.glTexImage2D(GL45.GL_TEXTURE_2D, 0, GL45.GL_DEPTH_COMPONENT32F, shadowMapSize, shadowMapSize, 0, GL45.GL_DEPTH_COMPONENT, GL45.GL_FLOAT, (ByteBuffer) null);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_COMPARE_MODE, GL45.GL_COMPARE_REF_TO_TEXTURE);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_COMPARE_FUNC, GL45.GL_LESS);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_MIN_FILTER, GL45.GL_NEAREST);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_MAG_FILTER, GL45.GL_NEAREST);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_WRAP_S, GL45.GL_CLAMP_TO_EDGE);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_WRAP_T, GL45.GL_CLAMP_TO_EDGE);
        colorTexture = GL45.glGenTextures();
        GL45.glBindTexture(GL45.GL_TEXTURE_2D, colorTexture);
        GL45.glTexImage2D(GL45.GL_TEXTURE_2D, 0, GL45.GL_RGBA8, shadowMapSize, shadowMapSize, 0, GL45.GL_RGBA, GL45.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        
        
    }
    
    void bindTexture() {
    	GL45.glActiveTexture(GL45.GL_TEXTURE1);
    	GL45.glBindTexture(GL45.GL_TEXTURE_2D, depthTexture);
    	GL45.glBindSampler(TEXTURE_UNIT, depthSampler);
        
       
    }
   
    void bind() {
    	glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    }
    
    void unbind() {
    	glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    void clear() {
    	glClear(GL_DEPTH_BUFFER_BIT);
    }
}
