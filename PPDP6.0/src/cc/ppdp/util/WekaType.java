package cc.ppdp.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @serialData2015.1.4 用于转换成weka格式的工具类
 */
public class WekaType {
	/**
	 * 用于分割的格式处理
	 * 
	 * @serialData2015.1.4
	 */
	@SuppressWarnings("resource")
	public void DataToFileForAT(String filename,
			LinkedList<LinkedList<Map<String, Object>>> resultList,
			List<List<String>> SABlockList) throws FileNotFoundException {// 将数据写入arff文件
		filename = filename + ".arff";
		FileOutputStream fos = new FileOutputStream(filename);
		PrintStream p = new PrintStream(fos);
		p.println("@relation Anatomizationadultreponses");
		p.println();
		p.println("@attribute age numeric");
		p.println("@attribute sex {Male,Female}");
		p.println("@attribute education {10th,11th,12th,1st-4th,5th-6th,7th-8th,9th,Assoc-acdm,Assoc-voc,Bachelors,Doctorate,HS-grad,Masters,Preschool,Prof-school,Some-college}");
		p.println("@attribute marital_status {Divorced,Married-AF-spouse,Married-civ-spouse,Married-spouse-absent,Never-married,Separated,Widowed}");
		p.println("@attribute workclass {Federal-gov,Local-gov,Private,Self-emp-inc,Self-emp-not-inc,State-gov}");
		p.println("@attribute relationship {Husband,Not-in-family,Other-relative,Own-child,Unmarried,Wife}");
		p.println("@attribute race {Amer-Indian-Eskimo,Asian-Pac-Islander,Black,Other,White}");
		p.println("@attribute occupation {Adm-clerical,Armed-Forces,Craft-repair,Exec-managerial,Farming-fishing,Handlers-cleaners,Machine-op-inspct,Other-service,Priv-house-serv,Prof-specialty,Protective-serv,Sales,Tech-support,Transport-moving}");
		p.println();
		p.println("@data");
		String[] NSA = { "age", "sex", "education", "marital_status",
				"workclass", "relationship", "race" };
		for (int i = 0; i < resultList.size(); i++) {
			LinkedList<Map<String, Object>> recordList = resultList.get(i);
			List<String> SAList = SABlockList.get(i);
			for (int j = 0; j < recordList.size(); j++) {
				StringBuffer record = new StringBuffer("");
				for (String temp : NSA) {
					record = record.append(recordList.get(j).get(temp)
							.toString().trim()
							+ ",");
				}
				record.append(SAList.get(j).trim());
				System.out.println("j = " + j + ", SA = "
						+ SAList.get(j).trim());
				p.println(record);
			}
		}
	}

