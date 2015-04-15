package cc.ppdp.util;

import java.util.List;
import java.util.Map;

public class Common {
	public static String TABLENAME = "adult_TDS";// 要操作的表
	public static String RECORDNUM = "";// 要对TABLE操作的记录数
	public static String DESCARTESTABLE = "";// 笛卡尔积表名
	public static int LDIVERSITY = 3;
	public static List<Map<String, Object>> DESCARTESLIST = null;
	public static String[] NSAs = { //"age",
		"sex", "education",
			"marital_status", "workclass", "relationship"//, "race" 
			};
	public static String SA = "age";
							//"occupation";
}
