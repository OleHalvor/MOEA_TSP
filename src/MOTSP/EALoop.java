package MOTSP;

import java.util.*;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ole on 03.05.2016.
 */
public class EALoop {
    private static int nGenerations = 100, curGen = 0, popSize = 100;
    public static final double mutationRate = 1;
    private static ArrayList<MOTSP> population;
    public static Fitness fitness = new Fitness();

    public static void main (String[] args){
        System.out.println("running");
        MOTSP p1 = new MOTSP();
        MOTSP p2 = new MOTSP();
        MOTSP child = crossOver(p1,p2);

        int[] childDistAndCost = fitness.getDistanceAndCost(child);
        System.out.println("Child dist: " + childDistAndCost[0]);
        System.out.println("Child cost: " + childDistAndCost[1]);
        child.tryToMutate();
        childDistAndCost = fitness.getDistanceAndCost(child);
        System.out.println("Child dist: " + childDistAndCost[0]);
        System.out.println("Child cost: " + childDistAndCost[1]);


    }

    public static boolean contains(final int[] arrayS, final int keyS) {
        String array = Arrays.toString(arrayS);
        String key = (Integer.toString(keyS));
        return array.contains(key);
    }

    private static MOTSP crossOver(MOTSP p1, MOTSP p2){
        int[] p1_gen = p1.getGenome();
        int[] p2_gen = p2.getGenome();

        int[] genome = new int[48];
        Arrays.fill(genome, -1);

        Random rand = new Random();
        int cut1 = rand.nextInt(48);
        int cut2 = rand.nextInt(48-cut1)+cut1;

        int[] p1_cut = new int[cut2-cut1];

        for (int i = cut1; i<cut2; i++){
            genome[i] = p1_gen[i];
        }

        for (int i = 0; i <48; i++){ //Loop through new genome
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

    private static void initPopulation(){
        for (int n=0; n<popSize; n++){
            population.add(new MOTSP());
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
