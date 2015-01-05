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
/* 	function dogenaration(){
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
	} */
</script>
<body>
	<%
	List<List<SubBlock>> resultList  = (List<List<SubBlock>>) request
				.getAttribute("resultList");
	Long runningTime = (Long)request.getAttribute("runningTime");
	%>

	<%if(runningTime!=null){ %>
	运行时间：<%=runningTime %>ms
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
			<!-- <td></td> -->
		</tr>
		<%	int columnnum = 1;
			if (resultList != null && resultList.size() > 0) {
				for (List<SubBlock> BlockList : resultList) {
					int blocknum = 0;
					for(SubBlock sblock:BlockList){
						for(Map<String, Object>temp:sblock.recordList){
		%>
		<tr>
			<td><%=temp.get("occupation") %></td>	
			<td><%=(temp.get("age"))%></td>
			<td><%=(temp.get("sex"))%></td>
			<td><%=(temp.get("education"))%></td>
			<td><%=(temp.get("maritalstatus"))%></td>
			<td><%=(temp.get("workclass"))%></td>
			<td><%=(temp.get("relationship"))%></td>
			<td><%=(temp.get("race"))%></td>
			<td><%=(temp.get("ID"))%></td>
			<td><%=columnnum %></td>
		</tr>
		<%columnnum++;blocknum++;}
						%>
						<tr>
			<td>*****</td>
			<td>*****</td>
			<td>*****</td>
			<td>*****</td>
			<td>*****</td>
			<td>*****</td>
			<td>*****</td>
			<td>*****</td>
			<td>*****</td>
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
			<td>块的记录数:<%=blocknum %></td>		</tr>
					<% 
					//groupID++;
			}
			}
		%>

	</table>
</body>
</html>