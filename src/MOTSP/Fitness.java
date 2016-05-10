package MOTSP;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;


public class Fitness {
    private static ArrayList<ArrayList<Integer>> distances = new ArrayList<ArrayList<Integer>>();
    private static ArrayList<ArrayList<Integer>> costs = new ArrayList<ArrayList<Integer>>();
    private static int probSize;

    public Fitness(){
        try {
            distances = readFiles("Distance.xlsx");
            costs = readFiles("Cost.xlsx");
        }catch (Exception e){e.printStackTrace();}
        probSize = distances.size();
    }

    public int getProblemLength(){
        System.out.println(distances.size());
        return distances.size();
    }

    public static int[] getDistanceAndCost(MOTSP individual){
        int[] path = individual.getGenome();
        int totalCost = 0, totalDist = 0;
        for (int i=0; i<probSize-1; i++){   //Loops over path and calculates distance and cost of each pair of cities
            int city1 = path[i], city2 = path[i+1];
            if (city2 < city1){
                int tempCity = city1;
                city1 = city2;
                city2 = tempCity;
            }
            totalDist += distances.get(city2).get(city1);
            totalCost += costs.get(city2).get(city1);
        }
        //the following lines calculates distance from last town back to first
        int city1 = path[0], city2 = path[probSize-1];
        if (city2 < city1){
            int tempCity = city1;
            city1 = city2;
            city2 = tempCity;
        }
        totalDist+= distances.get(city2).get(city1);
        totalCost+= costs.get(city2).get(city1);
        //the above lines calculates distance from last town back to first

        int[] distAndCost = new int[2];
        distAndCost[0] = totalDist;
        distAndCost[1] = totalCost;
        return distAndCost;
    }

    private static ArrayList readFiles(String filename) throws Exception{
        System.out.println("Loading "+filename);
        ArrayList<ArrayList<Integer>> outer = new ArrayList<ArrayList<Integer>>();
        int i = 0;
        int k;
        File myFile = new File(filename);
        FileInputStream fis = new FileInputStream(myFile);
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
