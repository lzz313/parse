package org.test.parse;

import java.text.ParseException;

public final class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length < 1){
			Common.logln("请输入功能参数:");
			Common.logln("\t-f 数据过滤");
			Common.logln("\t-l 查找丢失数据");
			Common.logln("\t-t 转换数据");
			return;
		}
		
		if("-f".equals(args[0])){
			HtmlTableParser.run(args);
		} 

		if("-l".equals(args[0])){
			try {
				FindLoseData.run(args);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} 
		
		if("-t".equals(args[0])){
			AnalysisLog.run(args);
		}

	}

}
