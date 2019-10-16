package perceptron;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

// CTRL + SHIFT + O pour générer les imports
public class DrawPanel extends JPanel {


    public JPanel firstPanel;
    //Couleur du pointeur
    private Color pointerColor = Color.black;
    //Forme du pointeur
    private String pointerType = "CIRCLE";
    //Position X du pointeur
    private int posX = -10, oldX = -10;
    //Position Y du pointeur
    private int posY = -10, oldY = -10;
    //Pour savoir si on doit dessiner ou non
    private boolean erasing = true;
    //Taille du pointeur
    private int pointerSize = 15;
    //Collection de points !
    private ArrayList<Point> points = new ArrayList<Point>();

    public DrawPanel(){

        this.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                points.add(new Point(e.getX() - (pointerSize / 2), e.getY() - (pointerSize / 2), pointerSize, pointerColor, pointerType));
                repaint();
            }
        });

        this.addMouseMotionListener(new MouseMotionListener(){
            public void mouseDragged(MouseEvent e) {
                //On récupère les coordonnées de la souris et on enlève la moitié de la taille du pointeur pour centrer le tracé
                points.add(new Point(e.getX() - (pointerSize / 2), e.getY() - (pointerSize / 2), pointerSize, pointerColor, pointerType));
                repaint();
            }

            public void mouseMoved(MouseEvent e) {}
        });

    }

    // Vous la connaissez maintenant, celle-là
    public void paintComponent(Graphics g) {

        g.setColor(Color.white);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        //Si on doit effacer, on ne passe pas dans le else => pas de dessin
        if(this.erasing){
            this.erasing = false;
        }
        else{
            //On parcourt notre collection de points
            for(Point p : this.points)
            {
                //On récupère la couleur
                g.setColor(p.getColor());

                //Selon le type de point
                if(p.getType().equals("SQUARE")){
                    g.fillRect(p.getX(), p.getY(), p.getSize(), p.getSize());
                }
                else{
                    g.fillOval(p.getX(), p.getY(), p.getSize(), p.getSize());
                }
            }
        }
    }

    //Efface le contenu
    public void erase(){
        this.erasing = true;
        this.points = new ArrayList<Point>();
        repaint();
    }


    public static void saveImage(JPanel panel){
        BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(),BufferedImage.TYPE_INT_RGB);
        panel.paint(img.getGraphics());
        try {
            System.out.println("c'est fait");
            ImageIO.write(img, "png", new File("screen.png"));
        }
        catch (Exception e){
            System.out.println("panel not saved" +e.getMessage());
        }
    }

}