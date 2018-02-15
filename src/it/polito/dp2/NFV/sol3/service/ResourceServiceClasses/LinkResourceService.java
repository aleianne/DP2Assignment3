package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.service.ServiceXML.LinkType;
import it.polito.dp2.NFV.sol3.service.DaoClasses.GraphDao;

public class LinkResourceService {

	public String addLink(String graphId, LinkType newLink) throws ServiceException, AllocationException {
		GraphDao graphDao = GraphDao.getInstance();
		synchronized(graphDao) {
			graphDao.updateGraph(graphId, newLink);
			return newLink.getLinkName();
		}	
	}
	
	public LinkType getLink(String linkId) {
		
	}
	
}
