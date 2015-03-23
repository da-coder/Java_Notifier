package javanotifier;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

public class Settings extends JDialog implements ActionListener, ChangeListener{
    public String verNum = "Ver. 5.2";
    
    private JPanel post, switcher, setG, setD, setA, setAn, abt, dbackPrev, dtextPrev, outPrev, colorPnl1, colorPnl2, colorPnl3;
    
    private JTextPane aboutArea;
    public JTextField userField, concatField, numField;
    public JSlider animationSlider, time2, time3, time4, volume, outEnabledSlider, updateSlider;
    public JComboBox fontSelect = new JComboBox(), soundSelect = new JComboBox(), animationSelect = new JComboBox(), imageSelect = new JComboBox(), animationSelectOut = new JComboBox(), writeSelect = new JComboBox();
    private String[] animationSelections;
    public String[] followerSelections;
    private String colorPicked = "";
    public String decodedPath = "", animationChoice = "Fade", animationChoiceOut = "Fade";
    public int selctedIndexImage, timingA = 20;
    private JButton onTopButton, getPermission, dbackButton, dtextButton, postToChatButton, updateFilesButton, updateAudioButton, saveSetBtn,
            generalBtn, displayBtn, audioBtn, animBtn, aboutBtn, outButton, clearButton;
    private Label animationSliderLabel, featLabel, chatLabel, authLabel, updateFilesLabel, updateAudioLabel, userLabel, fontLabel, dbackLabel, dtextLabel, soundLabel, imageLabel,
            volumeLabel, animationLabel, animationLabelOut, time2Label, time3Label, time4Label, concatLabel, numLabel, writeLabel, updateLabel, outLabel, outEnabledLabel, clearLabel;
    
    public MyBot myBot;
    public BufferedImage[] images;
    private Color tempColor;
    
    Preferences savedSettings;
    
    public boolean startedUp, postToChat, isAdjusting = false, isDisplayed = false;;
    private String newVerNum;
    
    private LoadFiles Gl;
    private Component frame;
    private JavaRender og;
    private RGBPicker rgbPicker;
    private JavaNotifier on;
    private JDisplayFrame jdisp;
    
