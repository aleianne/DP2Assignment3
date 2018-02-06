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

	static private Map<String, VnfType> functionCatalogMap = new HashMap<String, VnfType>();
	
	public static VnfDao getInstance() {
		return Vnfsdao;
	}
	
	/* DAO methods */
	
	public Collection<VnfType> readAllVnfs() {
		return functionCatalogMap.values();
	}
	
	public VnfType readVnf(String vnfName) {
		return functionCatalogMap.get(vnfName);
	}
	
	public void createVnfList(List<VnfType> functionList) {
		for(VnfType function: functionList) {
			functionCatalogMap.put(function.getName(), function);
		}
	}
	
	public void createVnf(VnfType function) {
		functionCatalogMap.put(function.getName(), function);
	}

	public void deleteVnf() {
		
	}
	
	protected void updateVnf() {
		
	}
}
