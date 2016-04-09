After cloning the repo, please use ./run_tests.sh in directory insight_testsuite to run my code.
The only jar I used is "json-20140107.jar" checked in, in the src folder of repo.

Algorithm use to implement the Insight Coding Challenge:
1) The algorithm reads each line representing a new tweet from the tweet_input/tweets.txt and process the tweet to extract
the 'created_at' and hashtags of the tweet. Once these are extracted a new Tweet object with nodes as hashtags, edges and timestamp is created if the
hashtags are more than 1. If during extraction 'created_at' field is absent in tweet which can occur for track messages, the Tweet object is null and
 next line from tweets.txt is read.
2) We maintain three data structures that are essential for calculating the rolling average degree:
 i) edgeTimeStampMap: This Map contains lexically ordered pair of hashtags with the maximum timestamp at which this edge(or pair) was added in Graph.
 The pairs are all within the 60 second sliding window
 ii) nodeTimeStampMap: Set of all nodes that have occured in tweets with more than 1 hashtag in the current 60 second sliding window and the maximum timestamp
 at which a node occured.
 iii) minHeap: This maintains the Tweets ordered by timestamps of tweets.
 All these data structures are updated when a tweet with atleast two hashtags appears. Also the maximum timestamp observed so far is updated when a new tweet
 arrives. After this, the minHeap, nodeTimeStamp, edgeTimeStampMap are all updated to remove the entries from tweets older than 60 sec from max timeStamp.
 3) The rolling average is calculated as 2* size of edgeTimeStampMap/size of nodeTimeStampMap.

