package app;

import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import oglutils.*;
import transforms.*;

import java.awt.event.*;

public class Renderer implements GLEventListener, MouseListener,
        MouseMotionListener, KeyListener {

    private OGLTextRenderer textRenderer;
    private int shaderProgram;
    private int locImageHeight, locImageWidth;
    private float imageHeight=512f, imageWidth=512f;
    private int width, height;
    private String imagePath = "/res/testTexture.jpg";
    private boolean imageChanged = false;
    private Camera cam = new Camera();
    private Mat4 proj;
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
/*        locMatGrid = gl.glGetUniformLocation(shaderProgram, "mat");
        locImage = gl.glGetUniformLocation(shaderProgram, "image");*/
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
//        GL2GL3 gl = glDrawable.getGL().getGL2GL3();
        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);
        gl.glUniform1f(locImageHeight, imageHeight);
        gl.glUniform1f(locImageWidth, imageWidth);
//        gl.glUniform1f(locEffectIntensity, effectIntesity);
/*        if(this.imageChanged){*/
            image = new OGLTexture2D(gl, imagePath);
            this.imageChanged=false;
            image.bind(shaderProgram, "image", 0);
/*        }*/

        // vykresleni
        buff.draw(GL2GL3.GL_QUADS, shaderProgram);
/*        textRenderer.drawStr2D(width-220, 3, " (c) Martin Polreich - PGRF3 - FIM UHK");*/
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
//        proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
        textRenderer.updateSize(width, height);
    }

    public void loadImage(String imagePath) {
        this.imagePath = imagePath;
        this.imageChanged = true;
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
