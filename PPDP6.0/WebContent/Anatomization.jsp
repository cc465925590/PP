<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@page import="java.util.*"%>
<%@page import="cc.ppdp.model.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<%
	LinkedList<LinkedList<Map<String, Object>>> resultList  = (LinkedList<LinkedList<Map<String, Object>>>) request
				.getAttribute("resultList");
	LinkedList<Map<Object, Integer>> STBlockList = (LinkedList<Map<Object, Integer>>) request
			.getAttribute("STBlockList");
	String []NSAstr = (String [])request.getAttribute("NSAstr");
	%>
	<table border="1" cellspacing="0">
		<tr>
			<td   style="background:gray;">GroupID</td>
			<td  style="background:gray;">occupation</td>
			<td   style="background:gray;">count</td>
			<% if(STBlockList!=null && STBlockList.size()>0){
				int GroupID = 1;
				for(Map<Object, Integer> STmap : STBlockList){
					Set<Object> key = STmap.keySet();
					for (Iterator<Object> it = key.iterator(); it.hasNext();) {
						String s = (String) it.next();
						%>
						<tr>
						<td><%=GroupID %></td>
						<td><%=s %></td>
						<td><%=STmap.get(s) %></td>
						</tr>
						<%
					}
					GroupID++;
					%>
					<%
				}
			}%>
		</tr>
	</table>
	----------------------------------------
	<table border="1" cellspacing="0">
		<tr>
				<%if(NSAstr!=null && NSAstr.length>0){
			for(String str:NSAstr){
				if(str.equals("age")){
					%>
					<td width="10%" style="background:gray;">age</td>
					<%
				}
				if(str.equals("sex")){
					%>
					<td width="5%" style="background:gray;">sex</td>
					<%
				}
				if(str.equals("education")){
					%>
					<td width="10%" style="background:gray;">education</td>
					<%
				}
				if(str.equals("maritalstatus")){
					%>
					<td width="10%" style="background:gray;">maritalstatus</td>
					<%
				}
				if(str.equals("workclass")){
					%>
					<td width="10%" style="background:gray;">workclass</td>
					<%
				}
				if(str.equals("relationship")){
					%>
					<td width="10%" style="background:gray;">relationship</td>
					<%
				}
				if(str.equals("race")){
					%>
					<td width="10%" style="background:gray;">race</td>
					<%
				}
			}
			%>
				<td width="5%" style="background:gray;">GroupID</td>
			<%
		} %>
			<!-- <td width="5%" style="background:gray;">GroupID</td>
			<td width="10%" style="background:gray;">age</td>
			<td width="5%" style="background:gray;">sex</td>
			<td width="10%" style="background:gray;">education</td>
			<td width="15%" style="background:gray;">maritalstatus</td>
			<td width="10%" style="background:gray;">workclass</td>
			<td width="10%" style="background:gray;">relationship</td>
			<td width="10%" style="background:gray;">race</td> -->
		</tr>
		<%	
			if (resultList != null && resultList.size() > 0) {
				int groupID = 1;
				for (LinkedList<Map<String, Object>> tempList : resultList) {
					for(Map<String, Object>temp:tempList){
						String SAvalue = "";
					//System.out.println(temp);
		%>
		<tr>
		<%
			for(String str:NSAstr){
		%>
			<td><%=(temp.get(str))%></td>

		<%}%>
			<td><%=groupID %></td>
			<%-- <td><%=groupID %></td>	
			<td><%=(temp.get("age"))%></td>
			<td><%=(temp.get("sex"))%></td>
			<td><%=(temp.get("education"))%></td>
			<td><%=(temp.get("maritalstatus"))%></td>
			<td><%=(temp.get("workclass"))%></td>
			<td><%=(temp.get("relationship"))%></td>
			<td><%=(temp.get("race"))%></td>
			<td><%=(temp.get("ID"))%></td> --%>
		</tr>
		<%
			}
				groupID++;
				%>
				<tr><td>----------------------</td></tr>
				<%
			}
			}
		%>

	</table>
</body>
</html>