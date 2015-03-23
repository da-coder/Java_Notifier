package javanotifier;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.prefs.Preferences;
import javax.swing.*;

public class JavaRender extends JPanel implements ActionListener, MouseListener, MouseMotionListener {    
    JPopupMenu popGoesTheWeasel = new JPopupMenu();
    JMenu editMenu = new JMenu("Edit mode");
    JMenuItem editMenuImage = new JMenuItem("Edit image");
    JMenuItem editMenuText = new JMenuItem("Edit text");
    JMenuItem editMenuDisabled = new JMenuItem("Disabled");
    
    public int intRectX, intRectY, intRectWidth, intRectHeight, intImgRectX, intImgRectY, intImgRectWidth, intImgRectHeight, intTempX, intTempY,
            intImgTempX, intImgTempY, intTempX2, intTempY2, corner;
    public float deltaX = 0.0f, deltaY = 0.0f, deltaA = 0.0f;
    public boolean comingIn = false, paintRect = false, paintRectImage = false;
    public String followerMessage = "";

    private Preferences savedSettings;
    private Settings Gs;
        private Image[] scaledImages;
    
    private Font fontRender;
    private FontMetrics fontMetrics;
    
    public JavaRender() {
        super(true);
    }
    
    public void JavaRender(Settings s) {        
        Gs = s;
        savedSettings = Preferences.userNodeForPackage(JavaNotifier.class);
        
        editMenuImage.addActionListener(this);
        editMenuText.addActionListener(this);
        editMenuDisabled.addActionListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        
        scaledImages = new BufferedImage[Gs.images.length];
        for(int i = 0; i < Gs.images.length; i++) {
            scaledImages[i] = scaleImage(Gs.images[i], intImgRectWidth, intImgRectHeight);
        }
        
        setFont(savedSettings.get("FONT_SELECTED","Arial"));
        
        startRender();
    }
    
