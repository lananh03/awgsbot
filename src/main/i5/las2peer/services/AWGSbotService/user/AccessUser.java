package i5.las2peer.services.AWGSbotService.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessUser {
	
	// get authorization of user by sub
	public User AuthStas (Connection con, String sub, String email) throws SQLException {
		//int auth = 0;
		User user = new User();
		PreparedStatement stmt = con.prepareStatement("SELECT AUTHORIZATION FROM USER WHERE SUB ='" + sub + "'");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			user.setAuthorization(rs.getInt("authorization"));
			user.setSub(sub);
			user.setEmail(email);
		}
		return user;
	}
}

