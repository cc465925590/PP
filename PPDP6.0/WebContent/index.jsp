<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<script language="javascript" src="<%=request.getContextPath()%>/jquery.js"></script>  
<script type="text/javascript">
	function Randomization() {
		window.open("Randomization.jsp");
	}
	function doAnatomization() {
		window.open("http://localhost:8080/PPDP/ShowPartition?method=Anatomization");
	}
	function doPartition(){
		window
		.open("http://localhost:8080/PPDP/ShowPartition?method=PatitionView");
	}
	function doWangkePartition(){
		window
		.open("http://localhost:8080/PPDP/ShowPartition?method=DoWangkePartition");
	}
	function doRecordNum(){
		 var table = $("#table").val();
		 var Descartestable = $("#Descartestable").val();
		 alert(table);
		  $.ajax({
			  type:"post",//请求方式
			  url:"http://localhost:8080/PPDP/ShowRandomization?method=SetRecordNum&table="+table+"&Descartestable="+Descartestable,//发送请求地址
			   //请求成功后的回调函数有两个参数
			   datatype:"text",
			   success:function(data){
			    alert("设置成功!"+data);
			   }
			   });
	}
</script>
<body>
	<table>
		<tr>
			<td><button onclick="Randomization()">对整表进行随机化</button></td>
			<td><button onclick="doPartition()">对表进行分组</button></td>
			<td><button onclick="doWangkePartition()">王珂分组算法</button></td>
		</tr>
		<tr>
		<td>输入要操作的表<input type="text" id="table" name="table"></td>
		<td>输入要操作的笛卡尔积表<input type="text" id="Descartestable" name="Descartestable"></td>
		<td><button onclick="doRecordNum()">提交</button></td>
		</tr>
	</table>
</body>
</html>