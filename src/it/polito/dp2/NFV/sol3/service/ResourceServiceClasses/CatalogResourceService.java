package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.DaoClasses.*;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CatalogResourceService {

	private VnfDao virtualFunctionDao;
	
	public CatalogResourceService() {
		virtualFunctionDao = VnfDao.getInstance();
	}
	
	// read the vnf catalog from the dao class
	public CatalogType getCatalog() {		
		List<VnfType> vnfList = new ArrayList<VnfType> (virtualFunctionDao.readAllVnfs());
		
		CatalogType catalog = new CatalogType();
		catalog.getVnf().addAll(vnfList);
		return catalog;
	}
	
	// add a list of function inside the the system
	public void setVnfsAtStartup(List<VnfType> functionList) {
		for(VnfType function: functionList) {
			virtualFunctionDao.createVnf(function);
		}
	}
	
}
