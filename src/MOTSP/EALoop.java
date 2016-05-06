package MOTSP;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;

public class EALoop {
    private static int nGenerations = 500, curGen = 1, popSize = 5000;
    public static final double mutationRate = 0.2;
    private static ArrayList<MOTSP> population = new ArrayList<MOTSP>();
    private static Fitness fitness = new Fitness(); //this is needed to make Fitness.java load the distance and cost files
    private static boolean removeDupes = true;

    public static void main (String[] args){
        System.out.println("Starting EALoop");
        initPopulation();

        while (curGen <= nGenerations){
            System.out.println("Generation: "+curGen+" of "+nGenerations);//printing generation count

            //Calculate Pareto Fronts
            ArrayList<ArrayList<MOTSP>> paretoFronts = Pareto.generateParetoFronts(population);
            //printFronts(paretoFronts);//print Pareto fronts

            // Select Parents
            ArrayList<MOTSP> parents = ParentSelection(population, paretoFronts);


            //Make children
            ArrayList<MOTSP> children = makeChildren(parents);

            if (removeDupes) {
                ArrayList<MOTSP> toBeRemoved = new ArrayList<MOTSP>();
                for (int i = 0; i < children.size(); i++) {
                    for (int k = 0; k < children.size(); k++) {
                        if (i != k) {
                            if (children.get(i).getDistance() == children.get(k).getDistance()) {
                                toBeRemoved.add(children.get(k));
                            }
                        }
                    }
                }
                children.removeAll(toBeRemoved);
            }

            //Select adults from parents and children
            population = adultSelection(population, children);

            //increment generation counter
            curGen += 1;
        }
        ArrayList<ArrayList<MOTSP>> paretoFronts = Pareto.generateParetoFronts(population);
        printFronts(paretoFronts);//print Pareto fronts
        plot();
    }

    private static int getNumberOfDups(ArrayList<MOTSP> m){
        int count = 0;
        for (int i=0; i<m.size(); i++){
            for (int k=0; k<m.size(); k++){
                if (i!=k){
                    if ((m.get(i).getDistance()==m.get(k).getDistance())||(m.get(i).getCost()==m.get(k).getCost())) count++;
                }
            }
        }
        return count;
    }

    private static void initPopulation(){
        for (int n=0; n<popSize; n++){
            population.add(new MOTSP());
        }
    }

    private static MOTSP crossOver(MOTSP p1, MOTSP p2){
        /*
        Crossover makes a new empty genome, filled with "-1"s
        Then it chooses a sequence of DNA from p1 and adds it to the new genome
        Then it fills the rest of the empty DNA with chromosomes from p2 in sequential order, as long as the chromosome is not already present.
         */
        int[] p1_gen = p1.getGenome(), p2_gen = p2.getGenome(), genome = new int[48];
        Arrays.fill(genome, -1);         //Fills the genome with chromosomes of "-1"
        Random rand = new Random();
        int cut1 = rand.nextInt(48);             //Start of dna sequence from parent 1
        int cut2 = rand.nextInt(48-cut1)+cut1;   //End of dna sequence from parent 1
        for (int i = cut1; i<cut2; i++){         //Sets the sequence from parent 1 to child
            genome[i] = p1_gen[i];
        }
        for (int i = 0; i <48; i++){ //Loop through new genome to fill empty spots of dna with chromosomes from p2 which are not present of the section from p1
            if (genome[i]==-1){      //Indicates chromosome not set
                for (int k= 0; k<48; k++){ //Loop over p2 genome to find 1st value not in new genome
                    int p2_val = p2_gen[k];
                    boolean found = false;
                    for (int o=0; o<48; o++){   //This could be sped up with a .contains method..
                        if (genome[o]==p2_val){
                            found = true;
                            break;
                        }
                    }
                    if (!found){
                        genome[i] = p2_val;
                        break;
                    }
                }
            }
        }
        return new MOTSP(genome);
    }

    private static ArrayList<MOTSP> makeChildren(ArrayList<MOTSP> parents){
        ArrayList<MOTSP> children = new ArrayList<MOTSP>();
        for (int n=0; n<parents.size(); n+=2){
            children.add(crossOver(parents.get(n), parents.get(n+1)));
        }
        return children;
    }

