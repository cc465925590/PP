package cc.ppdp.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cc.ppdp.model.RBRecord;
import cc.ppdp.model.SubBlock;
import cc.ppdp.util.AccessConnet;
import cc.ppdp.util.Common;

public class Partition {
	private AccessConnet ac;
	// 用于保存划分好了的QIT表的块
	public LinkedList<LinkedList<Map<String, Object>>> resultList = new LinkedList<LinkedList<Map<String, Object>>>();
	// 用于处理不满足L的块
	private LinkedList<Map<String, Object>> pretemp = new LinkedList<Map<String, Object>>();
	// private Set<Object> preset = new HashSet<Object>();
	private Boolean preflag = false;// true表示前面的块不满足L
	private Map<Object, Integer> preSAmap = new HashMap<Object, Integer>();

	/**
	 * 参数type用于选择判断块的条件 (于2015.1.9修改，增加type参数)
	 */
	public void DoPartition(String[] NSA, int l, int type) {
		LinkedList<Map<String, Object>> BlockSet = new LinkedList<Map<String, Object>>();
		ac = new AccessConnet();
		ac.connect();
		try {
			String orderstr = "select " + Common.RECORDNUM + " * from "
					+ Common.TABLENAME + " order by ";
			int i = 0;
			for (String str : NSA) {
				if (i < NSA.length - 1) {
					orderstr = orderstr + str + ",";
				} else {
					orderstr = orderstr + str;
				}
				i++;
			}
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
				BlockSet.add(adult);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();

		// String[] str = { "sex", "education" };
		try {
			Partitioning(BlockSet, l, NSA, 0, "occupation", type);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		}
		ac.close();
	}

	public void Partitioning(LinkedList<Map<String, Object>> BlockSet, int l,
			String[] NSA, int i, String SA, int type) throws SQLException {
		if (i == NSA.length) {
			resultList.add(BlockSet);// 完成算法
			return;
		}
		String Attribute = NSA[i];
		// Partition B into B1, B2,…,Bm using Ai start
		String sql = "select " + Common.RECORDNUM + " " + Attribute + " from "
				+ Common.TABLENAME + " group by " + Attribute;
		System.out.println(sql);
		PreparedStatement pstmt = ac.con.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		LinkedList<LinkedList<Map<String, Object>>> BlockSetList = new LinkedList<LinkedList<Map<String, Object>>>();// 用于记录分割的块
		LinkedList<Boolean> isLDiversityList = new LinkedList<Boolean>();// 用于记录是否在属性NSA[i]进行过合并(满足不再继续分块的条件)
		while (rs.next()) {
			String attr = rs.getString(1);
			LinkedList<Map<String, Object>> temp = new LinkedList<Map<String, Object>>();
			Boolean isExist = false;// 用来判断当前块里有没有当前字段的值
			for (Map<String, Object> adult : BlockSet) {
				if (adult.get(Attribute).equals(attr)) {
					isExist = true;
					break;
				}

			}
			if (!isExist)// 如果当前分块字段里没有此值，则进行下一次循环
				continue;
			if (!preflag) {
				Set<Object> SAset = new HashSet<Object>();// 记录SA的个数是否有L个
				Map<Object, Integer> SAmap = new HashMap<Object, Integer>();

				for (Map<String, Object> adult : BlockSet) {
					if (adult.get(Attribute).equals(attr)) {
						temp.add(adult);
						SAset.add(adult.get(SA));
						// System.out.println(adult);
						if (SAmap.get(adult.get(SA)) == null)
							SAmap.put(adult.get(SA), 1);
						else
							SAmap.put(adult.get(SA),
									SAmap.get(adult.get(SA)) + 1);
					}
				}

				if (!this.CheckBlock(SAmap, l, temp.size(), type)) {// 如果前一个块满足L，当前块不满足，先记录下当前块
					preflag = true;
					for (Map<String, Object> adult : temp) {
						pretemp.add(adult);
					}
					preSAmap = SAmap;
					// preset = SAset;
				} else {
					pretemp.clear();
					preSAmap.clear();
					BlockSetList.add(temp);// 如果当前块满足L，则生成当前块

					isLDiversityList.add(false);
				}
			} else {
				for (Map<String, Object> adult : BlockSet) {// 如果前一块不满足L，则当前块与前一块合并
					if (adult.get(Attribute).equals(attr)) {
						pretemp.add(adult);
						// preset.add(adult.get(SA));
						if (preSAmap.get(adult.get(SA)) == null)
							preSAmap.put(adult.get(SA), 1);
						else
							preSAmap.put(adult.get(SA),
									preSAmap.get(adult.get(SA)) + 1);
					}
				}
				if (this.IsLDiversity(preSAmap, l, pretemp.size())) {// 如果合并之后满足L
					for (Map<String, Object> adult : pretemp) {
						temp.add(adult);
					}

					preflag = false;
					pretemp.clear();
					preSAmap.clear();
					BlockSetList.add(temp);
					isLDiversityList.add(true);
				}
			}

		}
		/*
		 * if (!pretemp.isEmpty()) { LinkedList<Map<String, Object>> lastSet =
		 * BlockSetList.pollLast(); if (lastSet == null) lastSet = new
		 * LinkedList<Map<String, Object>>(); if (lastSet != null) { for
		 * (Map<String, Object> adult : pretemp) { lastSet.add(adult); }
		 * 
		 * preflag = false; pretemp.clear(); preSAmap.clear();
		 * 
		 * BlockSetList.add(lastSet); isLDiversityList.pollLast();
		 * isLDiversityList.add(true); }
		 * 
		 * }
		 */
		// 20140728 修改:在最后一个块不满足L-Diversity原则，不应该只和满足的前一个块合并，
		// 如果仍然不满足需要继续合并 start
		while (!pretemp.isEmpty()) {
			LinkedList<Map<String, Object>> lastSet = BlockSetList.pollLast();
			if (lastSet == null)
				lastSet = new LinkedList<Map<String, Object>>();
			if (lastSet != null) {
				// tempList用于保证记录的有序性
				LinkedList<Map<String, Object>> tempList = new LinkedList<Map<String, Object>>();
				for (Map<String, Object> adult : lastSet) {
					tempList.add(adult);
					// preset.add(adult.get(SA));
					if (preSAmap.get(adult.get(SA)) == null)
						preSAmap.put(adult.get(SA), 1);
					else
						preSAmap.put(adult.get(SA),
								preSAmap.get(adult.get(SA)) + 1);
				}
				for (Map<String, Object> adult : pretemp) {
					tempList.add(adult);
				}
				pretemp = tempList;
				if (this.CheckBlock(preSAmap, l, pretemp.size(), type)) {// 如果合并之后满足L
					lastSet.clear();
					for (Map<String, Object> adult : pretemp) {
						lastSet.add(adult);
					}
					preflag = false;
					pretemp.clear();
					preSAmap.clear();
					BlockSetList.add(lastSet);
					isLDiversityList.add(true);
				}
			}
		}
		// 修改 end
		rs.close();
		pstmt.close();
		BlockSet.clear();// 清空旧集合
		// Partition B into B1, B2,…,Bm using Ai end
		// 第二步
		for (LinkedList<Map<String, Object>> temp : BlockSetList) {
			Boolean isDiversity = isLDiversityList.pollFirst();
			if (isDiversity != null && isDiversity) {
				resultList.add(temp);// 完成算法
			} else {
				Partitioning(temp, l, NSA, i + 1, SA, type);
			}

		}

	}

	/**
	 * 判断是否满足L条件
	 */
	private Boolean IsLDiversity(Map<Object, Integer> SAmap, int L,
			int recordsum) {
		if (SAmap.size() < L) {
			return false;
		} else {
			for (Integer i : SAmap.values()) {
				float result = (1.0f / L) * recordsum;
				if (i > result) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 判断是否满足递归-(c,L)多样性 这里使用(1,L)多样性,即取c=1
	 * 
	 * @author cc 2015.1.9
	 */
	private Boolean IsRecursiveDiversity(Map<Object, Integer> SAmap, int L) {
		Boolean flag = null;
		if (SAmap.size() < L) {
			return false;
		}
		// 先对SAmap按个数排序
		int[] SAcount = new int[SAmap.size()];
		Set<Object> saset = SAmap.keySet();
		int i = 0;
		for (Object obj : saset) {
			SAcount[i] = SAmap.get(obj);
			i++;
		}
		// 从大到小排序
		for (int x = 0; x < SAcount.length; x++)
			for (int y = x + 1; y < SAcount.length; y++)
				if (SAcount[x] < SAcount[y]) {
					int temp = SAcount[x];
					SAcount[x] = SAcount[y];
					SAcount[y] = temp;
				}
		int sum = 0;
		for (int x = L - 1; x < SAcount.length; x++)
			sum += SAcount[x];
		if (sum > SAcount[0])
			return flag = true;
		else
			return flag = false;
	}

	/**
	 * 选择块的校验器
	 * 
	 * @author cc 2015.1.9
	 */
	public Boolean CheckBlock(Map<Object, Integer> SAmap, int L, int recordsum,
			int type) {
		Boolean flag = null;
		if (type == 0)// 类型0使用L-多样性条件检测
			flag = IsLDiversity(SAmap, L, recordsum);
		else if (type == 1)// 类型1使用递归(c,L)-多样性条件检测
			flag = IsRecursiveDiversity(SAmap, L);
		return flag;
	}

	/**
	 * 细化算法
	 * 
	 * @2014.07.24
	 */
	public List<List<SubBlock>> RefiningPartition(
			LinkedList<LinkedList<Map<String, Object>>> resultList, int L) {
		List<List<SubBlock>> result = null;
		if (resultList != null && resultList.size() > 0) {
			result = new ArrayList<List<SubBlock>>();// 所有大块
			int superBlockID = 0;// 原始块的id
			Map<String, List<RBRecord>> RemainBuckets = new HashMap<String, List<RBRecord>>();// RB集合
			for (LinkedList<Map<String, Object>> block : resultList) {
				List<SubBlock> SubBlockList = new ArrayList<SubBlock>();// 一个大块的细化的子块集合
				// step1: 进行桶划分
				Map<String, List<Map<String, Object>>> buckets = new HashMap<String, List<Map<String, Object>>>();
				for (Map<String, Object> record : block) {
					String SA = (String) record.get("occupation");
					if (buckets.get(SA) == null) {
						buckets.put(SA, new ArrayList<Map<String, Object>>());
						buckets.get(SA).add(record);
					} else
						buckets.get(SA).add(record);
				}
				// step2: 取数量在前L个的桶，并从各桶中随机取一个生成一个细化块
				do {
					List<Map<String, Object>>[] MaxL = GetLargest_L(buckets, L);
					if (MaxL != null) {
						SubBlock subblock = new SubBlock(superBlockID);
						for (int i = 1; i <= L; i++) {
							// 从一个桶中随机选取一个记录
							int index = new Random().nextInt(MaxL[i].size());
							subblock.recordList.add(MaxL[i].get(index));
							MaxL[i].remove(index);
						}
						SubBlockList.add(subblock);

					} else
						break;
				} while (true);
				// step3: 将剩余桶中的记录加到RB集里
				for (String key : buckets.keySet()) {
					if (buckets.get(key).size() > 1) {
						for (Map<String, Object> tempRecord : buckets.get(key))
							System.out.println(tempRecord);
					}
					for (Map<String, Object> tempRecord : buckets.get(key)) {
						RBRecord rbRecord = new RBRecord(superBlockID,
								tempRecord);
						if (RemainBuckets.get(key) == null) {
							List<RBRecord> list = new ArrayList<RBRecord>();
							list.add(rbRecord);
							RemainBuckets.put(key, list);
						} else
							RemainBuckets.get(key).add(rbRecord);
					}
					/*
					 * RBRecord rbRecord = new RBRecord(superBlockID,
					 * buckets.get( key).get(0)); if (RemainBuckets.get(key) ==
					 * null) { List<RBRecord> list = new ArrayList<RBRecord>();
					 * list.add(rbRecord); RemainBuckets.put(key, list); } else
					 * RemainBuckets.get(key).add(rbRecord);
					 */
				}
				// step4: 将RB集里满足L-diversity的生成sub-Block
				do {
					List<RBRecord>[] MaxL = GetLargest_L2(RemainBuckets, L);
					if (MaxL != null) {
						SubBlock subblock = new SubBlock(superBlockID);
						for (int i = 1; i <= L; i++) {
							// 从一个桶中随机选取一个记录
							int index = new Random().nextInt(MaxL[i].size());
							subblock.recordList.add(MaxL[i].get(index).Obj);
							MaxL[i].remove(index);
						}
						SubBlockList.add(subblock);

					} else
						break;
				} while (true);
				superBlockID++;
				result.add(SubBlockList);
			}
			// End For
			// 测试记录个数start
			int blocknum = 0;
			for (List<SubBlock> BlockList : result) {
				for (SubBlock sblock : BlockList) {
					for (@SuppressWarnings("unused")
					Map<String, Object> temp : sblock.recordList) {
						blocknum++;
					}
				}
			}
			System.out.println("当前记录个数为: " + blocknum);
			int remainnum = 0;
			// 测试记录个数end
			// step5: 将RB中剩余的加到原来的块中
			if (!RemainBuckets.isEmpty()) {
				Map<Integer, List<RBRecord>> orderRemain = new HashMap<Integer, List<RBRecord>>();
				for (String key : RemainBuckets.keySet()) {// 将SA桶转化为块id桶
					List<RBRecord> recordList = RemainBuckets.get(key);
					for (RBRecord rbrecord : recordList) {
						remainnum++;// 测试剩余记录个数

						if (orderRemain.get(rbrecord.SuperBlockID) == null) {
							List<RBRecord> list = new ArrayList<RBRecord>();
							list.add(rbrecord);
							orderRemain.put(rbrecord.SuperBlockID, list);
						} else {
							orderRemain.get(rbrecord.SuperBlockID)
									.add(rbrecord);
						}
					}
				}
				for (Integer SuperBlockID : orderRemain.keySet()) {
					List<RBRecord> recordlist = orderRemain.get(SuperBlockID);
					for (RBRecord record : recordlist) {
						System.out.println("块号 = " + record.SuperBlockID
								+ " ID = " + record.Obj.get("ID"));
						List<SubBlock> subBlockList = result.get(SuperBlockID);
						InsertToSubBlock(record, subBlockList);
					}
				}
				System.out.println("剩余记录个数为: " + remainnum);
			}
		}
		return result;
	}

	/**
	 * 将一个剩余的rb记录插入到原来的父块中的一个sub块中，使其满足L-diversity
	 * */
	private void InsertToSubBlock(RBRecord record, List<SubBlock> subBlockList) {
		for (SubBlock subblock : subBlockList) {
			boolean flag = false;
			for (Map<String, Object> obj : subblock.recordList) {
				if (obj.get("occupation").equals(record.Obj.get("occupation")))
					flag = true;
			}
			if (!flag) {
				subblock.recordList.add(record.Obj);
				break;
			}
		}
	}

	private List<Map<String, Object>>[] GetLargest_L(
			Map<String, List<Map<String, Object>>> buckes, int L) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> MaxL[] = new List[L + 1];
		int size = 1;
		List<String> removekey = new ArrayList<String>();// 用于记录需要从buckes中删除的key
		for (String key : buckes.keySet()) {
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

	private List<RBRecord>[] GetLargest_L2(Map<String, List<RBRecord>> buckes,
			int L) {
		@SuppressWarnings("unchecked")
		List<RBRecord> MaxL[] = new List[L + 1];
		int size = 1;
		List<String> removekey = new ArrayList<String>();// 用于记录需要从buckes中删除的key
		for (String key : buckes.keySet()) {
			List<RBRecord> recordList = buckes.get(key);
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

	public static void main(String[] args) {
		/*
		 * Partition test = new Partition(); test.DoPartition(null, 1); for
		 * (LinkedList<Map<String, Object>> tempList : test.resultList) { for
		 * (Map<String, Object> tempmap : tempList) {
		 * System.out.println(tempmap); } }
		 */
		/*
		 * int L = 5; float k = 1.0f / L; System.out.println(k);
		 */
		System.out.println(Partition.class.getName());
	}
}
