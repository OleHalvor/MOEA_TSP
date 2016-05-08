package MOTSP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
            for (MOTSP m : fronts.get(i)){
                m.setParetoRank(i);
            }
        }
        return fronts;
    }
    static ExecutorService service = Executors.newFixedThreadPool(4);
    static Future<ArrayList<ArrayList<MOTSP>>> task;
    static Future<ArrayList<ArrayList<MOTSP>>> task1;
    static Future<ArrayList<ArrayList<MOTSP>>> task2;
    static Future<ArrayList<ArrayList<MOTSP>>> task3;

    public static void shutDown(){
        service.shutdown();
    }

    static ArrayList<MOTSP> pFront;
    static  ArrayList<MOTSP> pBack;
    static ArrayList<ArrayList<MOTSP>> result;
    static ArrayList<MOTSP> temp;
    static ArrayList<MOTSP> temp1;
    static ArrayList<MOTSP> temp2;
    static ArrayList<MOTSP> temp3;

    public static ArrayList<ArrayList<MOTSP>> extractNonDominated(ArrayList<MOTSP> population){
        pFront = new ArrayList<MOTSP>();
        pBack = new ArrayList<MOTSP>();
        result = new ArrayList<ArrayList<MOTSP>>();
        temp = new ArrayList<MOTSP> (population.subList(0,population.size()/4));
        temp1 = new ArrayList<MOTSP> (population.subList(population.size()/4,population.size()/2));
        temp2 = new ArrayList<MOTSP> (population.subList(population.size()/2,(population.size()/2)+population.size()/4));
        temp3 = new ArrayList<MOTSP> (population.subList((population.size()/2)+population.size()/4,population.size()));

        task    = service.submit(new MPDominated(population, temp));
        task1    = service.submit(new MPDominated(population, temp1));
        task2    = service.submit(new MPDominated(population, temp2));
        task3    = service.submit(new MPDominated(population, temp3));

        try {
            pFront.addAll(task.get().get(0));
            pBack.addAll(task.get().get(1));
            pFront.addAll(task1.get().get(0));
            pBack.addAll(task1.get().get(1));
            pFront.addAll(task2.get().get(0));
            pBack.addAll(task2.get().get(1));
            pFront.addAll(task3.get().get(0));
            pBack.addAll(task3.get().get(1));
        }catch (Exception e){e.printStackTrace();}
        /*boolean isDominated;
        for (MOTSP s1: population){ //Maybe split this loop into threads?
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
        }*/

        result.add(pFront);
        result.add(pBack);
        return result;
    }
}
