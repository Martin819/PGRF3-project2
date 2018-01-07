package app;

import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import oglutils.*;
import transforms.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

public class Renderer implements GLEventListener, MouseListener,
        MouseMotionListener, KeyListener {

    private OGLTextRenderer textRenderer;
    static final int LINEAR = 0, SIMPLE_REINHARD = 1, LUMA_REINHARD = 2, ROMBINDAHOUSE = 3, FILMIC = 4, UNCHARTED = 5, ORIGINAL = 6;
    private int shaderProgram, mappingType = 6;
    private int locImageHeight, locImageWidth, locExposure, locGamma, locLumaR, locLumaG, locLumaB, locMapType, locMat;
    private float imageHeight=1024f, imageWidth=768f, exposure=1.2f, gamma=0.7f, lumaR=0.2126f, lumaG=0.7152f, lumaB=0.0722f;
    private int width, height;
    private String imagePath;
    private boolean imageChanged = false;
    private OGLBuffers buff;
    private OGLTexture2D image;
    private GL2GL3 gl;

    @Override
    public void init(GLAutoDrawable glDrawable) {
        // check whether shaders are supported
        gl = glDrawable.getGL().getGL2GL3();
        OGLUtils.shaderCheck(gl);

        // get and set debug version of GL class
        gl = OGLUtils.getDebugGL(gl);
        glDrawable.setGL(gl);

        OGLUtils.printOGLparameters(gl);

        textRenderer = new OGLTextRenderer(gl, glDrawable.getSurfaceWidth(), glDrawable.getSurfaceHeight());
        shaderProgram = ShaderUtils.loadProgram(gl, "/shader");
        gl.glUseProgram(shaderProgram);
        locImageHeight = gl.glGetUniformLocation(shaderProgram, "imageHeight");
        locImageWidth = gl.glGetUniformLocation(shaderProgram, "imageWidth");
        locExposure = gl.glGetUniformLocation(shaderProgram, "exposure");
        locGamma = gl.glGetUniformLocation(shaderProgram, "gamma");
        locLumaR = gl.glGetUniformLocation(shaderProgram, "lumaR");
        locLumaG = gl.glGetUniformLocation(shaderProgram, "lumaG");
        locLumaB = gl.glGetUniformLocation(shaderProgram, "lumaB");
        locMapType = gl.glGetUniformLocation(shaderProgram, "mappingType");
        createBuffers();
    }

    void createBuffers() {
        float[] vertexBufferData = {
                -1, 1,
                1, 1,
                1, -1,
                -1 ,-1
        };
        int[] indexBufferData = { 0, 1, 2, 3 };
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPos", 2),
        };
        buff = new OGLBuffers(gl, vertexBufferData, attributes,
                indexBufferData);
    }

    @Override
    public void display(GLAutoDrawable glDrawable) {
        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);
        gl.glUniform1f(locImageHeight, imageHeight);
        gl.glUniform1f(locImageWidth, imageWidth);
        gl.glUniform1f(locExposure, exposure);
        gl.glUniform1f(locGamma, gamma);
        gl.glUniform1f(locLumaR, lumaR);
        gl.glUniform1f(locLumaG, lumaG);
        gl.glUniform1f(locLumaB, lumaB);
        gl.glUniform1f(locMapType, mappingType);
        if(this.imageChanged){
            image = new OGLTexture2D(gl, imagePath);
            this.imageChanged=false;
            image.bind(shaderProgram, "image", 0);
        }
        buff.draw(GL2GL3.GL_QUADS, shaderProgram);
    }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            this.width = width;
            this.height = height;
            textRenderer.updateSize(width, height);
        }

    public void loadImage(String imagePath) {
        this.imagePath = imagePath;
        this.imageChanged = true;
    }

    public BufferedImage getBufferedImage(GLProfile profile){
        AWTGLReadBufferUtil bufferUtils = new AWTGLReadBufferUtil(profile, false);
        gl.getContext().makeCurrent();
        return bufferUtils.readPixelsToBufferedImage(gl, true);
    }

    public void setExposure(float exposure) {
        this.exposure = exposure;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    public void setMappingType(int mappingType) {
        this.mappingType = mappingType;
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
