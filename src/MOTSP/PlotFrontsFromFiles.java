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
import java.awt.geom.Arc2D;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.Inflater;

/**
 * Created by Ole on 09.05.2016.
 */
public class PlotFrontsFromFiles {

    public static void main (String[] args){
        ArrayList<ArrayList<Double>> front0 = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> front1 = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> front2 = new ArrayList<ArrayList<Double>>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("pop100gen1000mut0.1.txt"));
            //StringBuilder sb = new StringBuilder();
            String line = br.readLine();


            while (line != null) {
                List<String> temp = Arrays.asList(line.split(","));
                ArrayList<Double> temp2 = new ArrayList<Double>();
                temp2.add(Double.valueOf (temp.get(0)));
                temp2.add(Double.valueOf(temp.get(1)));
                front0.add(temp2);
                line = br.readLine();
            }
        } catch (Exception e){e.printStackTrace();}
        try {
            br = new BufferedReader(new FileReader("pop1000gen100mut0.1.txt"));
            //StringBuilder sb = new StringBuilder();
            String line = br.readLine();


            while (line != null) {
                List<String> temp = Arrays.asList(line.split(","));
                ArrayList<Double> temp2 = new ArrayList<Double>();
                temp2.add(Double.valueOf (temp.get(0)));
                temp2.add(Double.valueOf(temp.get(1)));
                front1.add(temp2);
                line = br.readLine();
            }
        } catch (Exception e){e.printStackTrace();}
        try {
            br = new BufferedReader(new FileReader("pop2000gen100mut0.1.txt"));
            //StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                List<String> temp = Arrays.asList(line.split(","));
                ArrayList<Double> temp2 = new ArrayList<Double>();
                temp2.add(Double.valueOf (temp.get(0)));
                temp2.add(Double.valueOf(temp.get(1)));
                front2.add(temp2);
                line = br.readLine();
            }
        } catch (Exception e){e.printStackTrace();}
        plotAll(front0,front1,front2);


    }

    private static void plotAll(ArrayList<ArrayList<Double>> front0,ArrayList<ArrayList<Double>> front1,ArrayList<ArrayList<Double>> front2) {
        XYDataset front0X = createDatasetAll(front0,"0");
        XYDataset front1X = createDatasetAll(front1,"1");
        XYDataset front2X = createDatasetAll(front2,"2");

        JFreeChart chart = ChartFactory.createScatterPlot(
                "all pareto fronts", // chart title
                "Distance", // x axis label
                "Cost", // y axis label
                createDatasetAll(front0,"0"), // data  ***-----PROBLEM------***
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
        );
        XYPlot xyPlot = (XYPlot) chart.getPlot();
        xyPlot.setDataset(0, front0X);
        xyPlot.setDataset(1, front1X);
        xyPlot.setDataset(2, front2X);


        XYLineAndShapeRenderer renderer0 = new XYLineAndShapeRenderer();
        XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer();
        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer();
        xyPlot.setRenderer(0, renderer0);
        xyPlot.setRenderer(1, renderer1);
        xyPlot.setRenderer(2, renderer2);
        xyPlot.getRendererForDataset(xyPlot.getDataset(2)).setSeriesPaint(0, Color.red);
        xyPlot.getRendererForDataset(xyPlot.getDataset(1)).setSeriesPaint(0, Color.blue);
        xyPlot.getRendererForDataset(xyPlot.getDataset(0)).setSeriesPaint(0, Color.black);

        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        //domain.setRange(25000, 180000);
        domain.setVerticalTickLabels(true);
        NumberAxis range = (NumberAxis) xyPlot.getRangeAxis();
        //range.setRange(200, 2000);
        ChartFrame frame = new ChartFrame("MOTSP", chart);
        frame.pack();
        frame.setVisible(true);
    }

    private static XYDataset createDatasetAll(ArrayList<ArrayList<Double>> front, String name) {
        XYSeriesCollection result = new XYSeriesCollection();
        XYSeries series = new XYSeries(name);
        for (int i = 0; i <= front.size()-1; i++) {
            double x = front.get(i).get(0);
            double y = front.get(i).get(1);
            series.add(x, y);
        }
        result.addSeries(series);
        return result;
    }

}
