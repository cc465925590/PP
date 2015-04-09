package cc.ppdp.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cc.ppdp.util.AccessConnet;



public class DataUtil {
	public List<Map<String, Object>> OrgRecordList = null;// 原始数据集
	public Map<String, Set<Object>> Attr_ValueSetMap = null;// 属性与属性值集的映射
															// 用于生成weka文件

	public List<Map<String, Object>> TestRecordList = null;// 测试集
	/** 读取原始数据 */
	public void GetOriginalData() {
		this.Attr_ValueSetMap = new HashMap<String, Set<Object>>();
		// for (String NSA : Common.NSAs) {
		// this.Attr_ValueSetMap.put(NSA, new HashSet<Object>());
		// }
		this.OrgRecordList = new ArrayList<Map<String, Object>>();
		AccessConnet ac = new AccessConnet();
		ac.connect();
		try {
			String orderstr = "select  * from " + Common.TABLENAME;
			System.out.println(orderstr);
			PreparedStatement pstmt = ac.con.prepareStatement(orderstr);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> adult = new HashMap<String, Object>();
				//adult_TDS
				/*adult.put("ID", rs.getInt(1));
				adult.put("age", rs.getInt(2));
				adult.put("workclass", rs.getString(3));
				adult.put("education", rs.getString(5));
				adult.put("marital_status", rs.getString(7));
				adult.put("occupation", rs.getString(8));
				adult.put("relationship", rs.getString(9));
				adult.put("race", rs.getString(10));
				adult.put("sex", rs.getString(11));
				adult.put("class", rs.getString(16));*/
				// adult_o
				adult.put("ID", rs.getString(1));
				adult.put("age", rs.getString(2));
				adult.put("sex", rs.getString(3));
				adult.put("education", rs.getString(4));
				adult.put("marital_status", rs.getString(5));
				adult.put("workclass", rs.getString(6));
				adult.put("relationship", rs.getString(7));
				adult.put("race", rs.getString(8));
				adult.put("occupation", rs.getString(9));
				// for (String NSA : Common.NSAs) {
				// this.Attr_ValueSetMap.get(NSA).add(adult.get(NSA));
				// }
				this.OrgRecordList.add(adult);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ac.close();
	}
	
	/**count查询测试数据*/
	public void GetDataForCountTest(){
		// for (String NSA : Common.NSAs) {
		// this.Attr_ValueSetMap.put(NSA, new HashSet<Object>());
		// }
		List<Map<String, Object>>OrgRecordList = new ArrayList<Map<String, Object>>();
		AccessConnet ac = new AccessConnet();
		ac.connect();
		try {
			//String orderstr = "select  * from " + this.table+" order by workclass,education,marital_status,race,relationship,sex";
			String orderstr = "select  * from " + Common.TABLENAME;
			System.out.println(orderstr);
			PreparedStatement pstmt = ac.con.prepareStatement(orderstr);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> adult = new HashMap<String, Object>();
				//adult_TDS
				/*adult.put("ID", rs.getInt(1));
				adult.put("age", rs.getInt(2));
				adult.put("workclass", rs.getString(3));
				adult.put("education", rs.getString(5));
				adult.put("marital_status", rs.getString(7));
				adult.put("occupation", rs.getString(8));
				adult.put("relationship", rs.getString(9));
				adult.put("race", rs.getString(10));
				adult.put("sex", rs.getString(11));
				adult.put("class", rs.getString(16));*/
				// adult_o
				adult.put("ID", rs.getString(1));
				adult.put("age", rs.getString(2));
				adult.put("sex", rs.getString(3));
				adult.put("education", rs.getString(4));
				adult.put("marital_status", rs.getString(5));
				adult.put("workclass", rs.getString(6));
				adult.put("relationship", rs.getString(7));
				adult.put("race", rs.getString(8));
				adult.put("occupation", rs.getString(9));
				// for (String NSA : Common.NSAs) {
				// this.Attr_ValueSetMap.get(NSA).add(adult.get(NSA));
				// }
				OrgRecordList.add(adult);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ac.close();		
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		for(int i=0;i<OrgRecordList.size();i+=10){
			Random rd = new Random();
			int index = rd.nextInt(10);
			while((i+index)>=OrgRecordList.size()){
				index = rd.nextInt(10);
			}
			resultList.add(OrgRecordList.get(i+index));
		}
		this.TestRecordList = resultList;
	}
	/**
	 * GLP的预处理-生成每个元组的具有所有NSAs相同值的SA值的集合
	 * 
	 * @serialData 2014.12.2
	 */
	public void InitialNLP(List<Map<String, Object>> DataSet,
			String[] NSAs, String SA) {
		for (int i = 0; i < DataSet.size(); i++) {
			if (DataSet.get(i).get("NLPSASet") == null) {
				DataSet.get(i).put("NLPSASet", new HashSet<String>());
			}
			@SuppressWarnings("unchecked")
			Set<String> GLPSASet1 = (Set<String>) DataSet.get(i)
					.get("NLPSASet");
			GLPSASet1.add((String) DataSet.get(i).get(SA));
			// 寻找NSAs都相等的元组
			for (int j = i + 1; j < DataSet.size(); j++) {
				boolean flag = true;
				for (String nsa : NSAs) {
					if (!DataSet.get(i).get(nsa)
							.equals(DataSet.get(j).get(nsa))) {
						flag = false;
						break;
					}
				}
				// 如果所有的NSAs的值都相等
				if (flag) {
					GLPSASet1.add((String) DataSet.get(j).get(SA));
					if (DataSet.get(j).get("NLPSASet") == null) {
						DataSet.get(j).put("NLPSASet", new HashSet<String>());
					} else {
						@SuppressWarnings("unchecked")
						Set<String> GLPSASet = (Set<String>) DataSet.get(j)
								.get("NLPSASet");
						GLPSASet.add((String) DataSet.get(i).get(SA));
					}
				}
			}
//			System.out.println("第"+i+"条记录");
		}
	}

	/**
	 * 计算一个块的NLP
	 * 
	 * @param SGSet为最终发布时该块中的SA的值的集合
	 */
	@SuppressWarnings("unchecked")
	public float NLP(List<Map<String, Object>> BlockSet, Set<String> SGSet) {
		float probility = 0.0f;// 值不在真是SA值集合中的概率
		for (Map<String, Object> obj : BlockSet) {
			int sum = 0;
			Set<String> NLPSet = (Set<String>) obj.get("NLPSASet");
			for (String sa : SGSet) {
				try {
					if (!NLPSet.contains(sa))
						sum++;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			probility = probility + (float) sum / (float) SGSet.size();
		}
		return probility;
	}
	/**比较相对错误率 2015 03 23*/
	public Float ComputeRelativeCor(List<Map<String, Object>>OrgRecordList,List<Map<String, Object>>RandomRecordList,String []NSAs){
		Float act = 0.0f;
		Float est = 0.0f;
		Float sum = 0.0f;
		for (int i = 0; i < OrgRecordList.size(); i++) {
			Map<String, Object> oldobj = OrgRecordList.get(i);
			System.out.println("比较第"+i+"条");
			if (FindRecord(oldobj, RandomRecordList, NSAs)){
				est++;
			}
		}
		act = (float)OrgRecordList.size();
		if (act > est)
			sum = (act - est) / act;
		else
			sum = (est - act) / act;
		return sum;
	}
	public Boolean FindRecord(Map<String, Object> record,
			List<Map<String, Object>> recordList, String[] NSA) {
		Boolean flag = false;
//		int i=0;
		for (Map<String, Object> tempobj : recordList) {
//			System.out.println("比较第"+i+"条");
//			i++;
			Boolean flag2 = true;
			for (String tempNSA : NSA) {
				if (!record.get(tempNSA).equals(tempobj.get(tempNSA))) {
					flag2 = false;
					break;
				}
			}
			if (flag2
					&& record.get("occupation").equals(
							tempobj.get("occupation"))) {
				flag = true;
				break;
			}
		}
		return flag;
	}
	public static void main(String[] args) {
		DataUtil datautil = new DataUtil();
	}
}
