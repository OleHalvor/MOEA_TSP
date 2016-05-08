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


    public static void shutDown(){
        service.shutdown();
    }
    static ExecutorService service = Executors.newFixedThreadPool(4);
    static Future<ArrayList<ArrayList<MOTSP>>> task;
    static Future<ArrayList<ArrayList<MOTSP>>> task1;
    static Future<ArrayList<ArrayList<MOTSP>>> task2;
    static Future<ArrayList<ArrayList<MOTSP>>> task3;
    static ArrayList<MOTSP> pFront;
    static ArrayList<MOTSP> pBack;
    static ArrayList<ArrayList<MOTSP>> result;

    public static ArrayList<ArrayList<MOTSP>> extractNonDominated(ArrayList<MOTSP> population){
        pFront = new ArrayList<MOTSP>();
        pBack = new ArrayList<MOTSP>();
        result = new ArrayList<ArrayList<MOTSP>>();

        task     = service.submit(new MPDominated(population, new ArrayList<MOTSP> (population.subList(0,population.size()/4))));
        task1    = service.submit(new MPDominated(population, new ArrayList<MOTSP> (population.subList(population.size()/4,population.size()/2))));
        task2    = service.submit(new MPDominated(population, new ArrayList<MOTSP> (population.subList(population.size()/2,(population.size()/2)+population.size()/4))));
        task3    = service.submit(new MPDominated(population, new ArrayList<MOTSP> (population.subList((population.size()/2)+population.size()/4,population.size()))));

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

        result.add(pFront);
        result.add(pBack);
        return result;
    }
}
