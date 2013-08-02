package org.test.parse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class FindLoseData {
	
	public static void parseTable2(String content,int sortCol,int col) throws ParseException {
		NodeFilter tableFilter = new NodeClassFilter(TableTag.class);
		Node[] nodes = htmlToNode(content, new NodeFilter[] { tableFilter });
		
		int msgRefId;
		String dateStr;
		MsgRef msgRef;
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		List<MsgRef> msgList = new ArrayList<MsgRef>();
		for (Node node : nodes) {
			TableTag table = (TableTag)node;
			TableRow[] rows = table.getRows();
			
			//遍历每行
            for (int r=8; r<rows.length; r++) {
                TableRow tr = rows[r];
                TableColumn[] td = tr.getColumns();
                
                try{
	                msgRefId = Integer.valueOf(td[col].toPlainTextString().replaceAll("&nbsp;", ""));
	                dateStr = String.valueOf(td[sortCol].toPlainTextString().replaceAll("&nbsp;", " "));
	                
	                msgRef = new MsgRef(msgRefId,sdf.parse(dateStr));
	                msgList.add(msgRef);
	                
                } catch (Exception e){
                	
                }
            }
		}
		
		int actListIdx = 0;
		int subMaxIdx = findSubMaxIdx(msgList,msgList.get(0).getMsgRefId(),actListIdx);
		while(actListIdx < msgList.size()){
			findSameIdx(msgList.subList(actListIdx, actListIdx+subMaxIdx));
			int findActNextFirstIdx = findActCurrLastIdx(msgList, subMaxIdx, actListIdx);
			actListIdx = findActNextFirstIdx +1;
			if(actListIdx < msgList.size())
			subMaxIdx = findSubMaxIdx(msgList,msgList.get(actListIdx).getMsgRefId(),actListIdx);
		}
	}
	
	public static void findSameIdx(List<MsgRef> subList){
		MsgRef curr,next;
		String key;
		
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		Map<String,String> ctMap = new HashMap<String,String>();
		for (int i = 0; i < subList.size(); i++) {
			curr = subList.get(i);
			key = curr.getMsgRefId()+"";
			if(ctMap.get(key) != null){
				continue;
			}
			for (int j = i+1; j < subList.size(); j++) {
				next = subList.get(j);
				if(curr.getMsgRefId().equals(next.getMsgRefId())){
					String msg = ctMap.get(curr.getMsgRefId()+"");
					Integer ct = Common.isEmpty(msg)?0:Integer.valueOf(msg.split("#")[0]);
					ctMap.put(key, ct == 0?1+"#"+sdf.format(curr.getDate()):(ct+1)+"#"+msg.split("#")[1]);
				}
			}
		}
		
		for(Entry<String, String> entry:ctMap.entrySet()){
			Common.logln("repeat#"+entry.getKey()+"#"+entry.getValue());
		}
		
	}
	
	/**
	 * 
	 * 子序列长度为第一个序列的值
	 * 由于当前第一个可能不是最大子序列号，需要查找子序列里面最大序列号，如果
	 * 最大序列号掉到子序列之外就无法查找到。
	 * @param msgList
	 * @param subFirstIdx
	 * @param actListIdx
	 * @return
	 */
	public static int findSubMaxIdx(List<MsgRef> msgList,int subFirstIdx,int actListIdx){
		int maxIdx = subFirstIdx;
		
		int endIdx = actListIdx+subFirstIdx;
		if(endIdx >= msgList.size()) endIdx = msgList.size();
		
		for(int i = actListIdx; i < endIdx ; i++){
			//Common.logln("index:"+i);
			MsgRef o = msgList.get(i);
			if(maxIdx < o.getMsgRefId().intValue()){
				maxIdx = o.getMsgRefId();
			} 
		}
		
		return maxIdx;
	}
	
	/**
	 * 查找当前子序列的最后一个行号
	 * 
	 * @param msgList 所有列表
	 * @param subMaxIdx 子序列的最大数
	 * @param actListIdx 开始遍历的行号
	 * @return
	 */
	public static int findActCurrLastIdx(List<MsgRef> msgList,int subMaxIdx,int actListIdx){
		int actNextFirstIdx = 0;
		int endIdx = actListIdx+subMaxIdx;
		for (int i = subMaxIdx; i >=0; i--) {
			if(endIdx > msgList.size()) endIdx = msgList.size();
			int temp = subList(msgList,actListIdx,endIdx,i);
			if(temp > actNextFirstIdx){
				actNextFirstIdx = temp;
			} 
			
			if(temp == -1){
				Common.logln("lost message ref id:"+i);
			}
		}
		
		return actNextFirstIdx;
	}
	
	/**
	 * 判断序列是否丢失
	 * 
	 * @param msgList
	 * @param fIdx
	 * @param eIdx
	 * @param expect
	 * @return
	 */
	public static int subList(List<MsgRef> msgList,int fIdx,int eIdx,int expect){
		int last = 0;
		for(int i = fIdx; i <= eIdx && i < msgList.size(); i++){
			//Common.logln("index:"+i);
			MsgRef o = msgList.get(i);
			if(expect == o.getMsgRefId().intValue()){
				return i;
			}
			last = i;
		}
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		Common.log(sdf.format(msgList.get(last).getDate()) + "#");
		return -1;
	}
	
	private static Node[] htmlToNode(String content, NodeFilter[] filters) {
		Parser parser = Parser.createParser(content, "GBK");
		NodeList nodeList = null;
		OrFilter orFilter = new OrFilter();
		orFilter.setPredicates(filters);
		try {
			nodeList = parser.parse(orFilter);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		if (nodeList != null) {
			return nodeList.toNodeArray();
		} else {
			return null;
		}
	}
	
	public static void run(String[] args) throws ParseException {
		
		if(args.length < 5){
			Common.logln("列数据从0开始");
			Common.logln("参数2为文件路径");
			Common.logln("参数3为文件编码");
			Common.logln("参数4为排序列,为时间格式");
			Common.logln("参数5为Message Ref Id 列");
			Common.logln("例子:java -jar parseTable.jar -l \"E://1.xls\" GBK 0 21");
			
			return; 
		}
		
		String filePath = args[1];
		String fileEncoding = args[2];
		String sortCols = args[3];
		String refCols = args[4];
		
		parseTable2(Common.readResource(filePath,fileEncoding),Integer.valueOf(sortCols),Integer.valueOf(refCols));
	}
}

class MsgRef implements Comparable<MsgRef>{
	public MsgRef(int mrId,Date date){
		this.msgRefId = mrId;
		this.date = date;
	}
	
	private Date date;
	private Integer msgRefId;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Integer getMsgRefId() {
		return msgRefId;
	}
	public void setMsgRefId(Integer msgRefId) {
		this.msgRefId = msgRefId;
	}
	public int compareTo(MsgRef o) {
		if(date.after(o.getDate())){
			return -1;
		} else if( date.before(o.getDate())){
			return 1;
		} else {
			return 0;
		}
	}
}
