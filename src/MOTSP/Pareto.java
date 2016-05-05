package MOTSP;

import java.util.ArrayList;

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

    public static ArrayList<ArrayList<MOTSP>> generateParetoFronts (ArrayList<MOTSP> population){
        ArrayList<ArrayList<MOTSP>> fronts = new ArrayList<ArrayList<MOTSP>>();
        ArrayList<ArrayList<MOTSP>> tempFronts = extractNonDominated(population);
        while (true){
            fronts.add(tempFronts.get(0));
            tempFronts = extractNonDominated(tempFronts.get(1));
            if ((tempFronts.get(0).size()==0))
                break;
        }
        for (int i=0; i<fronts.size(); i++){
            for (MOTSP m : fronts.get(i).setParetoRank(i));
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
            if (!isDominated)
                pFront.add(s1);
            else
                pBack.add(s1);
        }
        result.add(pFront);
        result.add(pBack);
        return result;
    }
}
