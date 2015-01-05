package cc.ppdp.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Generalization {
	public List<List<String>> SABlockRandomList = new ArrayList<List<String>>();

	private LinkedList<LinkedList<Map<String, Object>>> CopyBloclList(
			LinkedList<LinkedList<Map<String, Object>>> blockList) {
		LinkedList<LinkedList<Map<String, Object>>> blockList2 = new LinkedList<LinkedList<Map<String, Object>>>();
		for (LinkedList<Map<String, Object>> block : blockList) {
			LinkedList<Map<String, Object>> block2 = new LinkedList<Map<String, Object>>();
			for (Map<String, Object> obj : block) {
				Map<String, Object> adult = new HashMap<String, Object>();
				adult.put("ID", obj.get("ID"));
				adult.put("age", obj.get("age"));
				adult.put("sex", obj.get("sex"));
				adult.put("education", obj.get("education"));
				adult.put("marital_status", obj.get("marital_status"));
				adult.put("workclass", obj.get("workclass"));
				adult.put("relationship", obj.get("relationship"));
				adult.put("race", obj.get("race"));
				adult.put("occupation", obj.get("occupation"));
				block2.add(adult);
			}
			blockList2.add(block2);
		}
		return blockList2;
	}

	public LinkedList<LinkedList<Map<String, Object>>> DoGeneral(
			LinkedList<LinkedList<Map<String, Object>>> blockList, String[] NSA) {
		LinkedList<LinkedList<Map<String, Object>>> blockList2 = CopyBloclList(blockList);
		for (LinkedList<Map<String, Object>> mapList : blockList2) {
			for (int i = 0; i < NSA.length; i++) {
				if (!NSA[i].equals("age")) {// 针对非数值型
					Set<Object> valueSet = new TreeSet<Object>();
					for (Map<String, Object> obj : mapList)
						valueSet.add(obj.get(NSA[i]));
					String str = "";
					if (valueSet.size() > 1) {
						for (Object strobj : valueSet)
							str = str + strobj + ",";
						for (Map<String, Object> obj : mapList) {
							obj.put(NSA[i], str);
						}
					}

				} else {// 针对数值类型
					float min = 10000.0f;
					float max = 0.0f;
					for (Map<String, Object> obj : mapList) {
						float temp = Float.parseFloat((String) obj.get(NSA[i]));
						if (temp > max)
							max = temp;
						if (temp < min)
							min = temp;
					}
					if (min < max) {
						String str = min + "-" + max;
						for (Map<String, Object> obj : mapList) {
							obj.put(NSA[i], str);
						}
					}
				}
			}
		}
		return blockList2;
	}

	public void DoPermutation(
			LinkedList<LinkedList<Map<String, Object>>> blockList, String SA) {
		for (LinkedList<Map<String, Object>> mapList : blockList) {
			List<String> SAList = new ArrayList<String>();
			for (Map<String, Object> obj : mapList) {
				SAList.add(obj.get(SA).toString());
			}
			// 打乱SAList
			Collections.shuffle(SAList);
			SABlockRandomList.add(SAList);
		}
	}
}
