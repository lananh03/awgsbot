package i5.las2peer.services.AWGSbotService.database;

import java.sql.*;
import java.util.ArrayList;

import i5.las2peer.services.AWGSbotService.item.Item;

public class AccessItem {
	
	public ArrayList<Item> getItems(Connection con) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEM");
		ResultSet rs = stmt.executeQuery();
		
		return this.xQuery(rs);
	}
	
	public ArrayList<Item> getItemsbyOwner(Connection con, String owner) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEM WHERE owner like'" + "%" + owner + "%" + "'");
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
