package perceptron;

import mnisttools.MnistReader;

import java.io.FileOutputStream;
import java.util.Random;
import java.lang.*;
import java.util.Vector;

import static perceptron.ImageOnlinePerceptron.*;
import static perceptron.OnlinePerceptron.ecrire;
import static perceptron.OnlinePerceptron.w;

public class PerceptronMulti {

    private static int TAILLE = 10;

    /*
     *  InitialiseW :
     *      sizeW : la taille du vecteur de poids
     *      alpha : facteur à rajouter devant le nombre aléatoire
     *
     *  le vecteur de poids est crée et initialisé à l'aide d'un générateur
     *  de nombres aléatoires.
     */

    public static float[][] InitialiseW2(int sizeW, float alpha) {
        float [][] res = new float[10][sizeW];
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < sizeW; i++) {
                res[j][i] = alpha * (GenRdm.nextFloat()-0.5f);
            }
        }
        return res;
    }

    public static int[] OneHot(int etiquette){
        int[] res = new int[TAILLE];
        for (int i =0; i < res.length; i++){
            if(i == etiquette){
                res[i] = 1;
            }
            else{
                res[i]=0;
            }
        }
        return res;
    }


    public static float[] InfPerceptron(float[] donnee, float[][] w){
        float[] res = new float[w.length];
        float[] tabNum = new float[w.length];
        float den = 0;
        for (int i = 0; i < w.length; i++){
            for (int j = 0; j < w[i].length; j++){
                tabNum[i] += donnee[j]*w[i][j];
            }
            tabNum[i] = (float) Math.pow(Math.E,tabNum[i]);
            den += tabNum[i];
        }
        for (int i =0; i < w.length; i++){
            res[i] = tabNum[i]/den;
        }
        return res;
    }

    public  static int verifPoint(float [][] tabPoint, float[][] w, int[] refs, float eta){
        int res = 0;
        for (int i = 0; i < tabPoint.length; i++) {
            float[] y = InfPerceptron(tabPoint[i], w);
            int[] p = OneHot(refs[i]);
            float yMax = 0;
            int indiceMax = 0;
            for (int l = 0 ; l < 10; l++) {
                if(y[l] >yMax){
                    yMax = y[l];
                    indiceMax = l;
                }
            }
            if (p[indiceMax] != 1){
                res++;
            }
        }
        return res;
    }



    public static void perceptronM(float [][] tabPoint, float[][] w, int[] refs,float eta){
        for (int i = 0; i < tabPoint.length; i++) {
            float[] y = InfPerceptron(tabPoint[i], w);
            int[] p = OneHot(refs[i]);
            for (int l = 0 ; l < 10; l++) {
                for (int j = 0; j < tabPoint[i].length; j++) {
                    w[l][j] = w[l][j] - tabPoint[i][j] * eta * (y[l]-p[l]);
                }
            }
        }
    }

    public static void ecriPoids(float[][] poids){
        String wStr = "";
        for (int i = 0; i < poids.length; i++){
            for (int j = 0; j < poids[i].length ; j++){
                wStr = wStr +" " + poids[i][j];
            }
            wStr = wStr +"\n";
        }
        ecrire("poids.txt" , wStr);
    }

    /*Fonction qui ecrit les erreurs par rapport au jeu d'entrainement
    Et en fonction des epoques
    @param le tableau d'erreur en rapport des epoques
     */
    public static void ecriErreur1(float[] tabErreur){
        String texteD = "";
        for (int i = 0; i < tabErreur.length; i++){
            texteD = texteD + "" + tabErreur[i] + "\n";
        }
        ecrire("erreur.d", texteD);
    }

    /*Fonction qui ecrit les erreurs par rapport au jeu de validation
    Et en fonction des epoques
    @param le tableau d'erreur en rapport des epoques
     */
    public static void ecriErreur2(float[] tabErreur2){
        String texteB = "";
        for (int i = 0; i < tabErreur2.length; i++){
            texteB = texteB + "" + tabErreur2[i] + "\n";
        }
        ecrire("erreur2.d", texteB);
    }

    /*Fonction qui ecrit un script gnuplot pour afficher les deux graphiques
    @param eta pour changer le nom du fichier en fonction de eta
     */
    public static void ecriScript(float eta){
        String texte = "set terminal pngcairo  \n" +
                "set output 'mon-fichier"+eta+".png' \n" +
                "set grid \n" +
                "set style data linespoints \n"+
                "plot 'erreur.d' , 'erreur2.d'";
        ecrire("script2.gnu", texte);
    }


    //Fonction qui sert a impressioner les gens ^^
    public static void devine(float[][] poids, float[][] validData, int[] validRefs, float eta){
        MnistReader db = new MnistReader(labelDB, imageDB);
        int jambe = GenRdm.nextInt((int)(Math.random()*1000));
        int [][] image = db.getImage(jambe);
        int label = db.getLabel(jambe);
        int [][] imageBinarise = BinariserImage(image,128);
        float[] imageConv = ConvertImage(imageBinarise);
        for (int i = 0; i< imageBinarise.length; i++){
            for (int j = 0; j < imageBinarise[i].length; j++){
                System.out.print(imageBinarise[i][j]);
            }
            System.out.println();
        }

        float[] a = InfPerceptron(imageConv,poids);
        float yMax = 0;
        int varPense = 0;
        for (int l = 0 ; l < 10; l++) {
            if(a[l] >yMax){
                yMax = a[l];
                varPense = l;
            }
        }
        System.out.println("L'algo pense que cette image est un "  + varPense);
        System.out.println("C'est officiellement un " + db.getLabel(jambe));
        System.out.println("Avec une proba de " + (100-(((float)verifPoint(validData,poids,validRefs,eta)/Na)*100)) +  "%");
    }



    public static float costFunctionTotal(float[][] poids, float[][] data, int[] refs){
        float tmp;
        float[] tabRes = new float[data.length];
        for (int u =0; u < data.length; u++){
            tmp = 1;
            float[] y = InfPerceptron(data[u], poids);
            int[] p = OneHot(refs[u]);
            for (int i = 0; i < poids.length; i++) {
                tmp = tmp * (float)Math.pow(y[i],p[i]);
            }
            tabRes[u] = tmp;
        }
        float finalRes =0;
        for (int j =0; j < tabRes.length; j++ ){
            finalRes += Math.log(tabRes[j]);
        }
        finalRes = finalRes/tabRes.length;
        return finalRes;
    }

    public static void ecriCout(float[] cout, float eta){
        String texteC = "";
        for (int i = 0; i < cout.length; i++){
            texteC = texteC + "" + cout[i] + "\n";
        }
        ecrire("cout.d", texteC);

        String texte = "set terminal pngcairo  \n" +
                "set output 'cout"+ eta +".png' \n" +
                "set grid \n" +
                "set style data linespoints \n"+
                "plot 'cout.d'";
        ecrire("script1.gnu", texte);
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
        for (int i = 0; i < Na; i++){
            int [][] image = db.getImage(i+1);
            int label = db.getLabel(i+1);
            int [][] imageBinarise = ImageOnlinePerceptron.BinariserImage(image,128);
            trainData[i]= ImageOnlinePerceptron.ConvertImage(imageBinarise);
            trainRefs[i] = label;
        }

        for (int i = Na; i < Nv+Na; i++){
            int [][] image = db.getImage(i);
            int [][] imageBinarise = ImageOnlinePerceptron.BinariserImage(image,128);
            validData[i-Na]= ImageOnlinePerceptron.ConvertImage(imageBinarise);
            validRefs[i-Na]= db.getLabel(i);
        }
        float[] tabErreur = new float[EPOCHMAX];
        float[] tabErreur2 = new float[EPOCHMAX];
        int dim = (db.getImage(1).length*db.getImage(1)[0].length)+1;

        float[] tabCoup = new float[EPOCHMAX];

        float eta = 5* (float)(Math.pow(10,-3));
        float[][] poids = InitialiseW2(dim,eta);
        for (int i = 0; i < EPOCHMAX; i++) {
            tabCoup[i] = costFunctionTotal(poids, trainData, trainRefs);
            perceptronM(trainData, poids, trainRefs, 0.01f);
            tabErreur[i] = verifPoint(validData, poids, validRefs, eta);
            tabErreur2[i] = verifPoint(trainData, poids, trainRefs, eta);
        }
        ecriErreur1(tabErreur);
        ecriErreur2(tabErreur2);
        ecriCout(tabCoup, eta);
        ecriScript(eta);

//        ecriPoids(poids);



//        devine(poids, validData, validRefs, eta);

    }
}
