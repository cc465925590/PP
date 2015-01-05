<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@page import="java.util.*"%>
<%@page import="cc.ppdp.model.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>整表SA随机化</title>
</head>
<script language="javascript" src="<%=request.getContextPath()%>/jquery.js"></script>  
<script type="text/javascript">
	/* function doCompar2e() {
		alert($("[name='attr']"));
		var selectVal = new Array(); 
		var selectCheckbox = $("[name='attr']:checked").size(); 
		   $("[name='attr']:checked").each(function(){ 
		      selectVal.push('attr='+$(this).val()); 
		    });
		   selectVal = selectVal.join("&");
		   alert(selectVal);
		$.ajax({
			  type:"post",//请求方式
			  url:"http://localhost:8080/PPDP/ShowRandomization?method=doCompare",//发送请求地址
			   //请求成功后的回调函数有两个参数
			   success:function(data,textStatus){
			    $("#resText").html(data);
			   }
			   });
	}
	
	function doCompare2() {
			 val url="http://localhost:8080/PPDP/ShowRandomization?method=doCompare";//发送请求地址
	} */
	function doCompare(){
		var selectVal = new Array(); 
		var selectCheckbox = $("[name='attr']:checked").size(); 
		if(selectCheckbox==0)
			return;
		   $("[name='attr']:checked").each(function(){ 
		      selectVal.push('attr='+$(this).val()); 
		    });
		   selectVal = selectVal.join("&");
		   $.ajax({
				  type:"post",//请求方式
				  url:"http://localhost:8080/PPDP/ShowRandomization?method=DoCompare&"+selectVal,//发送请求地址
				   //请求成功后的回调函数有两个参数
				   datatype:"text",
				   success:function(data){
				    $("#resultvalue").text("查询准确率："+data)
				   }
				   });
	}
	function doDescartesCompare(){
		var selectVal = new Array(); 
		var selectCheckbox = $("[name='attr']:checked").size(); 
		if(selectCheckbox==0)
			return;
		   $("[name='attr']:checked").each(function(){ 
		      selectVal.push('attr='+$(this).val()); 
		    });
		   selectVal = selectVal.join("&");
		   $.ajax({
				  type:"post",//请求方式
				  url:"http://localhost:8080/PPDP/ShowRandomization?method=DoDescartesCompare&"+selectVal,//发送请求地址
				   //请求成功后的回调函数有两个参数
				   datatype:"text",
				   success:function(data){
				    $("#relativeError").text("相对错误率："+data)
				   }
				   });
	}
	function SelectAll() {
		 var checkboxs=document.getElementsByName("attr");
		 for (var i=0;i<checkboxs.length;i++) {
		  var e=checkboxs[i];
		  e.checked=!e.checked;
		 }
		}
</script>
<body>
	<%
		List<Map<String, Object>> resultList = (List<Map<String, Object>>) request
				.getAttribute("resultList");
		Long runningTime = (Long) request.getAttribute("runningTime");
	%>
	<%
		if (runningTime != null) {
	%>
	运行时间：<%=runningTime%>ms	
	<%} %>
	<div id="resultvalue"></div>
	<div id="relativeError"></div>
		<table>
			<tr>
				<td><input value="age" id="age" name="attr" type="checkbox">age</td>
				<td><input value="sex" name="attr" type="checkbox">sex</td>
				<td><input value="education" name="attr" type="checkbox">education</td>
				<td><input value="marital_status" name="attr" type="checkbox">marital_status</td>
				<td><input value="workclass" name="attr" type="checkbox">workclass</td>
				<td><input value="relationship" name="attr" type="checkbox">relationship</td>
				<td><input value="race" name="attr" type="checkbox">race</td>
				<td><button onclick="SelectAll()">全选/反选</button></td>
			</tr>
		</table>
		<table>
			<tr>
				<td><button onclick="javascript:doCompare();">选择要作为NSA的字段并计算查询准确率</button></td>
				<td><button onclick="javascript:doDescartesCompare();">选择要作为NSA的字段并计算笛卡尔积相对错误率</button></td>
			</tr>
		</table>
	<table border="1" cellspacing="0">
		<tr>
			<td width="15%" style="background: gray;">occupation</td>
			<td width="10%" style="background: gray;">age</td>
			<td width="5%" style="background: gray;">sex</td>
			<td width="10%" style="background: gray;">education</td>
			<td width="15%" style="background: gray;">maritalstatus</td>
			<td width="10%" style="background: gray;">workclass</td>
			<td width="10%" style="background: gray;">relationship</td>
			<td width="10%" style="background: gray;">race</td>
			<td width="5%" style="background: gray;">ID</td>
			<!-- <td></td> -->
		</tr>
		<%
			if (resultList != null && resultList.size() > 0) {
				for (Map<String, Object> temp : resultList) {
		%>
		<%-- <tr>
			<td><%=temp.get("occupation")%></td>
			<td><%=(temp.get("age"))%></td>
			<td><%=(temp.get("sex"))%></td>
			<td><%=(temp.get("education"))%></td>
			<td><%=(temp.get("maritalstatus"))%></td>
			<td><%=(temp.get("workclass"))%></td>
			<td><%=(temp.get("relationship"))%></td>
			<td><%=(temp.get("race"))%></td>
			<td><%=(temp.get("ID"))%></td>
		</tr> --%>
		<%
			}
			}
		%>

	</table>
</body>
</html>