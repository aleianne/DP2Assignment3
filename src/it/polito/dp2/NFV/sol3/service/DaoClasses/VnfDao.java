package it.polito.dp2.NFV.sol3.service.DaoClasses;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class VnfDao {

	static VnfDao Vnfsdao = new VnfDao();

	static private Map<String, FunctionType> functionCatalogMap = new HashMap<String, FunctionType>();
	
	public static VnfDao getInstance() {
		return Vnfsdao;
	}
	
	/*
	 * get all the virtual function allocated into the server
	 */
	public Collection<FunctionType> readAllVnfs() {
		return functionCatalogMap.values();
	}
	
	/*
	 * return a a single virtual function 
	 */
	public FunctionType readVnf(String vnfName) {
		return functionCatalogMap.get(vnfName);
	}
	
	
	/*
	 * insert into the server a single function
	 */
	public void createVnfList(List<FunctionType> functionList) {
		for(FunctionType function: functionList) {
			functionCatalogMap.put(function.getName(), function);
		}
	}
	
	/*
	 * insert into the server a single function
	 */
	public void createVnf(FunctionType function) {
		functionCatalogMap.put(function.getName(), function);
	}

	/*
	 * delete a virtual function 
	 */
	public void deleteVnf() {
		
	}
	
	/*
	 * update a single vnf
	 */
	public void updateVnf() {
		
	}
}
