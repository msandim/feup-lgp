package utils;

import java.util.*;

/**
 * Created by Miguel on 05-05-2016.
 */
public class MapUtils
{
    // Sorts a Map by Value in decreasing order
    public static <K,V extends Comparable<? super V>>
    List<Map.Entry<K, V>> orderByValueDecreasing(Map<K,V> map) {

        List<Map.Entry<K,V>> sortedEntries = new ArrayList<Map.Entry<K,V>>(map.entrySet());

        Collections.sort(sortedEntries,
                (e1, e2) -> e2.getValue().compareTo(e1.getValue())
        );

        return sortedEntries;
    }
}
