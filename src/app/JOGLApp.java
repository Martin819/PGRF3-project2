package app;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;

public class JOGLApp {
    private static final int FPS = 60; // animator's target frames per second
    private static final String APP_TITLE ="PGRF3 - 2.uloha - Martin Polreich";
    private static final Dimension windowSize= new Dimension(1024, 768);
    private static Renderer ren;
    private static GLCanvas canvas;
    private static GLProfile profile;
    private static GLCapabilities capabilities;
    private static JFrame jFrame;
    private static JMenuBar menuBar;
    private static JButton jOpenButton;
    private static JButton jSaveButton;
    private static JFileChooser jFileChooser;
    private static JSlider jExposureSlider;
    private static JLabel jExposureSliderValue;
    private static JSlider jGammaSlider;
    private static JLabel jGammaSliderValue;
    private static String mappingType;
    public static void main(String[] args) {

        try {
            jFrame = new JFrame("TestFrame");
            jFrame.setSize(windowSize.width, windowSize.height);
            jFrame.setJMenuBar(getMenus());
            profile = GLProfile.getMaximum(true);
            capabilities = new GLCapabilities(profile);
            canvas = new GLCanvas(capabilities);
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
        menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createMatteBorder(1, 1, 2, 1, Color.BLACK));
        jOpenButton = new JButton("Choose Image");
        jOpenButton.addActionListener(JOGLApp::ChooseImageButtonActionPerformed);
        menuBar.add(jOpenButton);
        jSaveButton = new JButton("Save Image");
        jSaveButton.addActionListener(JOGLApp::SaveImageButtonActionPerformed);
        menuBar.add(jSaveButton);
        menuBar.add(Box.createHorizontalGlue());

/* MAPPING TYPES */
        JMenu mappingMenu = new JMenu("Mapping type");
        ButtonGroup btnMapping = new ButtonGroup();
/* LILNEAR */
        JMenuItem menuObjectLinear = new JCheckBoxMenuItem("Linear");
        menuObjectLinear.addActionListener(e -> {
            ren.setMappingType(Renderer.LINEAR);
            jGammaSlider.setEnabled(true);
            jExposureSlider.setEnabled(true);
            mappingMenu.setText("Mapping type: Linear");
            mappingType = "Linear";
        });
        btnMapping.add(menuObjectLinear);
        mappingMenu.add(menuObjectLinear);
/* SIMPLE REINHARD */
        JMenuItem menuObjectSimpleReinhard = new JCheckBoxMenuItem("Simple Reinhard");
        menuObjectSimpleReinhard.addActionListener(e -> {
            ren.setMappingType(Renderer.SIMPLE_REINHARD);
            jGammaSlider.setEnabled(true);
            jExposureSlider.setEnabled(true);
            mappingMenu.setText("Mapping type: Simple Reinhard");
            mappingType = "SimpleReinhard";
        });
        btnMapping.add(menuObjectSimpleReinhard);
        mappingMenu.add(menuObjectSimpleReinhard);
/* LUMA REINHARD */
        JMenuItem menuObjectLumaReinhard = new JCheckBoxMenuItem("Luma based Reinhard");
        menuObjectLumaReinhard.addActionListener(e -> {
            ren.setMappingType(Renderer.LUMA_REINHARD);
            jGammaSlider.setEnabled(true);
            jExposureSlider.setEnabled(false);
            mappingMenu.setText("Mapping type: Luma based Reinhard");
            mappingType = "LumaBasedReinhard";
        });
        btnMapping.add(menuObjectLumaReinhard);
        mappingMenu.add(menuObjectLumaReinhard);
/* ROMBINDAHOUSE */
        JMenuItem menuObjectRomBinDaHouse = new JCheckBoxMenuItem("RomBinDaHouse");
        menuObjectRomBinDaHouse.addActionListener(e -> {
            ren.setMappingType(Renderer.ROMBINDAHOUSE);
            jGammaSlider.setEnabled(true);
            jExposureSlider.setEnabled(false);
            mappingMenu.setText("Mapping type: RomBinDaHouse");
            mappingType = "RomBinDaHouse";
        });
        btnMapping.add(menuObjectRomBinDaHouse);
        mappingMenu.add(menuObjectRomBinDaHouse);
/* FILMIC */
        JMenuItem menuObjectFilmic = new JCheckBoxMenuItem("Filmic");
        menuObjectFilmic.addActionListener(e -> {
            ren.setMappingType(Renderer.FILMIC);
            jGammaSlider.setEnabled(false);
            jExposureSlider.setEnabled(false);
            mappingMenu.setText("Mapping type: Filmic");
            mappingType = "Filmic";
        });
        btnMapping.add(menuObjectFilmic);
        mappingMenu.add(menuObjectFilmic);
/* UNCHARTED */
        JMenuItem menuObjectUncharted = new JCheckBoxMenuItem("Uncharted");
        menuObjectUncharted.addActionListener(e -> {
            ren.setMappingType(Renderer.UNCHARTED);
            jGammaSlider.setEnabled(true);
            jExposureSlider.setEnabled(true);
            mappingMenu.setText("Mapping type: Uncharted");
            mappingType = "Uncharted";
        });
        btnMapping.add(menuObjectUncharted);
        mappingMenu.add(menuObjectUncharted);
/* ORIGINAL */
        JMenuItem menuObjectOriginal = new JCheckBoxMenuItem("Original image");
        menuObjectOriginal.addActionListener(e -> {
            ren.setMappingType(Renderer.ORIGINAL);
            jGammaSlider.setEnabled(false);
            jExposureSlider.setEnabled(false);
            mappingMenu.setText("Mapping type: None");
            mappingType = "OriginalImage";
        });
        btnMapping.add(menuObjectOriginal);
        mappingMenu.addSeparator();
        mappingMenu.add(menuObjectOriginal);
        mappingMenu.setBackground(Color.gray);
        mappingMenu.setText("Mapping type: None");
        mappingType = "OriginalImage";
        btnMapping.setSelected(menuObjectOriginal.getModel(), true);
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        menuBar.add(mappingMenu);
        menuBar.add(sep);
        menuBar.add(Box.createHorizontalGlue());

/* EXPOSURE SLIDER */
        jExposureSlider = new JSlider(0, 50);
        jExposureSlider.setMajorTickSpacing(10);
        jExposureSlider.setMinorTickSpacing(5);
        jExposureSlider.setPaintTicks(true);
        Hashtable labelTable = new Hashtable();
        labelTable.put(jExposureSlider.getMinimum(), new JLabel("0") );
        labelTable.put(jExposureSlider.getMaximum(), new JLabel("5") );
        jExposureSlider.setLabelTable( labelTable );
        jExposureSlider.setPaintLabels(true);
        jExposureSlider.setValue(15);
        jExposureSlider.setEnabled(false);
        JLabel jExposureSliderLabel = new JLabel("Exposure:  ", JLabel.CENTER);
        jExposureSliderValue = new JLabel(String.valueOf((float) jExposureSlider.getValue() / 10));
        jExposureSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        jExposureSlider.addChangeListener(e1 -> {
            ren.setExposure((float) jExposureSlider.getValue() / 10);
            updateSliderValue();
        });
        menuBar.add(jExposureSliderLabel);
        menuBar.add(jExposureSliderValue);
        menuBar.add(Box.createRigidArea(new Dimension(15, 10)));
        menuBar.add(jExposureSlider);

/* GAMMA SLIDER */
        jGammaSlider = new JSlider(0, 50);
        jGammaSlider.setMajorTickSpacing(10);
        jGammaSlider.setMinorTickSpacing(5);
        jGammaSlider.setPaintTicks(true);
        labelTable.put(jGammaSlider.getMinimum(), new JLabel("0") );
        labelTable.put(jGammaSlider.getMaximum(), new JLabel("5") );
        jGammaSlider.setLabelTable( labelTable );
        jGammaSlider.setPaintLabels(true);
        jGammaSlider.setValue(7);
        jGammaSlider.setEnabled(false);
        JLabel jGammaSliderLabel = new JLabel("Gamma:  ", JLabel.CENTER);
        jGammaSliderValue = new JLabel(String.valueOf((float) jGammaSlider.getValue() / 10));
        jGammaSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        jGammaSlider.addChangeListener(e1 -> {
            ren.setGamma((float) jGammaSlider.getValue() / 10);
            updateSliderValue();
        });
        menuBar.add(jGammaSliderLabel);
        menuBar.add(jGammaSliderValue);
        menuBar.add(Box.createRigidArea(new Dimension(15, 10)));
        menuBar.add(jGammaSlider);
        menuBar.add(Box.createHorizontalGlue());

        /* INFO */
        JButton btnAbout = new JButton("info");
        btnAbout.setFocusable(false);
        btnAbout.addActionListener(e -> JOptionPane.showMessageDialog(getFrame(),
                "This project demonstrates basic tone mapping algorithms on the images.\n\n" +
                        "USAGE:\n" +
                        "\nClick 'Choose Image' button to open the image. (JPG and PNG files are supported)" +
                        "\nClick the 'Mapping type' to select desired Tone mapping type. " +
                        "\nUse the sliders to adjust Exposure and Gamma of the tone mapping. (Availability varies for each mapping type)" +
                        "\nClick 'Save image' to save the edited image to the computer.\n\n" +
                        "EXAMPLE PRESETS:\n" +
                        "\nLinear ---------------- Exposure = 1.2, Gamma = 0.4-0.5" +
                        "\nSimple Reinhard -- Exposure = 1.5, Gamma = 0.5" +
                        "\nRomBinDaHouse -- Gamma = 0.5" +
                        "\nUncharted ----------- Exposure = 3.0, Gamma = 0.8\n\n" +
                        "NOTE: Uncharted type shows tone mapping used in Uncharted 2 PC game\n" +
                        "NOTE 2: See Readme.md file for references","About",JOptionPane.INFORMATION_MESSAGE));
        menuBar.add(btnAbout);
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
        int returnValue = jFileChooser.showOpenDialog(getFrame());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jFileChooser.getSelectedFile();
            String imagePath = null;
            imagePath = selectedFile.getPath();
            imagePath=imagePath.replace("\\", "/");
            Dimension imageSize = getImageSize(imagePath);
            ren.loadImage(imagePath);
            getFrame().setSize(imageSize);
            getFrame().setPreferredSize(imageSize);
        }
    }

    private static void SaveImageButtonActionPerformed(ActionEvent e) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String outputName = FilenameUtils.removeExtension(jFileChooser.getSelectedFile().getName());
        File outputImage = new File(outputName + "_" + mappingType + "_" + dtf.format(now) + ".jpg");
        saveCanvasToImage(outputImage, "jpg");
        JOptionPane.showMessageDialog(jFrame, "File saved to "+ outputImage.getAbsolutePath(),"Saved",JOptionPane.INFORMATION_MESSAGE);
    }

    private static void saveCanvasToImage(File file, String format){

        try{
            ImageIO.write(ren.getBufferedImage(profile), format, file);
        }catch(IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(getFrame(), e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static Dimension getImageSize(String imagePath){
        BufferedImage readImage = null;
        int h=0,w=0;
        try {
            readImage = ImageIO.read(new File(imagePath));
            h = readImage.getHeight() + menuBar.getHeight();
            w = readImage.getWidth();
            System.out.println("w: " + w + "\nh: " + h);
        } catch (Exception e) {
            readImage = null;
            JOptionPane.showMessageDialog(getFrame(), e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return new Dimension(w, h);
    }

    private static void updateSliderValue(){
        jExposureSliderValue.setText(String.valueOf((float) jExposureSlider.getValue() / 10));
        jGammaSliderValue.setText(String.valueOf((float) jGammaSlider.getValue() / 10));
    }

    private static JFrame getFrame(){
        return jFrame;
    }
}
