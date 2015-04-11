package javanotifier;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class JavaNotifier implements ComponentListener, ActionListener, MouseListener {
    
    public JFrame frame = new JFrame();
    private JPanel topP = new JPanel();
    private JPanel titP = new JPanel();
    private JPanel centerP = new JPanel();
    private JPanel titleP = new JPanel();
    private JPanel sideP = new JPanel();
    
    private JPanel dispSize = new JPanel();
    
    private JLabel titleLabel = new JLabel("Title: ");
    private JLabel gameLabel = new JLabel("Game:  ");
    private JTextField titleField = new JTextField();
    private JTextField gameField = new JTextField();
    private JTextArea outArea = new JTextArea();
    
    private JButton prevButton = new JButton("Preview");
    private JButton updateButton = new JButton("Set T/G");
    private JButton settingsButton = new JButton("Settings");
    
    public String[][] databaseFollowers;
    private Long totalFollowers;
    private String databaseTotal, totalViewers;
    private int jsonArraySize;
    public boolean animating = false;
    private boolean starting = true, startedUp = false;
    
    Preferences savedSettings = Preferences.userNodeForPackage(JavaNotifier.class);

    private static Settings s;
    private static RGBPicker rgbPicker;
    private JavaRender og;
    private JDisplayFrame jDraw;
    private LoadFiles l;
    private Splash startup;
   
    public static void main(String[] args) {
        JavaNotifier on = new JavaNotifier();
        //define
        s = new Settings(on);
        rgbPicker = new RGBPicker();
        on.start();
        on.JavaNotifier();
    }
    
    private void start() {
        //don't allow user to change things before it loads
        prevButton.setEnabled(false);
        settingsButton.setEnabled(false);
        //splashScreen
        startup = new Splash(s);
        //defaultOffForAuth
        updateButton.setEnabled(false);
        titleField.setEnabled(false);
        gameField.setEnabled(false);
        startup.updateSplash("Java2D...");
        
        try {
            l = new LoadFiles(frame, s, savedSettings, og, rgbPicker);
            og = new JavaRender();
            jDraw = new JDisplayFrame(frame, og);
            frame.setSize(1128, 230);
            s.construct(frame, l, savedSettings, og, rgbPicker, jDraw);
            og.JavaRender(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startup.updateSplash("Updating...");
        //update
        if (s.checkForUpdates()) {
            if(JOptionPane.showConfirmDialog(frame, "Update available!\nUpdate to newest version now?")==0) {
                try {
                    Desktop.getDesktop().browse(new URI("https://docs.google.com/uc?authuser=0&id=0Bxt82UPHLhUgeC1QYm91dGdUWXM&export=download"));
                    og.setVisible(false);
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void JavaNotifier() {
        startup.updateSplash("GUI...");

        try {
            frame.setIconImage(ImageIO.read(new FileInputStream("icon.png")));
        } catch(Exception e) {
            System.out.println("failed to set icon image.");
        }
        
        frame.setLayout(new BorderLayout());
        frame.add(topP, BorderLayout.NORTH);
            topP.setLayout(new BorderLayout());
            topP.add(titP, BorderLayout.CENTER);
                titP.setLayout(new BorderLayout());
                titP.add(titleP, BorderLayout.WEST);
                    titleP.setLayout(new GridLayout(2,1,0,0));
                    titleP.add(titleLabel);
                    titleP.add(gameLabel);
                titP.add(centerP, BorderLayout.CENTER);
                    centerP.setLayout(new GridLayout(2,1,0,0));
                    centerP.add(titleField);
                    titleField.setText("Fetching...");
                    centerP.add(gameField);
                    gameField.setText("Fetching...");
                titP.add(updateButton, BorderLayout.EAST);
            topP.add(prevButton, BorderLayout.EAST);
                prevButton.setPreferredSize(new Dimension(128, 30));
        frame.add(sideP, BorderLayout.EAST);
            sideP.setLayout(new BorderLayout());
            sideP.add(outArea, BorderLayout.CENTER);
                outArea.setEditable(false);
                outArea.setPreferredSize(new Dimension(128,frame.getHeight()));
                outArea.setText("Twitch info:\nFetching...\n\nThanks for using\n my software!");
            sideP.add(settingsButton, BorderLayout.NORTH);
                settingsButton.setPreferredSize(new Dimension(128, 30));
        frame.add(dispSize, BorderLayout.CENTER);
        
        titleField.addMouseListener(this);
        gameField.addMouseListener(this);
        settingsButton.addActionListener(this);
        updateButton.addActionListener(this);
        prevButton.addActionListener(this);
        
        colorMe(topP);
        colorMe(titP);
        colorMe(centerP);
        colorMe(titleP);
        colorMe(sideP);
        
        databaseTotal = "0";
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    s.savePreferences();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
                System.out.println("saving");
                try {
                    s.savePreferences();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                s.myBot.sendMessage("#" + savedSettings.get("USER_NAME", ""), "/me is leaving this channel.");
                s.myBot.partChannel("#" + savedSettings.get("USER_NAME", "").toLowerCase());
                s.myBot.disconnect();
                System.out.println("shutting down");
                System.exit(0);
            }
        });
        
        frame.setMinimumSize(new Dimension(300, 100));
        frame.setVisible(true);
        frame.setTitle("Java2D Notifier | " + s.verNum);
        startup.setVisible(false);
        frame.addComponentListener(this);
        
        clearFollowers();
        timer();
        try {
            s.savePreferences();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        prevButton.setEnabled(true);
        settingsButton.setEnabled(true);
    }
    
    private void timer() {      
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                grabStats();
                while (true) {
                    try {
                        //applyAuthAccess
                        if (savedSettings.get("AUTH_KEY", "").equals("")) {
                            updateButton.setEnabled(false);
                            titleField.setEnabled(false);
                            gameField.setEnabled(false);
                        } else {
                            updateButton.setEnabled(true);
                            titleField.setEnabled(true);
                            gameField.setEnabled(true);
                        }
                        grabStats();
                        Thread.sleep(savedSettings.getInt("TIMING_U", 60 * 1000));
                    } catch (Exception e) {
                        outArea.setText("Error");
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
        
        Thread r = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (animating) {
                        prevButton.setEnabled(false);
                    } else {
                        prevButton.setEnabled(true);
                    }
                    try {
                        //setup
                        String animationChoice = s.animationChoice;
                        String animationChoiceOut = s.animationChoiceOut;
                        int[] timing = {savedSettings.getInt("TIMING_0", 600 * 1000), savedSettings.getInt("TIMING_1", 20), savedSettings.getInt("TIMING_2", 20)};
                        //in
                        if (og.comingIn) {
                            animating = true;
                            switch (animationChoice) {
                                case "Fade":
                                    og.deltaY = 0.0f;
                                    og.deltaX = 0.0f;
                                    og.deltaA += 0.01;
                                    if (og.deltaA >= 1.0f) {
                                        og.comingIn = false;
                                        og.deltaA = 1.0f;
                                        Thread.sleep(timing[0]);
                                    }
                                    Thread.sleep(timing[1]);
                                    break;
                                case "Rise":
                                    og.deltaX = 0.0f;
                                    og.deltaA = 1.0f;
                                    og.deltaY -= 1;
                                    if (og.deltaY <= 0.0f) {
                                        og.comingIn = false;
                                        og.deltaY = 0.0f;
                                        Thread.sleep(timing[0]);
                                    }
                                    Thread.sleep((int)((float)timing[1]/((float)og.getHeight()/(float)100)));
                                    break;
                                case "Fall":
                                    og.deltaX = 0.0f;
                                    og.deltaA = 1.0f;
                                    og.deltaY += 1;
                                    if (og.deltaY >= 0.0f) {
                                        og.comingIn = false;
                                        og.deltaY = 0.0f;
                                        Thread.sleep(timing[0]);
                                    }
                                    Thread.sleep((int)((float)timing[1]/((float)og.getHeight()/(float)100)));
                                    break;
                                case "Right":
                                    og.deltaY = 0.0f;
                                    og.deltaA = 1.0f;
                                    og.deltaX += 1;
                                    if (og.deltaX >= 0.0f) {
                                        og.comingIn = false;
                                        og.deltaX = 0.0f;
                                        Thread.sleep(timing[0]);
                                    }
                                    Thread.sleep((int)((float)timing[1]/((float)og.getWidth()/(float)200)));
                                    break;
                                case "Left":
                                    og.deltaY = 0.0f;
                                    og.deltaA = 1.0f;
                                    og.deltaX -= 1;
                                    if (og.deltaX <= 0.0f) {
                                        og.comingIn = false;
                                        og.deltaX = 0.0f;
                                        Thread.sleep(timing[0]);
                                    }
                                    Thread.sleep((int)((float)timing[1]/((float)og.getWidth()/(float)200)));
                                    break;
                            }
                        }
                        //out
                        else {
                            switch (animationChoiceOut) {
                                case "Fade":
                                    og.deltaY = 0.0f;
                                    og.deltaX = 0.0f;
                                    og.deltaA -= 0.01;
                                    if (og.deltaA <= 0.0f) {
                                        og.deltaA = 0.0f;
                                        animating = false;
                                    }
                                    Thread.sleep(timing[2]);
                                    break;
                                case "Fall":
                                    og.deltaX = 0.0f;
                                    og.deltaA = 1.0f;
                                    og.deltaY += 1;
                                    if (og.deltaY >= og.getHeight()) {
                                        og.deltaY = og.getHeight();
                                        animating = false;
                                    }
                                    Thread.sleep((int)((float)timing[2]/((float)og.getHeight()/(float)100)));
                                    break;
                                case "Rise":
                                    og.deltaX = 0.0f;
                                    og.deltaA = 1.0f;
                                    og.deltaY -= 1;
                                    if (og.deltaY <= -og.getHeight()) {
                                        og.deltaY = -og.getHeight();
                                        animating = false;
                                    }
                                    Thread.sleep((int)((float)timing[2]/((float)og.getHeight()/(float)100)));
                                    break;
                                case "Left":
                                    og.deltaY = 0.0f;
                                    og.deltaA = 1.0f;
                                    og.deltaX -= 1;
                                    if (og.deltaX <= -og.getWidth()) {
                                        og.deltaX = -og.getWidth();
                                        animating = false;
                                    }
                                    Thread.sleep((int)((float)timing[2]/((float)og.getWidth()/(float)200)));
                                    break;
                                case "Right":
                                    og.deltaY = 0.0f;
                                    og.deltaA = 1.0f;
                                    og.deltaX += 1;
                                    if (og.deltaX >= og.getWidth()) {
                                        og.deltaX = og.getWidth();
                                        animating = false;
                                    }
                                    Thread.sleep((int)((float)timing[2]/((float)og.getWidth()/(float)200)));
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        outArea.setText("Error");
                        e.printStackTrace();
                    }
                }
            }
        });
        r.start();
    }
    
    private void colorMe(JComponent inComponent) {
        for (int i = 0; i < inComponent.getComponentCount(); i++) {
            inComponent.getComponent(i).setBackground(Color.white);
            inComponent.getComponent(i).setForeground(Color.black);
        }
    }
    
    private void setStats(String input) {
        try {
            String writeMe;
            String titleOut = titleField.getText().replace("%","%25")
                    .replace("&","%26")
                    .replace("^","%5E")
                    .replace("+","%2B")
                    .replace("#","%23")
                    .replace("@","%40")
                    .replace(" ","+")
                    .replace("{","%7B")
                    .replace("}","%7D")
                    .replace("[","%5B")
                    .replace("]","%5D")
                    .replace("|","%7C")
                    .replace("?","%3F")
                    .replace("\"","%22")
                    .replace("<","%3C")
                    .replace(">","%3E")
                    .replace(",","%2C")
                    .replace(";", "%3B");
            String gameOut = gameField.getText().replace("%","%25")
                    .replace("&","%26")
                    .replace("^","%5E")
                    .replace("+","%2B")
                    .replace("#","%23")
                    .replace("@","%40")
                    .replace(" ","+")
                    .replace("{","%7B")
                    .replace("}","%7D")
                    .replace("[","%5B")
                    .replace("]","%5D")
                    .replace("|","%7C")
                    .replace("?","%3F")
                    .replace("\"","%22")
                    .replace("<","%3C")
                    .replace(">","%3E")
                    .replace(",","%2C")
                    .replace(";", "%3B");
            if (!gameField.getText().equals("")) {
                writeMe = "channel[status]="
                    + titleOut
                    + "&channel[game]="
                    + gameOut;
                gameField.setBackground(Color.white);
            } else {
                writeMe = "channel[status]="
                    + titleOut
                    + "&channel[game]=";
                gameField.setBackground(Color.white.darker());
            }
            System.out.println("Writing : " + writeMe);
            URL url = new URL(input);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestProperty("Accept","application/vnd.twitchtv.v2+json");
            httpCon.setRequestProperty("Authorization","OAuth " + savedSettings.get("AUTH_KEY", ""));
            System.setProperty("http.agent", "");
            httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            httpCon.setRequestProperty("Content-Length", "" + writeMe.getBytes().length);
            httpCon.setRequestMethod("PUT");
            httpCon.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(writeMe);
            out.close();
            InputStreamReader in = new InputStreamReader(httpCon.getInputStream());
            System.out.println(in.read());
            in.close();
        } catch (Exception e) {
            if (savedSettings.get("AUTH_KEY", "").equals("")) {
                updateButton.setEnabled(false);
                titleField.setEnabled(false);
                gameField.setEnabled(false);
                JOptionPane.showMessageDialog(frame, "Update failed\nAuthorized : false");
            } else {
                updateButton.setEnabled(true);
                titleField.setEnabled(true);
                gameField.setEnabled(true);
                JOptionPane.showMessageDialog(frame, "Update failed\nPlease check internet connection.");
            }
        }
    }
    
    private void grabStats() {     
        JSONParser jimParser = new JSONParser();
        //urls
        try {
            URL twitchAPI = new URL("https://api.twitch.tv/kraken/channels/" + savedSettings.get("USER_NAME", "").toLowerCase() + "/follows?limit=20");
            URL twitchAPIstream = new URL("https://api.twitch.tv/kraken/streams/" + savedSettings.get("USER_NAME", "").toLowerCase());
            URL twitchAPIchannel = new URL("https://api.twitch.tv/kraken/channels/" + savedSettings.get("USER_NAME", "").toLowerCase());
            BufferedReader reader = new BufferedReader(new InputStreamReader(twitchAPI.openStream()));
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(twitchAPIstream.openStream()));
            BufferedReader reader3 = new BufferedReader(new InputStreamReader(twitchAPIchannel.openStream()));
            String follows = reader.readLine();
            String streams = reader2.readLine();
            String channel = reader3.readLine();
            reader.close();
            reader2.close();
            reader3.close();
            //parseing
            Object objFollows = jimParser.parse(follows);
            Object objStreams = jimParser.parse(streams);
            Object objChannel = jimParser.parse(channel);
            JSONObject jsonObjFollows = (JSONObject)objFollows;
            JSONObject jsonObjStreams = (JSONObject)objStreams;
            JSONObject jsonObjChannel = (JSONObject)objChannel;
            JSONArray jsonArrayFollows = (JSONArray)jsonObjFollows.get("follows");
            totalFollowers = (Long)jsonObjFollows.get("_total");
            JSONObject jsonObjStream = (JSONObject)jsonObjStreams.get("stream");
            String status = (String)jsonObjChannel.get("status");
            String game = (String)jsonObjChannel.get("game");
            jsonArraySize = jsonArrayFollows.size();
            String[][] tempFollowers = new String[jsonArraySize][2];
            //runOnce
            if (starting) {
                for(int i = 0; i < jsonArraySize; i++) {
                    JSONObject followerx = (JSONObject)jsonArrayFollows.get(i);
                    databaseFollowers[i][0] = (String)followerx.get("created_at");
                    JSONObject jsonfollowerxy = (JSONObject)followerx.get("user");
                    databaseFollowers[i][1] = (String)jsonfollowerxy.get("display_name");
                }
                starting = false;
            }
            for(int i = 0; i < jsonArraySize; i++) {
                JSONObject followerx = (JSONObject)jsonArrayFollows.get(i);
                tempFollowers[i][0] = (String)followerx.get("created_at");
                JSONObject jsonfollowerxy = (JSONObject)followerx.get("user");
                tempFollowers[i][1] = (String)jsonfollowerxy.get("display_name");
            }
            //isFollowing?
            boolean isFollowing;
            for(int i = 0; i < jsonArraySize; i++) {
                isFollowing = false;
                for(int y = 0; y < databaseFollowers.length; y++) {
                    if(tempFollowers[i][1].equals(databaseFollowers[y][1])) {
                        isFollowing = true;
                    }
                }
                if(!isFollowing) {
                    addFollowerToDatabase(tempFollowers[i][1]);
                    //playAnimation
                    og.followerMessage = savedSettings.get("FOLLOWER_MESSAGE", "N/A").replaceAll("!f", databaseFollowers[0][1]);
                    og.setFont(savedSettings.get("FONT_SELECTED", "Arial"));
                    s.applyAnimations();
                    og.comingIn = true;
                    s.myBot.sendMessage("#" + savedSettings.get("USER_NAME", "").toLowerCase(), og.followerMessage);
                    s.playSound(savedSettings.get("SOUND_SELECTED", "Disabled"));
                    while(animating) {
                        System.out.println("Waiting for turn");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

            //output
            //title
            if(!titleField.isFocusOwner() && !gameField.isFocusOwner()) {
                titleField.setText(status);
            }
            //game
            if(!gameField.isFocusOwner() && !titleField.isFocusOwner()) {
                if(game != null) {
                    gameField.setText(game);
                    gameField.setBackground(Color.white);
                } else {
                    gameField.setText("");
                    gameField.setBackground(Color.white.darker());
                }
            }
            //console
            String outText = savedSettings.get("USER_NAME", "") + "@twitch.tv\n";
            if(jsonObjStream == null) {
                outText = outText + "Status: offline\n\n\n";
                totalViewers = "offline";
            } else {
                Long jsonObjViewers = (Long)jsonObjStream.get("viewers");
                outText = outText + "Status: online\n";
                outText = outText + "Viewers: " + jsonObjViewers + "\n\n";
                totalViewers = "" + jsonObjViewers;
            }
            outText = outText + "Followers: " + totalFollowers;
            if (jsonArraySize < savedSettings.getInt("RECENT_NUM", 5)) {
                for (int x = 0; x < jsonArraySize; x++) {
                    outText = outText + "\n" + databaseFollowers[x][1];
                }
            } else {
                for (int x = 0; x < savedSettings.getInt("RECENT_NUM", 5); x++) {
                    outText = outText + "\n" + databaseFollowers[x][1];
                }
            }
            outArea.setText(outText);
            //outputFollowers
            if(savedSettings.getBoolean("PRINT_FOLLOWERS", true)) {
                writeFollowers();
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    
    private void addFollowerToDatabase(String input){
        System.out.println("adding : " + input + " to the database as a new follower");
        String[][] temp = databaseFollowers;
        databaseFollowers = new String[temp.length+1][2];
        for (int i = 0; i < temp.length; i++) {
            databaseFollowers[i+1][1] = temp[i][1];
        }
        databaseFollowers[0][1] = input;
    }
    
    private void writeFollowers() {
        PrintWriter writer = null;
        try {
            if (s.writeSelect.getSelectedItem().equals("Comma/Space")) {
                writer = new PrintWriter(s.decodedPath + "recent_followers.txt", "UTF-8");
                if (jsonArraySize < savedSettings.getInt("RECENT_NUM", 5)) {
                    for (int x = 0; x < jsonArraySize; x++) {
                        writer.print(databaseFollowers[x][1]);
                        if (x != jsonArraySize-1)
                            writer.print(", ");
                    }
                } else {
                    for (int x = 0; x < savedSettings.getInt("RECENT_NUM",0); x++) {
                        writer.print(databaseFollowers[x][1]);
                        if (x != savedSettings.getInt("RECENT_NUM",0)-1)
                            writer.print(", ");
                    }
                }
            }
            if (s.writeSelect.getSelectedItem().equals("Block/Line")) {
                writer = new PrintWriter(s.decodedPath + "recent_followers.txt", "UTF-8");
                if (jsonArraySize < savedSettings.getInt("RECENT_NUM", 5)) {
                    for (int x = 0; x < jsonArraySize; x++) {
                        writer.println(databaseFollowers[x][1]);
                    }
                } else {
                    for (int x = 0; x < savedSettings.getInt("RECENT_NUM",0); x++) {
                        writer.println(databaseFollowers[x][1]);
                    }
                }
            }
            if (s.writeSelect.getSelectedItem().equals("Dash/Space")) {
                writer = new PrintWriter(s.decodedPath + "recent_followers.txt", "UTF-8");
                if (jsonArraySize < savedSettings.getInt("RECENT_NUM", 5)) {
                    for (int x = 0; x < jsonArraySize; x++) {
                        writer.print(databaseFollowers[x][1]);
                        if (x != jsonArraySize-1)
                            writer.print(" - ");
                        else
                            writer.print(" ");
                    }
                } else {
                    for (int x = 0; x < savedSettings.getInt("RECENT_NUM",0); x++) {
                        writer.print(databaseFollowers[x][1]);
                        if (x != savedSettings.getInt("RECENT_NUM",0)-1)
                            writer.print(" - ");
                        else
                            writer.print(" ");
                    }
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            writer.close();
        }
        
        writer = null;
        try {
            writer = new PrintWriter(s.decodedPath + "total_followers.txt", "UTF-8");
            writer.print(totalFollowers);
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            writer.close();
        }
        writer = null;
        try {
            writer = new PrintWriter(s.decodedPath + "viewers.txt", "UTF-8");
            writer.print(totalViewers);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            writer.close();
        }
    }
    
    public void clearFollowers() {
        try {
            //grabCurrentFollowers(Avoids possible null's)
            URL twitchAPI = new URL("https://api.twitch.tv/kraken/channels/" + savedSettings.get("USER_NAME", "").toLowerCase() + "/follows?limit=20");
            BufferedReader reader = new BufferedReader(new InputStreamReader(twitchAPI.openStream()));
            String temp = reader.readLine();
            String croppedTemp;
            int index = 0;
            int indexDelta = 0;
            int indexEnd = 0;
            //grabCurrentTotal (Avoids "" for total)
            index = temp.indexOf("\"_total\":") + 9;
            indexEnd = temp.indexOf(',', index);
            croppedTemp = temp.substring(index, indexEnd);
            databaseTotal = croppedTemp;
            databaseFollowers = new String[20][2];
            for(int x=0; x<20; x++) {
                databaseFollowers[x][1] = "";
            }
            //reset
            index = 0;
            indexDelta = 0;
            indexEnd = 0;
            if(!(databaseTotal.equals("0"))) {
                for (int x = 0; x < 20; x++) {
                    index = temp.indexOf("display_name", index + indexDelta) + 15;
                    indexEnd = temp.indexOf('"', index);
                    indexDelta = indexEnd - index;
                    croppedTemp = temp.substring(index, indexEnd);
                    databaseFollowers[x][1] = croppedTemp;
                }
                savedSettings.put("JUST_FOLLOWED", databaseFollowers[0][1]);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "ERROR: Unable to connect to TwitchAPI\nShutting down.");
            System.exit(0);
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        og.setFont(savedSettings.get("FONT_SELECTED","Arial"));
        jDraw.setSizeOf(dispSize);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        jDraw.setLocationOf(dispSize);
    }

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}

    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == settingsButton) {
            s.setTitle("Settings");
            s.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    s.setVisible(false);
                }
            });
            s.setModal(true);
            s.setResizable(false);
            s.setSize(566,262);
            if(!startedUp) {
                s.setLocationRelativeTo(frame);
                startedUp = true;
            }
            s.setVisible(true);
        }
        if (ae.getSource() == updateButton) {
            try {
                setStats("https://api.twitch.tv/kraken/channels/" + savedSettings.get("USER_NAME", "").toLowerCase());
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (ae.getSource() == prevButton) {
            s.playSound(savedSettings.get("SOUND_SELECTED","Disabled"));
            try {
                s.applyAnimations();
                og.comingIn = true;
                if (s.postToChat) {
                    s.myBot.sendMessage("#" + savedSettings.get("USER_NAME", "").toLowerCase(), "(PREVIEW) " + og.followerMessage);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}