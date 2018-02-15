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
	
	/* DAO methods */
	
	public Collection<FunctionType> readAllVnfs() {
		return functionCatalogMap.values();
	}
	
	public FunctionType readVnf(String vnfName) {
		return functionCatalogMap.get(vnfName);
	}
	
	public void createVnfList(List<FunctionType> functionList) {
		for(FunctionType function: functionList) {
			functionCatalogMap.put(function.getName(), function);
		}
	}
	
	public void createVnf(FunctionType function) {
		functionCatalogMap.put(function.getName(), function);
	}

	public void deleteVnf() {
		
	}
	
	protected void updateVnf() {
		
	}
}
