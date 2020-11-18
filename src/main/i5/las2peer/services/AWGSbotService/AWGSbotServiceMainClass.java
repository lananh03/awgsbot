package i5.las2peer.services.AWGSbotService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;

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
	
	public AWGSbotServiceMainClass() {
		//read and set properties values
		setFieldValues();
		
	}
	
	public Connection conDB() throws Exception {
		this.database = new Database(this.dbUser, this.dbPassword, this.dbHost, this.dbPort, this.dbName);
		return database.getConnection();
	}
	
	@GET
	@Path("/items")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Get the list of AWGS items",
			notes = "")
	
	public Response getAWGSitems() throws Throwable {
		//String items = null;
		ArrayList<Item> itemList = new ArrayList<Item>();
		JSONObject text = new JSONObject();	
		try {
			itemList = this.getItems();
			//Gson gson = new Gson();
			//items = gson.toJson(itemList);
			text.put("text", itemList);
	        text.put("closeContext", "true");
	        return Response.ok().entity(text).build();

		} catch (Exception e) {
			e.printStackTrace();
			text.put("text", "I don't understand");
		    text.put("closeContext", "true");
		    return Response.status(Status.OK).entity(text).build();
		}
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
		//String items = null;
		ArrayList<Item> itemList = new ArrayList<Item>();
		try {
			itemList = this.getItems();
			//Gson gson = new Gson();
			//items = gson.toJson(itemList); 
			text.put("text", itemList);
	        text.put("closeContext", "true");
	        return Response.status(Status.OK).entity(text).build();
		} catch (Exception e) {
			e.printStackTrace();
			text.put("text", "I don't understand");
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
		
	@GET
	@Path("/items/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Search the AWGS items which have phrase <query>",
			notes = "")
	
	public Response searchAWGSItemsbyQuery(@PathParam("query") String query) throws Throwable {
		String items = null;
		ArrayList<Item> itemList = new ArrayList<Item>();
		try {
			itemList = this.searchItemsbyQuery(query);
			Gson gson = new Gson();
			items = gson.toJson(itemList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok().entity(items).build();
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

	@GET
	@Path("/items/{tabname}/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Get the AWGS items by id/owner/url",
			notes = "")
	
	public Response getAWGSItemsbyQuery(@PathParam("tabname") String tabname, @PathParam("query") String query) throws Throwable {
		String items = null;
		ArrayList<Item> itemList = new ArrayList<Item>();
		try {
			itemList = this.getItemsbyQuery(tabname, query);
			Gson gson = new Gson();
			items = gson.toJson(itemList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok().entity(items).build();
	}
	
	@POST
	@Path("/getitembot")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "getAWGSbotItemsbyQuery - Get the AWGS items by id",
			notes = "")
	
	public Response getAWGSbotItemsbyId(String body) throws ParseException {
		//String items = null;
		JSONObject text = new JSONObject();
		JSONParser p = new JSONParser(JSONParser.MODE_PERMISSIVE);
		JSONObject triggeredBody = (JSONObject) p.parse(body);
		String id = triggeredBody.getAsString("msg").substring(9);
		ArrayList<Item> itemList = new ArrayList<Item>();
		try {
			itemList = this.getItemsbyId(id);
			text.put("text", itemList);
	        text.put("closeContext", "true");
	        return Response.ok().entity(text).build();
			//Gson gson = new Gson();
			//items = gson.toJson(itemList);
		} catch (Exception e) {
			e.printStackTrace();
			text.put("text", "there is something wrong");
	        text.put("closeContext", "true");
			return Response.ok().entity(text).build();
		}
		
	}
	
	public ArrayList<Item> getItemsbyId(String id) throws Exception {
		ArrayList<Item> itemList = new ArrayList<Item>();
		AccessItem access = new AccessItem();
		try {
			itemList = access.getItemsbyId(conDB(), id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemList;
	}
	
	public ArrayList<Item> getItemsbyQuery(String tabname, String query) throws Exception {
		ArrayList<Item> itemList = new ArrayList<Item>();
		AccessItem access = new AccessItem();
		try {
			switch(tabname) {
			  case "id":
				itemList = access.getItemsbyId(conDB(),query);
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

	//all codes for get types of items
	@GET
	@Path("/types")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Get the list of AWGS types",
			notes = "")
	
	public Response getAWGStypes() throws Throwable {
		//String types = null;
		ArrayList<ItemType> itemTypeList = new ArrayList<ItemType>();
		JSONObject text = new JSONObject();	
		try {
			itemTypeList = this.getItemTypes();
			text.put("text", itemTypeList);
			text.put("closeContext", "true");
			//Gson gson = new Gson();
			//types = gson.toJson(itemTypeList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok().entity(text).build();
	}
	
	@POST
	@Path("/bottypes")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "getAWGSbottypes - Get the list of AWGS types",
			notes = "")
	
	public Response getAWGSbottypes() throws Throwable {
		//String types = null;
		ArrayList<ItemType> itemTypeList = new ArrayList<ItemType>();
		JSONObject text = new JSONObject();	
		try {
			itemTypeList = this.getItemTypes();
			text.put("text", itemTypeList);
			text.put("closeContext", "true");
			//Gson gson = new Gson();
			//types = gson.toJson(itemTypeList);
		} catch (Exception e) {
			e.printStackTrace();
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
		
	@GET
	@Path("/types/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Search the AWGS types which have phrase <query>",
			notes = "")
	
	public Response searchAWGSItemTypesbyQuery(@PathParam("query") String query) throws Throwable {
		String types = null;
		ArrayList<ItemType> itemTypeList = new ArrayList<ItemType>();
		try {
			itemTypeList = this.searchItemTypesbyQuery(query);
			Gson gson = new Gson();
			types = gson.toJson(itemTypeList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok().entity(types).build();
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

	@GET
	@Path("/types/{tabname}/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Get the AWGS types by id/name/description",
			notes = "")
	
	public Response getAWGSItemTypesbyQuery(@PathParam("tabname") String tabname, @PathParam("query") String query) throws Throwable {
		String types = null;
		ArrayList<ItemType> itemTypeList = new ArrayList<ItemType>();
		try {
			itemTypeList = this.getItemTypesbyQuery(tabname, query);
			Gson gson = new Gson();
			types = gson.toJson(itemTypeList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok().entity(types).build();
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

}
