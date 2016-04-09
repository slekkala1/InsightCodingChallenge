import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by swapnalekkala on 4/5/16.
 */
public class AverageDegree {

    public static Map<Pair<String, String>, Long> edgeTimeStampMap = new HashMap<>();
    public static Map<String, Long> nodeTimeStampMap = new HashMap<>();
    public static PriorityQueue<Tweet> minHeap = new PriorityQueue<>(10, new TweetComparator());

    public static long maxTimeStamp = 0;
    public static long SLIDING_WINDOW_TIME_IN_MILLIS = 60000;


    public static Tweet getTweetFromJson(String line) throws ParseException {
        Tweet newTweet = null;
        JSONObject jsonObj = new JSONObject(line);

        if (jsonObj.has("created_at")) {
            String createdAt = (String) jsonObj.get("created_at");

            String twitterDateFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterDateFormat, Locale.ENGLISH);
            sf.setLenient(true);
            Date date = sf.parse(createdAt);
            long timeInMillis = date.getTime();

            JSONObject entities = (JSONObject) jsonObj.get("entities");
            JSONArray list = (JSONArray) entities.get("hashtags");

            Set<String> hashtags = new TreeSet<>();
            for (int i = 0; i < list.length(); i++) {
                JSONObject ht = (JSONObject) list.get(i);
                String hts = (String) ht.get("text");
                hashtags.add(hts);
            }

            newTweet = new Tweet(timeInMillis, hashtags);
        }
        return newTweet;
    }

    public static boolean processLine(String line) throws ParseException {
        Tweet tweet = getTweetFromJson(line);
        if (tweet == null) {
            return false;
        }

        maxTimeStamp = Math.max(maxTimeStamp, tweet.getTimeStamp());
        if (!(maxTimeStamp - tweet.getTimeStamp() > SLIDING_WINDOW_TIME_IN_MILLIS)) {
            if (tweet.getNodes().size() <= 1) {
                return true;
            }
            for (String hashTag : tweet.getNodes()) {
                nodeTimeStampMap.put(hashTag, tweet.getTimeStamp());
            }
            generateEdges(tweet);
            minHeap.add(tweet);
        }

        if (!minHeap.isEmpty()) {
            updateMinHeap();
        }
        return true;
    }

    private static void updateMinHeap() {
        while (maxTimeStamp - minHeap.peek().getTimeStamp() >= SLIDING_WINDOW_TIME_IN_MILLIS) {
            Tweet removeTweet = minHeap.poll();
            removeNodes(removeTweet);
            removeEdges(removeTweet);
            if (minHeap.isEmpty()) break;
        }
    }

    private static void removeEdges(Tweet tweet) {
        long tweetTimeStamp = tweet.getTimeStamp();
        Set<Pair<String, String>> edgeSet = tweet.getEdgeList();

        for (Pair<String, String> hashtagEdge : edgeSet) {
            if (edgeTimeStampMap.containsKey(hashtagEdge)) {
                if (edgeTimeStampMap.get(hashtagEdge) <= tweetTimeStamp) {
                    edgeTimeStampMap.remove(hashtagEdge);
                }
            }
        }
    }

    private static void removeNodes(Tweet tweet) {
        long tweetTimeStamp = tweet.getTimeStamp();

        for (String hashtag : tweet.getNodes()) {
            if (nodeTimeStampMap.containsKey(hashtag)) {
                if (nodeTimeStampMap.get(hashtag) <= tweetTimeStamp) {
                    nodeTimeStampMap.remove(hashtag);
                }
            }
        }
    }

    private static void generateEdges(Tweet tweet) {
        for (Pair<String, String> edge : tweet.getEdgeList()) {
            if (edgeTimeStampMap.containsKey(edge)) {
                if (edgeTimeStampMap.get(edge) < tweet.getTimeStamp()) {
                    edgeTimeStampMap.put(edge, tweet.getTimeStamp());
                }
            } else {
                edgeTimeStampMap.put(edge, tweet.getTimeStamp());
            }
        }
    }

    public static double calculateRollingAverage() {
        double rollingAverage = 0.00;
        if (nodeTimeStampMap.size() != 0) {
            rollingAverage = (double) edgeTimeStampMap.size() * 2.00 / (double) nodeTimeStampMap.size();
        }
        return rollingAverage;
    }

    private static void writeOutputToFile(BufferedWriter pw, double averageDegree) {
        DecimalFormat df2 = new DecimalFormat("0.00");
        df2.setRoundingMode(RoundingMode.DOWN);

        try {
            pw.write(String.valueOf(df2.format(averageDegree)));
            pw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        FileInputStream fileInputStream = new FileInputStream(new File(args[0]));
        BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));

        File outputFile = new File(args[1]);
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        String line;
        while ((line = br.readLine()) != null) {
            boolean tweetOk = processLine(line);
            if (tweetOk) {
                double rollingAverage = calculateRollingAverage();
                writeOutputToFile(bw, rollingAverage);
            }
        }

        br.close();
        bw.flush();
        bw.close();
    }
}
