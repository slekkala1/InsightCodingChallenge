import java.util.Comparator;

/**
 * Created by swapnalekkala on 4/6/16.
 */

public class TweetComparator implements Comparator<Tweet> {
    @Override
    public int compare(Tweet o1, Tweet o2) {
        //check
        if (o1.getTimeStamp() - o2.getTimeStamp() < 0) {
            return -1;
        }
        if (o1.getTimeStamp() - o2.getTimeStamp() > 0) {
            return 1;
        }
        return 0;
    }
}

