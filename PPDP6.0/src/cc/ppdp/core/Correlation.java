package cc.ppdp.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import cc.ppdp.model.ColumnColrelation;
import cc.ppdp.util.AccessConnet;
import cc.ppdp.util.Common;
import cc.ppdp.view.ShowPartition;

public class Correlation {
	public ColumnColrelation[] showAllCol(String[] str) throws SQLException {
		String[] col = str;
		String SAcol = "occupation";
		ColumnColrelation[] colObj = new ColumnColrelation[col.length];
		int i = 0;
		for (String temp : col) {
			// System.out.print("col = " + temp + "\t");
			ColumnColrelation tempobj = new ColumnColrelation();
			tempobj.setColName(temp);
			tempobj.setColrelatValue(colrelation(temp, SAcol));
			colObj[i] = tempobj;
			i++;
		}
		// 按关联度大小对数组进行排序
		for (int x = 0; x < colObj.length; x++) {
			Boolean flag = false;
			for (int y = colObj.length - 1; y > x; y--) {
				if (colObj[y - 1].getColrelatValue() < colObj[y]
						.getColrelatValue()) {
					ColumnColrelation tempobj = colObj[y - 1];
					colObj[y - 1] = colObj[y];
					colObj[y] = tempobj;
					flag = true;
				}
			}
			if (!flag)
				break;

		}
		return colObj;
	}

	public float colrelation(String NSAcol, String SAcol) {
		String sql = "";
		float relation = 0.0f;
		float num = 1;
		try {
			AccessConnet ac = new AccessConnet();
			PreparedStatement NSAstat;
			PreparedStatement SAstat;
			PreparedStatement Costat;
			PreparedStatement stmt;
			ac.connect();
			stmt = ac.con.prepareStatement("select " + Common.RECORDNUM
					+ " count(*) from " + Common.TABLENAME);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				num = Integer.parseInt(rs.getString(1));
			// NSA 预处理
			sql = "select " + Common.RECORDNUM + " " + NSAcol + ",count("
					+ NSAcol + ") from " + Common.TABLENAME + " group by "
					+ NSAcol;
			System.out.println(sql);
			NSAstat = ac.con.prepareStatement(sql);
			ResultSet NSArs = NSAstat.executeQuery();
			ResultSetMetaData NSAmete = NSArs.getMetaData();

			// SA 预处理
			sql = "select " + Common.RECORDNUM + " " + SAcol + ",count("
					+ SAcol + ") from " + Common.TABLENAME + " group by "
					+ SAcol;
			SAstat = ac.con.prepareStatement(sql);
			ResultSet SArs = SAstat.executeQuery();
			ResultSetMetaData SAmete = SArs.getMetaData();
			// 计算关联度算法
			float sum = 0.0f;
			float NSAnum = 0.0f;
			float SAnum = 0.0f;
			while (NSArs.next()) {
				NSAnum = NSAnum + 1;
				float fNSA = Integer.parseInt(NSArs.getString(2)) / num;
				String tempNsa = NSArs.getString(1);
				// System.out.println("fNSA = " + fNSA);

				while (SArs.next()) {
					if (NSAnum == 1)
						SAnum = SAnum + 1;
					float fSA = Integer.parseInt(SArs.getString(2)) / num;
					// System.out.println("fSA = " + fSA);
					// NSA SA联合
					// System.out.println("type = " + NSAmete.getColumnType(1));
					String NSAstr = tempNsa;
					if (NSAmete.getColumnType(1) != 8) {
						NSAstr = "'" + NSAstr + "'";
					}
					String SAstr = SArs.getString(1);
					if (SAmete.getColumnType(1) != 8) {
						SAstr = "'" + SAstr + "'";
					}
					sql = "select " + Common.RECORDNUM + " " + NSAcol + ","
							+ SAcol + " ,count(*) from " + Common.TABLENAME
							+ " " + " where " + NSAcol + " = " + NSAstr
							+ " and " + SAcol + "=" + SAstr + " group by "
							+ NSAcol + "," + SAcol;
					// System.out.println(sql);
					Costat = ac.con.prepareStatement(sql);
					ResultSet Cors = Costat.executeQuery();
					float fij = 0;
					if (Cors.next()) {
						fij = Integer.parseInt(Cors.getString(3)) / num;
					}
					sum += ((fij - fNSA * fSA) * (fij - fNSA * fSA))
							/ (fNSA * fSA);
					// System.out.println(sum);
					Cors.close();
					Costat.close();
				}
			}
			// System.out.println("final222 = " + sum);

			if (NSAnum < SAnum)
				sum = sum / (NSAnum - 1);
			else
				sum = sum / (SAnum - 1);
			relation = sum;
			System.out.println("final = " + sum);
			NSArs.close();
			SArs.close();
			ac.close();
			NSAstat.close();
			SAstat.close();
			stmt.close();
			ac.close();
			// ac.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return relation;
	}

	public static void main(String[] args) throws SQLException {
		Correlation test = new Correlation();
		// test.colrelation("sex", "occupation");
		// test.colrelation("age", "occupation");
		// test.colrelation("race", "occupation");
		// test.colrelation("age", "occupation");
		// test.showAllCol("race,age,sex,occupation");
	}
}
