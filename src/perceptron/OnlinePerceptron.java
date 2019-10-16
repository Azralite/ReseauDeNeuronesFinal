package perceptron;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.lang.Runtime;

public class OnlinePerceptron  {
    public static final int DIM = 3; // dimension de l'espace de representation
    public static float[] w = new float[DIM]; // parametres du modèle
    public static float[][] data = { // les observations
            {1,0,0}, {1,0,1} , {1,2,0},
            {1,1,1}
    };
    public static int[] refs = {-1, -1, -1, 1}; // les references



    public static void perceptron(float [][] tabPoint, float[] w, int[] refs,int ep,float eta){
        System.out.println("Epoque : " + ep);
        for (int i = 0; i < tabPoint.length; i++) {
            float xiwi = 0;
            for (int j = 1; j < tabPoint[i].length; j++) {
                xiwi = xiwi + tabPoint[i][j]*w[j];
                }
            xiwi = (xiwi + w[0]) * refs[i];
            if (xiwi <= 0){
                System.out.println("Le point " + i + " est mal placé");
                w[0] = w[0] + eta*refs[i];
                System.out.println(w[0]);
                for (int j = 1; j < tabPoint[i].length; j++){
                    w[j] = w[j]+refs[i]*tabPoint[i][j]*eta;
                    System.out.println(w[j]);
                }
            }
            else {
               System.out.println("Le point "+ i+ " est bien placé");
            }
        }
    }

    public  static int verifPoint(float [][] tabPoint, float[] w, int[] refs){
        int res = 0;
        for (int i = 0; i < tabPoint.length; i++) {
            float xiwi = 0;
            for (int j = 1; j < tabPoint[i].length; j++) {
                xiwi = xiwi + tabPoint[i][j]*w[j];
            }
            xiwi = (xiwi + w[0]) * refs[i];
            if (xiwi <= 0){
                res++;
            }
        }
        return res;
    }

    public static void afficheEqua(float [] w){
        if (w[2] == 0){
            System.out.println("x = " + (-w[0]/w[1]));
        }
        else{
            System.out.print("y = ");
            if (-w[1]%w[2] == 0){
                System.out.print(-w[1]/w[2] + " x ");
            }
            else{
                System.out.print(-w[1] +"/"+w[2] + " x ");
            }
            if (-w[0]%w[2] == 0) {
                if (-w[0]/w[2] > 0){
                    System.out.println("+" + -w[0]/w[2] );
                }
                else{
                    System.out.println(-w[0]/w[2] );
                }
            }
            else{
                if (-w[0]/w[2] > 0){
                    System.out.println("+" + -w[0] + "/" + w[2] );
                }
                else{
                    System.out.println(-w[0] + "/" + w[2] );
                }
            }
        }
    }

    public static void ecrire(String nomFic, String texte) { //on va chercher le chemin et le nom du fichier et on me tout ca dans un String
        String adressedufichier = System.getProperty("user.dir") + "/"+ nomFic; //on met try si jamais il y a une exception
        try {
            FileWriter fw = new FileWriter(adressedufichier); // le BufferedWriter output auquel on donne comme argument le FileWriter fw cree juste au dessus
            BufferedWriter output = new BufferedWriter(fw);//on marque dans le fichier ou plutot dans le BufferedWriter qui sert comme un tampon(stream)
            output.write(texte); //on peut utiliser plusieurs fois methode write
            output.flush(); //ensuite flush envoie dans le fichier, ne pas oublier cette methode pour le BufferedWriter
            output.close(); //et on le ferme
            System.out.println("fichier créé");
        }
        catch(IOException ioe){
            System.out.print("Erreur : ");
            ioe.printStackTrace();
        }
    }
    public static void texte(int epoque) {
        String texte = "set terminal pngcairo  \n" + //Ici je sais pas trop ce que ca fait mais ca rend l'image net
                "set output 'photo/mon-fichier"+(100+epoque)+".png' \n" + //Là je fait en sorte que mon image soit enregistré dans le fichier photo sous le nom mon-fichier 100 quelque chose
                //C'est juste quand on a plus de 10 photos pour qu'elles soient dans le bon ordre quand on les affiche
                "set grid \n" + // Pour mettre une grille en arrière plan
                "set yrange[-2:4] \n"; // On regle les bornes de y
        for (int i = 0; i < data.length; i++) {
            texte += "set object circle fc rgb "; //Ici on declare un point
            if (refs[i] == -1) {
                texte += "\"blue\" "; //qui est bleu si c'est classe -1
            } else {
                texte += "\"red\" "; //ou rouge si +1
            }
            texte += "at " + data[i][1] + "," + data[i][2] + " size 0.1 \n"; // et on donne ses coordonées
        }
        texte += "plot [-5:5] ((" + -w[1] / w[2] + "*x)+(" + -w[0] / w[2] + "))"; //Apres on trace la separatrice
        ecrire("scripts/script"+epoque+".gnu", texte); //Enfait la on a juste ecrit dans une var String le texte qu'on veut mettre dans le fichier
        //et la fonction ecrire va s'occuper du reste
    }

    public static void main(String[] args) {
        // Exemple de boucle qui parcourt tous les exemples d'apprentissage
        // pour en afficher a chaque fois l'observation et la reference.
        for (int i = 0; i < data.length; i++) {
            float[] x = data[i];
            System.out.println("x= "+Arrays.toString(x)+ " / y = "+refs[i]);
        }

        int epoque = 1;
        w = ImageOnlinePerceptron.InitialiseW(DIM,2);

        while (epoque < 100 && verifPoint(data, w, refs) != 0 ) {
            perceptron(data, w, refs, epoque,1);
            texte(epoque);
            epoque++;
        }

    }

}