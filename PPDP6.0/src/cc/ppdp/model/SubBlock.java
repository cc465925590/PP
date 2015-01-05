package cc.ppdp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubBlock {// 用于记录将大块细化划分的子块
	public Integer SuperBlockID;
	public List<Map<String, Object>> recordList;

	public SubBlock(Integer superBlockID) {
		SuperBlockID = superBlockID;
		recordList = new ArrayList<Map<String, Object>>();
	}

}
