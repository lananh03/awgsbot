package i5.las2peer.services.AWGSbotService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
import i5.las2peer.services.AWGSbotService.database.AccessItem;
import i5.las2peer.services.AWGSbotService.database.Database;
import i5.las2peer.services.AWGSbotService.item.Item;

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
@ServicePath("/awgs")
@ManualDeployment

public class AWGSbotServiceMainClass extends RESTService {
	
	private String dbUser;
	private String dbPassword;
	private String dbHost;
	private int dbPort;
	private String dbName;
	private Database database;
	
	public AWGSbotServiceMainClass() {
		super();
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
		String items = null;
		ArrayList<Item> itemList = new ArrayList<Item>();
		try {
			itemList = this.getItems();
			Gson gson = new Gson();
			items = gson.toJson(itemList);
			System.out.println(items);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok().entity(items).build();
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
			value = "Search the AWGS items which has phrase query",
			notes = "")
	
	public Response searchAWGSItemsbyQuery(@PathParam("query") String query) throws Throwable {
		String items = null;
		ArrayList<Item> itemList = new ArrayList<Item>();
		try {
			itemList = this.searchItemsbyQuery(query);
			Gson gson = new Gson();
			items = gson.toJson(itemList);
			System.out.println(items);
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
			System.out.println(items);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok().entity(items).build();
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


}
