package cc.ppdp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubBlock {// ���ڼ�¼�����ϸ�����ֵ��ӿ�
	public Integer SuperBlockID;
	public List<Map<String, Object>> recordList;

	public SubBlock(Integer superBlockID) {
		SuperBlockID = superBlockID;
		recordList = new ArrayList<Map<String, Object>>();
	}

}
