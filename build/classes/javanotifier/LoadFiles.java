package javanotifier;

import java.awt.*;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;

public class LoadFiles {
    private Preferences savedSettings;
    private JavaRender og;
    private Settings s;
    private RGBPicker rgbPicker;
    
    public LoadFiles(Component parent, Settings sin, Preferences in, JavaRender inOG, RGBPicker inRGB) {
        rgbPicker = inRGB;
        og = inOG;
        savedSettings = in;
        s = sin;
    }
    public String[] getFiles(String path) throws Exception {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        String files[] = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                files[i] = listOfFiles[i].getName();
            }
        }
        return files;
    }
    public void checkUser(Preferences in) {
        if(in.get("USER_NAME", "").trim().equals("")){
            in.put("USER_NAME", JOptionPane.showInputDialog(null, "Please input your Twitch.tv username:"));
            if(in.get("USER_NAME", "").trim().equals("") || in.get("USER_NAME", "") == null) {
                in.put("USER_NAME", "");
                System.exit(0);
            }
        }
    }
}
