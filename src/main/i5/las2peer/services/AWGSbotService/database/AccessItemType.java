package i5.las2peer.services.AWGSbotService.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import i5.las2peer.services.AWGSbotService.itemtype.ItemType;

public class AccessItemType {

		// get all types of items
		public ArrayList<ItemType> getItemTypes(Connection con) throws SQLException {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEMTYPE ORDER BY id ASC");
			ResultSet rs = stmt.executeQuery();
			
			return this.xQuery(rs);
		}
		
		// get type by id
		public ArrayList<ItemType> getItemTypesbyId(Connection con, String id) throws SQLException {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEMTYPE WHERE id ='" + id + "'");
			ResultSet rs = stmt.executeQuery();
			
			return this.xQuery(rs);
		}
		
		// get type by name
		public ArrayList<ItemType> getItemTypesbyName(Connection con, String name) throws SQLException {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEMTYPE WHERE name ='" + name + "'");
			ResultSet rs = stmt.executeQuery();
			
			return this.xQuery(rs);
		}
		
		// get type by description
		public ArrayList<ItemType> getItemTypesbyDesp(Connection con, String desp) throws SQLException {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEMTYPE WHERE description like'" + "%" + desp + "%" + "'ORDER BY id DESC");
			ResultSet rs = stmt.executeQuery();
					
			return this.xQuery(rs);
		}
				
		// search items' types by query
		public ArrayList<ItemType> searchItemTypesbyQuery(Connection con, String query) throws SQLException {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM ITEMTYPE WHERE name like'" + "%" + query + "%" + 
					"'or id like'" + "%" + query + "%" + "'or description like '" + "%" + query + "%" + "'ORDER BY id ASC");
			ResultSet rs = stmt.executeQuery();
			
			return this.xQuery(rs);
		}
		
		public ArrayList<ItemType> xQuery (ResultSet rs) {
			ArrayList<ItemType> itemTypeList = new ArrayList<ItemType>();
			try {
				while (rs.next()) {
					ItemType itemObj = new ItemType();
					itemObj.setId(rs.getInt("id"));
					itemObj.setName(rs.getString("name"));
					itemObj.setDescription(rs.getString("description"));	
					itemTypeList.add(itemObj);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return itemTypeList;
		}
		
}
