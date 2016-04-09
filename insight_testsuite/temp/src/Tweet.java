import javafx.util.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by swapnalekkala on 4/6/16.
 */
public class Tweet {

    private long timeStamp;
    private Set<String> nodes;
    private Set<Pair<String, String>> edgeSet = new HashSet<>();

    public Tweet(long timeInMillis, Set<String> hashtags) {
        timeStamp = timeInMillis;
        nodes = hashtags;
        edgeSet = createEdgeList();
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Set<String> getNodes() {
        return nodes;
    }

    public void setNodes(Set<String> nodes) {
        this.nodes = nodes;
    }

    private Set<Pair<String, String>> createEdgeList() {
        String[] result = this.nodes.toArray(new String[this.nodes.size()]);
        for (int i = 0; i < result.length; i++) {
            for (int j = i + 1; j < result.length; j++) {
                Pair<String, String> pair = new Pair<String, String>(result[i], result[j]);
                edgeSet.add(pair);
            }
        }
        return edgeSet;
    }

    public Set<Pair<String, String>> getEdgeList() {
        return edgeSet;
    }
}
