package MOTSP;

import java.util.ArrayList;

/**
 * Created by Ole on 03.05.2016.
 */
public class EALoop {
    private static int nGenerations = 100, curGen = 0, popSize = 100;
    private static ArrayList<MOTSP> population;

    public static void Main (){
        initPopulation();

        while (curGen < nGenerations){
            makeChildren();

            adultSelection();
        }

    }

    private static void initPopulation(){
        for (int n=0; n<popSize; n++){
            population.add(MOTSP.MOTSP());
        }
    }

    private static void makeChildren(){
        parentSelection();
    }

    private static void parentSelection(){
        //return
    }

    private static void adultSelection(){

    }
}
