package javanotifier;

import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class JDisplayFrame {
    JDialog display;
    
    public JDisplayFrame(JFrame inFrame, JavaRender inOG) {
        display = new JDialog(inFrame);
        display.setUndecorated(true);
        display.setBounds(0,0,inFrame.getWidth()-128,inFrame.getHeight()-70);
        display.setLocation(inFrame.getX(),inFrame.getY());
        display.setLayout(new BorderLayout());
        display.add(inOG, BorderLayout.CENTER);
        display.setTitle("Display Frame | WINDOW CAPTURE ME!");
        display.setVisible(true);
    }
    public void setLocationOf(JPanel inPanel) {
        display.setLocation(inPanel.getLocationOnScreen());
        display.revalidate();
        display.repaint();
    }
    public void setSizeOf(JPanel inPanel) {
        display.setSize(inPanel.getSize());
        display.revalidate();
        display.repaint();
    }
}
