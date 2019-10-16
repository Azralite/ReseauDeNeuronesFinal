package perceptron;

import javax.imageio.ImageIO;
import javax.print.DocFlavor;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.rmi.server.ExportException;
import java.security.spec.ECField;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class DetectionEcriture {

    public static String readFile(String filename) {
        try {
            FileInputStream stream = new FileInputStream(new File(filename));
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                    fc.size());
            stream.close();
            return Charset.defaultCharset().decode(bb).toString();
        } catch (IOException e) {
            System.out.println("Erreur lors de l'accÃ¨s au fichier " + filename);
            System.exit(1);
        }
        return null;
    }


    public static void main(String[] args) {
        String poidsStr = readFile("poids.txt");
        float[][] wavant = recupereW(poidsStr);
        float[][] w = new float[wavant.length][(wavant[0].length)-1];
        for (int i = 0; i < w.length; i++){
            for (int j = 0; j < w[i].length; j++){
                w[i][j] = wavant[i][j];
            }
        }

        Fenetre fenetre = new Fenetre();
        try
        {
            Thread.sleep(5000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }

        DrawPanel.saveImage(fenetre.drawPanel);
        BufferedImage a = readImage("screen.png");
        BufferedImage aBis = scale(a, 28,28);
        BufferedImage b = binarize(aBis,128);

        float[] cAvant = img2seq(b);
        float[] c = new float[cAvant.length+1];
        c[0] = 1;
        for (int i =1; i <c .length; i++){
            c[i]= cAvant[i-1];
            System.out.println(c[i]);
        }
        System.out.println(c.length);
        System.out.println(w[0].length);
        float[] y = PerceptronMulti.InfPerceptron(c,w);
        float yMax = 0;
        int varPense = 0;
        for (int l = 0 ; l < 10; l++) {
            if(y[l] >yMax){
                yMax = y[l];
                varPense = l;
            }
        }
        System.out.println("L'algo pense que cette image est un "  + varPense);
    }

    public static BufferedImage readImage(String filename) {
        try {
            return ImageIO.read(new File(filename));
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de " + filename);
            System.exit(1);
        }
        return null;
    }

    public static float[][] recupereW(String texte){
        int nbPoids = texte.split("\n").length;
        int nbMot = texte.split(" ").length;
        String[][] poidStr = new String[nbPoids][(nbMot/nbPoids)+1];
        String[] lignePoids = texte.split("\n");
        int com = 0;
        for(int i =0; i < lignePoids.length; i++){
            String[] mots = lignePoids[i].split(" ");
            for (String mot : mots){
                //System.out.println(mot);
                if (mot.length() != 0) {
                    poidStr[i][com] = mot;
                    com++;
                }
            }
            com = 0;

        }



        float[][] poids = new float[poidStr.length][poidStr[0].length];
        for(int i =0; i < poidStr.length; i++){
            for (int j = 0; j < poidStr[i].length - 1; j++) {
                String a = poidStr[i][j] + "f";
                //System.out.println(a);
                float tmp = Float.valueOf(a.trim()).floatValue();
                poids[i][j] = tmp;
            }

        }
        return poids;
    }

    // Conversion d'une image en une image binaire
    public static BufferedImage binarize(BufferedImage original, int threshold) {

        int red;
        int newPixel;

        BufferedImage binarized = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        for (int i = 0; i < original.getWidth(); i++) {
            for (int j = 0; j < original.getHeight(); j++) {

                red = new Color(original.getRGB(i, j)).getRed();
                int alpha = new Color(original.getRGB(i, j)).getAlpha();
                if (red > threshold) {
                    newPixel = 255;
                } else {
                    newPixel = 0;
                }
                binarized.setRGB(i, j, newPixel);

            }
        }

        return binarized;

    }

    public static float[] img2seq(BufferedImage img) {
        float[] res = new float[img.getWidth() * img.getHeight()];

        int count = 0;
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                res[count] = img.getRGB(i, j) == -1 ? 0 : 1;
                count += 1;
            }
        }

        return res;
    }

    public static BufferedImage scale(BufferedImage src, int w, int h)
    {
        BufferedImage img =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int x, y;
        int ww = src.getWidth();
        int hh = src.getHeight();
        int[] ys = new int[h];
        for (y = 0; y < h; y++)
            ys[y] = y * hh / h;
        for (x = 0; x < w; x++) {
            int newX = x * ww / w;
            for (y = 0; y < h; y++) {
                int col = src.getRGB(newX, ys[y]);
                img.setRGB(x, y, col);
            }
        }
        return img;
    }
}
