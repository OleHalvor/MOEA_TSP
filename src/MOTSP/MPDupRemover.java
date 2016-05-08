package MOTSP;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by Olli on 08.05.2016.
 */
public class MPDupRemover implements Callable<ArrayList<MOTSP>> {

    private  ArrayList<MOTSP> children;
    private  ArrayList<MOTSP> population;
    private  ArrayList<MOTSP> subChild;

    public MPDupRemover(ArrayList<MOTSP> children, ArrayList<MOTSP> population, int number){
        this.children = children;
        this.population = population;
        if (number==0){
            this.subChild = new ArrayList<MOTSP>(children.subList(0,children.size()/4));
        }
        else
        if (number==1){
            this.subChild = new ArrayList<MOTSP>(children.subList(children.size()/4,children.size()/2));
        }
        else
        if (number==2){
            this.subChild = new ArrayList<MOTSP>(children.subList(children.size()/2,children.size()/4+children.size()/2));
        }
        else
        if (number==3){
            this.subChild = new ArrayList<MOTSP>(children.subList(children.size()/4+children.size()/2,children.size()));
        }
    }

    public ArrayList<MOTSP> call(){
        ArrayList<MOTSP> toBeRemoved = new ArrayList<MOTSP>();
        for (int i = 0; i < subChild.size(); i++) {
            for (int k = 0; k < children.size(); k++) {
                if (i != k) {
                    if (subChild.get(i).getDistance() == children.get(k).getDistance()) {
                        toBeRemoved.add(subChild.get(i));
                    }
                }
            }
            for (int k = 0; k < population.size(); k++) {
                if (i != k) {
                    if (subChild.get(i).getDistance() == population.get(k).getDistance()) {
                        toBeRemoved.add(subChild.get(i));
                    }
                }
            }
        }
        return toBeRemoved;
    }
}
