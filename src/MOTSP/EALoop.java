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
    public static final double mutationRate = 0.1;
    private static ArrayList<MOTSP> population;

    public static void main (String[] args){
        System.out.println("running");
        MOTSP p1 = new MOTSP();
        MOTSP p2 = new MOTSP();
        MOTSP child = crossOver(p1,p2);
        System.out.println(Arrays.toString(p1.getGenome()));
        System.out.println(Arrays.toString(p2.getGenome()));
        System.out.println(Arrays.toString(child.getGenome()));

        child.tryToMutate();
        System.out.println(Arrays.toString(child.getGenome()));
        child.tryToMutate();
        System.out.println(Arrays.toString(child.getGenome()));
        child.tryToMutate();
        System.out.println(Arrays.toString(child.getGenome()));
        child.tryToMutate();
        System.out.println(Arrays.toString(child.getGenome()));

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

    private static ArrayList readFiles(String filename) throws Exception{
        ArrayList<ArrayList<Integer>> outer = new ArrayList<ArrayList<Integer>>();
        int i = 0;
        int k;
        File myFile = new File(filename);
        FileInputStream fis = new FileInputStream(myFile);
        // Finds the workbook instance for XLSX file
        XSSFWorkbook myWorkBook = new XSSFWorkbook (fis); // Return first sheet from the XLSX workbook
        XSSFSheet mySheet = myWorkBook.getSheetAt(0); // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = mySheet.iterator(); // Traversing over each row of XLSX file
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            ArrayList<Integer> inner = new ArrayList<Integer>();
            k = 0;
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if (k != 0) {
                    inner.add(((int) cell.getNumericCellValue()));
                }
                k++;
            }
            if ( i != 0) {
                outer.add(inner);
            }
            i++;
        }
        return outer;
    }
}
