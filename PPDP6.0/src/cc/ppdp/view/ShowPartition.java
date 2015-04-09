package cc.ppdp.view;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.ppdp.core.Anatomization;
import cc.ppdp.core.Correlation;
import cc.ppdp.core.Generalization;
import cc.ppdp.core.MyRandomization;
import cc.ppdp.core.Partition;
import cc.ppdp.core.WangKePartition;
import cc.ppdp.model.ColumnColrelation;
import cc.ppdp.model.SubBlock;
import cc.ppdp.util.Common;
import cc.ppdp.util.DataUtil;
import cc.ppdp.util.WekaType;

public class ShowPartition extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Partition test = null;
	private WangKePartition wangke = new WangKePartition();
	private String[] NSAstr;// ={"workclass","education","marital_status","race","relationship","sex"};
	private int Lvalue = 0;
	private List<List<Map<String, Object>>> BlockListRandResult;
	private List<List<Map<String, Object>>> WangKePartitionResult;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String method = request.getParameter("method");
		if ("partition".equals(method)) {
			Patition(request, response);
		} else if ("generation".equals(method)) {
			Generalization(request, response);
		} else if ("doCompare".equals(method)) {
			Anatomization(request, response);
		} else if ("BlockListRandomize".equals(method)) {
			BlockListRandomize(request, response);
		} else if ("BlockDetailRandomize".equals(method)) {
			BlockDetailRandomize(request, response);
		} else if ("Anatomization".equals(method)) {
			Anatomization(request, response);
		} else if ("PatitionView".equals(method)) {
			PatitionView(request, response);
		} else if ("DoDetailPartition".equals(method)) {
			DoDetailPartition(request, response);
		} else if ("DoWangkePartition".equals(method)) {
			DoWangkePartition(request, response);
		} else if ("DoWangkeBlockSASetRandomize".equals(method)) {
			DoWangkeBlockSASetRandomize(request, response);
		} else if ("DoWangkeBlockSARandomize".equals(method)) {
			DoWangkeBlockSARandomize(request, response);
		} else if ("DoWangKeDescartesCompare".equals(method)) {
			DoWangKeDescartesCompare(request, response);
		}
	}

	public void PatitionView(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("partition.jsp");
	}

	public void Patition(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();
		String[] attrs = request.getParameterValues("attr");
		String Lvalue = request.getParameter("Lvalue");
		if (Lvalue == null || Lvalue.trim().equals("")) {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("partition.jsp");
			dispatcher.forward(request, response);
			return;
		}
		int l = Integer.parseInt(Lvalue);
		this.Lvalue = l;
		System.out.println("L = " + l);
		if (attrs == null || attrs.length == 0) {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("partition.jsp");
			dispatcher.forward(request, response);
			return;
		}
		test = new Partition();
		String AttrStr = // "sex,education";
		// "race,age,sex";
		"race,age,sex,education,workclass";
		ColumnColrelation[] colObj;
		LinkedList<ColumnColrelation> columncolrelationList = new LinkedList<ColumnColrelation>();
		try {
			colObj = new Correlation().showAllCol(attrs);

			NSAstr = new String[colObj.length];
			int x = 0;
			for (ColumnColrelation columncolrelation : colObj) {
				NSAstr[x] = columncolrelation.getColName();
				columncolrelationList.add(columncolrelation);
				x++;
			}

			// 按属性相关性的顺序将集合划成分组
			test.DoPartition(NSAstr, l, 0);// type=0 表示用L多样性检测器 2015.1.9
			 //test.DoPartition(NSAstr, l, 1);// type=1 表示用递归(c,L)多样性检测器
			// 2015.1.9
			request.setAttribute("columncolrelationList", columncolrelationList);
			request.setAttribute("resultList", test.resultList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		request.setAttribute("runningTime", endTime - startTime);
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("partition.jsp");
		dispatcher.forward(request, response);
	}

	public void Generalization(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Generalization general = new Generalization();
		LinkedList<LinkedList<Map<String, Object>>> resultList = general
				.DoGeneral(test.resultList, NSAstr);
		general.DoPermutation(test.resultList, "occupation");
		request.setAttribute("SABlockRandomList", general.SABlockRandomList);
		request.setAttribute("resultList", resultList);
		request.setAttribute("NSAstr", this.NSAstr);
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("generation.jsp");
		dispatcher.forward(request, response);
	}

	public void Anatomization(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 表切割
		Anatomization anatomization = new Anatomization();
		anatomization.DoAnatomy(test.resultList, "occupation");

		request.setAttribute("STBlockList", anatomization.STBlockList);// 切割后的SA表
		request.setAttribute("resultList", test.resultList);
		request.setAttribute("NSAstr", this.NSAstr);
		// 2015.1.4 start
		List<List<String>> SABlockList = anatomization.RandomSwap(
				anatomization.STBlockList, "occupation");// 用于生成随机替换的SA集合
	/*	WekaType wekatype = new WekaType();
		wekatype.DataToFileForAT(
				"D:/WekaData/Anatomization" + wekatype.TimeToStr(),
				test.resultList, SABlockList);// 生成用于weka的数据文件
*/		// end
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("Anatomization.jsp");
		dispatcher.forward(request, response);
	}

	public void BlockListRandomize(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int L = this.Lvalue;
		MyRandomization myRandomization = new MyRandomization();
		BlockListRandResult = new ArrayList<List<Map<String, Object>>>();
		for (LinkedList<Map<String, Object>> Block : test.resultList) {
			List<Map<String, Object>> NewBlock = myRandomization
					.BlockRandomize(L, Block);
			BlockListRandResult.add(NewBlock);
		}
		request.setAttribute("resultList", BlockListRandResult);
		request.setAttribute("NSAstr", this.NSAstr);
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("BlockListRandomize.jsp");
		dispatcher.forward(request, response);
	}

	public void BlockDetailRandomize(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		MyRandomization myRandomization = new MyRandomization();
		List<List<Map<String, Object>>> BlockDetailListRandResult = new ArrayList<List<Map<String, Object>>>();
		for (List<Map<String, Object>> Block : this.BlockListRandResult) {
			List<Map<String, Object>> NewBlock = myRandomization
					.SASetRandomize(Block);
			BlockDetailListRandResult.add(NewBlock);
		}
		request.setAttribute("resultList", BlockDetailListRandResult);
		// 计算查询准确率
		Iterator<LinkedList<Map<String, Object>>> olditerator = test.resultList
				.iterator();
		Iterator<List<Map<String, Object>>> iterator = BlockDetailListRandResult
				.iterator();
		while (olditerator.hasNext() && iterator.hasNext()) {
			LinkedList<Map<String, Object>> oldblock = olditerator.next();
			List<Map<String, Object>> block = iterator.next();
			myRandomization.Compare(oldblock, block, this.NSAstr);
		}
		float value = myRandomization.getCompareResult();
		request.setAttribute("resultvalue", value);
		Float relativeError = null;// 笛卡尔积的相对错误率
		if (Common.DESCARTESTABLE != null
				&& !Common.DESCARTESTABLE.trim().equals("")) {
			if (Common.DESCARTESLIST == null) {
				try {
					// myRandomization.GetDescartesList(Common.DESCARTESTABLE);
					List<Map<String, Object>> originalList = myRandomization
							.GetOriginalList(Common.DESCARTESTABLE);
					// myRandomization.descartesList
					/*Common.DESCARTESLIST = myRandomization.CreateDescartes(
							originalList, this.NSAstr);*/
					Common.DESCARTESLIST = originalList;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			relativeError = myRandomization.ComputeDescartesBlock(
					BlockDetailListRandResult, test.resultList,
					Common.DESCARTESLIST, this.NSAstr);
			request.setAttribute("relativeError", relativeError);
		}
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("BlockDetailRandomize.jsp");
		dispatcher.forward(request, response);
	}

	public void DoDetailPartition(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		List<List<SubBlock>> resultList = test.RefiningPartition(
				test.resultList, this.Lvalue);
		request.setAttribute("resultList", resultList);
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("DetailPartition.jsp");
		dispatcher.forward(request, response);
	}

	public void DoWangkePartition(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Integer Lvalue = 0;
		if (request.getParameter("Lvalue") != null) {
			Lvalue = Integer.parseInt(request.getParameter("Lvalue"));
			this.Lvalue = Lvalue;
		}
		if (request.getParameter("Lvalue") == null
				|| request.getParameter("Lvalue").equals("")) {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("WangkePartition.jsp");
			dispatcher.forward(request, response);
			return;
		}

		WangKePartition.GetOriginalSet();
		List<List<Map<String, Object>>> resultList = wangke.Partition(
				WangKePartition.ORIGINALRECORDLIST, Lvalue);
		this.wangke.WangkePartitionResult = resultList;
		request.setAttribute("resultList", resultList);
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("WangkePartition.jsp");
		dispatcher.forward(request, response);
	}

	public void DoWangkeBlockSASetRandomize(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int L = this.Lvalue;
		MyRandomization myRandomization = new MyRandomization();
		this.wangke.WangkeRandomSASetResult = new ArrayList<List<Map<String, Object>>>();
		for (List<Map<String, Object>> Block : this.wangke.WangkePartitionResult) {
			List<Map<String, Object>> NewBlock = myRandomization
					.BlockRandomize(L, Block);
			this.wangke.WangkeRandomSASetResult.add(NewBlock);
		}
		request.setAttribute("resultList", this.wangke.WangkeRandomSASetResult);
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("WangkeBlockSASetRandomize.jsp");
		dispatcher.forward(request, response);
	}

	public void DoWangkeBlockSARandomize(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		MyRandomization myRandomization = new MyRandomization();
		List<List<Map<String, Object>>> BlockDetailListRandResult = new ArrayList<List<Map<String, Object>>>();
		for (List<Map<String, Object>> Block : this.wangke.WangkeRandomSASetResult) {
			List<Map<String, Object>> NewBlock = myRandomization
					.SASetRandomize(Block);
			BlockDetailListRandResult.add(NewBlock);
		}
		this.wangke.WangkeRandomSAResult = BlockDetailListRandResult;
		/*WekaType wekatype = new WekaType();

		wekatype.DataToWeka("D:/WekaData/wangke" + wekatype.TimeToStr(),
				BlockDetailListRandResult);*/
		request.setAttribute("resultList", BlockDetailListRandResult);
		// 计算查询准确率
		/*
		 * Iterator<LinkedList<Map<String, Object>>> olditerator =
		 * test.resultList .iterator(); Iterator<List<Map<String, Object>>>
		 * iterator = BlockDetailListRandResult .iterator(); while
		 * (olditerator.hasNext() && iterator.hasNext()) {
		 * LinkedList<Map<String, Object>> oldblock = olditerator.next();
		 * List<Map<String, Object>> block = iterator.next();
		 * myRandomization.Compare(oldblock, block, this.NSAstr); } float value
		 * = myRandomization.getCompareResult();
		 * request.setAttribute("resultvalue", value);
		 */
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("WangkeBlockSARandomize.jsp");
		dispatcher.forward(request, response);
	}

	public void DoWangKeDescartesCompare(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		MyRandomization myRandomization = new MyRandomization();
		if (Common.DESCARTESTABLE != null
				&& !Common.DESCARTESTABLE.trim().equals("")) {
			String[] attrs = request.getParameterValues("attr");
			if (Common.DESCARTESLIST == null) {
				try {
					// myRandomization.GetDescartesList(Common.DESCARTESTABLE);
					List<Map<String, Object>> originalList = myRandomization
							.GetOriginalList(Common.DESCARTESTABLE);
					// myRandomization.descartesList
//					Common.DESCARTESLIST = myRandomization.CreateDescartes(
//							originalList, attrs);
					// 修改20150320
					Common.DESCARTESLIST = originalList;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Float relativeError = myRandomization.ComputeDescartesBlock2(
					this.wangke.WangkeRandomSAResult,
					this.wangke.WangkePartitionResult, Common.DESCARTESLIST,
					attrs);
			response.getWriter().println(relativeError);
		}
	}
}
