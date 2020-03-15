package myflink.kafka;

import com.alibaba.fastjson.JSON;
import myflink.pojo.UserBehavior;
import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author huangqingshi
 * @Date 2020-03-15
 */
public class KafkaWriter {

    //本地的kafka机器列表
    public static final String BROKER_LIST = "localhost:9092";
    //kafka的topic
    public static final String TOPIC_USER_BEHAVIOR = "user_behaviors";
    //key序列化的方式，采用字符串的形式
    public static final String KEY_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";
    //value的序列化的方式
    public static final String VALUE_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";

    private static final String[] BEHAVIORS = {"pv","buy","cart", "fav"};

    private static KafkaProducer<String, String> producer;

    public static void writeToKafka() throws Exception{


        //构建userBehavior, 数据都是随机产生的
        int randomInt = RandomUtils.nextInt(0, 4);
        UserBehavior userBehavior = new UserBehavior();
        userBehavior.setBehavior(BEHAVIORS[randomInt]);
        Long ranUserId = RandomUtils.nextLong(1, 10000);
        userBehavior.setUserId(ranUserId);
        int ranCate = RandomUtils.nextInt(1, 10);
        userBehavior.setCategoryId(ranCate);
        Long ranItemId = RandomUtils.nextLong(1, 100000);
        userBehavior.setItemId(ranItemId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        userBehavior.setTimestamp(sdf.format(new Date()));

        //转换为json
        String userBehaviorStr = JSON.toJSONString(userBehavior);

        //包装成kafka发送的记录
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC_USER_BEHAVIOR, null,
                null, userBehaviorStr);
        //发送到缓存
        producer.send(record);
        System.out.println("向kafka发送数据:" + userBehaviorStr);
        //立即发送
        producer.flush();

    }

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", BROKER_LIST);
        props.put("key.serializer", KEY_SERIALIZER);
        props.put("value.serializer", VALUE_SERIALIZER);

        producer = new KafkaProducer<>(props);

        while(true) {
            try {
                //每一秒写一条数据
                TimeUnit.SECONDS.sleep(1);
                writeToKafka();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
