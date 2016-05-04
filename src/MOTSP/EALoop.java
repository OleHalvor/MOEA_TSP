package MOTSP;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ole on 03.05.2016.
 */
public class EALoop {
    private static int nGenerations = 100, curGen = 0, popSize = 20;
    public static final double mutationRate = 1;
    private static ArrayList<MOTSP> population = new ArrayList<MOTSP>();
    private static Fitness fitness = new Fitness(); //this is needed to make Fitness.java read the distance and cost files

    public static void main (String[] args){
        System.out.println("Starting EALoop");
        initPopulation();
        ArrayList<ArrayList<MOTSP>> paretoFronts = Pareto.getParetoFronts(population);
        printFronts(paretoFronts);
    }

    private static void initPopulation(){
        for (int n=0; n<popSize; n++){
            population.add(new MOTSP());
        }
    }

    private static MOTSP crossOver(MOTSP p1, MOTSP p2){
        /*
        Crossover makes a new empty genome, filled with "-1"s
        Then it chooses a sequence of DNA from p1 and adds it to the new genome
        Then it fills the rest of the empty DNA with chromosomes from p2 in sequential order, as long as the chromosome is not already present.
         */
        int[] p1_gen = p1.getGenome(), p2_gen = p2.getGenome(), genome = new int[48];
        Arrays.fill(genome, -1);         //Fills the genome with chromosomes of "-1"
        Random rand = new Random();
        int cut1 = rand.nextInt(48);             //Start of dna sequence from parent 1
        int cut2 = rand.nextInt(48-cut1)+cut1;   //End of dna sequence from parent 1
        for (int i = cut1; i<cut2; i++){         //Sets the sequence from parent 1 to child
            genome[i] = p1_gen[i];
        }
        for (int i = 0; i <48; i++){ //Loop through new genome to fill empty spots of dna with chromosomes from p2 which are not present of the section from p1
            if (genome[i]==-1){      //Indicates chromosome not set
                for (int k= 0; k<48; k++){ //Loop over p2 genome to find 1st value not in new genome
                    int p2_val = p2_gen[k];
                    boolean found = false;
                    for (int o=0; o<48; o++){   //This could be sped up with a .contains method..
                        if (genome[o]==p2_val){
                            found = true;
                            break;
                        }
                    }
                    if (!found){
                        genome[i] = p2_val;
                        break;
                    }
                }
            }
        }
        return new MOTSP(genome);
    }



    private static void makeChildren(){
        parentSelection();
    }

    private static void parentSelection(){
        //return
    }

    private static void adultSelection(){

    }

    private static void printFronts(ArrayList<ArrayList<MOTSP>> paretoFronts){
        for (int i =0; i<paretoFronts.size(); i++){
            for ( int k=0; k<paretoFronts.get(i).size(); k++){
                System.out.println("Front "+i+" Solution #"+k+"(distance,cost): "+paretoFronts.get(i).get(k).getDistance()+", "+paretoFronts.get(i).get(k).getCost() );
            }
        }
    }

}
