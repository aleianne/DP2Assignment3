package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class ObjectFactoryManager {

	private static ObjectFactory objManager = new ObjectFactory();
	
	public static ObjectFactory getObjectFactory() {
		return objManager;
	}
	
}
