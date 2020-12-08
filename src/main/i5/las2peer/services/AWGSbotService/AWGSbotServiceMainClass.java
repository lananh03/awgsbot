package i5.las2peer.services.AWGSbotService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import i5.las2peer.api.ManualDeployment;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import i5.las2peer.services.AWGSbotService.database.AccessItem;
import i5.las2peer.services.AWGSbotService.database.AccessItemType;
import i5.las2peer.services.AWGSbotService.database.Database;
import i5.las2peer.services.AWGSbotService.item.Item;
import i5.las2peer.services.AWGSbotService.itemtype.ItemType;
import i5.las2peer.services.AWGSbotService.user.AccessUser;
import i5.las2peer.services.AWGSbotService.user.User;

/**
 * las2peer-AWGS-Service
 * 
 * A REST service for accessing the ACIS Working Group Series via slack bot
 */

@Api
@SwaggerDefinition(
		info = @Info(
				title = "las2peer AWGS bot Service",
				version = "1.0.0",
				description = "A las2peer AWGS Service for accessing the ACIS Working Group Serires.",
				termsOfService = "",
				contact = @Contact(
						name = "Tran Lan Anh",
						url = "",
						email = "tran@dbis.rwth-aachen.de"),
				license = @License(
						name = "",
						url = "")))
@ServicePath("awgs")
@ManualDeployment

public class AWGSbotServiceMainClass extends RESTService {
	
	private String dbUser;
	private String dbPassword;
	private String dbHost;
	private int dbPort;
	private String dbName;
	private Database database;
	//public static Boolean usrright = false;
	public static User user = new User();
	
	public AWGSbotServiceMainClass() {
		//read and set properties values
		setFieldValues();
		
	}
	
	public Connection conDB() throws Exception {
		this.database = new Database(this.dbUser, this.dbPassword, this.dbHost, this.dbPort, this.dbName);
		return database.getConnection();
	}
	
