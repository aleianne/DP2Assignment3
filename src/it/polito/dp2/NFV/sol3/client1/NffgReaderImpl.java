package it.polito.dp2.NFV.sol3.client1;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import javax.xml.datatype.DatatypeConfigurationException;

import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.DateConverter;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class NffgReaderImpl implements NffgReader {

	private NffgGraphType newGraph;
	
	public NffgReaderImpl(NffgGraphType newGraph) throws ServiceException {
		if(newGraph == null) 
			throw new ServiceException();
		else 
			this.newGraph = newGraph;
	}

	@Override
	public String getName() {
		return newGraph.getNffgName();
	}

	@Override
	public Calendar getDeployTime() {
		Calendar deployDate;
		try {
			// convert from  xml gregorian calendar to Calendar instance
			// if the conversion encour in some problem the deploy-date is setted to null
			deployDate = DateConverter.fromXMLGregorianCalendar(newGraph.getDeployDate());
		} catch(DatatypeConfigurationException dce) {
			deployDate = null;
		}
		return deployDate;
	}

	@Override
	public NodeReader getNode(String nodeName) {
		List<NodeType> graphNodeList = newGraph.getNodes().getNode();
		NodeType node;
		NodeReader nodeReader;
		
		// return null if the node is empty 
		if(graphNodeList.isEmpty()) 
			return null;
		
		// check if the node searched is inside the graph
		Predicate<NodeType> nodePredicate = p-> p.getName() == nodeName;
		node = graphNodeList.stream().filter(nodePredicate).findFirst().get();
		nodeReader = new NodeReaderImpl(node, newGraph);
		
		return nodeReader;
	}

	@Override
	public Set<NodeReader> getNodes() {
		Set<NodeReader> nodeSet = new HashSet<NodeReader> ();
		
		// put all the graph's nodes into the node reader set
		for(NodeType xmlNode: newGraph.getNodes().getNode()) {
			NodeReader nodeReader = new NodeReaderImpl(xmlNode, newGraph);
			nodeSet.add(nodeReader);
		}
		
		return nodeSet;
	}
	
}
