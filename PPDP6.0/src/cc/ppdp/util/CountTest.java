package cc.ppdp.util;

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


/** count��ѯ ����NSA��SA����Դ����� */
public class CountTest {

	/**
	 * �Բ��÷����������������ݽ���count��ѯ�������ȷ�ʼ���
	 * 
	 * @param OrgGenBlockList
	 *            ԭʼ�ķ������ݿ�
	 * @param FinGenBlockList
	 *            ����������ݿ�
	 * @param record
	 *            ��ѯ����
	 * @serialData 2015.4.12
	 */
	public float CountForGen(List<LinkedList<Map<String, Object>>> OrgGenBlockList,
			List<LinkedList<Map<String, Object>>> FinGenBlockList, List<Map<String, Object>> recordList) {
		float avgResult = 0.0f;
		float queryNum = recordList.size();
		// ���в�ѯ
		int i=0;
		for (Map<String, Object> CountQuery : recordList) {
			float act = 0.0f;
			float est = 0.0f;
			// ��ԭʼ���ݽ���ͳ��
			for (LinkedList<Map<String, Object>> OrggenBlock : OrgGenBlockList) {
				act += getCountInBlock(OrggenBlock, CountQuery);
			}
			// ���������ݽ���ͳ��
			for (LinkedList<Map<String, Object>> FingenBlock : FinGenBlockList) {
				est += getCountInBlock(FingenBlock, CountQuery);
			}
			if (act >= est)
				avgResult += (act - est) / act;
			else
				avgResult += (est - act) / act;
			System.out.println("�Ƚϵ�"+i);
			i++;
		}
		avgResult = avgResult / queryNum;
		return avgResult;
	}

	/** ���ɲ�ѯ�������ݼ� */
	public List<Map<String, Object>> CreateCountQuery(int n, String tablename) {
		List<Map<String, Object>> queryList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>();
		AccessConnet ac = new AccessConnet();
		ac.connect();
		try {
			String orderstr = "select * from " + tablename;
			System.out.println(orderstr);
			PreparedStatement pstmt = ac.con.prepareStatement(orderstr);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> adult = new HashMap<String, Object>();
				/*
				 * adult.put("ID", rs.getString(1).trim()); adult.put("age",
				 * rs.getString(2).trim()); adult.put("sex",
				 * rs.getString(3).trim()); adult.put("education",
				 * rs.getString(4).trim()); adult.put("marital_status",
				 * rs.getString(5).trim()); adult.put("workclass",
				 * rs.getString(6).trim()); adult.put("relationship",
				 * rs.getString(7).trim()); adult.put("race",
				 * rs.getString(8).trim()); adult.put("occupation",
				 * rs.getString(9).trim());
				 */
				adult.put("ID", rs.getInt(1));
				adult.put("age", rs.getInt(2));
				adult.put("workclass", rs.getString(3));
				adult.put("education", rs.getString(5));
				adult.put("marital_status", rs.getString(7));
				adult.put("occupation", rs.getString(8));
				adult.put("relationship", rs.getString(9));
				adult.put("race", rs.getString(10));
				adult.put("sex", rs.getString(11));
				adult.put("class", rs.getString(16));
				/*
				 * adult.put("ID", rs.getInt(1)); adult.put("sex",
				 * rs.getString(2)); adult.put("education", rs.getString(3));
				 * adult.put("marital_status", rs.getString(4));
				 * adult.put("workclass", rs.getString(5));
				 * adult.put("relationship", rs.getString(6));
				 * adult.put("occupation", rs.getString(7)); adult.put("class",
				 * rs.getString(8));
				 */
				recordList.add(adult);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ac.close();

		for (int i = 0; i < recordList.size(); i = i + n) {
			int index = new Random().nextInt(n);
			if((i + index)<recordList.size()){
				Map<String, Object> record = recordList.get(i + index);
				queryList.add(record);
			}
		}

		return queryList;
	}

	/** �ӷ������и��ݲ�ѯ��������count */
	private float getCountInBlock(LinkedList<Map<String, Object>> genBlock, Map<String, Object> record) {
		float count = 0.0f;
		// 1 �ӷ��������ҵ�SAֵ����record��SAֵ�ĸ���
		float SAnum = GetSANum(genBlock, record, Common.SA);
		if (SAnum != 0) {
			// 2 �����record��NSA�ڷ������еĸ���
			float probability = GetNSAProbability(genBlock, record, Common.NSAs);
			count = SAnum * probability;
		}
		return count;
	}

	private float GetSANum(LinkedList<Map<String, Object>> genBlock, Map<String, Object> record, String SA) {
		float SAnum = 0.0f;
		String SAValue = record.get(SA).toString();
		for (Map<String, Object> obj : genBlock) {
			if (obj.get(SA).toString().equals(SAValue))
				SAnum++;
		}
		return SAnum;
	}

	/** ��ѯ�����ڷ�������Ϊ��ĸ��� */
	private float GetNSAProbability(LinkedList<Map<String, Object>> genBlock, Map<String, Object> record,
			String[] NSAs) {
		float probability = 1.0f;
		Map<String, Set<Object>> NSANumMap = new HashMap<String, Set<Object>>();
		for (String NSA : NSAs) {
			NSANumMap.put(NSA, new HashSet<Object>());
		}
		for (Map<String, Object> obj : genBlock) {
			for (String NSA : NSAs) {
				NSANumMap.get(NSA).add(obj.get(NSA));
			}
		}
		for (String NSA : NSAs) {
			if (NSANumMap.get(NSA).contains(record.get(NSA))) {
				float NSANum = NSANumMap.get(NSA).size();
				probability = probability * (1 / NSANum);
			} else {
				probability = 0;
				break;
			}
		}
		return probability;
	}

}
