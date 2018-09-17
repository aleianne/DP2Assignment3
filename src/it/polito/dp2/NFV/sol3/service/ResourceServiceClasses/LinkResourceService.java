package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import javax.ws.rs.InternalServerErrorException;

import it.polito.dp2.NFV.sol3.service.Exceptions.LinkAlreadyPresentException;
import it.polito.dp2.NFV.sol3.service.Exceptions.NoNodeException;
import it.polito.dp2.NFV.sol3.service.Exceptions.ServiceException;
import it.polito.dp2.NFV.sol3.service.Exceptions.UnknownEntityException;
import it.polito.dp2.NFV.sol3.service.ServiceXML.ExtendedLinkType;
import it.polito.dp2.NFV.sol3.service.DaoClasses.GraphDao;
import it.polito.dp2.NFV.sol3.service.Exceptions.*;

public class LinkResourceService {

    /*
     * add a new link into the web service
     */
    public void addLink(String graphId, ExtendedLinkType newLink) throws ServiceException, NoNodeException, LinkAlreadyPresentException, InternalServerErrorException, UnknownEntityException {
        GraphDao graphDao = GraphDao.getInstance();
        graphDao.updateGraph(graphId, newLink);
    }

    /*
     * get a single link instance
     */
    public void getLink(String linkId) {

    }

}
