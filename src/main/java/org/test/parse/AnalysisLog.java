package org.test.parse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AnalysisLog {
	
	public static void analysis(String filePath,String encoding){
		List<String> dataList = Common.readLog(filePath, encoding);
		
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("MM-dd HH:mm:ss");
		
		String line,nextLine;
		for (int i = 0; i < dataList.size(); i++) {
			line = dataList.get(i);
			if(i<dataList.size()-1){
				nextLine = dataList.get(i+1);
			} else {
				nextLine = "";
			}
			if(line.startsWith("N:")){
				String lastTwo;
				if(nextLine.startsWith("A:")){
					lastTwo = getLastTwoItme(nextLine.split("\\ ")[3].split(","));
				}else{
					lastTwo = "0#0";
				}
				
				if(line.split("\\ ").length <3){
					continue;
				}
				
				String date = line.split("\\ ")[1]+" "+line.split("\\ ")[2];
				Calendar cal = Calendar.getInstance();
				try {
					cal.setTime(sdf.parse(date));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				cal.add(Calendar.HOUR_OF_DAY, 8);
				date = sdf.format(cal.getTime());
					
				String [] firstArr = line.split("\\ ")[3].split(",");
				
				for (int j = 0; j < firstArr.length; j++) {
					if(j==0){
						Common.log(line.split("\\ ")[1]+ "#" +line.split("\\ ")[2]+"#"+date.replace(" ", "#")+"#");
					} else {
						Common.log("###");
					}
					Common.logln(firstArr[j]+"#"+lastTwo);
				}
				
			}
		}
 	}
	
	public static String getLastTwoItme(String [] temp){
		int length = temp.length;
		return temp[length-2]+"#" +temp[length-1];
	}
	
	public static void run(String [] args){
		
		if(args.length < 3){
			Common.logln("列数据从0开始");
			Common.logln("参数2为文件路径");
			Common.logln("参数3为文件编码");
			Common.logln("例子:java -jar parse.jar -t \"E://1.txt\" GBK");
			
			return; 
		}
		
		String filePath = args[1];
		String fileEncoding = args[2];
		analysis(filePath, fileEncoding);
	}
}