    public Settings(JavaNotifier inOGN) {
         on = inOGN;
    }
    public void construct(Component parent, LoadFiles l, Preferences in, JavaRender inOG, RGBPicker inRBG, JDisplayFrame indisp) {
        rgbPicker = inRBG;
        og = inOG;
        //setDisabled
        og.paintRect = false;
        og.paintRectImage = false;
        og.editMenuText.setBackground(Color.white);
        og.editMenuText.setForeground(Color.black);
        og.editMenuImage.setBackground(Color.white);
        og.editMenuImage.setForeground(Color.black);
        og.editMenuDisabled.setBackground(Color.white.darker());
        og.editMenuDisabled.setForeground(Color.black);
        og.popGoesTheWeasel.setVisible(false);
        frame = parent;
        savedSettings = in;
        Gl = l;
        jdisp = indisp;
        
        //define&construct
        post = new JPanel();
        switcher = new JPanel();
        setG = new JPanel();
        setD = new JPanel();
        setA = new JPanel();
        setAn = new JPanel();
        abt = new JPanel();
        dbackPrev = new JPanel();
        dtextPrev = new JPanel();
        outPrev = new JPanel();
        colorPnl1 = new JPanel();
        colorPnl2 = new JPanel();
        colorPnl3 = new JPanel();

        aboutArea = new JTextPane();
        userField = new JTextField();
        concatField = new JTextField();
        numField = new JTextField();
        time2 = new JSlider(1,10000,1000);
        time3 = new JSlider(1,30,20);
        time4 = new JSlider(1,30,20);
        volume = new JSlider(-50,6,-22);
        outEnabledSlider = new JSlider(0,50,20);
        updateSlider = new JSlider(60 * 1000,120 * 1000,60 * 1000);
        animationSlider = new JSlider(1,100,20);
        animationSelections = new String[]{"Fade", "Rise", "Fall", "Right", "Left"};
        followerSelections = new String[]{"Comma/Space", "Block/Line", "Dash/Space"};
        animationSelect = new JComboBox(animationSelections);
        animationSelectOut = new JComboBox(animationSelections);
        onTopButton = new JButton("Always_On_Top");
        writeSelect = new JComboBox(followerSelections);
        getPermission = new JButton("Authorize");
        dbackButton = new JButton("Pick Color");//color
        dtextButton = new JButton("Pick Color");//color
        outButton = new JButton("Pick Color");//color
        postToChatButton = new JButton("Post_To_Chat");
        updateFilesButton = new JButton("Re-Scan");
        updateAudioButton = new JButton("Re-Scan");
        saveSetBtn = new JButton("Apply and Save Settings");
        generalBtn = new JButton("General");
        displayBtn = new JButton("Display");
        audioBtn = new JButton("Audio");
        animBtn = new JButton("Animation");
        aboutBtn = new JButton("About");
        clearButton = new JButton("Clear?");
        animationSliderLabel = new Label("Animation: (<-smooth : performance->)");
        featLabel = new Label("Display window always on top:");
        writeLabel = new Label("How to write recently followed:");
        chatLabel = new Label("Post follower in chat:");
        authLabel = new Label("Get authorization: (for setting T/G)");
        updateFilesLabel = new Label("Re-scan for images:");
        updateAudioLabel = new Label("Re-scan for audio:");
        updateLabel = new Label("Update:    (<-every 1 min : every 2 min->)");
        userLabel = new Label("Twitch username:");
        fontLabel = new Label("Display font:");
        dbackLabel = new Label("Back color:");
        dtextLabel = new Label("Fore color:");
        outLabel = new Label("Outline color:");
        outEnabledLabel = new Label("Outline type: (<-thin : thick->)");
        soundLabel = new Label("Sound file:");
        imageLabel = new Label("Image file:");
        volumeLabel = new Label("Volume: (<-quiet : loud->)");
        animationLabel = new Label("Animation type in:");
        animationLabelOut = new Label("Animation type out:");
        time2Label = new Label("Display delay: (<-fast : slow->)");
        time3Label = new Label("Transition in:   (<-fast : slow->)");
        time4Label = new Label("Transition out: (<-fast : slow->)");
        concatLabel = new Label("Follower message: (!f = follower)");
        numLabel = new Label("Number of recently followed:");
        clearLabel = new Label("Clear all settings/followers: (hard reset)");

        myBot = new MyBot();

        startedUp = false;
        postToChat = false;
        
        //postingArea
        this.setLayout(new BorderLayout());
        this.add(post, BorderLayout.CENTER);
        post.setLayout(new BorderLayout());
        post.setDoubleBuffered(true);

        //display
        post.add(setD, BorderLayout.CENTER);
        setD.setLayout(new GridLayout(8,2,0,0));
        setD.add(fontLabel);
        setD.add(fontSelect);
        setD.add(dbackLabel);
        setD.add(colorPnl1);
        colorPnl1.setLayout(new BorderLayout());
        colorPnl1.add(dbackButton, BorderLayout.CENTER);
        colorPnl1.add(dbackPrev, BorderLayout.WEST);
        setD.add(dtextLabel);
        setD.add(colorPnl2);
        colorPnl2.setLayout(new BorderLayout());
        colorPnl2.add(dtextButton, BorderLayout.CENTER);
        colorPnl2.add(dtextPrev, BorderLayout.WEST);
        setD.add(outLabel);
        setD.add(colorPnl3);
        colorPnl3.setLayout(new BorderLayout());
        colorPnl3.add(outButton, BorderLayout.CENTER);
        colorPnl3.add(outPrev, BorderLayout.WEST);
        setD.add(outEnabledLabel);
        setD.add(outEnabledSlider);
        setD.add(imageLabel);
        setD.add(imageSelect);
        setD.add(updateFilesLabel);
        setD.add(updateFilesButton);
        setD.add(featLabel);
        setD.add(onTopButton);

        //general
        setG.setLayout(new GridLayout(8,2,0,0));
        setG.add(userLabel);
        setG.add(userField);
        setG.add(concatLabel);
        setG.add(concatField);
        setG.add(numLabel);
        setG.add(numField);
        setG.add(updateLabel);
        setG.add(updateSlider);
        setG.add(writeLabel);
        setG.add(writeSelect);
        setG.add(authLabel);
        setG.add(chatLabel);
        setG.add(clearLabel);
        setG.add(clearButton);
        setG.add(getPermission);
        setG.add(postToChatButton);

        //audio
        setAn.setLayout(new GridLayout(6,2,0,0));
        setAn.add(animationLabel);
        setAn.add(animationSelect);
        setAn.add(animationLabelOut);
        setAn.add(animationSelectOut);
        setAn.add(animationSliderLabel);
        setAn.add(animationSlider);
        setAn.add(time2Label);
        setAn.add(time2);
        setAn.add(time3Label);
        setAn.add(time3);
        setAn.add(time4Label);
        setAn.add(time4);
        
        //animation
        setA.setLayout(new GridLayout(6,2,0,0));
        setA.add(soundLabel);
        setA.add(soundSelect);
        setA.add(updateAudioLabel);
        setA.add(updateAudioButton);
        setA.add(volumeLabel);
        setA.add(volume);
        
        newVerNum = "";

        //about
        abt.setLayout(new BorderLayout());
        abt.add(aboutArea, BorderLayout.CENTER);
        aboutArea.setEditable(false);
        StyledDocument doc = aboutArea.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(0, "Author: DaCoder"
                    + "\nDate: January 2014"
                    + "\n" + verNum
                    + "\nModify to your hearts content!"
                    + "\nCheck out my stream?"
                    + "\nwww.twitch.tv/D4_C0D3R"
                    + "\nOr my website?"
                    + "\ndacoder.crabdance.com"
                    + "\nThanks for using!", center);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //switcher
        this.add(switcher, BorderLayout.WEST);
        switcher.setDoubleBuffered(true);
        switcher.setLayout(new GridLayout(5, 1, 0, 0));
        switcher.add(displayBtn);
        displayBtn.setFocusPainted(false);
        switcher.add(generalBtn);
        generalBtn.setFocusPainted(false);
        switcher.add(audioBtn);
        audioBtn.setFocusPainted(false);
        switcher.add(animBtn);
        animBtn.setFocusPainted(false);
        switcher.add(aboutBtn);
        aboutBtn.setFocusPainted(false);

        //saveSettings
        this.add(saveSetBtn, BorderLayout.SOUTH);

        //coloring
        saveSetBtn.setBackground(Color.white);
        saveSetBtn.setForeground(Color.black);
        for (int i = 0; i < post.getComponentCount(); i++) {
            post.getComponent(i).setBackground(Color.white);
            post.getComponent(i).setForeground(Color.black);
        }
        for (int i = 0; i < setD.getComponentCount(); i++) {
            setD.getComponent(i).setBackground(Color.white);
            setD.getComponent(i).setForeground(Color.black);
        }
        for (int i = 0; i < colorPnl1.getComponentCount(); i++) {
            colorPnl1.getComponent(i).setBackground(Color.white);
            colorPnl1.getComponent(i).setForeground(Color.black);
        }
        for (int i = 0; i < colorPnl2.getComponentCount(); i++) {
            colorPnl2.getComponent(i).setBackground(Color.white);
            colorPnl2.getComponent(i).setForeground(Color.black);
        }
        for (int i = 0; i < colorPnl3.getComponentCount(); i++) {
            colorPnl3.getComponent(i).setBackground(Color.white);
            colorPnl3.getComponent(i).setForeground(Color.black);
        }
        for (int i = 0; i < setG.getComponentCount()-2; i++) {
            setG.getComponent(i).setBackground(Color.white);
            setG.getComponent(i).setForeground(Color.black);
        }
        setG.setBackground(Color.white);
        for (int i = 0; i < setA.getComponentCount(); i++) {
            setA.getComponent(i).setBackground(Color.white);
            setA.getComponent(i).setForeground(Color.black);
        }
        for (int i = 0; i < setAn.getComponentCount(); i++) {
            setAn.getComponent(i).setBackground(Color.white);
            setAn.getComponent(i).setForeground(Color.black);
        }
        setAn.setBackground(Color.white);
        for (int i = 0; i < abt.getComponentCount(); i++) {
            abt.getComponent(i).setBackground(Color.white);
            abt.getComponent(i).setForeground(Color.black);
        }

        //settingUpSettings
        displayBtn.setBorderPainted(false);
        for (int i = 0; i < switcher.getComponentCount(); i++) {
            switcher.getComponent(i).setBackground(Color.white);
            switcher.getComponent(i).setForeground(Color.black);
        }
        generalBtn.setBackground(Color.white.darker());
        audioBtn.setBackground(Color.white.darker());
        animBtn.setBackground(Color.white.darker());
        aboutBtn.setBackground(Color.white.darker());
        
        l.checkUser(savedSettings);
        //getRunPath
        String path = JavaNotifier.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try{decodedPath = URLDecoder.decode(path, "UTF-8");}catch(Exception e){System.out.println(e);}
        //removeJarFromPathIfItExists
        if (decodedPath.contains("JavaNotifier.jar")) {
            decodedPath = decodedPath.replaceFirst("JavaNotifier.jar", "");
        }
        refreshFiles();
        try {
            readPreferences();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            addActions();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void refreshFiles() {
        //getAllFonts
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] allFonts = ge.getAllFonts();
        for (int i = 0; i < allFonts.length; i++) {
            fontSelect.addItem(allFonts[i].getFontName());
        }
        //getFilesORMakeAudioDir
        String[] tempArray;
        try {
            tempArray = Gl.getFiles("audio\\");
            for (int i = 0; i < tempArray.length; i++) {
                if (tempArray[i].contains(".wav")) {
                    System.out.println("Found audio file : " + tempArray[i] + " @ audio\\");
                    soundSelect.addItem(tempArray[i]);
                }
            }
        } catch (Exception e) {
            Boolean worked = new File("audio").mkdir();
            System.out.println("Made dir : " + worked);
        }
        //getImagesORMakeImageDir
        images = null;
        try {
            tempArray = Gl.getFiles("imgs\\");
            images = new BufferedImage[tempArray.length];
            for (int i = 0; i < tempArray.length; i++) {
                System.out.println("Found image file : " + tempArray[i] + " @ imgs\\");
                imageSelect.addItem(tempArray[i]);
                images[i] = ImageIO.read(new File("imgs\\" + tempArray[i]));
            }
        } catch (Exception e) {
            Boolean worked = new File("imgs").mkdir();
            System.out.println("Made dir : " + worked);
        }
        //selectTheJComboBoxes
        imageSelect.addItem("Disabled");
        soundSelect.addItem("Disabled");
    }
    public void addActions() {
        onTopButton.addActionListener(this);
        postToChatButton.addActionListener(this);
        getPermission.addActionListener(this);
        updateFilesButton.addActionListener(this);
        updateAudioButton.addActionListener(this);
        volume.addChangeListener(this);
        time2.addChangeListener(this);
        time3.addChangeListener(this);
        time4.addChangeListener(this);
        outEnabledSlider.addChangeListener(this);
        updateSlider.addChangeListener(this);
        animationSlider.addChangeListener(this);
        rgbPicker.RGBpicker.getSelectionModel().addChangeListener(this);
        saveSetBtn.addActionListener(this);
        aboutBtn.addActionListener(this);
        animBtn.addActionListener(this);
        audioBtn.addActionListener(this);
        displayBtn.addActionListener(this);
        generalBtn.addActionListener(this);
        dbackButton.addActionListener(this);
        dtextButton.addActionListener(this);
        outButton.addActionListener(this);
        rgbPicker.select.addActionListener(this);
        rgbPicker.cancel.addActionListener(this);
        animationSelect.addActionListener(this);
        animationSelectOut.addActionListener(this);
        writeSelect.addActionListener(this);
        clearButton.addActionListener(this);
        
        rgbPicker.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                rgbPicker.RGBpicker.setColor(tempColor);
                if (colorPicked.equals("dback")) {
                    savedSettings.putInt("BACK_COLOR_R", tempColor.getRed());
                    savedSettings.putInt("BACK_COLOR_G", tempColor.getGreen());
                    savedSettings.putInt("BACK_COLOR_B", tempColor.getBlue());
                    savedSettings.putInt("BACK_COLOR_A", tempColor.getAlpha());
                    dbackPrev.setBackground(tempColor);
                }
                if (colorPicked.equals("dtext")) {
                    savedSettings.putInt("FORE_COLOR_R", tempColor.getRed());
                    savedSettings.putInt("FORE_COLOR_G", tempColor.getGreen());
                    savedSettings.putInt("FORE_COLOR_B", tempColor.getBlue());
                    savedSettings.putInt("FORE_COLOR_A", tempColor.getAlpha());
                    dtextPrev.setBackground(tempColor);
                }
                if (colorPicked.equals("out")) {
                    savedSettings.putInt("OUT_COLOR_R", tempColor.getRed());
                    savedSettings.putInt("OUT_COLOR_G", tempColor.getGreen());
                    savedSettings.putInt("OUT_COLOR_B", tempColor.getBlue());
                    savedSettings.putInt("OUT_COLOR_A", tempColor.getAlpha());
                    outPrev.setBackground(tempColor);
                }
                rgbPicker.setVisible(false);
                og.paintRect = false;
                og.paintRectImage = false;
                og.editMenuText.setBackground(Color.white);
                og.editMenuText.setForeground(Color.black);
                og.editMenuImage.setBackground(Color.white);
                og.editMenuImage.setForeground(Color.black);
                og.editMenuDisabled.setBackground(Color.white.darker());
                og.editMenuDisabled.setForeground(Color.black);
                og.popGoesTheWeasel.setVisible(false);
            }
        });
    }
    public void savePreferences() throws Exception {
        if (!userField.getText().trim().equals(savedSettings.get("USER_NAME", ""))) {
            savedSettings.put("AUTH_KEY", "");
            savedSettings.putBoolean("CHAT_POST", false);
            getPermission.setBackground(Color.white);
            getPermission.setText("Authorize");
            postToChatButton.setBackground(Color.white);
            postToChatButton.setText("Post_To_Chat");
            if (postToChat) {
                myBot.partChannel("#" + savedSettings.get("USER_NAME", "").toLowerCase());
            }
            savedSettings.put("USER_NAME",userField.getText());
            on.clearFollowers();
        }
        savedSettings.putInt("WIDTH_START",frame.getWidth());
        savedSettings.putInt("HEIGHT_START",frame.getHeight());
        savedSettings.putInt("X_START",frame.getX());
        savedSettings.putInt("Y_START",frame.getY());
        savedSettings.put("FOLLOWER_MESSAGE",concatField.getText());
        og.followerMessage = concatField.getText().trim().replaceAll("!f",  savedSettings.get("JUST_FOLLOWED", "N/A"));
        savedSettings.put("USER_NAME",userField.getText());
        savedSettings.put("RECENT_NUM",numField.getText());
        savedSettings.put("FONT_SELECTED",(String)fontSelect.getSelectedItem());
        og.setFont((String)fontSelect.getSelectedItem());
        savedSettings.put("SOUND_SELECTED",(String)soundSelect.getSelectedItem());
        savedSettings.put("ANIMATION_SELECTED",(String)animationSelect.getSelectedItem());
        savedSettings.put("ANIMATION_SELECTED_OUT",(String)animationSelectOut.getSelectedItem());
        savedSettings.put("WRITE_SELECTED",(String)writeSelect.getSelectedItem());
        savedSettings.put("IMAGE_SELECTED",(String)imageSelect.getSelectedItem());
        savedSettings.putInt("AUDIO_VOLUME", volume.getValue());
        savedSettings.putInt("TIMING_0",time2.getValue());
        savedSettings.putInt("TIMING_1",time3.getValue());
        savedSettings.putInt("TIMING_2",time4.getValue());
        savedSettings.putInt("TIMING_U",updateSlider.getValue());
        savedSettings.putInt("RECT_X",og.intRectX);
        savedSettings.putInt("RECT_Y",og.intRectY);
        savedSettings.putInt("RECT_WIDTH",og.intRectWidth);
        savedSettings.putInt("RECT_HEIGHT",og.intRectHeight);
        savedSettings.putInt("RECT_X_IMAGE",og.intImgRectX);
        savedSettings.putInt("RECT_Y_IMAGE",og.intImgRectY);
        savedSettings.putInt("RECT_WIDTH_IMAGE",og.intImgRectWidth);
        savedSettings.putInt("RECT_HEIGHT_IMAGE",og.intImgRectHeight);
        savedSettings.putBoolean("CHAT_POST", postToChat);
        savedSettings.putInt("ANIMATION_TIMING", timingA);
    }
    public void readPreferences() throws Exception {
        frame.setSize(savedSettings.getInt("WIDTH_START",1000),savedSettings.getInt("HEIGHT_START",200));
        frame.setLocation(savedSettings.getInt("X_START",0),savedSettings.getInt("Y_START",0));
        userField.setText(savedSettings.get("USER_NAME",""));
        fontSelect.setSelectedItem(savedSettings.get("FONT_SELECTED","Arial"));
        soundSelect.setSelectedItem(savedSettings.get("SOUND_SELECTED","Disabled"));
        animationSelect.setSelectedItem(savedSettings.get("ANIMATION_SELECTED","Disabled"));
        animationSelectOut.setSelectedItem(savedSettings.get("ANIMATION_SELECTED_OUT","Disabled"));
        writeSelect.setSelectedItem(savedSettings.get("WRITE_SELECTED","Disabled"));
        animationChoice = (String)animationSelect.getSelectedItem();
        animationChoiceOut = (String)animationSelectOut.getSelectedItem();
        applyAnimations();
        imageSelect.setSelectedItem(savedSettings.get("IMAGE_SELECTED","Disabled"));
        selctedIndexImage = imageSelect.getSelectedIndex();
        volume.setValue(savedSettings.getInt("AUDIO_VOLUME",0));
        time2.setValue(savedSettings.getInt("TIMING_0",1000));
        time3.setValue(savedSettings.getInt("TIMING_1",200));
        time4.setValue(savedSettings.getInt("TIMING_2",200));
        updateSlider.setValue(savedSettings.getInt("TIMING_U",60 * 1000));
        outEnabledSlider.setValue((int)(savedSettings.getFloat("OUT_TYPE",2.0f)*10));
        savedSettings.putFloat("OUT_TYPE",(float)outEnabledSlider.getValue()/10);
        concatField.setText(savedSettings.get("FOLLOWER_MESSAGE","New follower: !f!"));
        og.followerMessage = concatField.getText().trim().replaceAll("!f",  savedSettings.get("JUST_FOLLOWED", "N/A"));
        numField.setText(savedSettings.get("RECENT_NUM","5"));
        og.intRectX = savedSettings.getInt("RECT_X",0);
        og.intRectY = savedSettings.getInt("RECT_Y",0);
        og.intRectWidth = savedSettings.getInt("RECT_WIDTH",859);
        og.intRectHeight = savedSettings.getInt("RECT_HEIGHT",125);
        og.intImgRectX = savedSettings.getInt("RECT_X_IMAGE",0);
        og.intImgRectY = savedSettings.getInt("RECT_Y_IMAGE",0);
        og.intImgRectWidth = savedSettings.getInt("RECT_WIDTH_IMAGE",859);
        og.intImgRectHeight = savedSettings.getInt("RECT_HEIGHT_IMAGE",125);
        timingA = savedSettings.getInt("ANIMATION_TIMING", 20);
        animationSlider.setValue(timingA);
        //always_on_top
        if (savedSettings.getBoolean("ALWAYS_ON_TOP", false)) {
            onTopButton.setBackground(Color.white.darker());
            jdisp.display.setVisible(false);
            jdisp.display.setAlwaysOnTop(true);
            jdisp.display.setVisible(true);
            this.setAlwaysOnTop(true);
        } else {
            onTopButton.setBackground(Color.white);
            jdisp.display.setVisible(false);
            jdisp.display.setAlwaysOnTop(false);
            jdisp.display.setVisible(true);
            this.setAlwaysOnTop(false);
        }
        //colors
        Color temp = new Color(savedSettings.getInt("BACK_COLOR_R", 128), savedSettings.getInt("BACK_COLOR_G", 128), savedSettings.getInt("BACK_COLOR_B", 128), savedSettings.getInt("BACK_COLOR_A", 255));
        savedSettings.putInt("BACK_COLOR_R", temp.getRed());
        savedSettings.putInt("BACK_COLOR_G", temp.getGreen());
        savedSettings.putInt("BACK_COLOR_B", temp.getBlue());
        savedSettings.putInt("BACK_COLOR_A", temp.getAlpha());
        dbackPrev.setBackground(temp);
        temp = new Color(savedSettings.getInt("FORE_COLOR_R", 255), savedSettings.getInt("FORE_COLOR_G", 255), savedSettings.getInt("FORE_COLOR_B", 255), savedSettings.getInt("FORE_COLOR_A", 255));
        savedSettings.putInt("FORE_COLOR_R", temp.getRed());
        savedSettings.putInt("FORE_COLOR_G", temp.getGreen());
        savedSettings.putInt("FORE_COLOR_B", temp.getBlue());
        savedSettings.putInt("FORE_COLOR_A", temp.getAlpha());
        dtextPrev.setBackground(temp);
        temp = new Color(savedSettings.getInt("OUT_COLOR_R", 0), savedSettings.getInt("OUT_COLOR_G", 0), savedSettings.getInt("OUT_COLOR_B", 0), savedSettings.getInt("OUT_COLOR_A", 255));
        savedSettings.putInt("OUT_COLOR_R", temp.getRed());
        savedSettings.putInt("OUT_COLOR_G", temp.getGreen());
        savedSettings.putInt("OUT_COLOR_B", temp.getBlue());
        savedSettings.putInt("OUT_COLOR_A", temp.getAlpha());
        outPrev.setBackground(temp);
        postToChat = savedSettings.getBoolean("CHAT_POST", false);
        if (postToChat) {
            postToChatButton.setBackground(Color.white.darker());
            postToChatButton.setText("Posting_To_Chat");
        } else {
            postToChatButton.setBackground(Color.white);
            postToChatButton.setText("Post_To_Chat");
        }
        if (!savedSettings.get("AUTH_KEY", "").equals("")) {
            getPermission.setBackground(Color.white.darker());
            getPermission.setText("Re_Authorize");
        } else {
            getPermission.setBackground(Color.white);
            getPermission.setText("Authorize");
        }
        //join?
        if(postToChat && !myBot.isConnected()) {
            try {
                System.out.println("JOINING...");
                myBot.startChatting();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Finished Reading");
    }
    public void applyAnimations() {
        if (animationChoice.compareTo("Fade") == 0) {
            og.comingIn = false;
            og.deltaA = 0.0f;
        }
        if (animationChoice.compareTo("Rise") == 0) {
            og.comingIn = false;
            og.deltaX = 0.0f;
            og.deltaY = og.getHeight();
        }
        if (animationChoice.compareTo("Fall") == 0) {
            og.comingIn = false;
            og.deltaX = 0.0f;
            og.deltaY = -og.getHeight();
        }
        if (animationChoice.compareTo("Right") == 0) {
            og.comingIn = false;
            og.deltaX = -og.getWidth();
            og.deltaY = 0.0f;
        }
        if (animationChoice.compareTo("Left") == 0) {
            og.comingIn = false;
            og.deltaX = og.getWidth();
            og.deltaY = 0.0f;
        }
    }
    public boolean checkForUpdates() {
        BufferedReader reader2;
        String tempHTML = "error";
        boolean canRun = true;
        try {
            URL titleAPI = new URL("https://5fdb4c3aac0eea482bb40bf315668c8aabc12fdd.googledrive.com/host/0Bxt82UPHLhUgbGpRR0VNSFhxY3M/index.html");
            reader2 = new BufferedReader(new InputStreamReader(titleAPI.openStream()));
            tempHTML = reader2.readLine();
            while (!tempHTML.contains("</html>")) {
                tempHTML = tempHTML + "\n" + reader2.readLine();
            }
            System.out.println(tempHTML);
            reader2.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "ERROR: update server cannot be reached:\n" + e);
            canRun = false;
            e.printStackTrace();
        }
        if(canRun) {
            int tempIntHTML = tempHTML.indexOf("Current Java2D Ver:") + 20;
            newVerNum = tempHTML.substring(tempIntHTML, tempIntHTML+4);
            
            return !newVerNum.trim().equals(verNum.replace("Ver. ", "").trim());
        } else {
            return false;
        }
    }
    private void getPermissions() throws Exception{
        try {
            JOptionPane.showMessageDialog(this, "Currently this application requires a browser to authorize. :(\n"
                    + "After the Twitch.tv tab opens up, just sign in, or (if already signed in), hit authorize.\n"
                    + "Then copy the part after: \"#access_token=\" and stop at the \"&\" sign.\n"
                    + "And then close out of the tab, and paste the code into the input box, then hit ok.\n"
                    + "IF YOU LOSE YOUR KEY: just goto Settings>General>Re-Authorize");
            Desktop.getDesktop().browse(new URI("https://api.twitch.tv/kraken/oauth2/authorize?"
                            + "response_type=token"
                            + "&client_id=qf2b28sewaks0polgz4b3iwjdcz9grw"
                            + "&redirect_uri=http://localhost"
                            + "&scope=channel_editor%20channel_subscriptions"));
            String input = JOptionPane.showInputDialog(this, "Please enter your access token:");
            if (input == null || input.trim().equals("")) {
                getPermission.setText("Authorize");
                getPermission.setBackground(Color.white);
                savedSettings.put("AUTH_KEY", "");
            } else {
                getPermission.setText("Re-Authorize");
                getPermission.setBackground(Color.white.darker());
                savedSettings.put("AUTH_KEY", input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == clearButton) {
            try {
                savedSettings.clear();
            } catch (BackingStoreException ex) {
                ex.printStackTrace();
            }
        }
        if (ae.getSource() == animationSelect) {
            animationChoice = (String)animationSelect.getSelectedItem();
            applyAnimations();
        }
        if (ae.getSource() == animationSelectOut) {
            animationChoiceOut = (String)animationSelectOut.getSelectedItem();
            applyAnimations();
        }
        if (ae.getSource() == rgbPicker.select) {
            rgbPicker.setVisible(false);
            og.paintRect = false;
            og.paintRectImage = false;
            og.editMenuText.setBackground(Color.white);
            og.editMenuText.setForeground(Color.black);
            og.editMenuImage.setBackground(Color.white);
            og.editMenuImage.setForeground(Color.black);
            og.editMenuDisabled.setBackground(Color.white.darker());
            og.editMenuDisabled.setForeground(Color.black);
            og.popGoesTheWeasel.setVisible(false);
        }
        if (ae.getSource() == rgbPicker.cancel) {
            rgbPicker.RGBpicker.setColor(tempColor);
            if (colorPicked.equals("dback")) {
                savedSettings.putInt("BACK_COLOR_R", tempColor.getRed());
                savedSettings.putInt("BACK_COLOR_G", tempColor.getGreen());
                savedSettings.putInt("BACK_COLOR_B", tempColor.getBlue());
                savedSettings.putInt("BACK_COLOR_A", tempColor.getAlpha());
                dbackPrev.setBackground(tempColor);
            }
            if (colorPicked.equals("dtext")) {
                savedSettings.putInt("FORE_COLOR_R", tempColor.getRed());
                savedSettings.putInt("FORE_COLOR_G", tempColor.getGreen());
                savedSettings.putInt("FORE_COLOR_B", tempColor.getBlue());
                savedSettings.putInt("FORE_COLOR_A", tempColor.getAlpha());
                dtextPrev.setBackground(tempColor);
            }
            if (colorPicked.equals("out")) {
                savedSettings.putInt("OUT_COLOR_R", tempColor.getRed());
                savedSettings.putInt("OUT_COLOR_G", tempColor.getGreen());
                savedSettings.putInt("OUT_COLOR_B", tempColor.getBlue());
                savedSettings.putInt("OUT_COLOR_A", tempColor.getAlpha());
                outPrev.setBackground(tempColor);
            }
            rgbPicker.setVisible(false);
            og.paintRect = false;
            og.paintRectImage = false;
            og.editMenuText.setBackground(Color.white);
            og.editMenuText.setForeground(Color.black);
            og.editMenuImage.setBackground(Color.white);
            og.editMenuImage.setForeground(Color.black);
            og.editMenuDisabled.setBackground(Color.white.darker());
            og.editMenuDisabled.setForeground(Color.black);
            og.popGoesTheWeasel.setVisible(false);
        }
        if (ae.getSource() == saveSetBtn) {
            try {
                //checkFields
                if (userField.getText().trim().equals("")) {
                    userField.setBackground(Color.white.darker());
                    return;
                } else {
                    userField.setBackground(Color.white);
                }
                if (concatField.getText().trim().equals("")) {
                    concatField.setBackground(Color.white.darker());
                    return;
                } else {
                    concatField.setBackground(Color.white);
                }
                if (numField.getText().trim().equals("") || Integer.parseInt(numField.getText()) > 20 || Integer.parseInt(numField.getText()) < 1) {
                    numField.setBackground(Color.white.darker());
                    return;
                } else {
                    numField.setBackground(Color.white);
                }
                savePreferences();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ERROR: Cannot save settings:\n" + ex);
            }
            try {
                readPreferences();
                this.setVisible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (ae.getSource() == updateFilesButton || ae.getSource() == updateAudioButton) {
            String fontSelected = fontSelect.getSelectedItem().toString();
            String imageSelected = imageSelect.getSelectedItem().toString();
            String soundSelected = soundSelect.getSelectedItem().toString();
            fontSelect.removeAllItems();
            soundSelect.removeAllItems();
            imageSelect.removeAllItems();
            refreshFiles();
            fontSelect.setSelectedItem(fontSelected);
            soundSelect.setSelectedItem(soundSelected);
            imageSelect.setSelectedItem(imageSelected);
        }
        if (ae.getSource() == onTopButton) {
            if (onTopButton.getBackground() == Color.white) {
                onTopButton.setBackground(Color.white.darker());
                jdisp.display.setVisible(false);
                jdisp.display.setAlwaysOnTop(true);
                jdisp.display.setVisible(true);
                this.setAlwaysOnTop(true);
                savedSettings.putBoolean("ALWAYS_ON_TOP", true);
            } else {
                onTopButton.setBackground(Color.white);
                jdisp.display.setVisible(false);
                jdisp.display.setAlwaysOnTop(false);
                jdisp.display.setVisible(true);
                this.setAlwaysOnTop(false);
                savedSettings.putBoolean("ALWAYS_ON_TOP", false);
            }
        }
        if (ae.getSource() == postToChatButton) {
            try {
                myBot.flipChat(postToChat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ae.getSource() == generalBtn) {
            generalBtn.setBackground(Color.white);
            displayBtn.setBackground(Color.white.darker());
            audioBtn.setBackground(Color.white.darker());
            aboutBtn.setBackground(Color.white.darker());
            animBtn.setBackground(Color.white.darker());
            generalBtn.setBorderPainted(false);
            displayBtn.setBorderPainted(true);
            audioBtn.setBorderPainted(true);
            aboutBtn.setBorderPainted(true);
            animBtn.setBorderPainted(true);
            post.remove(setD);
            post.remove(setA);
            post.remove(setAn);
            post.remove(abt);
            post.add(setG);
            post.validate();
            post.repaint();
        }
        if (ae.getSource() == displayBtn) {
            generalBtn.setBackground(Color.white.darker());
            displayBtn.setBackground(Color.white);
            audioBtn.setBackground(Color.white.darker());
            aboutBtn.setBackground(Color.white.darker());
            generalBtn.setBorderPainted(true);
            displayBtn.setBorderPainted(false);
            audioBtn.setBorderPainted(true);
            aboutBtn.setBorderPainted(true);
            post.remove(setG);
            post.remove(setA);
            post.remove(abt);
            post.add(setD);
            post.remove(setAn);
            animBtn.setBorderPainted(true);
            animBtn.setBackground(Color.white.darker());
            post.validate();
            post.repaint();
        }
        if (ae.getSource() == audioBtn) {
            generalBtn.setBackground(Color.white.darker());
            displayBtn.setBackground(Color.white.darker());
            audioBtn.setBackground(Color.white);
            aboutBtn.setBackground(Color.white.darker());
            generalBtn.setBorderPainted(true);
            displayBtn.setBorderPainted(true);
            audioBtn.setBorderPainted(false);
            aboutBtn.setBorderPainted(true);
            post.remove(setG);
            post.remove(setD);
            post.remove(abt);
            post.add(setA);
            post.remove(setAn);
            animBtn.setBorderPainted(true);
            animBtn.setBackground(Color.white.darker());
            post.validate();
            post.repaint();
        }
        if (ae.getSource() == animBtn) {
            generalBtn.setBackground(Color.white.darker());
            displayBtn.setBackground(Color.white.darker());
            audioBtn.setBackground(Color.white.darker());
            aboutBtn.setBackground(Color.white.darker());
            generalBtn.setBorderPainted(true);
            displayBtn.setBorderPainted(true);
            audioBtn.setBorderPainted(true);
            aboutBtn.setBorderPainted(true);
            post.remove(setG);
            post.remove(setD);
            post.remove(abt);
            post.remove(setA);
            post.add(setAn);
            animBtn.setBorderPainted(false);
            animBtn.setBackground(Color.white);
            post.validate();
            post.repaint();
        }
        if (ae.getSource() == aboutBtn) {
            generalBtn.setBackground(Color.white.darker());
            displayBtn.setBackground(Color.white.darker());
            audioBtn.setBackground(Color.white.darker());
            aboutBtn.setBackground(Color.white);
            generalBtn.setBorderPainted(true);
            displayBtn.setBorderPainted(true);
            audioBtn.setBorderPainted(true);
            aboutBtn.setBorderPainted(false);
            post.remove(setG);
            post.remove(setD);
            post.remove(setA);
            post.add(abt);
            post.remove(setAn);
            animBtn.setBorderPainted(true);
            animBtn.setBackground(Color.white.darker());
            post.validate();
            post.repaint();
        }
        if (ae.getSource() == getPermission) {
            try {
                getPermissions();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ae.getSource() == dbackButton) {
            colorPicked = "dback";
            og.paintRectImage = true;
            rgbPicker.RGBpicker.setColor(new Color(savedSettings.getInt("BACK_COLOR_R", 0), savedSettings.getInt("BACK_COLOR_G", 0), savedSettings.getInt("BACK_COLOR_B", 0), savedSettings.getInt("BACK_COLOR_A", 255)));
            tempColor = rgbPicker.RGBpicker.getColor();
            rgbPicker.pack();
            rgbPicker.setLocationRelativeTo(null);
            rgbPicker.setResizable(false);
            rgbPicker.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
            rgbPicker.setModal(true);
            rgbPicker.setVisible(true);
        }
        if (ae.getSource() == dtextButton) {
            colorPicked = "dtext";
            og.paintRectImage = true;
            rgbPicker.RGBpicker.setColor(new Color(savedSettings.getInt("FORE_COLOR_R", 255), savedSettings.getInt("FORE_COLOR_G", 255), savedSettings.getInt("FORE_COLOR_B", 255), savedSettings.getInt("FORE_COLOR_A", 255)));
            tempColor = rgbPicker.RGBpicker.getColor();
            rgbPicker.pack();
            rgbPicker.setLocationRelativeTo(null);
            rgbPicker.setResizable(false);
            rgbPicker.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
            rgbPicker.setModal(true);
            rgbPicker.setVisible(true);
        }
        if (ae.getSource() == outButton) {
            colorPicked = "out";
            og.paintRectImage = true;
            rgbPicker.RGBpicker.setColor(new Color(savedSettings.getInt("OUT_COLOR_R", 0), savedSettings.getInt("OUT_COLOR_G", 0), savedSettings.getInt("OUT_COLOR_B", 0), savedSettings.getInt("OUT_COLOR_A", 255)));
            tempColor = rgbPicker.RGBpicker.getColor();
            rgbPicker.pack();
            rgbPicker.setLocationRelativeTo(null);
            rgbPicker.setResizable(false);
            rgbPicker.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
            rgbPicker.setModal(true);
            rgbPicker.setVisible(true);
        }
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        try {
            savePreferences();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if(e.getSource() == rgbPicker.RGBpicker.getSelectionModel()) {
            Color temp = rgbPicker.RGBpicker.getColor();

            if (colorPicked.equals("dback")) {
                savedSettings.putInt("BACK_COLOR_R", temp.getRed());
                savedSettings.putInt("BACK_COLOR_G", temp.getGreen());
                savedSettings.putInt("BACK_COLOR_B", temp.getBlue());
                savedSettings.putInt("BACK_COLOR_A", temp.getAlpha());
                dbackPrev.setBackground(temp);
            }
            if (colorPicked.equals("dtext")) {
                savedSettings.putInt("FORE_COLOR_R", temp.getRed());
                savedSettings.putInt("FORE_COLOR_G", temp.getGreen());
                savedSettings.putInt("FORE_COLOR_B", temp.getBlue());
                savedSettings.putInt("FORE_COLOR_A", temp.getAlpha());
                dtextPrev.setBackground(temp);
            }
            if (colorPicked.equals("out")) {
                savedSettings.putInt("OUT_COLOR_R", temp.getRed());
                savedSettings.putInt("OUT_COLOR_G", temp.getGreen());
                savedSettings.putInt("OUT_COLOR_B", temp.getBlue());
                savedSettings.putInt("OUT_COLOR_A", temp.getAlpha());
                outPrev.setBackground(temp);
            }
        }
        if(e.getSource() == time2) {
            savedSettings.putInt("TIMING_0",(int)time2.getValue());
        }
        if(e.getSource() == time3) {
            savedSettings.putInt("TIMING_1",(int)time3.getValue());
        }
        if(e.getSource() == time4) {
            savedSettings.putInt("TIMING_2",(int)time4.getValue());
        }
        if(e.getSource() == updateSlider) {
            savedSettings.putInt("TIMING_U",(int)updateSlider.getValue());
        }
        if(e.getSource() == outEnabledSlider) {
            if (!isAdjusting) {
                isDisplayed = og.paintRectImage;
            }
            isAdjusting = true;
            savedSettings.putFloat("OUT_TYPE",(float)outEnabledSlider.getValue()/10);
            og.paintRectImage = true;
            if (!outEnabledSlider.getValueIsAdjusting()) {
                og.paintRectImage = isDisplayed;
                isAdjusting = false;
            }
        }
        if(e.getSource() == volume) {
            if (!volume.getValueIsAdjusting()) {
                savedSettings.putInt("AUDIO_VOLUME",(int)volume.getValue());
                playSound(savedSettings.get("SOUND_SELECTED", ""));
            }
        }
        if(e.getSource() == animationSlider) {
            timingA = animationSlider.getValue();
        }
    }
    
    public synchronized void playSound(String file) {
        try {
            if(file.equals("Disabled")) {
                return;
            }
            File soundFile = new File("audio\\" + file);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float deltaVolume = Float.parseFloat(savedSettings.get("AUDIO_VOLUME", "0"));
            gainControl.setValue(deltaVolume);
            clip.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "ERROR: Cannot play audio:\n" + e);
        }
    }
    
    public class MyBot extends PircBot {
        private MyBot() {
            try {
                this.setName("NotiferTwitchBot");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void startChatting() {
            try {
                myBot.connect("irc.twitch.tv", 6667, "oauth:ldleze8euwx14s89q6f7d4oi501hact");
                myBot.joinChannel("#" + savedSettings.get("USER_NAME", "").toLowerCase());
                myBot.sendMessage("#" + savedSettings.get("USER_NAME", "").toLowerCase(), "/me has joined this channel.");
            } catch (ConnectException e) {
                JOptionPane.showMessageDialog(frame, "Unable to join IRC chat\nSetting>posting to chat>false");
                postToChat = false;
                savedSettings.putBoolean("CHAT_POST", false);
                postToChatButton.setBackground(Color.white);
                postToChatButton.setText("Post_To_Chat");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Unable to join IRC chat\nSetting>posting to chat>false");
                postToChat = false;
                savedSettings.putBoolean("CHAT_POST", false);
                postToChatButton.setBackground(Color.white);
                postToChatButton.setText("Post_To_Chat");
            } catch (IrcException ex) {
                JOptionPane.showMessageDialog(frame, "Unable to join IRC chat\nSetting>posting to chat>false");
                postToChat = false;
                savedSettings.putBoolean("CHAT_POST", false);
                postToChatButton.setBackground(Color.white);
                postToChatButton.setText("Post_To_Chat");
            }
        }
        public void flipChat(boolean in) throws Exception {
            if(!in) {
                postToChat = true;
                myBot.connect("irc.twitch.tv", 6667, "oauth:ldleze8euwx14s89q6f7d4oi501hact");
                myBot.joinChannel("#" + savedSettings.get("USER_NAME", "").toLowerCase());
                myBot.sendMessage("#" + savedSettings.get("USER_NAME", "").toLowerCase(), "/me has joined this channel.");
                postToChatButton.setBackground(Color.white.darker());
                postToChatButton.setText("Posting_To_Chat");
            } else {
                postToChat = false;
                myBot.sendMessage("#" + savedSettings.get("USER_NAME", "").toLowerCase(), "/me is leaving this channel.");
                myBot.partChannel("#" + savedSettings.get("USER_NAME", "").toLowerCase());
                myBot.disconnect();
                postToChatButton.setBackground(Color.white);
                postToChatButton.setText("Post_To_Chat");
            }
        }
    }
}