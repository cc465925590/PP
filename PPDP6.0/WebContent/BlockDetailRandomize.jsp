<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@page import="java.util.*"%>
<%@page import="cc.ppdp.model.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>分块SA集随机化</title>
</head>
<script type="text/javascript">
	function dogenaration(){
		window.open("http://localhost:8080/PPDP/ShowPartition?method=generation");
	}
	function doAnatomization(){
		window.open("http://localhost:8080/PPDP/ShowPartition?method=Anatomization");
	}
</script>
<body>
	<%
	List<List<Map<String, Object>>> resultList  = (List<List<Map<String, Object>>>) request
				.getAttribute("resultList");
	Long runningTime = (Long)request.getAttribute("runningTime");
	%>
	<%if(runningTime!=null){ %>
	运行时间：<%=runningTime %>ms
	<table>
	<tr>
	<td><button></button></td>
	</tr>
	</table>
	<%} %>
	查询准确率：<%=request.getAttribute("resultvalue") %>
	经过笛卡尔积计的中间表算出的错误率为:<%=request.getAttribute("relativeError") %>
	<table border="1" cellspacing="0">
		<tr>
		<td width="10%" style="background:gray;">occupation</td>
			<!-- <td width="5%" style="background:gray;">GroupID</td> -->
			<td width="10%" style="background:gray;">age</td>
			<td width="5%" style="background:gray;">sex</td>
			<td width="10%" style="background:gray;">education</td>
			<td width="15%" style="background:gray;">marital_status</td>
			<td width="10%" style="background:gray;">workclass</td>
			<td width="10%" style="background:gray;">relationship</td>
			<td width="10%" style="background:gray;">race</td>
			<td width="10%" style="background:gray;">ID</td>
			<!-- <td></td> -->
		</tr>
		<%	
			if (resultList != null && resultList.size() > 0) {
				for (List<Map<String, Object>> tempList : resultList) {
					for(Map<String, Object>temp:tempList){
		%>
		<tr>
			<td><%=temp.get("occupation") %></td>	
			<td><%=(temp.get("age"))%></td>
			<td><%=(temp.get("sex"))%></td>
			<td><%=(temp.get("education"))%></td>
			<td><%=(temp.get("marital_status"))%></td>
			<td><%=(temp.get("workclass"))%></td>
			<td><%=(temp.get("relationship"))%></td>
			<td><%=(temp.get("race"))%></td>
			<td><%=(temp.get("ID"))%></td>
		</tr>
		<%
			}
					%>
					<tr>
			<td>----</td>
			<td>----</td>
			<td>----</td>
			<td>----</td>
			<td>----</td>
			<td>----</td>
			<td>----</td>
			<td>----</td>
			<td>----</td>
					</tr>
					<% 
					//groupID++;
			}
			}
		%>

	</table>
</body>
</html>