package MOTSP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;

/**
 * Created by Olli on 08.05.2016.
 */
public class MPDominated implements Callable<ArrayList<ArrayList<MOTSP>>> {
    private ArrayList<MOTSP> population;
    private ArrayList<MOTSP> subpop;

        public MPDominated(ArrayList<MOTSP> population, ArrayList<MOTSP> subpop){
            this.population = population;
            this.subpop = subpop;
        }

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

        @Override
        public ArrayList<ArrayList<MOTSP>> call(){
            ArrayList<MOTSP> pFront = new ArrayList<MOTSP>();
            ArrayList<MOTSP> pBack = new ArrayList<MOTSP>();
            ArrayList<ArrayList<MOTSP>> result = new ArrayList<ArrayList<MOTSP>>();
            boolean isDominated;
            for (MOTSP s1: subpop){ //Maybe split this loop into threads?
                isDominated = false;
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


