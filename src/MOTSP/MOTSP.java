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

    public MOTSP(){
        generate_random_genome();
    }

    public MOTSP(int[] genome){
        this.genome = genome;
    }

    private void generate_random_genome(){
        int[] new_gene = new int[48];
        for (int i =0; i<48; i++){
            new_gene[i] = i;
        }
        new_gene = shuffleArray(new_gene);
        this.genome = new_gene;
    }

    private static int[] shuffleArray(int[] ar) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
        return ar;
    }

    public void setGenome(int[] genome){
        this.genome = genome;
    }

    public int[] getGenome(){
        return this.genome;
    }

    public void calc_fitness(){
        Random rand = new Random();
        dist_fitness = rand.nextDouble();

    }

    public void tryToMutate(){
        Random rand = new Random();
        if (rand.nextDouble() < EALoop.mutationRate) {
            int gen1 = rand.nextInt(48);
            int gen2 = rand.nextInt(48 - gen1) + gen1;
            int tempGen = this.genome[gen1];
            this.genome[gen1] = this.genome[gen2];
            this.genome[gen2] = tempGen;
        }
    }
}
