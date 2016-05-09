package MOTSP;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.PrintWriter;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EALoop {
    private static int nGenerations = 1550, curGen = 1, popSize = 500;
    public static final double mutationRate = 0.05;
    public static final int printGraph = 50;
    private static ArrayList<MOTSP> population = new ArrayList<MOTSP>();
    private static Fitness fitness = new Fitness(); //this is needed to make Fitness.java load the distance and cost files

    //MultiProcessing Variables
    static ExecutorService service = Executors.newFixedThreadPool(4);
    static Future<ArrayList<MOTSP>> task;
    static Future<ArrayList<MOTSP>> task1;
    static Future<ArrayList<MOTSP>> task2;
    static Future<ArrayList<MOTSP>> task3;

    public static void main (String[] args){
        System.out.println("Starting EALoop");
        initPopulation();

        while (curGen <= nGenerations){


            //Calculate Pareto Fronts
            ArrayList<ArrayList<MOTSP>> paretoFronts = Pareto.generateParetoFronts(population);

            //Render visualization
            if (curGen % printGraph == 0) {
                System.out.println("Generation: "+curGen+" of "+nGenerations);//printing generation count
                plotAll(paretoFronts);
                //plotNonDominated(paretoFronts);
            }

            // Select Parents
            ArrayList<MOTSP> parents = ParentSelection(population, paretoFronts);

            //Make children
            ArrayList<MOTSP> children = makeChildren(parents);

            //Remove duplicate children
            children = removeDupes(children,population);

            //Select adults from parents and children
            population = adultSelection(population, children);

            //increment generation counter
            curGen += 1;
        }
        ArrayList<ArrayList<MOTSP>> paretoFronts = Pareto.generateParetoFronts(population);
        printFronts(paretoFronts);//print Pareto fronts
        Pareto.shutDown();
        service.shutdown();
        writeFrontToFile(paretoFronts.get(0));
        printBestWorst(population);
    }

    private static ArrayList<MOTSP> removeDupes(ArrayList<MOTSP> children,ArrayList<MOTSP> population){
        task     = service.submit(new MPDupRemover( children, population, 0));
        task1    = service.submit(new MPDupRemover( children, population, 1));
        task2    = service.submit(new MPDupRemover( children, population, 2));
        task3    = service.submit(new MPDupRemover( children, population, 3));
        ArrayList<MOTSP> toBeRemoved = new ArrayList<MOTSP>();
        try {
            toBeRemoved.addAll(task.get());
            toBeRemoved.addAll(task1.get());
            toBeRemoved.addAll(task2.get());
            toBeRemoved.addAll(task3.get());
        }catch (Exception e){e.printStackTrace();}

        children.removeAll(toBeRemoved);
        return children;
    }

    private static void initPopulation(){
        for (int n=0; n<popSize; n++){
            population.add(new MOTSP());
        }
    }

    private static ArrayList<MOTSP> crossOver(MOTSP p1, MOTSP p2){
        /*
        Crossover makes a new empty genome, filled with "-1"s
        Then it chooses a sequence of DNA from p1 and adds it to the new genome
        Then it fills the rest of the empty DNA with chromosomes from p2 in sequential order, as long as the chromosome is not already present.
         */
        int[] p1_gen = p1.getGenome(), p2_gen = p2.getGenome(), genome1 = new int[48], genome2 = new int[48];
        Arrays.fill(genome1, -1);         //Fills the genome with chromosomes of "-1"
        Arrays.fill(genome2, -1);
        Random rand = new Random();
        int cut1 = rand.nextInt(48);             //Start of dna sequence from parent 1
        int cut2 = rand.nextInt(48);        //End of dna sequence from parent 1
        if (cut1>cut2){
            int temp = cut1;
            cut1 = cut2;
            cut2 = temp;

        }
        for (int i = cut1; i<cut2; i++){         //Sets the sequence from parent 1 to child
            genome1[i] = p1_gen[i];
        }
        for (int i = cut1; i<cut2; i++){
            genome2[i] = p2_gen[i];
        }
        for (int i = 0; i <48; i++){ //Loop through new genome to fill empty spots of dna with chromosomes from p2 which are not present of the section from p1
            if (genome1[i]==-1){      //Indicates chromosome not set
                for (int k= 0; k<48; k++){ //Loop over p2 genome to find 1st value not in new genome
                    int p2_val = p2_gen[k];
                    boolean found = false;
                    for (int o=0; o<48; o++){   //This could be sped up with a .contains method..
                        if (genome1[o]==p2_val){
                            found = true;
                            break;
                        }
                    }
                    if (!found){
                        genome1[i] = p2_val;
                        break;
                    }
                }
            }
        }
        for (int i = 0; i <48; i++){ //Loop through new genome to fill empty spots of dna with chromosomes from p2 which are not present of the section from p1
            if (genome2[i]==-1){      //Indicates chromosome not set
                for (int k= 0; k<48; k++){ //Loop over p2 genome to find 1st value not in new genome
                    int p1_val = p1_gen[k];
                    boolean found = false;
                    for (int o=0; o<48; o++){   //This could be sped up with a .contains method..
                        if (genome2[o]==p1_val){
                            found = true;
                            break;
                        }
                    }
                    if (!found){
                        genome2[i] = p1_val;
                        break;
                    }
                }
            }
        }
        ArrayList<MOTSP> temp = new ArrayList<MOTSP>();
        temp.add(new MOTSP(genome1));
        temp.add(new MOTSP(genome2));
        return temp;
    }

    private static ArrayList<MOTSP> makeChildren(ArrayList<MOTSP> parents){
        ArrayList<MOTSP> children = new ArrayList<MOTSP>();
        ArrayList<MOTSP> temp = new ArrayList<MOTSP>();
        for (int n=0; n<parents.size()/2; n+=2){
            temp = crossOver(parents.get(n), parents.get(n+1));
            children.add(temp.get(0));
            children.add(temp.get(1));
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
                try{
                    Collections.sort(sortedArray, new CustomComparator());
                }catch (Exception e){
                }
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

    private static XYDataset createDatasetBest(ArrayList<ArrayList<MOTSP>> paretoFront) {
        XYSeriesCollection result = new XYSeriesCollection();
        XYSeries series2 = new XYSeries("Non-Dominated");
        ArrayList<MOTSP> pareto = paretoFront.get(0);
        for (int i = 0; i <= pareto.size()-1; i++) {
            double x = pareto.get(i).getDistance();
            double y = pareto.get(i).getCost();
            series2.add(x, y);
        }
        result.addSeries(series2);
        return result;
    }

    private static XYDataset createDatasetWorst() {
        MOTSP worstX = new MOTSP();
        int worstXI = 0;
        MOTSP worstY = new MOTSP();
        int worstYI = 0;
        for (MOTSP m : population){
            if (m.getDistance()>worstXI){
                worstX = m;
                worstXI = (int) m.getDistance();
            }
            if (m.getCost()>worstYI){
                worstY = m;
                worstYI = (int) m.getCost();
            }
        }
        XYSeriesCollection result = new XYSeriesCollection();
        XYSeries series2 = new XYSeries("Worst");
        series2.add(worstX.getDistance(), worstX.getCost());
        series2.add(worstY.getDistance(), worstY.getCost());
        result.addSeries(series2);
        return result;
    }

    private static XYDataset createDatasetAll() {
        XYSeriesCollection result = new XYSeriesCollection();
        XYSeries series = new XYSeries("Dominated");
        for (int i = 0; i <= population.size()-1; i++) {
            double x = population.get(i).getDistance();
            double y = population.get(i).getCost();
            series.add(x, y);
        }
        result.addSeries(series);
        return result;
    }

    private static void plotAll(ArrayList<ArrayList<MOTSP>> paretoFronts) {
        XYDataset best = createDatasetBest(paretoFronts);
        XYDataset all = createDatasetAll();
        XYDataset worst = createDatasetWorst();

        JFreeChart chart = ChartFactory.createScatterPlot(
                "Generation: "+curGen+", Mutation rate: "+mutationRate+", PopSize: "+popSize+", ParetoFront size: "+paretoFronts.get(0).size(), // chart title
                "Distance", // x axis label
                "Cost", // y axis label
                createDatasetAll(), // data  ***-----PROBLEM------***
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
        );
        XYPlot xyPlot = (XYPlot) chart.getPlot();
        xyPlot.setDataset(0, worst);
        xyPlot.setDataset(2, best);
        xyPlot.setDataset(1, all);

        XYLineAndShapeRenderer renderer0 = new XYLineAndShapeRenderer();
        XYDotRenderer renderer1 = new XYDotRenderer();
        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer();
        xyPlot.setRenderer(0, renderer2);
        xyPlot.setRenderer(1, renderer1);
        xyPlot.setRenderer(2, renderer0);
        xyPlot.getRendererForDataset(xyPlot.getDataset(2)).setSeriesPaint(0, Color.red);
        xyPlot.getRendererForDataset(xyPlot.getDataset(1)).setSeriesPaint(0, Color.blue);
        xyPlot.getRendererForDataset(xyPlot.getDataset(0)).setSeriesPaint(0, Color.black);
        renderer2.setSeriesLinesVisible(2,false);
        renderer2.setSeriesLinesVisible(1,false);
        renderer2.setSeriesLinesVisible(0,false);


        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        //domain.setRange(25000, 180000);
        domain.setVerticalTickLabels(true);
        NumberAxis range = (NumberAxis) xyPlot.getRangeAxis();
        //range.setRange(200, 2000);
        ChartFrame frame = new ChartFrame("MOTSP", chart);
        frame.pack();
        frame.setVisible(true);
    }

    private static void plotNonDominated(ArrayList<ArrayList<MOTSP>> paretoFronts) {
        XYDataset best = createDatasetBest(paretoFronts);
        XYDataset worst = createDatasetWorst();

        JFreeChart chart = ChartFactory.createScatterPlot(
                "Generation: "+curGen+", Mutation rate: "+mutationRate+", PopSize: "+popSize+", ParetoFront size: "+paretoFronts.get(0).size(), // chart title
                "Distance", // x axis label
                "Cost", // y axis label
                createDatasetAll(), // data  ***-----PROBLEM------***
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
        );
        XYPlot xyPlot = (XYPlot) chart.getPlot();
        xyPlot.setDataset(0, best);


        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer();
        xyPlot.setRenderer(0, renderer2);

        xyPlot.getRendererForDataset(xyPlot.getDataset(0)).setSeriesPaint(0, Color.red);
        renderer2.setSeriesLinesVisible(2,true);
        renderer2.setSeriesLinesVisible(0,true);
        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        //domain.setRange(25000, 180000);
        domain.setVerticalTickLabels(true);
        NumberAxis range = (NumberAxis) xyPlot.getRangeAxis();
        //range.setRange(200, 2000);
        ChartFrame frame = new ChartFrame("MOTSP", chart);
        frame.pack();
        frame.setVisible(true);
    }

    private static void writeFrontToFile(ArrayList<MOTSP> front){
        try {
            PrintWriter writer = new PrintWriter("pop"+popSize+"gen"+nGenerations+"mut"+mutationRate+".txt", "UTF-8");
            for (MOTSP m: front){
                writer.println(m.getDistance()+","+m.getCost());
            }
            writer.close();
        }catch (Exception e){e.printStackTrace();}
    }

    private static void printBestWorst(ArrayList<MOTSP> population ){
        MOTSP worstX = new MOTSP();
        int worstXI = 0;
        MOTSP worstY = new MOTSP();
        int worstYI = 0;
        MOTSP bestX = new MOTSP();
        int bestXI = 5000000;
        MOTSP bestY = new MOTSP();
        int bestYI = 5000000;
        for (MOTSP m : population){
            if (m.getDistance()>worstXI){
                worstX = m;
                worstXI = (int) m.getDistance();
            }
            if (m.getCost()>worstYI){
                worstY = m;
                worstYI = (int) m.getCost();
            }
            if (m.getDistance()<bestXI){
                bestX = m;
                bestXI = (int) m.getDistance();
            }
            if (m.getCost()<bestYI){
                bestY = m;
                bestYI = (int) m.getCost();
            }
        }
        System.out.println("Best distance: "+bestXI);
        System.out.println("Best cost: "+bestYI);
        System.out.println("Worst distance: "+worstXI);
        System.out.println("Worst cost: "+worstYI);
    }

}
