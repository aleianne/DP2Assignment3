package it.polito.dp2.NFV.sol3.client1;

import it.polito.dp2.NFV.lab3.NfvClient;
import it.polito.dp2.NFV.lab3.NfvClientException;
import it.polito.dp2.NFV.lab3.NfvClientFactory;
import it.polito.dp2.NFV.lab3.ServiceException;

public class NfvClientFactoryImpl extends NfvClientFactory {

	@Override
	public NfvClient newNfvClient() throws NfvClientException {
		return new Client1Impl();
	}

}
