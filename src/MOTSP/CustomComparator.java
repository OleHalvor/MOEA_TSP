package MOTSP;

import java.util.Comparator;

/**
 * Created by Ole on 05.05.2016.
 */
public class CustomComparator implements Comparator<MOTSP> {
    @Override
    public int compare(MOTSP o1, MOTSP o2) {
        return (int)(o1.getCrowdingDistance() - o2.getCrowdingDistance());
    }
}
