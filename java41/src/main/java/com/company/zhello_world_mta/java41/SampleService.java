package com.company.zhello_world_mta.java41;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.sap.cloud.sdk.hana.connectivity.cds.CDSException;
import com.sap.cloud.sdk.hana.connectivity.cds.CDSQuery;
import com.sap.cloud.sdk.hana.connectivity.cds.CDSSelectQueryBuilder;
import com.sap.cloud.sdk.hana.connectivity.cds.CDSSelectQueryResult;
import com.sap.cloud.sdk.hana.connectivity.cds.ConditionBuilder;
import com.sap.cloud.sdk.hana.connectivity.handler.CDSDataSourceHandler;
import com.sap.cloud.sdk.hana.connectivity.handler.DataSourceHandlerFactory;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQueryBuilder;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQueryResult;
import com.sap.cloud.sdk.service.prov.api.DatasourceExceptionType;
import com.sap.cloud.sdk.service.prov.api.EntityData;
import com.sap.cloud.sdk.service.prov.api.Severity;
import com.sap.cloud.sdk.service.prov.api.operations.Create;
import com.sap.cloud.sdk.service.prov.api.operations.Delete;
import com.sap.cloud.sdk.service.prov.api.operations.Query;
import com.sap.cloud.sdk.service.prov.api.operations.Read;
import com.sap.cloud.sdk.service.prov.api.operations.Update;
import com.sap.cloud.sdk.service.prov.api.request.CreateRequest;
import com.sap.cloud.sdk.service.prov.api.request.DeleteRequest;
import com.sap.cloud.sdk.service.prov.api.request.QueryRequest;
import com.sap.cloud.sdk.service.prov.api.request.ReadRequest;
import com.sap.cloud.sdk.service.prov.api.request.UpdateRequest;
import com.sap.cloud.sdk.service.prov.api.response.CreateResponse;
import com.sap.cloud.sdk.service.prov.api.response.DeleteResponse;
import com.sap.cloud.sdk.service.prov.api.response.ErrorResponse;
import com.sap.cloud.sdk.service.prov.api.response.QueryResponse;
import com.sap.cloud.sdk.service.prov.api.response.ReadResponse;
import com.sap.cloud.sdk.service.prov.api.response.UpdateResponse;


public class SampleService {
private static final String DESTINATION_NAME = "ODataEndPoint";

	Logger logger = LoggerFactory.getLogger(SampleService.class);

	@Query(entity = "Customer", serviceName = "SampleService")
	public QueryResponse getAllCustomers(QueryRequest queryRequest) {
		QueryResponse queryResponse = null;
		try{
			queryResponse =  QueryResponse.setSuccess().setEntityData(getEntitySet(queryRequest)).response();
		}catch(Exception e){
			queryResponse =  QueryResponse.setError(ErrorResponse.getBuilder().setMessage(e.getMessage()).setStatusCode(500).response());
		}
		return queryResponse;
	}
	
	@Create(entity = "Customer", serviceName = "SampleService")
	public CreateResponse createCustomer(CreateRequest createRequest) {
		return createCustomerEntity( createRequest);
	}		
	
	@Read(entity = "Customer", serviceName = "SampleService")
	public ReadResponse getSalesOrder(ReadRequest readRequest) {
		ReadResponse readResponse = null;
		try{
			readResponse = ReadResponse.setSuccess().setData(readEntity(readRequest)).response();
		}catch (Exception e){
			readResponse = ReadResponse.setError(ErrorResponse.getBuilder().setMessage(e.getMessage()).setStatusCode(500).response());
		}
		return readResponse;
	}
	@Update(entity = "Customer", serviceName = "SampleService")
	public UpdateResponse updateSalesOrder(UpdateRequest updateRequest) {
		UpdateResponse updateResponse  = null;
		try{
			updateEntity(updateRequest);
			updateResponse = UpdateResponse.setSuccess().response();
		}catch (Exception e){
			updateResponse = UpdateResponse.setError(ErrorResponse.getBuilder().setMessage(e.getMessage()).setStatusCode(500).response());
		}
		return updateResponse;
	}
	@Delete(entity = "Customer", serviceName = "SampleService")
	public DeleteResponse deleteSalesOrder(DeleteRequest deleteRequest) {
		DeleteResponse deleteResponse = null;
		try{
			deleteEntity(deleteRequest);
			deleteResponse = DeleteResponse.setSuccess().response();
		}catch(Exception e){
			deleteResponse = DeleteResponse.setError(ErrorResponse.getBuilder().setMessage(e.getMessage()).setStatusCode(500).response());
		}
		return deleteResponse;
	}	
	
	
	@Query(serviceName = "SampleService", entity = "Products")
	public QueryResponse getProducts(QueryRequest queryRequest) { // the name of the method can be arbitrary
		logger.debug("==> now call backend OData V2 service");		

		QueryResponse queryResponse = null;
		try {

			logger.debug("==> now execute query on Products");
			ODataQueryResult result = ODataQueryBuilder
					.withEntity("/sap/opu/odata/IWBEP/GWSAMPLE_BASIC", "ProductSet") 
					.select("ProductID", "Name", "Description", "Category")
					.build()
					.execute(DESTINATION_NAME);

			logger.debug("==> After calling backend OData V2 service: result: " + result);		

			final List<ProductEntity> v2ProductList = result.asList(ProductEntity.class);

			queryResponse = QueryResponse.setSuccess().setData(v2ProductList).response();
			return queryResponse;

		} catch (IllegalArgumentException | ODataException e) {
			logger.error("==> Exception calling backend OData V2 service for Query of Products: " + e.getMessage());


			ErrorResponse errorResponse = ErrorResponse.getBuilder()
					.setMessage("There is an error.  Check the logs for the details.")
					.setStatusCode(500)
					.setCause(e)
					.response();
			queryResponse = QueryResponse.setError(errorResponse);
		}
		return queryResponse;
	}		
	
