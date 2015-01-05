package cc.ppdp.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cc.ppdp.util.AccessConnet;
import cc.ppdp.util.Common;

public class WangKePartition {
	public static List<Map<String, Object>> ORIGINALRECORDLIST = null;// 原始数据集
	public List<List<Map<String, Object>>> WangkePartitionResult = null;// 分组后的数据集
	public List<List<Map<String, Object>>> WangkeRandomSASetResult = null;// 生成的SASet随机化数据集
	public List<List<Map<String, Object>>> WangkeRandomSAResult = null;// 生成的SA随机化数据集

	public static void GetOriginalSet() {
		ORIGINALRECORDLIST = new ArrayList<Map<String, Object>>();
		AccessConnet ac = new AccessConnet();
		ac.connect();
		try {
			String orderstr = "select  * from " + Common.TABLENAME;
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
				ORIGINALRECORDLIST.add(adult);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ac.close();
	}

	/**
	 * @param recordList原始表记录集合
	 *            L为L-diverse的参数
	 * @return
	 * */
	public List<List<Map<String, Object>>> Partition(
			List<Map<String, Object>> recordList, int L) {
		List<List<Map<String, Object>>> resultList = new ArrayList<List<Map<String, Object>>>();
		Map<String, List<Map<String, Object>>> buckets = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> record : recordList) {
			String SA = (String) record.get("occupation");
			if (buckets.get(SA) == null) {
				buckets.put(SA, new ArrayList<Map<String, Object>>());
				buckets.get(SA).add(record);
			} else
				buckets.get(SA).add(record);
		}
		// step2: 取数量在前L个的桶，并从各桶中随机取一个生成一个块
		do {
			List<Map<String, Object>>[] MaxL = GetLargest_L(buckets, L);
			if (MaxL != null) {
				List<Map<String, Object>> subblock = new ArrayList<Map<String, Object>>();
				for (int i = 1; i <= L; i++) {
					// 从一个桶中随机选取一个记录
					int index = new Random().nextInt(MaxL[i].size());
					subblock.add(MaxL[i].get(index));
					MaxL[i].remove(index);
				}
				resultList.add(subblock);

			} else
				break;
		} while (true);
		// step3: 将剩余桶中的记录加到RB集里
		for (String key : buckets.keySet()) {
			if (buckets.get(key).size() > 1) {
				for (Map<String, Object> tempRecord : buckets.get(key))
					System.out.println(tempRecord);
			}

			/* 找到SA值不等于当前剩余记录的SA值的所有块，然后从中随机选择一个块并将此记录添加到块中 */
			List<List<Map<String, Object>>> findlist = FindDifferentBlock(key,
					resultList);
			int index = new Random().nextInt(findlist.size());
			findlist.get(index).add(buckets.get(key).get(0));
		}
		return resultList;
	}

	private List<Map<String, Object>>[] GetLargest_L(
			Map<String, List<Map<String, Object>>> buckes, int L) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> MaxL[] = new List[L + 1];
		int size = 1;
		List<String> removekey = new ArrayList<String>();// 用于记录需要从buckes中删除的key
		for (String key : buckes.keySet()) {
			//System.out.println("test");
			List<Map<String, Object>> recordList = buckes.get(key);
			if (recordList.isEmpty()) {
				// buckes.remove(key);
				removekey.add(key);
				continue;
			}
			if (size == 1) {
				MaxL[1] = recordList;
				size++;
			} else if (size < L + 1) {
				int k = size;
				int i = k / 2;
				while (i > 0 && MaxL[i].size() > recordList.size()) {
					MaxL[k] = MaxL[i];
					k = i;
					i = k / 2;
				}
				MaxL[k] = recordList;
				size++;
			} else if (size == L + 1) {
				if (recordList.size() > MaxL[1].size()) {
					int k = 1;
					for (int i = k * 2; i < L + 1; i *= 2) {
						if (i < L && MaxL[i].size() > MaxL[i + 1].size())
							i++;
						if (recordList.size() > MaxL[i].size()) {
							MaxL[k] = MaxL[i];
							k = i;
						} else
							break;
					}
					MaxL[k] = recordList;
				}
			}
		}
		for (String key : removekey) {
			buckes.remove(key);
		}
		if (size <= L)
			MaxL = null;
		return MaxL;
	}

	private List<List<Map<String, Object>>> FindDifferentBlock(String SA,
			List<List<Map<String, Object>>> originalList) {
		List<List<Map<String, Object>>> findList = new ArrayList<List<Map<String, Object>>>();
		for (List<Map<String, Object>> block : originalList) {
			Boolean flag = true;
			for (Map<String, Object> record : block) {
				if (record.get("occupation").equals(SA)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				findList.add(block);
			}
		}
		return findList;
	}
}
