package i5.las2peer.services.AWGSbotService.database;

import java.sql.*;
import java.util.ArrayList;

import i5.las2peer.services.AWGSbotService.item.Item;

public class AccessItem {
	
	public ArrayList<Item> getItems(Connection con) throws SQLException {
		ArrayList<Item> itemList = new ArrayList<Item>();
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEM");
		//Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery();
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
