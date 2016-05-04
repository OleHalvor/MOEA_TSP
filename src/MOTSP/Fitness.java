package MOTSP;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Olli on 04.05.2016.
 */

public class Fitness {
    private static ArrayList<ArrayList<Integer>> distances = new ArrayList<ArrayList<Integer>>();
    private static ArrayList<ArrayList<Integer>> costs = new ArrayList<ArrayList<Integer>>();

    public Fitness(){
        try {
            this.distances = readFiles("Distance.xlsx");
            this.costs = readFiles("Cost.xlsx");
        }catch (Exception e){e.printStackTrace();}
    }

    public static int[] getDistanceAndCost(MOTSP individual){
        int[] path = individual.getGenome();
        int totalCost = 0;
        int totalDist = 0;
        for (int i=0; i<47; i++){   //Loops over path and calculates distance and cost of each pair of cities
            int city1 = path[i];
            int city2 = path[i+1];
            if (city2 < city1){
                int tempCity = city1;
                city1 = city2;
                city2 = tempCity;
            }
            totalDist+= distances.get(city2).get(city1);
            totalCost+= costs.get(city2).get(city1);
        }
        int city1 = path[0];    //the following lines calculates distance from last town back to first
        int city2 = path[47];
        if (city2 < city1){
            int tempCity = city1;
            city1 = city2;
            city2 = tempCity;
        }
        totalDist+= distances.get(city2).get(city1);
        totalCost+= costs.get(city2).get(city1); //the above lines calculates distance from last town back to first

        int[] distAndCost = new int[2];
        distAndCost[0] = totalDist;
        distAndCost[1] = totalCost;
        return distAndCost;
    }

    private static ArrayList readFiles(String filename) throws Exception{
        System.out.println("reading "+filename);
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
                if (k != 0) { //This is done to exclude the leftmost column
                    inner.add(((int) cell.getNumericCellValue()));
                }
                k++;
            }
            if ( i != 0) {//This is done to exclude the upper row
                outer.add(inner);
            }
            i++;
        }
        return outer;
    }
}
