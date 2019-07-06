package it.polito.tdp.ufo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.ufo.model.AnnoCount;
import it.polito.tdp.ufo.model.Sighting;

public class SightingsDAO {

	public List<Sighting> getSightings() {
		String sql = "SELECT * FROM sighting" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;

			List<Sighting> list = new ArrayList<>() ;

			ResultSet res = st.executeQuery() ;

			while(res.next()) {
				try {
					list.add(new Sighting(res.getInt("id"),
							res.getTimestamp("datetime").toLocalDateTime(),
							res.getString("city"), 
							res.getString("state"), 
							res.getString("country"),
							res.getString("shape"),
							res.getInt("duration"),
							res.getString("duration_hm"),
							res.getString("comments"),
							res.getDate("date_posted").toLocalDate(),
							res.getDouble("latitude"), 
							res.getDouble("longitude"))) ;
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}

			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}


	public List<AnnoCount> getAnni() {

		String sql = "SELECT YEAR(DATETIME) as anno, COUNT(*) AS cnt "+
				"FROM sighting "+
				"WHERE country =\"us\" "+
				"GROUP BY YEAR(DATETIME)";

		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			List<AnnoCount> anni = new LinkedList<AnnoCount>();
			ResultSet rs = st.executeQuery();
			// st.setString(1, "us"); ?????????????????????????

			while(rs.next()) {
				anni.add(new AnnoCount(Year.of(rs.getInt("anno")), rs.getInt("cnt")));
			}

			conn.close();
			return anni;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public List<String> getStati(Year anno) {
		String sql = "SELECT DISTINCT state "+ 
				"FROM sighting "+ 
				"WHERE country = \"us\" "+
				"AND Year(datetime) = ?";

		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno.getValue());
			List<String> stati = new LinkedList<String>();
			ResultSet rs = st.executeQuery();

			while(rs.next()) {
				stati.add(rs.getString("state")); // non creo nessuna nuova stringa
			}

			conn.close();
			return stati;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}


	public boolean esisteArco(String s1, String s2, Year anno) {
		String sql = "SELECT COUNT(*) AS cnt " + 
				"FROM Sighting s1, Sighting s2 " + 
				"WHERE YEAR(s1.DATETIME) = YEAR(s2.DATETIME) " + 
				"AND YEAR(s1.DATETIME) = ? " + 
				"AND s1.state = ? AND s2.state = ? " + 
				"AND s1.country = \"us\" AND s2.country = \"us\" " + 
				"AND s1.DATETIME < s2.DATETIME";
		boolean trovato = false;

		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno.getValue());
			st.setString(2, s1);
			st.setString(3, s2);

			ResultSet rs = st.executeQuery();
	
			if(rs.next()) { // perche' ritorna un singolo valore
			if(rs.getInt("cnt")>0)
				trovato = true;
			}

			conn.close();
			return trovato;

			/*
			if(rs.next()) { // perche' ritorna un singolo valore
				if(rs.getInt("cnt")>0) {
					conn.close();
					return true;
				}
				else {
					conn.close();
					return false;
				}
			}
			else {
				return false;
			}*/


		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

}
