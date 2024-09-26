import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import redis.clients.jedis.Jedis;
import scala.Tuple2;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.spark.SparkConf;
import RedisUtil;
public class RealtimeLogAnalysis {
    static final String topic = "flume2kafka";
    static final String brokerList = "localhost:9092";
    static final String zkQuorum = "localhost:2181";
    static final String NUM_THREAD = "64";
    static final String group = "g001";

    public static void main(String args[]) throws InterruptedException {
        final SparkConf conf = new SparkConf().setAppName("RealtimeLogAnalysis").setMaster("local[*]");
        final JavaStreamingContext jssc = new JavaStreamingContext(conf, Duration.apply(10000));
        int numThreads = Integer.parseInt(NUM_THREAD);
        Map<String, Integer> topicMap = new HashMap<String, Integer>();
        String[] topics = topic.split(",");
        for (String topic : topics) {
            topicMap.put(topic, numThreads);
        }
    }

    JavaPairReceiverInputDStream<String, String> messages =
            KafkaUtils.createStream(jssc, zkQuorum, group, topicMap);
    JavaDStream<String> lines = messages.map(new Function<Tuple2<String, String>, String>() {

        public String call(Tuple2<String, String> tuple2) {
            return tuple2._2();
        }
    });
  lines.print(100);
    JavaPairDStream<String, Integer> map = lines.mapToPair(new PairFunction<String, String, Integer>() {
        @Override
        public Tuple2<String, Integer> call(String s) throws Exception {
            if (s.split(",")[1] != "user_id") {
                return new Tuple2<String, Integer>(s.split(",")[1], 1);
            }
            return null;
        }
    });
    JavaPairDStream<String, Integer> mapreduce = map.reduceByKey(new Fuction2<Integer, Integer, Integer>() {
        public Integer call(Integer i1, Integer i2) throws Exception {
            return i1 + i2;
        }
    });
  mapreduce.foreachRDD(new VoidFunction<JavaPairRDD<String, Integer>>()

    {

        @Override
        public void call (JavaPaiRDD < String, Integer > arg0) throws Exception {
        arg0.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Integer>>>() {
            @Override
            public void call(Iterator<Tuple2<String, Integer>> arg0) throws Exception {
                Jedis jedis = RedisUtil.getJedis();
                while (arg0.hasNext()) {
                    Tuple2<String, Integer> resultT = arg0.next();
                    System.out.println(resultT._1 + "-" + resultT._2);
                    jedis.incrByFloat(resultT._1, resultT._2);
                }
                if (jedis.isConnected())
                    RedisUtil.returnResource(jedis);
            }
        });
    }
    }
    );
  jssr.start()
          try{
        jssr.awaitTermination();

    }catch(InterruptedException e){e.printStackTrace();}finally{}
}