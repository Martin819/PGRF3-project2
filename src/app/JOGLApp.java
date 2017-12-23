package app;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JOGLApp {
    private static final int FPS = 60; // animator's target frames per second
    private static final String APP_TITLE ="PGRF3 - 2.uloha - Martin Polreich";
    private static final Dimension windowSize= new Dimension(1024, 768);
    private static Renderer ren;
    private static JFrame frame;
    public static void main(String[] args) {

        try {
            frame = new JFrame("TestFrame");
            frame.setSize(windowSize.width, windowSize.height);
            GLProfile profile = GLProfile.getMaximum(true);
            GLCapabilities capabilities = new GLCapabilities(profile);
            GLCanvas canvas = new GLCanvas(capabilities);
            ren = new Renderer();
            canvas.addGLEventListener(ren);
            canvas.addMouseListener(ren);
            canvas.addMouseMotionListener(ren);
            canvas.addKeyListener(ren);
            canvas.setSize(windowSize.width,windowSize.height);
            frame.add(canvas);
            final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    new Thread(() -> {
                        if (animator.isStarted()) animator.stop();
                        System.exit(0);
                    }).start();
                }
            });
            frame.setTitle(APP_TITLE);
            frame.pack();
            frame.setVisible(true);
            animator.start(); // start the animation loop
        } catch (HeadlessException | GLException e) {
            e.printStackTrace();
        }
    }
}
