package com.company.zhello_world_mta.tiny_java_odata_v4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.cloud.sdk.service.prov.api.operations.Query;
import com.sap.cloud.sdk.service.prov.api.operations.Read;
import com.sap.cloud.sdk.service.prov.api.request.QueryRequest;
import com.sap.cloud.sdk.service.prov.api.request.ReadRequest;
import com.sap.cloud.sdk.service.prov.api.response.QueryResponse;
import com.sap.cloud.sdk.service.prov.api.response.ReadResponse;

public class ServiceImplementation {
	@Read(serviceName="DemoService", entity="People")
	public ReadResponse getPerson(ReadRequest request) {
			
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("UniqueId", 1);
		map.put("Name", "Kitty");
			
		return ReadResponse.setSuccess().setData(map).response();
	}
	
private List<Map<String, Object>> getSamplePeople() {
		List<Map<String, Object>> peopleSet = 
                     new ArrayList<Map<String, Object>>();

		Map<String, Object> peopleEntity1 = new HashMap<String, Object>();
		peopleEntity1.put("UniqueId", 1);
		peopleEntity1.put("Name", "Jerry");
		peopleSet.add(peopleEntity1);

		Map<String, Object> peopleEntity2 = new HashMap<String, Object>();
		peopleEntity2.put("UniqueId", 2);
		peopleEntity2.put("Name", "Jack");
		peopleSet.add(peopleEntity2);

		return peopleSet;
	}


	@Query(serviceName = "DemoService", entity = "People")
	public QueryResponse getAllPeople(QueryRequest queryRequest) {


	    List<Map<String, Object>> sampleData = this.getSamplePeople();
	    return QueryResponse.setSuccess() //indicates that the operation succeeded
				    .setData(sampleData)//set the response data
				    .response(); // returns the response 
	}	
	
}
