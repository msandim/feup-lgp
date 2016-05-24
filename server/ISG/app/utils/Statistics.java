package utils;

import java.util.List;

/**
 * Created by Miguel on 13-05-2016.
 * Source by:http://stackoverflow.com/questions/7988486/how-do-you-calculate-the-variance-median-and-standard-deviation-in-c-or-java
 */
public class Statistics
{
    static public Float getMean(List<Float> data)
    {
        if (data.isEmpty())
            return (float) 0.0;

        Float sum = (float) 0.0;
        for(Float a : data)
            sum += a;
        return sum/data.size();
    }

    static public float getVariance(List<Float> data)
    {
        if (data.isEmpty())
            return (float) 0.0;

        Float mean = getMean(data);
        Float temp = (float) 0.0;

        for(Float a :data)
            temp += (mean-a)*(mean-a);

        return temp/data.size();
    }
}