	/**
	 * 用于对原始数据在weka应用的格式处理
	 * 
	 * @throws FileNotFoundException
	 */
	public void DataToWekaForOrig(String filename, String tablename)
			throws FileNotFoundException {
		LinkedList<Map<String, Object>> recordList = new LinkedList<Map<String, Object>>();
		AccessConnet ac = new AccessConnet();
		ac.connect();
		try {
			String orderstr = "select  * from " + tablename;
			int i = 0;
			System.out.println(orderstr);
			PreparedStatement pstmt = ac.con.prepareStatement(orderstr);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> adult = new HashMap<String, Object>();
				adult.put("ID", rs.getString(1));
				adult.put("age", rs.getString(2));
				adult.put("sex", rs.getString(3));
				adult.put("education", rs.getString(4));
				adult.put("marital_status", rs.getString(5));
				adult.put("workclass", rs.getString(6));
				adult.put("relationship", rs.getString(7));
				adult.put("race", rs.getString(8));
				adult.put("occupation", rs.getString(9));
				recordList.add(adult);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		filename = filename + ".arff";
		FileOutputStream fos = new FileOutputStream(filename);
		PrintStream p = new PrintStream(fos);
		p.println("@relation originaladultreponses");
		p.println();
		p.println("@attribute age numeric");
		p.println("@attribute sex {Male,Female}");
		p.println("@attribute education {10th,11th,12th,1st-4th,5th-6th,7th-8th,9th,Assoc-acdm,Assoc-voc,Bachelors,Doctorate,HS-grad,Masters,Preschool,Prof-school,Some-college}");
		p.println("@attribute marital_status {Divorced,Married-AF-spouse,Married-civ-spouse,Married-spouse-absent,Never-married,Separated,Widowed}");
		p.println("@attribute workclass {Federal-gov,Local-gov,Private,Self-emp-inc,Self-emp-not-inc,State-gov}");
		p.println("@attribute relationship {Husband,Not-in-family,Other-relative,Own-child,Unmarried,Wife}");
		p.println("@attribute race {Amer-Indian-Eskimo,Asian-Pac-Islander,Black,Other,White}");
		p.println("@attribute occupation {Adm-clerical,Armed-Forces,Craft-repair,Exec-managerial,Farming-fishing,Handlers-cleaners,Machine-op-inspct,Other-service,Priv-house-serv,Prof-specialty,Protective-serv,Sales,Tech-support,Transport-moving}");
		p.println();
		p.println("@data");
		String[] NSA = { "age", "sex", "education", "marital_status",
				"workclass", "relationship", "race" };
		String SA = "occupation";
		for (int j = 0; j < recordList.size(); j++) {
			StringBuffer record = new StringBuffer("");
			for (String temp : NSA) {
				record = record.append(recordList.get(j).get(temp).toString()
						.trim()
						+ ",");
			}
			record.append(recordList.get(j).get(SA).toString().trim());
			p.println(record);
		}
	}

	/**
	 * 用于对List<List<Map<String, Object>>>格式在weka应用的格式处理
	 * 
	 * 2015.1.7
	 * 
	 * @throws FileNotFoundException
	 */
	public void DataToWeka(String filename,
			List<List<Map<String, Object>>> BlockDetailListRandResult)
			throws FileNotFoundException {
		filename = filename + ".arff";
		FileOutputStream fos = new FileOutputStream(filename);
		@SuppressWarnings("resource")
		PrintStream p = new PrintStream(fos);
		p.println("@relation originaladultreponses");
		p.println();
		p.println("@attribute age numeric");
		p.println("@attribute sex {Male,Female}");
		p.println("@attribute education {10th,11th,12th,1st-4th,5th-6th,7th-8th,9th,Assoc-acdm,Assoc-voc,Bachelors,Doctorate,HS-grad,Masters,Preschool,Prof-school,Some-college}");
		p.println("@attribute marital_status {Divorced,Married-AF-spouse,Married-civ-spouse,Married-spouse-absent,Never-married,Separated,Widowed}");
		p.println("@attribute workclass {Federal-gov,Local-gov,Private,Self-emp-inc,Self-emp-not-inc,State-gov}");
		p.println("@attribute relationship {Husband,Not-in-family,Other-relative,Own-child,Unmarried,Wife}");
		p.println("@attribute race {Amer-Indian-Eskimo,Asian-Pac-Islander,Black,Other,White}");
		p.println("@attribute occupation {Adm-clerical,Armed-Forces,Craft-repair,Exec-managerial,Farming-fishing,Handlers-cleaners,Machine-op-inspct,Other-service,Priv-house-serv,Prof-specialty,Protective-serv,Sales,Tech-support,Transport-moving}");
		p.println();
		p.println("@data");
		String[] NSA = { "age", "sex", "education", "marital_status",
				"workclass", "relationship", "race" };
		String SA = "occupation";
		for (List<Map<String, Object>> recordList : BlockDetailListRandResult) {
			for (int j = 0; j < recordList.size(); j++) {
				StringBuffer record = new StringBuffer("");
				for (String temp : NSA) {
					record = record.append(recordList.get(j).get(temp)
							.toString().trim()
							+ ",");
				}
				record.append(recordList.get(j).get(SA).toString().trim());
				p.println(record);
			}
		}
	}

	public static void main(String[] args) {
		WekaType wekatype = new WekaType();
		System.out.println("test");
		String filename = "D:/WekaData/adult_test";
		String tablename = "adult_test";
		try {
			wekatype.DataToWekaForOrig(filename, tablename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
