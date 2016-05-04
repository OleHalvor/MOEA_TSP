package MOTSP;

import java.util.ArrayList;

/**
 * Created by Olli on 04.05.2016.
 */
public class Pareto {

    public static boolean dominates(MOTSP i1, MOTSP i2){ //Checks if i1 dominates i2
        if (i1.getCost() < i2.getCost()){
            if (i1.getDistance() <= i2.getDistance())
                return true;
        }
        if (i1.getCost() <= i2.getCost()){
            if (i1.getDistance() < i2.getDistance())
                return true;
        }
        return false;
    }

    private static ArrayList<ArrayList<MOTSP>> getParetoSets(ArrayList<MOTSP> solutions){
        ArrayList<MOTSP> pFront = new ArrayList<MOTSP>();
        ArrayList<MOTSP> pBack = new ArrayList<MOTSP>();
        ArrayList<ArrayList<MOTSP>> result = new ArrayList<ArrayList<MOTSP>>();




        result.add(pFront);
        result.add(pBack);
        return result;
    }
}