	private void updateEntity(UpdateRequest updateRequest) throws Exception{
		CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), updateRequest.getEntityMetadata().getNamespace());
		try{
			dsHandler.executeUpdate(updateRequest.getData(), updateRequest.getKeys(), false);
		}catch(CDSException e){
			logger.error("Exception while updatiing an entity in CDS: "+e.getMessage());
			throw e;
		}
	}	
	
	
	private EntityData readEntity(ReadRequest readRequest)throws Exception {
		CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), readRequest.getEntityMetadata().getNamespace());
		EntityData ed = null;
		try{
			ed = dsHandler.executeRead(readRequest.getEntityMetadata().getName(), readRequest.getKeys(), readRequest.getEntityMetadata().getElementNames());
		}catch(CDSException e){
			logger.error("Exception while reading an entity in CDS: "+e.getMessage());
			throw e;
		}
		return ed;
	}
	
	private void deleteEntity(DeleteRequest deleteRequest) throws Exception{
		CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), deleteRequest.getEntityMetadata().getNamespace());
		try{
			dsHandler.executeDelete(deleteRequest.getEntityMetadata().getName(), deleteRequest.getKeys());
		}catch(CDSException e){
			logger.error("Exception while deleting an entity in CDS: "+e.getMessage());
			throw e;
		}
	}	
	
	private CreateResponse createCustomerEntity(CreateRequest createRequest)
	{   
		EntityData ed = null;
		try{
			CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), createRequest.getEntityMetadata().getNamespace());
			ed = dsHandler.executeInsert(createRequest.getData(), true);
		}catch(CDSException e){
			logger.error("Exception while creating a sales order entity in CDS: "+e.getMessage());
			ErrorResponse errorResponse = null;
			if(e.getType().equals(DatasourceExceptionType.INTEGRITY_CONSTRAINT_VIOLATION)){
				createRequest.getMessageContainer().addErrorMessage("INTEGRITY_CONSTRAINT_ERROR", "CDS");
				errorResponse =ErrorResponse.getBuilder().setStatusCode(500).addContainerMessages(Severity.ERROR).response();
			}else if(e.getType().equals(DatasourceExceptionType.DATABASE_CONNECTION_ERROR)){
				createRequest.getMessageContainer().addErrorMessage("DATABASE_CONNECTION_ERROR", "CDS");
				errorResponse =ErrorResponse.getBuilder().setStatusCode(500).addContainerMessages(Severity.ERROR).response();
			}else{
				errorResponse =ErrorResponse.getBuilder().setStatusCode(500).setMessage("Exception during CDS create operation: "+e.getMessage()).response();
			}
			return CreateResponse.setError(errorResponse);
		}catch(Exception e){
			logger.error("==> Exception while creating a SO in CDS: " + e.getMessage());
			return CreateResponse.setError(ErrorResponse.getBuilder().setMessage(e.getMessage()).setCause(e).response());
		}
		return CreateResponse.setSuccess().setData(ed).response();
	}
	
	
	private List<EntityData> getEntitySet(QueryRequest queryRequest) throws Exception{
		String fullQualifiedName = queryRequest.getEntityMetadata().getNamespace()+"."+queryRequest.getEntityMetadata().getName();
		CDSDataSourceHandler dsHandler = DataSourceHandlerFactory.getInstance().getCDSHandler(getConnection(), queryRequest.getEntityMetadata().getNamespace());
		try {
			CDSQuery cdsQuery = new CDSSelectQueryBuilder(fullQualifiedName).build();                    			
			CDSSelectQueryResult cdsSelectQueryResult = dsHandler.executeQuery(cdsQuery);
			return cdsSelectQueryResult.getResult();
		} catch (CDSException e) {
			logger.error("==> Exception while fetching query data from CDS: " + e.getMessage());
			throw e;
		}
	}	
	
	private static Connection getConnection() throws SQLException ,NamingException{
		Connection conn = null;
		Context ctx;
		try {
			ctx = new InitialContext();
			conn = ((DataSource) ctx.lookup("java:comp/env/jdbc/java-hdi-container")).getConnection();
		} catch (SQLException | NamingException e) {
			e.printStackTrace();
			throw e;
		}
		return conn;
	}	



}
