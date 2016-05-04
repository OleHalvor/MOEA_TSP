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


    public static ArrayList<ArrayList<MOTSP>> getParetoFronts (ArrayList<MOTSP> population){
        ArrayList<ArrayList<MOTSP>> fronts = new ArrayList<ArrayList<MOTSP>>();
        boolean done = false;
        ArrayList<ArrayList<MOTSP>> temp = extractNonDominated(population);
        ArrayList<MOTSP> remaining;
        while (!done){
            fronts.add(temp.get(0));
            remaining = temp.get(1);
            temp = extractNonDominated(remaining);
            if ((temp.get(0).size()==0)||(temp.get(1).size()==0)){
                done = true;
            }
        }
        return fronts;
    }

    public static ArrayList<ArrayList<MOTSP>> extractNonDominated(ArrayList<MOTSP> population){
        ArrayList<MOTSP> pFront = new ArrayList<MOTSP>();
        ArrayList<MOTSP> pBack = new ArrayList<MOTSP>();
        ArrayList<ArrayList<MOTSP>> result = new ArrayList<ArrayList<MOTSP>>();

        for (MOTSP s1: population){
            boolean isDominated = false;
            for (MOTSP s2: population){
                if (dominates(s2,s1)) {
                    isDominated = true;
                    break;
                }
            }
            if (!isDominated){
                pFront.add(s1);
            }
            else {
                pBack.add(s1);
            }
        }

        result.add(pFront);
        result.add(pBack);
        return result;
    }
}