    private void startRender() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    repaint();
                    try {
                        Thread.sleep(Gs.timingA);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    public void setFont(String inputFont) {
        Font fontReplace;
        fontReplace = new Font(inputFont, Font.PLAIN, 10);
        
        fontMetrics = this.getFontMetrics(fontReplace);
        fontReplace = scaleFont(fontReplace, (float)intRectWidth, (float)intRectHeight);
        fontMetrics = this.getFontMetrics(fontReplace);
        //calculate position
        intTempX2 = (int) ((float)intRectWidth / 2 - ((float)fontMetrics.stringWidth(followerMessage) / 2));
        intTempY2 = (int) (((float)intRectHeight / 2) + ((float)fontMetrics.getHeight() / 4));
        
        fontRender = fontReplace;
    }
    
    private Font scaleFont(Font fontIn, float widthIn, float heightIn) {
        float scaled = widthIn / (float)fontMetrics.stringWidth(followerMessage);
        float alsoScaled = 1.0f;
        Font fontSmall = new Font(fontIn.getFontName(), Font.PLAIN, (int)(scaled * 10.0f));
        if(heightIn < this.getFontMetrics(fontSmall).getHeight()) {
            alsoScaled = heightIn / (float)this.getFontMetrics(fontSmall).getHeight();
        }
        Font fontSmallerStill = new Font(fontIn.getName(), Font.PLAIN, (int)(alsoScaled * (float)fontSmall.getSize()));
        return fontSmallerStill;
    }
    
    public static BufferedImage scaleImage(BufferedImage sbi, int dWidth, int dHeight) {
        BufferedImage dbi = null;
        if(sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, 2);
            Graphics2D g = dbi.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double fWidth = (double)dWidth/(double)sbi.getWidth();
            double fHeight = (double)dHeight/(double)sbi.getHeight();
            AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
            g.drawRenderedImage(sbi, at);
        }
        return dbi;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        //clearScreen
        g2d.setColor(new Color(savedSettings.getInt("BACK_COLOR_R", 0), savedSettings.getInt("BACK_COLOR_G", 0), savedSettings.getInt("BACK_COLOR_B", 0), savedSettings.getInt("BACK_COLOR_A", 100)));
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        //frameCounter
        //drawStuff
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2d.setFont(fontRender);
        g2d.setColor(new Color(savedSettings.getInt("FORE_COLOR_R", 255), savedSettings.getInt("FORE_COLOR_G", 255), savedSettings.getInt("FORE_COLOR_B", 255), savedSettings.getInt("FORE_COLOR_A", 100)));
        //animations
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, deltaA));
        if(!(Gs.selctedIndexImage == Gs.imageSelect.getItemCount()-1))
            g2d.drawImage(scaledImages[Gs.selctedIndexImage],(int)(intImgRectX+deltaX),intImgRectY+(int)((float)intTempY2+deltaY-(float)intTempY2),null);
        g2d.drawString(followerMessage, intRectX+intTempX2+deltaX, intRectY+intTempY2+deltaY);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        
        //drawCursors
        if (paintRectImage) {                    
            //image
            for (int i = 0; i < Gs.imageSelect.getItemCount()-1; i++) {
                String temp2 = (String)Gs.imageSelect.getItemAt(i);
                String temp = savedSettings.get("IMAGE_SELECTED", "Disabled");
                if (temp.equals(temp2)&& !temp.equals("Disabled")) {
                    g2d.drawImage(scaledImages[i],intImgRectX,intImgRectY,null);
                }
            }
            //string
            g2d.drawString(followerMessage, intRectX+intTempX2, intRectY+intTempY2);

            g2d.setColor(Color.red);
            //rectangle
            g2d.drawRect(intImgRectX,intImgRectY,intImgRectWidth,intImgRectHeight);

            //cursors
            g2d.drawRect(intImgRectX,intImgRectY,10,10);
            g2d.drawRect(intImgRectX+intImgRectWidth-10,intImgRectY,10,10);
            g2d.drawRect(intImgRectX,intImgRectY+intImgRectHeight-10,10,10);
            g2d.drawRect(intImgRectX+intImgRectWidth-10,intImgRectY+intImgRectHeight-10,10,10);
        }
        if (paintRect) {
            //image
            for (int i = 0; i < Gs.imageSelect.getItemCount()-1; i++) {
                String temp2 = (String)Gs.imageSelect.getItemAt(i);
                String temp = savedSettings.get("IMAGE_SELECTED", "Disabled");
                if (temp.equals(temp2)&& !temp.equals("Disabled")) {
                    g2d.drawImage(scaledImages[i],intImgRectX,intImgRectY,null);
                }
            }
            g2d.setColor(new Color(savedSettings.getInt("FORE_COLOR_R", 255), savedSettings.getInt("FORE_COLOR_G", 255), savedSettings.getInt("FORE_COLOR_B", 255), savedSettings.getInt("FORE_COLOR_A", 100)));
            //string
            g2d.drawString(followerMessage, intRectX+intTempX2, intRectY+intTempY2);

            g2d.setColor(Color.red);
            //rectangle
            g2d.drawRect(intRectX,intRectY,intRectWidth,intRectHeight);

            //cursors
            g2d.drawRect(intRectX,intRectY,10,10);
            g2d.drawRect(intRectX+intRectWidth-10,intRectY,10,10);
            g2d.drawRect(intRectX,intRectY+intRectHeight-10,10,10);
            g2d.drawRect(intRectX+intRectWidth-10,intRectY+intRectHeight-10,10,10);
        }
        
        //outline
        if (savedSettings.getFloat("OUT_TYPE", 1.0f) != 0) {
            g2d.setColor(new Color(savedSettings.getInt("OUT_COLOR_R", 0), savedSettings.getInt("OUT_COLOR_G", 0), savedSettings.getInt("OUT_COLOR_B", 0), savedSettings.getInt("OUT_COLOR_A", 0)));
            GlyphVector gv = fontRender.createGlyphVector(g2d.getFontRenderContext(), followerMessage);
            Shape shape = gv.getOutline();
            g2d.setStroke(new BasicStroke(savedSettings.getFloat("OUT_TYPE", 1.0f)));
            //drawOutline
            if (paintRect || paintRectImage) {
                g2d.translate(intRectX+intTempX2, intRectY+intTempY2);
                g2d.draw(shape);
            } else {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, deltaA));
                g2d.translate(intRectX+intTempX2+deltaX, intRectY+intTempY2+deltaY);
                g2d.draw(shape);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            }
        }
        
        //dispose
        g2d.dispose();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == editMenuText) {
            paintRect = true;
            paintRectImage = false;
            editMenuText.setBackground(Color.white.darker());
            editMenuText.setForeground(Color.black);
            editMenuImage.setBackground(Color.white);
            editMenuImage.setForeground(Color.black);
            editMenuDisabled.setBackground(Color.white);
            editMenuDisabled.setForeground(Color.black);
            popGoesTheWeasel.setVisible(false);
        }
        if (ae.getSource() == editMenuImage) {
            paintRect = false;
            paintRectImage = true;
            editMenuText.setBackground(Color.white);
            editMenuText.setForeground(Color.black);
            editMenuImage.setBackground(Color.white.darker());
            editMenuImage.setForeground(Color.black);
            editMenuDisabled.setBackground(Color.white);
            editMenuDisabled.setForeground(Color.black);
            popGoesTheWeasel.setVisible(false);
        }
        if (ae.getSource() == editMenuDisabled) {
            paintRect = false;
            paintRectImage = false;
            editMenuText.setBackground(Color.white);
            editMenuText.setForeground(Color.black);
            editMenuImage.setBackground(Color.white);
            editMenuImage.setForeground(Color.black);
            editMenuDisabled.setBackground(Color.white.darker());
            editMenuDisabled.setForeground(Color.black);
            popGoesTheWeasel.setVisible(false);
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == 3) {
            popGoesTheWeasel.add(editMenuImage);
            popGoesTheWeasel.add(editMenuText);
            popGoesTheWeasel.add(editMenuDisabled);
            popGoesTheWeasel.show(this, e.getX(), e.getY());
            popGoesTheWeasel.updateUI();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(paintRect) {
            if(new Rectangle(0,0,this.getWidth(),this.getHeight()).contains(e.getPoint())) {
                corner = 5;
            }
            if(new Rectangle(intRectX,intRectY,intRectWidth,intRectHeight).contains(e.getPoint())) {
                corner = 4;
                intTempX = e.getX();
                intTempY = e.getY();
            }
            if(new Rectangle(intRectX,intRectY,10,10).contains(e.getPoint())) {
                corner = 0;
                intTempX = e.getX();
                intTempY = e.getY();
            }
            if(new Rectangle(intRectX+intRectWidth-10,intRectY,10,10).contains(e.getPoint())) {
                corner = 1;
                intTempX = e.getX();
                intTempY = e.getY();
            }
            if(new Rectangle(intRectX,intRectY+intRectHeight-10,10,10).contains(e.getPoint())) {
                corner = 2;
                intTempX = e.getX();
                intTempY = e.getY();
            }
            if(new Rectangle(intRectX+intRectWidth-10,intRectY+intRectHeight-10,10,10).contains(e.getPoint())) {
                corner = 3;
                intTempX = e.getX();
                intTempY = e.getY();
            }
        }
        if(paintRectImage) {
            if(new Rectangle(0,0,this.getWidth(),this.getHeight()).contains(e.getPoint())) {
                corner = 5;
            }
            if(new Rectangle(intImgRectX,intImgRectY,intImgRectWidth,intImgRectHeight).contains(e.getPoint())) {
                corner = 4;
                intImgTempX = e.getX();
                intImgTempY = e.getY();
            }
            if(new Rectangle(intImgRectX,intImgRectY,10,10).contains(e.getPoint())) {
                corner = 0;
                intImgTempX = e.getX();
                intImgTempY = e.getY();
            }
            if(new Rectangle(intImgRectX+intImgRectWidth-10,intImgRectY,10,10).contains(e.getPoint())) {
                corner = 1;
                intImgTempX = e.getX();
                intImgTempY = e.getY();
            }
            if(new Rectangle(intImgRectX,intImgRectY+intImgRectHeight-10,10,10).contains(e.getPoint())) {
                corner = 2;
                intImgTempX = e.getX();
                intImgTempY = e.getY();
            }
            if(new Rectangle(intImgRectX+intImgRectWidth-10,intImgRectY+intImgRectHeight-10,10,10).contains(e.getPoint())) {
                corner = 3;
                intImgTempX = e.getX();
                intImgTempY = e.getY();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //text
        if (intRectWidth >= 20
                && intRectHeight >= 20
                && intRectX+intRectWidth <= this.getWidth()-1
                && intRectY+intRectHeight <= this.getHeight()-1
                && intRectX >= 0
                && intRectY >= 0) {
            if(paintRect) {
                if(corner == 4) {
                    intRectX = (e.getX()-intTempX)+intRectX;
                    intRectY = (e.getY()-intTempY)+intRectY;
                    intTempX = e.getX();
                    intTempY = e.getY();
                }
                if(corner == 0) {
                    intRectWidth = -(e.getX()-intTempX)+intRectWidth;
                    intRectHeight = -(e.getY()-intTempY)+intRectHeight;
                    intRectX = intRectX+(e.getX()-intTempX);
                    intRectY = intRectY+(e.getY()-intTempY);
                    intTempX = e.getX();
                    intTempY = e.getY();
                }
                if(corner == 1) {
                    intRectWidth = (e.getX()-intTempX)+intRectWidth;
                    intRectHeight = -(e.getY()-intTempY)+intRectHeight;
                    intRectY = intRectY+(e.getY()-intTempY);
                    intTempX = e.getX();
                    intTempY = e.getY();
                }
                if(corner == 2) {
                    intRectWidth = -(e.getX()-intTempX)+intRectWidth;
                    intRectHeight = (e.getY()-intTempY)+intRectHeight;
                    intRectX = intRectX+(e.getX()-intTempX);
                    intTempX = e.getX();
                    intTempY = e.getY();
                }
                if(corner == 3) {
                    intRectWidth = (e.getX()-intTempX)+intRectWidth;
                    intRectHeight = (e.getY()-intTempY)+intRectHeight;
                    intTempX = e.getX();
                    intTempY = e.getY();
                }
            }
        } if (intRectWidth < 20) {
            intRectWidth = 20;
        } if (intRectHeight < 20) {
            intRectHeight = 20;
        } if (intRectX+intRectWidth > this.getWidth()-1) {
            intRectX = (this.getWidth()-1) - intRectWidth;
            if (intRectWidth > this.getWidth() - 1) {
                intRectWidth = this.getWidth() - 1;
            }
        } if (intRectY+intRectHeight > this.getHeight()-1) {
            intRectY = (this.getHeight()-1) - intRectHeight;
            if (intRectHeight > this.getHeight() - 1) {
                intRectHeight = this.getHeight() - 1;
            }
        } if (intRectX < 0) {
            intRectX = 0;
        } if (intRectY < 0) {
            intRectY = 0;
        }
        
        //image
        if (intImgRectWidth >= 20
                && intImgRectHeight >= 20
                && intImgRectX+intImgRectWidth <= this.getWidth()-1
                && intImgRectY+intImgRectHeight <= this.getHeight()-1
                && intImgRectX >= 0
                && intImgRectY >= 0) {
            if(paintRectImage) {
                if(corner == 4) {
                    intImgRectX = (e.getX()-intImgTempX)+intImgRectX;
                    intImgRectY = (e.getY()-intImgTempY)+intImgRectY;
                    intImgTempX = e.getX();
                    intImgTempY = e.getY();
                }
                if(corner == 0) {
                    intImgRectWidth = -(e.getX()-intImgTempX)+intImgRectWidth;
                    intImgRectHeight = -(e.getY()-intImgTempY)+intImgRectHeight;
                    intImgRectX = intImgRectX+(e.getX()-intImgTempX);
                    intImgRectY = intImgRectY+(e.getY()-intImgTempY);
                    intImgTempX = e.getX();
                    intImgTempY = e.getY();
                }
                if(corner == 1) {
                    intImgRectWidth = (e.getX()-intImgTempX)+intImgRectWidth;
                    intImgRectHeight = -(e.getY()-intImgTempY)+intImgRectHeight;
                    intImgRectY = intImgRectY+(e.getY()-intImgTempY);
                    intImgTempX = e.getX();
                    intImgTempY = e.getY();
                }
                if(corner == 2) {
                    intImgRectWidth = -(e.getX()-intImgTempX)+intImgRectWidth;
                    intImgRectHeight = (e.getY()-intImgTempY)+intImgRectHeight;
                    intImgRectX = intImgRectX+(e.getX()-intImgTempX);
                    intImgTempX = e.getX();
                    intImgTempY = e.getY();
                }
                if(corner == 3) {
                    intImgRectWidth = (e.getX()-intImgTempX)+intImgRectWidth;
                    intImgRectHeight = (e.getY()-intImgTempY)+intImgRectHeight;
                    intImgTempX = e.getX();
                    intImgTempY = e.getY();
                }
            }
        } if (intImgRectWidth < 20) {
            intImgRectWidth = 20;
        } if (intImgRectHeight < 20) {
            intImgRectHeight = 20;
        } if (intImgRectX+intImgRectWidth > this.getWidth()-1) {
            intImgRectX = (this.getWidth()-1) - intImgRectWidth;
            if (intImgRectWidth > this.getWidth()-1) {
                intImgRectWidth = this.getWidth()-1;
            }
        } if (intImgRectY+intImgRectHeight > this.getHeight()-1) {
            intImgRectY = (this.getHeight()-1) - intImgRectHeight;
            if (intImgRectHeight > this.getHeight()-1) {
                intImgRectHeight = this.getHeight()-1;
            }
        } if (intImgRectX < 0) {
            intImgRectX = 0;
        } if (intImgRectY < 0) {
            intImgRectY = 0;
        }
        
        scaledImages = new BufferedImage[Gs.images.length];
        for(int i = 0; i < Gs.images.length; i++) {
            scaledImages[i] = scaleImage(Gs.images[i], intImgRectWidth, intImgRectHeight);
        }
        
        setFont(savedSettings.get("FONT_SELECTED","Arial"));
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        if(paintRect) {
            if(new Rectangle(0,0,this.getWidth(),this.getHeight()).contains(e.getPoint())) {
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            if(new Rectangle(intRectX,intRectY,intRectWidth,intRectHeight).contains(e.getPoint())) {
                this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }
            if(new Rectangle(intRectX,intRectY,10,10).contains(e.getPoint())) {
                this.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
            }
            if(new Rectangle(intRectX+intRectWidth-10,intRectY,10,10).contains(e.getPoint())) {
                this.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
            }
            if(new Rectangle(intRectX,intRectY+intRectHeight-10,10,10).contains(e.getPoint())) {
                this.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
            }
            if(new Rectangle(intRectX+intRectWidth-10,intRectY+intRectHeight-10,10,10).contains(e.getPoint())) {
                this.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
            }
        }
        if(paintRectImage) {
            if(new Rectangle(0,0,this.getWidth(),this.getHeight()).contains(e.getPoint())) {
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            if(new Rectangle(intImgRectX,intImgRectY,intImgRectWidth,intImgRectHeight).contains(e.getPoint())) {
                this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }
            if(new Rectangle(intImgRectX,intImgRectY,10,10).contains(e.getPoint())) {
                this.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
            }
            if(new Rectangle(intImgRectX+intImgRectWidth-10,intImgRectY,10,10).contains(e.getPoint())) {
                this.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
            }
            if(new Rectangle(intImgRectX,intImgRectY+intImgRectHeight-10,10,10).contains(e.getPoint())) {
                this.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
            }
            if(new Rectangle(intImgRectX+intImgRectWidth-10,intImgRectY+intImgRectHeight-10,10,10).contains(e.getPoint())) {
                this.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
            }
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {}
    
    @Override
    public void mouseReleased(MouseEvent e) {}
}