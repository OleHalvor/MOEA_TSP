package MOTSP;

/**
 * Created by Ole on 03.05.2016.
 */
import java.util.*;
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
        //TODO make
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
}
