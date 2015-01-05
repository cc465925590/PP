package cc.ppdp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RBRecord {
	public Integer SuperBlockID;
	public Map<String, Object> Obj;

	public RBRecord(Integer superBlockID, Map<String, Object> obj) {
		SuperBlockID = superBlockID;
		Obj = new HashMap<String, Object>();
		for (Entry<String, Object> e : obj.entrySet()) {
			Obj.put(e.getKey(), e.getValue());
		}
	}
}
