package myflink.sql;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.sinks.CsvTableSink;
import org.apache.flink.table.sinks.TableSink;

public class SQLFromFile {

    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        BatchTableEnvironment tableEnv = BatchTableEnvironment.getTableEnvironment(env);

        env.setParallelism(1);
        //读取文件
        DataSource<String> input = env.readTextFile("test.txt");
        //将读取到的文件进行输出
        input.print();
        //转换为DataSet
        DataSet<Orders> inputDataSet = input.map(new MapFunction<String, Orders>() {
            @Override
            public Orders map(String s) throws Exception {
                String[] splits = s.split(" ");
                return Orders.of(Integer.valueOf(splits[0]), String.valueOf(splits[1]), String.valueOf(splits[2]), Double.valueOf(splits[3]));
            }
        });
        //转换为table
        Table order = tableEnv.fromDataSet(inputDataSet);
        //注册Orders表名
        tableEnv.registerTable("Orders", order);
        Table nameResult = tableEnv.scan("Orders").select("name");
        //输出一下表
        nameResult.printSchema();

        //执行一下查询
        Table sqlQueryResult = tableEnv.sqlQuery("select name, sum(price) as total from Orders group by name order by total desc");
        //查询结果转换为DataSet
        DataSet<Result> result = tableEnv.toDataSet(sqlQueryResult, Result.class);
        result.print();

        //以tuple的方式进行输出
        result.map(new MapFunction<Result, Tuple2<String, Double>>() {
            @Override
            public Tuple2<String, Double> map(Result result) throws Exception {
                String name = result.name;
                Double total = result.total;
                return Tuple2.of(name, total);
            }
        }).print();

        TableSink sink  = new CsvTableSink("SQLText.txt", " | ");

        //设置字段名
        String[] filedNames = {"name", "total"};
        //设置字段类型
        TypeInformation[] filedTypes = {Types.STRING(), Types.DOUBLE()};

        tableEnv.registerTableSink("SQLTEXT", filedNames, filedTypes, sink);

        sqlQueryResult.insertInto("SQLTEXT");

        env.execute();

    }

    public static class Orders {
        public Integer id;
        public String name;
        public String book;
        public Double price;

        public Orders() {
            super();
        }

        public static Orders of(Integer id, String name, String book, Double price) {
            Orders orders = new Orders();
            orders.id = id;
            orders.name = name;
            orders.book = book;
            orders.price = price;
            return orders;
        }
    }

    public static class Result {
        public String name;
        public Double total;

        public Result() {
            super();
        }

        public static Result of(String name, Double total) {
            Result result = new Result();
            result.name = name;
            result.total = total;
            return result;
        }
    }

}
