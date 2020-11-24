package i5.las2peer.services.AWGSbotService.database;

import java.sql.*;
import java.util.ArrayList;

import i5.las2peer.services.AWGSbotService.item.Item;

public class AccessItem {
	
	// register new items
	public int registerItems(Connection con, String id, String name, String description, String url, int type, String owner, Timestamp lastupdate) throws SQLException {
		int stats = 0;
		PreparedStatement stmt = con.prepareStatement("INSERT INTO ITEM VALUES ('" + id + "','" + name + "','"
				+ description + "','" + url + "','" + type + "','" + owner + "','" + lastupdate + "')");
		stats = stmt.executeUpdate();
		return stats;
	}
	
	// get all items
	public ArrayList<Item> getItems(Connection con) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEM ORDER BY id DESC");
		ResultSet rs = stmt.executeQuery();
		
		return this.xQuery(rs);
	}
	
	// get item by id
	public ArrayList<Item> getItemsbyId(Connection con, String id) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEM WHERE id ='" + id + "'");
		ResultSet rs = stmt.executeQuery();
		
		return this.xQuery(rs);
	}
	
	// get last item oder by id
	public ArrayList<Item> getlastItem(Connection con) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEM ORDER BY id DESC LIMIT 0,1");
		ResultSet rs = stmt.executeQuery();
		return this.xQuery(rs);
	}
	
	// delete item by id
	public int deleteItemsbyId(Connection con, String id) throws SQLException {
		int delStats = 0;
		PreparedStatement stmt = con.prepareStatement("DELETE FROM ITEM WHERE id ='" + id + "'");
		delStats = stmt.executeUpdate();
		return delStats;
	}
	
	// get items by owner
	public ArrayList<Item> getItemsbyOwner(Connection con, String owner) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEM WHERE owner like'" + "%" + owner + "%" + "'ORDER BY id DESC");
		ResultSet rs = stmt.executeQuery();
		
		return this.xQuery(rs);
	}
	
	//get item by url
	public ArrayList<Item> getItemsbyUrl(Connection con, String url) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEM WHERE url like'" + "%" + url + "%" + "'ORDER BY id DESC");
		ResultSet rs = stmt.executeQuery();
		
		return this.xQuery(rs);
	}
	
	//get item by name
		public ArrayList<Item> getItemsbyName(Connection con, String name) throws SQLException {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEM WHERE name like'" + "%" + name + "%" + "'ORDER BY id DESC");
			ResultSet rs = stmt.executeQuery();
			
			return this.xQuery(rs);
		}
	// search items by query
	public ArrayList<Item> searchItemsbyQuery(Connection con, String query) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEM WHERE owner like'" + "%" + query + "%" + 
				"'or id like'" + "%" + query + "%" + "'or name like '" + "%" + query + "%" + "'or url like'" + "%" + query + "%" + "'ORDER BY id DESC");
		ResultSet rs = stmt.executeQuery();
		
		return this.xQuery(rs);
	}
	
	public ArrayList<Item> xQuery (ResultSet rs) {
		ArrayList<Item> itemList = new ArrayList<Item>();
		try {
			while (rs.next()) {
				Item itemObj = new Item();
				itemObj.setId(rs.getString("id"));
				itemObj.setName(rs.getString("name"));
				itemObj.setDescription(rs.getString("description"));
				itemObj.setType(rs.getString("type"));
				itemObj.setUrl(rs.getNString("url"));
				itemObj.setOwner(rs.getNString("owner"));	
				itemObj.setLastupdate(rs.getTimestamp("lastupdate"));
				itemList.add(itemObj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return itemList;
	}
}
