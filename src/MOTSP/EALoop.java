package MOTSP;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ole on 03.05.2016.
 */
public class EALoop {
    private static final int nGenerations = 100;
    private static int curGen = 0;


    public static void main (String[] args){
        try {
            Workbook wb = WorkbookFactory.create(new File("Distance.xlsx"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("hello");
        while (curGen < nGenerations ){
            //TODO: DO LOOP STUFF
            curGen ++;
        }

    }
}
