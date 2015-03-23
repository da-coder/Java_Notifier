package javanotifier;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Splash extends JDialog {
    JTextPane screen = new JTextPane();
    JPanel display = new MyPanel();
    
    Settings s;
    
    public Splash(Settings sIn) {
        this.setBounds(0,0,184,42);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setUndecorated(true);
        this.setVisible(true);
        this.getRootPane().setOpaque(false);
        this.getContentPane().setBackground(new Color (0, 0, 0, 0));
        this.setBackground(new Color(0, 0, 0, 0));
        
        this.setLayout(new BorderLayout());
        this.add(display, BorderLayout.CENTER);
        display.setOpaque(false);
        display.setLayout(new BorderLayout());
        display.add(screen, BorderLayout.CENTER);
        screen.setOpaque(false);
        screen.setEditable(false);
        updateSplash("Libraries...");
        
        s = sIn;
    }
    
    public void updateSplash(String outputIn) {
        StyledDocument doc = screen.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(0, "\n" + outputIn, center);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    class MyPanel extends JPanel {
        public MyPanel() {
            super(true);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();

            try {
                BufferedImage back = ImageIO.read(new File("Splash.png"));
                g2d.drawImage(back,0,0,null);
                g2d.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