    private static ArrayList<MOTSP> ParentSelection(ArrayList<MOTSP> mostps, ArrayList<ArrayList<MOTSP>> paretoList){
        Random rng = new Random();
        ArrayList<MOTSP> parents = new ArrayList<MOTSP>();
        //
        for (int n=0; n<popSize*2; n++) {
            MOTSP parentOne = mostps.get(rng.nextInt(mostps.size()));
            MOTSP parentTwo = mostps.get(rng.nextInt(mostps.size()));
            //
            if (parentOne.getParetoRank() < parentTwo.getParetoRank()) {
                parents.add(parentOne);
            } else if (parentTwo.getParetoRank() < parentOne.getParetoRank()) {
                parents.add(parentTwo);
            } else {
                double oneDist = crowdingDistance(parentOne, paretoList.get(parentOne.getParetoRank()));
                double twoDist = crowdingDistance(parentTwo, paretoList.get(parentTwo.getParetoRank()));
                if (oneDist < twoDist) {
                    parents.add(parentOne);
                } else {
                    parents.add(parentTwo);
                }
            }
        }
        return parents;
    }

    private static ArrayList<MOTSP> adultSelection(ArrayList<MOTSP> parents, ArrayList<MOTSP> children){
        parents.addAll(children);
        ArrayList<ArrayList<MOTSP>> pareto = Pareto.generateParetoFronts(parents);
        for (MOTSP m : parents){
            m.setCrowdingDistance(crowdingDistance(m, pareto.get(m.getParetoRank())));
        }
        //
        ArrayList<MOTSP> newPopulation = new ArrayList<MOTSP>();
        int n=popSize;
        int index = 0;
        while(n > 0){
            if (pareto.get(index).size() <= n){
                newPopulation.addAll(pareto.get(index));
                n -= pareto.get(index).size();
            }
            else {
                ArrayList<MOTSP> sortedArray = pareto.get(index);
                Collections.sort(sortedArray, new CustomComparator());
                int index2 = 0;
                while(n > 0){
                    newPopulation.add(sortedArray.get(index2));
                    n -= 1;
                    index2 += 1;
                }
            }
            index += 1;
        }
        return newPopulation;
    }

    private static double crowdingDistance(MOTSP m, ArrayList<MOTSP> pareto){
        MOTSP closeHighDist = new MOTSP();
        MOTSP closeLowDist = new MOTSP();
        //
        Double distHigh = Double.MAX_VALUE;
        Double distLow = Double.MAX_VALUE;
        //
        for (MOTSP motsp : pareto){
            if (motsp.getDistance() < m.getDistance()){
                double dist = Math.sqrt( Math.pow(m.getDistance()-motsp.getDistance(),2) + Math.pow(m.getCost()-motsp.getCost(),2) );
                if (dist < distLow){
                    distLow = dist;
                    closeLowDist = motsp;
                }
            }
            else{
                double dist = Math.sqrt( Math.pow(motsp.getDistance()-m.getDistance(),2) + Math.pow(motsp.getCost()-m.getCost(),2) );
                if (dist < distHigh){
                    distHigh = dist;
                    closeHighDist = motsp;
                }
            }

        }
        return Math.sqrt( Math.pow(closeHighDist.getDistance()-closeLowDist.getDistance(),2) + Math.pow(closeHighDist.getCost()-closeLowDist.getCost(),2) );
    }

    private static void printFronts(ArrayList<ArrayList<MOTSP>> paretoFronts){
        //for (int i =0; i<paretoFronts.size(); i++){
            for ( int k=0; k<paretoFronts.get(0).size(); k++){
                System.out.println("Front "+0+" Solution #"+k+"(distance,cost): "+paretoFronts.get(0).get(k).getDistance()+", "+paretoFronts.get(0).get(k).getCost() );
            }
      //  }
    }

    private static XYDataset createDataset() {
        XYSeriesCollection result = new XYSeriesCollection();
        XYSeries series = new XYSeries("MOTSP");
        for (int i = 0; i <= population.size()-1; i++) {
            double x = population.get(i).getDistance();
            double y = population.get(i).getCost();
            series.add(x, y);
        }
        result.addSeries(series);
        return result;
    }

    private static void plot() {
        JFreeChart chart = ChartFactory.createScatterPlot(
                "nGenerations: "+nGenerations+", Mutation rate: "+mutationRate+", PopSize: "+popSize, // chart title
                "Distance", // x axis label
                "Cost", // y axis label
                createDataset(), // data  ***-----PROBLEM------***
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
        );
        ChartFrame frame = new ChartFrame("First", chart);
        frame.pack();
        frame.setVisible(true);
    }
}
