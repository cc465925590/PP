<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@page import="java.util.*"%>
<%@page import="cc.ppdp.model.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<script language="javascript" src="<%=request.getContextPath()%>/jquery.js"></script> 
<script type="text/javascript">
	function dogenaration(){
		window.open("http://localhost:8080/PPDP/ShowPartition?method=generation");
	}
	function doAnatomization(){
		window.open("http://localhost:8080/PPDP/ShowPartition?method=Anatomization");
	}
	function doBlockListRandomize(){
		window.open("http://localhost:8080/PPDP/ShowPartition?method=BlockListRandomize");
	}
	function SelectAll111() {
		 var checkboxs=document.getElementsByName("attr");
		 for (var i=0;i<checkboxs.length;i++) {
		  var e=checkboxs[i];
		  e.checked=!e.checked;
		 }
		}
	function doPartition(){
		document.form1.submit();
	}
	function doDetailPartion(){
		window.open("http://localhost:8080/PPDP/ShowPartition?method=DoDetailPartition");
	}
</script>
<body>
<button onclick="javascript:SelectAll111();">全选/反选</button>
	<form action="ShowPartition?method=partition" name="form1" id="form1" method="post">
	<table>
	<tr>
	<td><input value="age" name="attr" type="checkbox" >age</td>
	<td><input value="sex" name="attr" type="checkbox" >sex</td>
	<td><input value="education" name="attr" type="checkbox" >education</td>
	<td><input value="marital_status" name="attr" type="checkbox" >marital_status</td>
	<td><input value="workclass" name="attr" type="checkbox" >workclass</td>
	<td><input value="relationship" name="attr" type="checkbox" >relationship</td>
	<td><input value="race" name="attr" type="checkbox" >race</td>
	</tr>
	</table>
	<table>
	<tr>
	<td>L的值:<input type="text" name="Lvalue" id="Lvalue"></td>
	<td><input type="submit"></td>
	</tr>
	
	</table>
	</form>
	<%
	LinkedList<LinkedList<Map<String, Object>>> resultList  = (LinkedList<LinkedList<Map<String, Object>>>) request
				.getAttribute("resultList");
	LinkedList<ColumnColrelation> columncolrelationList = (LinkedList<ColumnColrelation>) request
				.getAttribute("columncolrelationList");
	LinkedList<Map<Object, Integer>> STBlockList = (LinkedList<Map<Object, Integer>>) request.getAttribute("STBlockList");
	List<List<String>> SABlockRandomList = (List<List<String>>) request.getAttribute("SABlockRandomList");
	Long runningTime = (Long)request.getAttribute("runningTime");
	%>
	<table>
		<tr>
			<td width="30%">NSA</td>
			<td width="30%">colrelation</td>
		</tr>
		<%
			if (columncolrelationList != null) {
				for (ColumnColrelation tempcolobj : columncolrelationList) {
		%>
		<tr>
			<td><%=tempcolobj.getColName()%></td>
			<td><%=tempcolobj.getColrelatValue()%></td>
		</tr>
		<%
			}
			}
		%>

	</table>
	<%if(runningTime!=null){ %>
	运行时间：<%=runningTime %>ms
	<table>
	<tr>
	<td><button onclick="javascript:dogenaration();">概化</button></td>
	<td><button onclick="javascript:doAnatomization();">表划分</button></td>
	<td><button onclick="javascript:doBlockListRandomize();">分组随机化</button></td>
	<td><button onclick="javascript:doDetailPartion();">细化</button></td>
	</tr>
	</table>
	<%} %>
	<table border="1" cellspacing="0">
		<tr>
		<td width="10%" style="background:gray;">occupation</td>
			<!-- <td width="5%" style="background:gray;">GroupID</td> -->
			<td width="10%" style="background:gray;">age</td>
			<td width="5%" style="background:gray;">sex</td>
			<td width="10%" style="background:gray;">education</td>
			<td width="15%" style="background:gray;">maritalstatus</td>
			<td width="10%" style="background:gray;">workclass</td>
			<td width="10%" style="background:gray;">relationship</td>
			<td width="10%" style="background:gray;">race</td>
			<td width="10%" style="background:gray;">ID</td>
			<td width="10%" style="background:gray;">序号</td>
			<!-- <td></td> -->
		</tr>
		<%	int columnnum = 1;
			if (resultList != null && resultList.size() > 0) {
				for (LinkedList<Map<String, Object>> tempList : resultList) {
					int blocknum = 0;
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
			<td><%=columnnum %></td>
		</tr>
		<%columnnum++;blocknum++;
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
			<td>块的记录数:<%=blocknum %></td>		</tr>
					<% 
					//groupID++;
			}
			}
		%>

	</table>
</body>
</html>