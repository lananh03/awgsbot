package i5.las2peer.services.AWGSbotService.database;

import java.sql.*;
import java.util.ArrayList;

import i5.las2peer.services.AWGSbotService.item.Item;

public class AccessItem {
	
	// get all items
	public ArrayList<Item> getItems(Connection con) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEM ORDER BY id DESC");
		ResultSet rs = stmt.executeQuery();
		
		return this.xQuery(rs);
	}
	
	// get item by id
	public ArrayList<Item> getItemsbyId(Connection con, String id) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEM WHERE id like'" + "%" + id + "%" + "'ORDER BY id DESC");
		ResultSet rs = stmt.executeQuery();
		
		return this.xQuery(rs);
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
				itemList.add(itemObj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return itemList;
	}
}
