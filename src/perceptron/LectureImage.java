package perceptron;
import mnisttools.MnistReader;

public class LectureImage {

    // Fonction qui binarise une image
    // Attention l'image est modifiee.
    // la variable seuil doit etre entre 0 et 255 (a tester)
    public static void binarisation(int [][] image, int seuil){
        for (int i = 0; i<image.length;i++) {
            for(int j = 0; j <image[i].length;j++) {
                if(image[i][j] > seuil)
                    image[i][j]=1;
                else
                    image[i][j]=0;
            }
        }
    }

    public static void main(String[] args) {
        String path="/home/paul/Téléchargements/IA/"; // TODO : indiquer le chemin correct
        String labelDB=path+"train-labels-idx1-ubyte";
        String imageDB=path+"train-images-idx3-ubyte";
        // Creation de la base de donnees
        MnistReader db = new MnistReader(labelDB, imageDB);
        // Acces a la premiere image
        int idx = 1; // une variable pour stocker l'index
        // Attention la premiere valeur est 1.
        int [][] image = db.getImage(idx); /// On recupere la premiere l'image numero idx
        // Son etiquette ou label
        int label = db.getLabel(idx);
        // Affichage du label
        binarisation(image, 128);
        for (int i = 0; i<image.length;i++) {
            for(int j = 0; j <image[i].length;j++) {
                System.out.print(image[i][j] + " ");
            }
            System.out.println();
        }


        System.out.println(image.length);
        System.out.println(image[1].length);

        System.out.print("Le label est "+ label+"\n");
        // note: le caractère \n est le 'retour charriot' (retour a la ligne).
        // Affichage du nombre total d'image
        System.out.print("Le total est "+ db.getTotalImages()+"\n");
        /* A vous de jouer pour la suite */
    }
}