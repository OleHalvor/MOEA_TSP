package MOTSP;

/**
 * Created by Ole on 03.05.2016.
 */
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MOTSP {

    // http://www.theprojectspot.com/tutorial-post/applying-a-genetic-algorithm-to-the-travelling-salesman-problem/5

    private int[] genome;
    private double dist_fitness = 0;
    private double cost_fitness = 0;
    private double tot_fitness  = 0;

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
        for (int i =0; i<48; i++){
            new_gene[i] = i;
        }
        new_gene = shuffleArray(new_gene);
        this.genome = new_gene;
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


    public int[] getGenome(){
        return this.genome;
    }

    private void calc_fitness(){
        Random rand = new Random();
        dist_fitness = rand.nextDouble();
    }

    public void setDistAndCost(int[] distAndCost){
        if ((distAndCost[0] >= 0)&&(distAndCost[1] >= 0)) {
            this.dist_fitness = distAndCost[0];
            this.cost_fitness = distAndCost[1];
        }
        else System.out.printf("Wrong distance of cost fitnesses given!");
    }



    public void tryToMutate(){
        Random rand = new Random();
        if (rand.nextDouble() < EALoop.mutationRate) {
            System.out.println("mutated!");
            int gen1 = rand.nextInt(48);
            int gen2 = rand.nextInt(48 - gen1) + gen1;
            int tempGen = this.genome[gen1];
            this.genome[gen1] = this.genome[gen2];
            this.genome[gen2] = tempGen;
        }
    }

    public double getDistance(){
        return this.dist_fitness;
    }
    public double getCost(){
        return this.cost_fitness;
    }
}
