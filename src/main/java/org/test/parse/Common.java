package org.test.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Common {
	public static String readResource(String filePath, String encoding) {
		StringBuffer fileContent = new StringBuffer();
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					fileContent.append(lineTxt);
				}
				read.close();
				bufferedReader.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		} finally {
			
		}

		return fileContent.toString();
	}

	public static void log(String msg){
		System.out.print(msg);
	}
	
	public static void logln(String msg){
		System.out.println(msg);
	}
	
	public static boolean isEmpty(String str){
		if(str == null || str == ""){
			return true;
		}
		
		return false;
	}
}