	@POST
	@Path("/botitems")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "getAWGSbotitems - Get the list of AWGS items",
			notes = "")
	
	public Response getAWGSbotitems() {
		JSONObject text = new JSONObject();	
		ArrayList<Item> itemList = new ArrayList<Item>();
		try {
			itemList = this.getItems();
			String gtext = this.gettext(itemList);
			text.put("text", gtext);
	        text.put("closeContext", "true");
	        return Response.status(Status.OK).entity(text).build();
		} catch (Exception e) {
			e.printStackTrace();
			text.put("text", "I don't understand. Please follow the Command list!");
		    text.put("closeContext", "true");
		    return Response.status(Status.OK).entity(text).build();
		} 
	}
	
	public ArrayList<Item> getItems() throws Exception {
		ArrayList<Item> itemList = new ArrayList<Item>();
		AccessItem access = new AccessItem();
		try {
			itemList = access.getItems(conDB());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemList;
	}
		
	
	@POST
	@Path("/searchitemsbot")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Search the AWGS items which have phrase <query>",
			notes = "")
	
	public Response searchAWGSbotItemsbyQuery(String body) throws Throwable {

		JSONObject text = new JSONObject();
		JSONParser p = new JSONParser(JSONParser.MODE_PERMISSIVE);
		JSONObject triggeredBody = (JSONObject) p.parse(body);
		String query = triggeredBody.getAsString("msg").substring(13);
		ArrayList<Item> itemList = new ArrayList<Item>();
		try {
			itemList = this.searchItemsbyQuery(query);
			String gtext = this.gettext(itemList);
			text.put("text", gtext);
	        text.put("closeContext", "true");
	        return Response.ok().entity(text).build();
		} catch (Exception e) {
			e.printStackTrace();
			text.put("text", "There is something wrong. Please follow the Command list!");
	        text.put("closeContext", "true");
			return Response.ok().entity(text).build();
		}
		
	}
	
	public ArrayList<Item> searchItemsbyQuery(String query) throws Exception {
		ArrayList<Item> itemList = new ArrayList<Item>();
		AccessItem access = new AccessItem();
		try {
			itemList = access.searchItemsbyQuery(conDB(),query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemList;
	}
	
	@POST
	@Path("/deleteItems")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Delete item by id",
			notes = "")
	
	public Response deleteAWGSbotItems(String body) throws Exception {
		System.out.println(user.getAuthorization());
		System.out.println(user.getEmail());
		JSONObject text = new JSONObject();
		if (user.getAuthorization() == 1) {
			AccessItem access = new AccessItem();
			JSONParser p = new JSONParser(JSONParser.MODE_PERMISSIVE);
			JSONObject triggeredBody = (JSONObject) p.parse(body);
			String email = triggeredBody.getAsString("email");
			if (user.getEmail().equals(email)) {
				String id = triggeredBody.getAsString("id");
				String owner = access.getItemsbyId(conDB(),id).get(0).getOwner();
				if (!owner.equals(email) ){
					text.put("text", "You don't have the right to delete this item!");
					text.put("closeContext", "true");
				} else {
					if (this.deleleItems(id) != 0) {
						text.put("text", "Delete successfully!");
						text.put("closeContext", "true");
					} else {
						text.put("text", "Can't find that id");
						text.put("closeContext", "true");
					} 
				} 
			}			
			user.setAuthorization(0);
			user.setSub("");
			user.setEmail("");
		}
		
		return Response.ok().entity(text).build();
	}
	
	private int deleleItems(String id) throws Exception {
		// TODO Auto-generated method stub
		AccessItem access = new AccessItem();
		return access.deleteItemsbyId(conDB(), id);
	}
	
	@POST
	@Path("/botlogin")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Send the link of login page",
			notes = "")
	
	public Response botLogin(String body) throws ParseException {
		user.setSub("");
		user.setAuthorization(0);
		JSONObject text = new JSONObject();
		JSONParser p = new JSONParser(JSONParser.MODE_PERMISSIVE);
		JSONObject trigBody = (JSONObject) p.parse(body);
		String msg = trigBody.getAsString("msg");
		user.setEmail(trigBody.getAsString("email"));
		System.out.println(user.getEmail());
		String url = "http://127.0.0.1:8888";
		if (msg != null) {
			text.put("text", "Please login first: " + url + '\n' + 
					"Then please follow this syntax: awgs register <type>, <name>, <description>, <url> - To register new item");
			text.put("closeContext", "true");
		}
		return Response.ok().entity(text).build();
	}
	
	@POST
	@Path("/checkUser")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	@ApiOperation(
			value = "Register a new item",
			notes = "")
	public Response checkUser (String body) throws SQLException, Exception {
		AccessUser access = new AccessUser();
		
		//JSONObject text = new JSONObject();
		JSONParser p = new JSONParser(JSONParser.MODE_PERMISSIVE);
		JSONObject trigBody = (JSONObject) p.parse(body);
		String userSub = trigBody.getAsString("sub");
		System.out.println(userSub);
		System.out.println(user.getEmail());
		user = access.AuthStas(conDB(), userSub, user.getEmail());
		return Response.ok().entity(user).build();
		/*if (access.AuthStas(conDB(), userSub) == 1) {
			usrright = true;
		}
		System.out.println(usrright); 
		return usrright; */
	}
	
	
	@POST
	@Path("/registerItems")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Register a new item",
			notes = "")
	
	public Response registerAWGSbotItems(String body) throws Exception {
		System.out.println(user.getAuthorization());
		System.out.println(user.getEmail());
		JSONObject text = new JSONObject();
		if (user.getAuthorization() == 1) {			
			JSONParser p = new JSONParser(JSONParser.MODE_PERMISSIVE);
			JSONObject trigBody = (JSONObject) p.parse(body);
			String msg = trigBody.getAsString("msg");
			String owner = trigBody.getAsString("email");
			System.out.println(owner);
			String[] regItem = handleString(msg,"awgs register");
			String typeName = regItem[0].trim();
			String name = regItem[1].trim();
			String desp = regItem[2].trim();
			String url = regItem[3].trim();
			String id = this.getNextItemId();
			int type = 0;
			int res = 0;
			//int type = Integer.parseInt(typeName);
			AccessItemType itemType = new AccessItemType();
			ArrayList<ItemType> i = itemType.getItemTypesbyName(conDB(),typeName);
			if (i!=null && i.size()!=0) {
				type = i.get(0).getId();
			}
			Timestamp lastupdate = new Timestamp(System.currentTimeMillis()); 
			if (user.getEmail().equals(owner)) {
				res = this.registerItem(id, name, desp, url, type, owner, lastupdate);
			} else {
				text.put("text", "awgsbot can't get the owner by email");
				text.put("closeContext", "true");
			}
			if (res!=0) {
				text.put("text", "Register successfully!");
				text.put("closeContext", "true");
			} else {
				text.put("text", "Please try again later!");
				text.put("closeContext", "true");
			}
			
		} else {
			text.put("text", "You don't have the right!");
			text.put("closeContext", "true");
		}
		
		return Response.ok().entity(text).build();
	}
	
	public int registerItem(String id, String name, String description, String url, int type, String owner, Timestamp lastupdate) throws SQLException, Exception {
		AccessItem access = new AccessItem();
		return access.registerItems(conDB(), id, name, description, url, type, owner, lastupdate);
	}
	
	public String[] handleString(String msg, String cmd) {
		String[] info = null;
		if (msg.trim().startsWith(cmd)) {
			String[] str = msg.split(cmd);
			info = str[1].split(",");
		}
		return info;
	}
	
	public String getNextItemId() throws SQLException, Exception {
		AccessItem item = new AccessItem();
		ArrayList<Item> lastItem = item.getlastItem(conDB());
		String lastId = lastItem.get(0).getId();
		String ynn = lastId.split("AWGS-")[1]; //from dominik.renzel@googlemail.com
		String[] ynarr = ynn.split("-"); 
		int year = Integer.parseInt(ynarr[0]);
		int num = Integer.parseInt(ynarr[1]);
		int curryear = Calendar.getInstance().get(Calendar.YEAR);
		if(year == curryear){
			num++;
		} else {
			year = curryear;
			num = 1;
		}
		String newyearStr = String.format("%04d", year);
		String newnumStr = String.format("%03d", num);

		return "AWGS-" + newyearStr + "-" + newnumStr;
	}
	
	@POST
	@Path("/getitembot")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "getAWGSbotItemsbyQuery - Get the AWGS items by id/url/name/owner",
			notes = "")
	
	public Response getAWGSbotItemsbyQuery(String body) throws ParseException {

		JSONObject text = new JSONObject();
		JSONParser p = new JSONParser(JSONParser.MODE_PERMISSIVE);
		JSONObject triggeredBody = (JSONObject) p.parse(body);
		//Iterator<?> keys = triggeredBody.keySet().iterator();
		//String colname = (String) keys.next();
		//String query = triggeredBody.getAsString(colname);
		String query = "";
		String colname = triggeredBody.getAsString("msg").substring(9,10);
		switch(colname) {
		case "i":
			colname = "id";
			query = triggeredBody.getAsString("msg").substring(12);
			break;
		case "o":
			colname = "owner";
			query = triggeredBody.getAsString("msg").substring(15);
			break;
		case "n":
			colname = "name";
			query = triggeredBody.getAsString("msg").substring(14);
			break;
		} 
		ArrayList<Item> itemList = new ArrayList<Item>();
		try {
			itemList = this.getItemsbyQuery(colname,query);
			String gtext = this.gettext(itemList);
			text.put("text", gtext);
	        text.put("closeContext", "true");
	        return Response.ok().entity(text).build();
		} catch (Exception e) {
			e.printStackTrace();
			text.put("text", "There is something wrong. Please follow the Command list!");
	        text.put("closeContext", "true");
			return Response.ok().entity(text).build();
		}
		
	}
	
	public ArrayList<Item> getItemsbyQuery(String colname, String query) throws Exception {
		ArrayList<Item> itemList = new ArrayList<Item>();
		AccessItem access = new AccessItem();
		try {
			switch(colname) {
			  case "id":
				itemList = access.getItemsbyId(conDB(),query);
			    break;
			  case "name":
				itemList = access.getItemsbyName(conDB(),query);
				break;
			  case "owner":
				itemList = access.getItemsbyOwner(conDB(),query);
			    break;
			  case "url":
				itemList = access.getItemsbyUrl(conDB(),query);
				break;
			  default:
			    // code block
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemList;
	}

	//ALL CODES FOR GET TYPES OF ITEMS
	
	@POST
	@Path("/bottypes")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "getAWGSbottypes - Get the list of AWGS types",
			notes = "")
	
	public Response getAWGSbottypes() throws Throwable {
		ArrayList<ItemType> itemTypeList = new ArrayList<ItemType>();
		JSONObject text = new JSONObject();	
		try {
			itemTypeList = this.getItemTypes();
			String gtext = this.getTypetext(itemTypeList);
			text.put("text", gtext);
			text.put("closeContext", "true");
		} catch (Exception e) {
			e.printStackTrace();
			text.put("text", "Please follow the Command list!");
			text.put("closeContext", "true");
		}
		return Response.ok().entity(text).build();
	}
	
	
	public ArrayList<ItemType> getItemTypes() throws Exception {
		ArrayList<ItemType> itemTypeList = new ArrayList<ItemType>();
		AccessItemType access = new AccessItemType();
		try {
			itemTypeList = access.getItemTypes(conDB());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemTypeList;
	}
		
	
	@POST
	@Path("/searchtypesbot")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Search the AWGS types which have phrase <query>",
			notes = "")
	
	public Response searchAWGSbotTypesbyQuery(String body) throws Throwable {

		JSONObject text = new JSONObject();
		JSONParser p = new JSONParser(JSONParser.MODE_PERMISSIVE);
		JSONObject triggeredBody = (JSONObject) p.parse(body);
		String query = triggeredBody.getAsString("msg").substring(17);
		ArrayList<ItemType> itemTypeList = new ArrayList<ItemType>();
		try {
			itemTypeList = this.searchItemTypesbyQuery(query);
			String gtext = this.getTypetext(itemTypeList);
			text.put("text", gtext);
	        return Response.ok().entity(text).build();
		} catch (Exception e) {
			e.printStackTrace();
			text.put("text", "There is something wrong. Please follow the Command list!");
	        text.put("closeContext", "true");
			return Response.ok().entity(text).build();
		}
		
	}
	
	public ArrayList<ItemType> searchItemTypesbyQuery(String query) throws Exception {
		ArrayList<ItemType> itemTypeList = new ArrayList<ItemType>();
		AccessItemType access = new AccessItemType();
		try {
			itemTypeList = access.searchItemTypesbyQuery(conDB(),query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemTypeList;
	}

	
	@POST
	@Path("/getypebot")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "getAWGSbotItemsbyQuery - Get the AWGS items by id/url/name/owner",
			notes = "")
	
	public Response getAWGSbotTypesbyQuery(String body) throws ParseException {

		JSONObject text = new JSONObject();
		JSONParser p = new JSONParser(JSONParser.MODE_PERMISSIVE);
		JSONObject triggeredBody = (JSONObject) p.parse(body);
		String tablname = triggeredBody.getAsString("msg").substring(10,11);
		String query = "";
		switch(tablname) {
		case "i":
			tablname = "id";
			query = triggeredBody.getAsString("msg").substring(13);
			break;
		case "d":
			tablname = "description";
			query = triggeredBody.getAsString("msg").substring(15);
			break;
		case "n":
			tablname = "name";
			query = triggeredBody.getAsString("msg").substring(15);
			break;
		}
		ArrayList<ItemType> itemTypeList = new ArrayList<ItemType>();
		try {
			itemTypeList = this.getItemTypesbyQuery(tablname,query);
			String gtext = this.getTypetext(itemTypeList);
			text.put("text", gtext);
	        text.put("closeContext", "true");
	        return Response.ok().entity(text).build();
		} catch (Exception e) {
			e.printStackTrace();
			text.put("text", "Please follow the Command list!");
			text.put("closeContext", "true");
			return Response.ok().entity(text).build();
		}
		
	}
	
	public ArrayList<ItemType> getItemTypesbyQuery(String tabname, String query) throws Exception {
		ArrayList<ItemType> itemTypeList = new ArrayList<ItemType>();
		AccessItemType access = new AccessItemType();
		try {
			switch(tabname) {
			  case "id":
				itemTypeList = access.getItemTypesbyId(conDB(),query);
			    break;
			  case "name":
				itemTypeList = access.getItemTypesbyName(conDB(),query);
			    break;
			  case "description":
				itemTypeList = access.getItemTypesbyName(conDB(),query);
				break;
			  default:
			    // code block
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemTypeList;
	}

	public String gettext(ArrayList<Item> itemList) {
		//JSONObject text = new JSONObject();
		String text = "";
		for (int i=0; i<itemList.size(); i++) {
			Item obj = itemList.get(i);
			String iid = obj.getId();
			String iname = obj.getName();
			//String idesp = obj.getDescription();
			String iurl = obj.getUrl();
			String itype = obj.getType();
			String iowner = obj.getOwner();
			Timestamp iupdate = obj.getLastupdate();
			text = text + "ID: "+iid+", NAME: "+iname+", URL: "+iurl+", TYPE: "+itype+", OWNER: "+iowner+", LAST UPDATE: "+iupdate+'\n';
		}
        return text;
	}
	
	public String getTypetext(ArrayList<ItemType> itemTypeList) {

		String text = "";
		for (int i=0; i<itemTypeList.size(); i++) {
			ItemType obj = itemTypeList.get(i);
			int iid = obj.getId();
			String iname = obj.getName();
			String idesp = obj.getDescription();
			text = text + "ID: "+iid+", NAME: "+iname+", DESCRIPTION: "+idesp+'\n';
		}
        return text;
	}
	
}
