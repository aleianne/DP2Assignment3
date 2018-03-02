package it.polito.dp2.NFV.sol3.client1;

import it.polito.dp2.NFV.FunctionalType;
import it.polito.dp2.NFV.VNFTypeReader;
import it.polito.dp2.NFV.sol3.service.ServiceXML.FunctionType;

public class VNFTypeReaderImpl implements VNFTypeReader{

	public FunctionType function;
	
	public VNFTypeReaderImpl(FunctionType function) {
		this.function = function;
	}
	
	@Override
	public String getName() {
		return function.getName();
	}

	@Override
	public FunctionalType getFunctionalType() {
		return null;
	}

	@Override
	public int getRequiredMemory() {
		return function.getRequiredMemory().intValue();
	}

	@Override
	public int getRequiredStorage() {
		return function.getRequiredStorage().intValue();
	}

}
