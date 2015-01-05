package cc.ppdp.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cc.ppdp.util.AccessConnet;
import cc.ppdp.util.Common;

public class MyRandomization {
	private AccessConnet ac;
	public List<Map<String, Object>> oldrecordList = null;// 原始表
	public List<Map<String, Object>> recordList = null;// 生成随机SA集合的表
	public List<Map<String, Object>> newrecord = null;// 生成随机SA的表
	public List<Map<String, Object>> descartesList = null;// 笛卡尔积表的记录
	private float sum = 0;
	private float time = 0;

	public List<Map<String, Object>> TableRandomize(int L) throws SQLException {
		oldrecordList = new ArrayList<Map<String, Object>>();
		recordList = new ArrayList<Map<String, Object>>();
		ac = new AccessConnet();
		ac.connect();
		String sql = "select " + Common.RECORDNUM + " * from "
				+ Common.TABLENAME;
		PreparedStatement pstmt = ac.con.prepareStatement(sql);
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
			oldrecordList.add(adult);
		}
		// 对每个记录生成L个SA的集
		recordList = BlockRandomize(L, oldrecordList);
		return recordList;
	}

	public List<Map<String, Object>> SASetRandomize(
			List<Map<String, Object>> recordList) {
		List<Map<String, Object>> newrecord = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> obj : recordList) {
			String[] SASet = ((String) obj.get("occupation")).split(",");
			Random rd = new Random();
			int randomnum = rd.nextInt(SASet.length);
			String SA = SASet[randomnum];
			Map<String, Object> adult = new HashMap<String, Object>();
			adult.put("ID", obj.get("ID"));
			adult.put("age", obj.get("age"));
			adult.put("sex", obj.get("sex"));
			adult.put("education", obj.get("education"));
			adult.put("marital_status", obj.get("marital_status"));
			adult.put("workclass", obj.get("workclass"));
			adult.put("relationship", obj.get("relationship"));
			adult.put("race", obj.get("race"));
			adult.put("occupation", SA);
			newrecord.add(adult);
		}
		// this.newrecord = newrecord;
		return newrecord;
	}

	public void Compare(List<Map<String, Object>> oldList,
			List<Map<String, Object>> changedList, String[] NSA) {

		for (int i = 0; i < oldList.size(); i++) {
			time++;
			// int randomnum = new Random().nextInt(10);
			Map<String, Object> oldobj = null;
			// if ((i + randomnum) < oldList.size())
			oldobj = oldList.get(i);
			// else
			// oldobj = oldList.get(i);
			if (FindRecord(oldobj, changedList, NSA))
				sum++;
			/*
			 * for (Map<String, Object> tempobj : changedList) { Boolean flag =
			 * true; for (String tempNSA : NSA) { if
			 * (!oldobj.get(tempNSA).equals(tempobj.get(tempNSA))) { flag =
			 * false; break; } } if (flag && oldobj.get("occupation").equals(
			 * tempobj.get("occupation"))) { sum++; break; } }
			 */

		}
	}

	public Float getCompareResult() {
		if (time != 0)
			return sum / time;
		else
			return 0.0f;
	}

	public List<Map<String, Object>> BlockRandomize(int L,
			List<Map<String, Object>> OldBlock) {
		List<Map<String, Object>> NewBlock = new ArrayList<Map<String, Object>>();
		// 对每个记录生成L个SA的集
		for (Map<String, Object> obj : OldBlock) {
			String[] SASet = new String[L];
			SASet[0] = (String) obj.get("occupation");
			for (int i = 1; i < L; i++) {
				Boolean flag = true;
				Random rd = new Random();
				while (flag) {
					int randomnum = rd.nextInt(OldBlock.size());
					Map<String, Object> tempobj = OldBlock.get(randomnum);
					String tempSA = (String) tempobj.get("occupation");
					for (int j = 0; j < i; j++) {
						if (tempSA.equals(SASet[j])) {
							flag = true;
							break;
						}
						flag = false;
					}
					if (!flag)
						SASet[i] = tempSA;
				}
			}
			String mergeSA = "";
			for (String str : SASet)
				mergeSA = mergeSA + str + ",";
			Map<String, Object> adult = new HashMap<String, Object>();
			adult.put("ID", obj.get("ID"));
			adult.put("age", obj.get("age"));
			adult.put("sex", obj.get("sex"));
			adult.put("education", obj.get("education"));
			adult.put("marital_status", obj.get("marital_status"));
			adult.put("workclass", obj.get("workclass"));
			adult.put("relationship", obj.get("relationship"));
			adult.put("race", obj.get("race"));
			adult.put("occupation", mergeSA);
			NewBlock.add(adult);
		}
		return NewBlock;
	}

	public Boolean FindRecord(Map<String, Object> record,
			List<Map<String, Object>> recordList, String[] NSA) {
		Boolean flag = false;
		for (Map<String, Object> tempobj : recordList) {
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

	// 计算笛卡尔积记录的整表错误率
	public Float ComputeDescartesTable(List<Map<String, Object>> oldList,
			List<Map<String, Object>> changedList,
			List<Map<String, Object>> DescartesList, String[] NSA) {
		Float act = 0.0f;
		Float est = 0.0f;
		Float sum = 0.0f;
		for (int i = 0; i < DescartesList.size(); i++) {
			Map<String, Object> oldobj = DescartesList.get(i);
			if (FindRecord(oldobj, oldList, NSA))
				act++;
			if (FindRecord(oldobj, changedList, NSA)) {
				est++;
			}
		}
		if (act > est)
			sum = (act - est) / act;
		else
			sum = (est - act) / act;
		return sum;
	}

	// 计算笛卡尔积的组划分后的表的错误率
	public Float ComputeDescartesBlock(
			List<List<Map<String, Object>>> BlockDetailListRandResult,
			List<LinkedList<Map<String, Object>>> BlockListResult,
			List<Map<String, Object>> DescartesList, String[] NSA) {
		Float act = 0.0f;
		Float est = 0.0f;
		Float sum = 0.0f;
		for (int i = 0; i < DescartesList.size(); i++) {
			Map<String, Object> oldobj = DescartesList.get(i);
			Iterator<LinkedList<Map<String, Object>>> olditerator = BlockListResult
					.iterator();
			Iterator<List<Map<String, Object>>> iterator = BlockDetailListRandResult
					.iterator();
			while (olditerator.hasNext()) {
				if (FindRecord(oldobj, olditerator.next(), NSA)) {
					act++;
					break;
				}
			}
			while (iterator.hasNext()) {
				if (FindRecord(oldobj, iterator.next(), NSA)) {
					est++;
					break;
				}
			}
		}
		if (act > est)
			sum = (act - est) / act;
		else
			sum = (est - act) / act;
		return sum;
	}

	// 计算笛卡尔积的组划分后的表的错误率
	public Float ComputeDescartesBlock2(
			List<List<Map<String, Object>>> BlockDetailListRandResult,
			List<List<Map<String, Object>>> BlockListResult,
			List<Map<String, Object>> DescartesList, String[] NSA) {
		Float act = 0.0f;
		Float est = 0.0f;
		Float sum = 0.0f;
		for (int i = 0; i < DescartesList.size(); i++) {
			Map<String, Object> oldobj = DescartesList.get(i);
			Iterator<List<Map<String, Object>>> olditerator = BlockListResult
					.iterator();
			Iterator<List<Map<String, Object>>> iterator = BlockDetailListRandResult
					.iterator();
			while (olditerator.hasNext()) {
				if (FindRecord(oldobj, olditerator.next(), NSA)) {
					act++;
					System.out.println("act = " + act);
					break;
				}
			}
			while (iterator.hasNext()) {
				if (FindRecord(oldobj, iterator.next(), NSA)) {
					est++;
					System.out.println("est = " + est);
					break;
				}
			}
		}
		if (act > est)
			sum = (act - est) / act;
		else
			sum = (est - act) / act;
		return sum;
	}

	// 取笛卡尔积的原始记录集
	public List<Map<String, Object>> GetOriginalList(String tablename)
			throws SQLException {
		String sql = "select * from " + tablename;
		List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> originalList = new ArrayList<Map<String, Object>>();
		ac = new AccessConnet();
		ac.connect();
		PreparedStatement pstmt = ac.con.prepareStatement(sql);
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
			tempList.add(adult);
		}
		Map<Integer, Object> flagMap = new HashMap<Integer, Object>();
		for (int i = 0; i < 100; i++) {
			Integer randnum = new Random().nextInt(tempList.size());
			while (flagMap.get(randnum) != null) {
				randnum = new Random().nextInt(tempList.size());
			}
			flagMap.put(randnum, new Object());
			originalList.add(tempList.get(randnum));
		}
		return originalList;
	}

	// 取笛卡尔积的记录集
	public List<Map<String, Object>> GetDescartesList(String tablename)
			throws SQLException {
		String sql = "select * from " + tablename;
		descartesList = new ArrayList<Map<String, Object>>();
		ac = new AccessConnet();
		ac.connect();
		PreparedStatement pstmt = ac.con.prepareStatement(sql);
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
			descartesList.add(adult);
		}
		return descartesList;
	}

	public List<Map<String, Object>> CreateDescartes(
			List<Map<String, Object>> originList, String[] NSA) {
		Map<String, Set<Object>> NSAMapSet = new HashMap<String, Set<Object>>();

		for (int i = 0; i < NSA.length; i++) {
			Set<Object> NSASet = new HashSet<Object>();
			for (Map<String, Object> obj : originList) {
				Object value = obj.get(NSA[i]);
				NSASet.add(value);
			}
			NSAMapSet.put(NSA[i], NSASet);
		}
		List<Map<String, Object>> descartesList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < originList.size(); i++) {
			Map<String, Object> record = new HashMap<String, Object>();
			record.put("occupation", originList.get(i).get("occupation"));
			CreateNSADescartesMap(record, NSAMapSet, NSA, 0, descartesList);
		}
		System.out.println("生成的笛卡尔积的记录数量: " + descartesList.size());
		return descartesList;
	}

	public void CreateNSADescartesMap(Map<String, Object> record,
			Map<String, Set<Object>> NSAMapSet, String[] NSA, int i,
			List<Map<String, Object>> descartesList) {
		if (i == NSA.length) {
			Map<String, Object> descartesRecord = new HashMap<String, Object>();
			descartesRecord.put("occupation", record.get("occupation"));
			for (String nsastr : NSA)
				descartesRecord.put(nsastr, record.get(nsastr));
			descartesList.add(descartesRecord);
			return;
		}
		Set<Object> NSAset = NSAMapSet.get(NSA[i]);
		for (Object value : NSAset) {
			record.put(NSA[i], value);
			CreateNSADescartesMap(record, NSAMapSet, NSA, i + 1, descartesList);
		}
	}

	// test
	public static void main(String[] args) {
		MyRandomization test = new MyRandomization();

		List<Map<String, Object>> originList = new ArrayList<Map<String, Object>>();
		originList.add(new HashMap<String, Object>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put("AA", "1");
				put("BB", "A");
				put("occupation", "S1");
			}
		});
		originList.add(new HashMap<String, Object>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put("AA", "2");
				put("BB", "B");
				put("occupation", "S2");
			}
		});
		originList.add(new HashMap<String, Object>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put("AA", "3");
				put("BB", "C");
				put("occupation", "S3");
			}
		});
		String[] NSA = { "AA", "BB" };
		for (Map<String, Object> obj : originList) {
			System.out.println(obj);
		}
		originList = test.CreateDescartes(originList, NSA);
		for (Map<String, Object> obj : originList) {
			System.out.println(obj);
		}
	}
}