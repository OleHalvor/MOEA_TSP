package MOTSP;

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
import java.util.Iterator;

/**
 * Created by Ole on 03.05.2016.
 */
public class EALoop {
    private static final int nGenerations = 100;
    private static int curGen = 0;


    public static void main (String[] args){
        ArrayList<ArrayList> test = new ArrayList<ArrayList>();
        try {
            test = readFiles("Distance.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(test.get(2).get(0));

        System.out.println("hello");
        while (curGen < nGenerations ){
            //TODO: DO LOOP STUFF
            curGen ++;
        }

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
