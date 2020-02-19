package myflink.sql;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.BatchTableEnvironment;


public class WordCountSQL {

    public static void main(String[] args) throws Exception {

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tEnv = BatchTableEnvironment.getTableEnvironment(env);

        DataSet<WC> input = env.fromElements(
                WC.of("hello", 1),
                WC.of("hqs", 1),
                WC.of("world", 1),
                WC.of("hello", 1)
        );
        //注册数据集
        tEnv.registerDataSet("WordCount", input, "word, frequency");

        //执行SQL，并结果集做为一个新表
        Table table = tEnv.sqlQuery("SELECT word, SUM(frequency) as frequency FROM WordCount GROUP BY word");

        DataSet<WC> result = tEnv.toDataSet(table, WC.class);

        result.print();

    }

    public static class WC {
        public String word; //hello
        public long frequency;

        //创建构造方法，让flink进行实例化
        public WC() {}

        public static WC of(String word, long frequency) {
            WC wc = new WC();
            wc.word = word;
            wc.frequency = frequency;
            return wc;
        }

        @Override
        public String toString() {
            return "WC " + word + " " + frequency;
        }
    }

}
