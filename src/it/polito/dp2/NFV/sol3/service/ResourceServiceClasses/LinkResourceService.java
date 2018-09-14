package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import javax.ws.rs.InternalServerErrorException;

import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.LinkAlreadyPresentException;
import it.polito.dp2.NFV.lab3.NoNodeException;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.service.ServiceXML.ExtendedLinkType;
import it.polito.dp2.NFV.sol3.service.DaoClasses.GraphDao;
import it.polito.dp2.NFV.sol3.service.Exceptions.GraphNotFoundException;

public class LinkResourceService {

    /*
     * add a new link into the web service
     */
    public void addLink(String graphId, ExtendedLinkType newLink) throws ServiceException, NoNodeException, LinkAlreadyPresentException, InternalServerErrorException, GraphNotFoundException {
        GraphDao graphDao = GraphDao.getInstance();
        graphDao.updateGraph(graphId, newLink);
    }

    /*
     * get a single link instance
     */
    public void getLink(String linkId) {

    }

}
