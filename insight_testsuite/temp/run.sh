cd src
javac -cp json-20140107.jar AverageDegree.java Tweet.java TweetComparator.java
java -cp json-20140107.jar:. AverageDegree ../tweet_input/tweets.txt ../tweet_output/output.txt
