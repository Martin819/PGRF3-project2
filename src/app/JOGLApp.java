package app;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;

public class JOGLApp {
    private static final int FPS = 60; // animator's target frames per second
    private static final String APP_TITLE ="PGRF3 - 2.uloha - Martin Polreich";
    private static final Dimension windowSize= new Dimension(1024, 768);
    private static Renderer ren;
    private static JFrame jFrame;
    private static JButton jOpenButton;
    private static JFileChooser jFileChooser;
    public static void main(String[] args) {

        try {
            jFrame = new JFrame("TestFrame");
            jFrame.setSize(windowSize.width, windowSize.height);
            jFrame.setJMenuBar(getMenus());
            GLProfile profile = GLProfile.getMaximum(true);
            GLCapabilities capabilities = new GLCapabilities(profile);
            GLCanvas canvas = new GLCanvas(capabilities);
            ren = new Renderer();
            canvas.addGLEventListener(ren);
            canvas.addMouseListener(ren);
            canvas.addMouseMotionListener(ren);
            canvas.addKeyListener(ren);
            canvas.setSize(windowSize.width,windowSize.height);
            jFrame.add(canvas);
            final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
            jFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    new Thread(() -> {
                        if (animator.isStarted()) animator.stop();
                        System.exit(0);
                    }).start();
                }
            });
            jFrame.setTitle(APP_TITLE);
            jFrame.pack();
            jFrame.setVisible(true);
            animator.start(); // start the animation loop
        } catch (HeadlessException | GLException e) {
            e.printStackTrace();
        }
    }
    private static JMenuBar getMenus() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createMatteBorder(1, 1, 2, 1, Color.BLACK));
        jOpenButton = new JButton("Choose Image");
        jOpenButton.addActionListener(JOGLApp::ChooseImageButtonActionPerformed);
        menuBar.add(jOpenButton);
        return menuBar;
    }

    private static void ChooseImageButtonActionPerformed(ActionEvent e) {
        jFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jFileChooser.setDialogTitle("Choose Image");
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Image files", "jpg", "JPG", "png", "PNG", "jpeg", "JPEG");
        jFileChooser.addChoosableFileFilter(fileFilter);
        jFileChooser.setAcceptAllFileFilterUsed(false);
        int returnValue = jFileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jFileChooser.getSelectedFile();
            System.out.println(selectedFile.getAbsolutePath());
        }

    }

}
