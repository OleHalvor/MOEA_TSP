package MOTSP;

/**
 * Created by Ole on 03.05.2016.
 */

public class MOTSP {

    // http://www.theprojectspot.com/tutorial-post/applying-a-genetic-algorithm-to-the-travelling-salesman-problem/5

    private int[] genome;
    private int dist_fitness = 0;
    private int cost_fitness = 0;

    public MOTSP(){
        generate_random_genome();
    }

    private void generate_random_genome(){
        //TODO make
    }

    public int calc_fitness(){
        dist_fitness = Math.random();
    }
}
