package MOTSP;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MOTSP {

    // http://www.theprojectspot.com/tutorial-post/applying-a-genetic-algorithm-to-the-travelling-salesman-problem/5

    private int[] genome;
    private double dist_fitness = 0;
    private double cost_fitness = 0;

    public MOTSP(){     //Constructor for new random individual
        generate_random_genome();
        setDistAndCost(Fitness.getDistanceAndCost(this));
    }

    public MOTSP(int[] genome){ //Constructor for new individual with given genome
        this.genome = genome;
        tryToMutate();
        setDistAndCost(Fitness.getDistanceAndCost(this));
    }

    private void generate_random_genome(){  //Generate all integers up to 48 and shuffle them.
        int[] new_gene = new int[48];
        for (int i =0; i<48; i++)
            new_gene[i] = i;
        this.genome = shuffleArray(new_gene);
    }

    public void tryToMutate(){
        Random rand = new Random();
        if (rand.nextDouble() < EALoop.mutationRate) {
            int gen1 = rand.nextInt(48), gen2 = rand.nextInt(48 - gen1) + gen1;
            int tempGen = genome[gen1];
            genome[gen1] = genome[gen2];
            genome[gen2] = tempGen;
        }
    }

    private static int[] shuffleArray(int[] ar) {   //Used when generating new random genomes
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
        return ar;
    }
    public void setDistAndCost(int[] distAndCost){
        this.dist_fitness = distAndCost[0];
        this.cost_fitness = distAndCost[1];
    }
    public int[] getGenome(){
        return this.genome;
    }
    public double getDistance(){
        return this.dist_fitness;
    }
    public double getCost(){
        return this.cost_fitness;
    }

}
