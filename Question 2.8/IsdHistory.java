package ecp.Lab1.Question28;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class IsdHistory extends Configured implements Tool {
   public static void main(String[] args) throws Exception {
      System.out.println(Arrays.toString(args));
      int res = ToolRunner.run(new Configuration(), new IsdHistory(), args);
      
      System.exit(res);
   }

   @Override
   public int run(String[] args) throws Exception {
      System.out.println(Arrays.toString(args));
      Configuration configuration = this.getConf();

      @SuppressWarnings("deprecation")
	Job job = new Job(configuration, "Isd-History");
      job.setNumReduceTasks(2);
      job.setJarByClass(IsdHistory.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(IntWritable.class);

      job.setMapperClass(Map.class);
      job.setReducerClass(Reduce.class);

      job.setInputFormatClass(TextInputFormat.class);
      job.setOutputFormatClass(TextOutputFormat.class);

      FileInputFormat.addInputPath(job, new Path("inputPath"));
      FileOutputFormat.setOutputPath(job, new Path("outputPath"));
      FileSystem hdfs = FileSystem.get(getConf());
 	  if (hdfs.exists(new Path("outputPath")))
 	      hdfs.delete(new Path("outputPath"), true);
      job.waitForCompletion(true);
      
      return 0;
   }
   
   public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
      private final static IntWritable ONE = new IntWritable(1);
      private Text word = new Text();

      @Override
      public void map(LongWritable key, Text value, Context context)
              throws IOException, InterruptedException {
    	  
    	  String line= value.toString();
    	  String stationName="";
    	  String FIPS = "";
    	 	String altitude = "";
    	  
    	  if(line.length()>41){
    		  stationName = line.substring(13, 42).trim(); //Names of station start at character 13 of a line
    	  }
    	  
    	  if(line.length()>44){
    		  FIPS = line.substring(43, 45).trim(); // FIPS begin at character 43 of a line 
    	  }
    	  
    	  if(line.length()>80){
    		  altitude = line.substring(74, 81).trim(); // The altitude starts at character 74 of a line 
    	  }
    	  
    	  	 
    	  
    	  if(key.get()>21) // Ignore first 22 lines
    		  System.out.println("Name of station : " + stationName + " FIPS : "+FIPS + " Altitude : "+ altitude);
   
      }
   }

   public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
      @Override
      public void reduce(Text key, Iterable<IntWritable> values, Context context)
              throws IOException, InterruptedException {
       
      }
   }
}