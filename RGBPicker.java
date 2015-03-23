package javanotifier;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;

public class RGBPicker extends JDialog{
    public JPanel panel = new JPanel();
    public JButton select = new JButton("Select Color");
    public JButton cancel = new JButton("Cancel");
    public JColorChooser RGBpicker = new JColorChooser(); 
    
    public RGBPicker() {
        this.setLayout(new BorderLayout());
        this.add(RGBpicker, BorderLayout.CENTER);
        RGBpicker.setPreviewPanel(new JPanel());
        AbstractColorChooserPanel[] panels = RGBpicker.getChooserPanels();
        for(AbstractColorChooserPanel panel : panels) {
            String clsName = panel.getDisplayName();
            if (!clsName.equals("HSV")) {
                RGBpicker.removeChooserPanel(panel);
            }
        }
        select.setBackground(Color.white);
        cancel.setBackground(Color.white);
        this.add(panel, BorderLayout.SOUTH);
        panel.add(select, BorderLayout.WEST);
        panel.add(cancel, BorderLayout.EAST);
        this.setTitle("Pick A Color");
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
}
