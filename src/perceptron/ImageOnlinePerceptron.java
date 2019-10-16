package perceptron;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import mnisttools.MnistReader;

import static perceptron.OnlinePerceptron.ecrire;


public class ImageOnlinePerceptron {

    /* Les donnees */
    public static String path="";
    public static String labelDB=path+"train-labels-idx1-ubyte";
    public static String imageDB=path+"train-images-idx3-ubyte";

    /* Parametres */
    // Na exemples pour l'ensemble d'apprentissage
    public static final int Na = 5000;
    // Nv exemples pour l'ensemble d'évaluation
    public static final int Nv = 1000;
    // Nombre d'epoque max
    public final static int EPOCHMAX=40;
    // Classe positive (le reste sera considere comme des ex. negatifs):
    public static int  classe = 5 ;

    // Générateur de nombres aléatoires
    public static int seed = 1234;
    public static Random GenRdm = new Random(seed);

    /*
     *  BinariserImage :
     *      image: une image int à deux dimensions (extraite de MNIST)
     *      seuil: parametre pour la binarisation
     *
     *  on binarise l'image à l'aide du seuil indiqué
     *
     */
    public static int[][] BinariserImage(int[][] image, int seuil) {
        int [][] res = new int[image.length][image[0].length];
        for (int i = 0; i<image.length;i++) {
            for(int j = 0; j <image[i].length;j++) {
                if(image[i][j] > seuil)
                    res[i][j]=1;
                else
                    res[i][j]=0;
            }
        }
        return res;
    }

    /*
     *  ConvertImage :
     *      image: une image int binarisée à deux dimensions
     *
     *  1. on convertit l'image en deux dimension dx X dy, en un tableau unidimensionnel de tail dx.dy
     *  2. on rajoute un élément en première position du tableau qui sera à 1
     *  La taille finale renvoyée sera dx.dy + 1
     *
     */
    public static float[] ConvertImage(int[][] image) {
        float [] res = new float[(image.length*image[0].length)+1];
        res[0] = 1;
        for (int i = 1; i < (image.length*image[0].length)+1; i++){
            res[i] = image[(i-1)/image.length][(i-1)%image[0].length];
        }
        return res;
    }

    /*
     *  InitialiseW :
     *      sizeW : la taille du vecteur de poids
     *      alpha : facteur à rajouter devant le nombre aléatoire
     *
     *  le vecteur de poids est crée et initialisé à l'aide d'un générateur
     *  de nombres aléatoires.
     */
    public static float[] InitialiseW(int sizeW, float alpha) {
        float [] res = new float[sizeW];
        for (int i = 0; i < sizeW; i++){
            res[i] = alpha * GenRdm.nextFloat();
        }
        return res;
    }




    public static void perceptron(float [][] tabPoint, float[] w, int[] refs,float eta){
        for (int i = 0; i < tabPoint.length; i++) {
            float xiwi = 0;
            for (int j = 1; j < tabPoint[i].length; j++) {
                xiwi = xiwi + tabPoint[i][j]*w[j];
            }
            xiwi = (xiwi + w[0]) * refs[i];
            if (xiwi <= 0){
                w[0] = w[0] + refs[i]*eta;
                for (int j = 1; j < tabPoint[i].length; j++){
                    w[j] = w[j]+refs[i]*tabPoint[i][j]*eta;
                }
            }
        }
    }



    public static void main(String[] args) {
        System.out.println("# Load the database !");
        /* Lecteur d'image */
        MnistReader db = new MnistReader(labelDB, imageDB);

        /* Creation des donnees */
        System.out.println("# Build train for digit "+ classe);
        /* Tableau où stocker les données */
        float[][] trainData = new float[Na][(db.getImage(1).length*db.getImage(1)[0].length)+1];
        int[] trainRefs = new int[Na];
        float[][] validData = new float[Nv][(db.getImage(1).length*db.getImage(1)[0].length)+1];
        int[] validRefs = new int [Nv];
        float[] imageConv = new float[(db.getImage(1).length*db.getImage(1)[0].length)+1];
        for (int i = 0; i < Na; i++){
            int [][] image = db.getImage(i+1);
            int label = db.getLabel(i+1);
            int [][] imageBinarise = BinariserImage(image,128);
            imageConv = ConvertImage(imageBinarise);
            trainData[i]=imageConv;
            trainRefs[i] = label;
        }

        for (int i = Na; i < Nv+Na; i++){
            int [][] image = db.getImage(i);
            int label = db.getLabel(i);
            int [][] imageBinarise = BinariserImage(image,128);
            imageConv = ConvertImage(imageBinarise);
            validData[i-Na]=imageConv;
            if (db.getLabel(i)==classe) {
                validRefs[i-Na] = 1;
            }
            else{
               validRefs[i-Na] = -1;
            }
        }


        float eta = 10f;
        float[] w = InitialiseW((db.getImage(1).length*db.getImage(1)[0].length)+1,1);

        float[] tabErreur = new float[EPOCHMAX];
        float[] tabErreur2 = new float[EPOCHMAX];

        for (int i = 0; i < EPOCHMAX; i++){
            perceptron(trainData,w,trainRefs,eta);
            tabErreur[i] = OnlinePerceptron.verifPoint(validData, w, validRefs);
            tabErreur2[i] = OnlinePerceptron.verifPoint(trainData,w,trainRefs);
        }

        String texteD = "";
        for (int i = 0; i < tabErreur.length; i++){
            texteD = texteD + "" + tabErreur[i] + "\n";
        }
        String texteB = "";
        for (int i = 0; i < tabErreur2.length; i++){
            texteB = texteB + "" + tabErreur2[i] + "\n";
        }

        ecrire("erreur.d", texteD);
        ecrire("erreur2.d", texteB);
        String texte = "set terminal pngcairo  \n" +
                       "set output 'mon-fichier"+eta+".png' \n" +
                        "set grid \n" +
                        "set style data linespoints \n"+
                        "plot 'erreur.d' , 'erreur2.d";

        ecrire("script2.gnu", texte);



    }
}