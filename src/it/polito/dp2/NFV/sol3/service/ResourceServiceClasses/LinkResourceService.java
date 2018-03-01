package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import javax.ws.rs.InternalServerErrorException;

import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.LinkAlreadyPresentException;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.service.ServiceXML.ExtendedLinkType;
import it.polito.dp2.NFV.sol3.service.DaoClasses.GraphDao;
import it.polito.dp2.NFV.sol3.service.Exceptions.GraphNotFoundException;

public class LinkResourceService {

	public void addLink(String graphId, ExtendedLinkType newLink) throws ServiceException, AllocationException, LinkAlreadyPresentException, InternalServerErrorException, GraphNotFoundException {
		GraphDao graphDao = GraphDao.getInstance();
		synchronized(graphDao) {
			graphDao.updateGraph(graphId, newLink);
		}	
	}
	
	public void getLink(String linkId) {
	
	}
	
}
