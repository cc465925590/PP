package cc.ppdp.view;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.ppdp.core.MyRandomization;
import cc.ppdp.util.Common;

public class ShowRandomization extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	MyRandomization randomization = null;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String method = request.getParameter("method");
		if ("Randomization".equals(method)) {
			try {
				Randomization(request, response);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("SASetRandomize".equals(method)) {
			SASetRandomize(request, response);
		} else if ("DoCompare".equals(method)) {
			DoCompare(request, response);
		} else if ("SetRecordNum".equals(method)) {
			SetRecordNum(request, response);
		} else if ("DoDescartesCompare".equals(method)) {
			DoDescartesCompare(request, response);
		}
	}

	public void Randomization(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			SQLException {
		long startTime = System.currentTimeMillis();
		int L = Integer.parseInt(request.getParameter("Lvalue"));
		randomization = new MyRandomization();
		List<Map<String, Object>> resultList = randomization.TableRandomize(L);
		request.setAttribute("resultList", resultList);
		long endTime = System.currentTimeMillis();
		request.setAttribute("runningTime", endTime - startTime);
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("Randomization.jsp");
		dispatcher.forward(request, response);
	}

	public void SASetRandomize(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();
		String[] attrs = request.getParameterValues("attr");
		List<Map<String, Object>> resultList = randomization
				.SASetRandomize(randomization.recordList);
		randomization.newrecord = resultList;
		request.setAttribute("resultList", resultList);
		long endTime = System.currentTimeMillis();
		request.setAttribute("runningTime", endTime - startTime);
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("SASetRandomize.jsp");
		dispatcher.forward(request, response);
	}

	public void DoCompare(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String[] attrs = request.getParameterValues("attr");
		List<Map<String, Object>> resultList = randomization.newrecord;
		randomization.Compare(randomization.oldrecordList, resultList, attrs);
		Float resultValue = randomization.getCompareResult();
		response.getWriter().println(resultValue);
	}

	public void SetRecordNum(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String table = request.getParameter("table");
		String Descartestable = request.getParameter("Descartestable");
		if (table != null && !table.trim().equals(""))
			Common.TABLENAME = table;
		if (Descartestable != null && !Descartestable.trim().equals(""))
			Common.DESCARTESTABLE = Descartestable;
		Common.DESCARTESLIST = null;
		response.getWriter().println(
				" table = " + table + " Descartestable = " + Descartestable);
	}

	public void DoDescartesCompare(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (Common.DESCARTESTABLE != null
				&& !Common.DESCARTESTABLE.trim().equals("")) {
			String[] attrs = request.getParameterValues("attr");
			List<Map<String, Object>> resultList = randomization.newrecord;
			if (Common.DESCARTESLIST == null) {
				try {
					List<Map<String, Object>> originalList = randomization
							.GetOriginalList(Common.DESCARTESTABLE);
					// randomization.descartesList
					Common.DESCARTESLIST = randomization.CreateDescartes(
							originalList, attrs);

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Float relativeError = randomization.ComputeDescartesTable(
					randomization.oldrecordList, resultList,
					Common.DESCARTESLIST, attrs);
			response.getWriter().println(relativeError);
		}
	}
}
