package it.polito.dp2.NFV.sol3.client2;

import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.client1.NfvDeployerServiceManager;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class NfvHelper {

    private NfvDeployerServiceManager serviceManager;

    public NfvHelper() {

        serviceManager = new NfvDeployerServiceManager();


        try {
            CatalogType catalog = serviceManager.getCatalog();

            if (catalog == null) {

            }




        } catch (ServiceException se) {

        }




    }

}
