package cc.ppdp.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Anatomization {
	// ���ڱ����ӦST��Ŀ�
	public LinkedList<Map<Object, Integer>> STBlockList = new LinkedList<Map<Object, Integer>>();

	public void DoAnatomy(
			LinkedList<LinkedList<Map<String, Object>>> blockList, String SA) {
		for (LinkedList<Map<String, Object>> mapList : blockList) {
			Map<Object, Integer> STmap = new HashMap<Object, Integer>();
			for (Map<String, Object> obj : mapList) {
				if (STmap.get(obj.get(SA)) == null)
					STmap.put(obj.get(SA), 1);
				else
					STmap.put(obj.get(SA), STmap.get(obj.get(SA)) + 1);
			}
			//
			STBlockList.add(STmap);
		}
	}

	/**
	 * ����L-diversity���������
	 * 
	 * @2015.1.4
	 * */
	public List<List<String>> RandomSwap(
			LinkedList<Map<Object, Integer>> STBlockList, String SA) {
		List<List<String>> SABlockList = new ArrayList<List<String>>();
		for (Map<Object, Integer> obj : STBlockList) {// map���ŵ���һ�����ֿ��е�SA���ϼ���Ӧ�ĸ���
			Set<Object> SAset = obj.keySet();
			List<String> SAList = new ArrayList<String>();
			// ����map
			for (Object tempSA : SAset) {
				int num = obj.get(tempSA);
				for (int i = 0; i < num; i++)
					SAList.add(tempSA.toString());
			}
			Collections.shuffle(SAList);// �������˳��
			SABlockList.add(SAList);
		}
		return SABlockList;
	}
}
