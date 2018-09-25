package com.company.zhello_world_mta.tiny_java_odata_v4;

import java.util.HashMap;
import java.util.Map;

import com.sap.cloud.sdk.service.prov.api.operations.Read;
import com.sap.cloud.sdk.service.prov.api.request.ReadRequest;
import com.sap.cloud.sdk.service.prov.api.response.ReadResponse;

public class ServiceImplementation {
	@Read(serviceName="DemoService", entity="People")
	public ReadResponse getPerson(ReadRequest request) {
			
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("UniqueId", 1);
		map.put("Name", "Kitty");
			
		return ReadResponse.setSuccess().setData(map).response();
	}
	
private List<Map<String, Object>> getSampleProducts() {
		List<Map<String, Object>> products = 
                     new ArrayList<Map<String, Object>>();

		Map<String, Object> productEntity1 = new HashMap<String, Object>();
		productEntity1.put("ProductID", 1);
		productEntity1.put("Name", "Laptop");
		productEntity1.put("Description", "Professional Laptop");
		productEntity1.put("Category", "Computers");
		products.add(productEntity1);

		Map<String, Object> productEntity2 = new HashMap<String, Object>();
		productEntity2.put("ProductID", 2);
		productEntity2.put("Name", "Monitor");
		productEntity2.put("Description", "24-inch Desktop Monitor");
		productEntity1.put("Category", "Display Monitors");
		products.add(productEntity2);

		return products;
	}


	@Query(serviceName = "SampleService", entity = "Products")
	public QueryResponse getAllProducts(QueryRequest queryRequest) {


	    List<Map<String, Object>> sampleData = this.getSampleProducts();
	    return QueryResponse.setSuccess() //indicates that the operation succeeded
				    .setData(sampleData)//set the response data
				    .response(); // returns the response 
	}	
	
}
