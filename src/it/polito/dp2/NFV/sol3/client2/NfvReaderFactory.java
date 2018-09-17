package it.polito.dp2.NFV.sol3.client2;

import it.polito.dp2.NFV.NfvReader;
import it.polito.dp2.NFV.NfvReaderException;
import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.NfvDeployer;

public class NfvReaderFactory extends it.polito.dp2.NFV.NfvReaderFactory {

    @Override
    public NfvReader newNfvReader() throws NfvReaderException {
        NfvHelper nfvHelper = new NfvHelper();
        return new NfvReaderImpl(nfvHelper);
    }

}
