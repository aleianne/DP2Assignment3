package it.polito.dp2.NFV.sol3.service.DaoClasses;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/*
 * 		Data access object for nffg graph, 
 * 		this is the main interface between the Database. In this case
 * 		the database used is Neo4J thrugh the Neo4jSimpleXML web service.
 * 		
 */
public class NffgDao {
	
	static NffgDao nffgDao = new NffgDao();
	
	static private ConcurrentMap<String, NffgType> nffgMap = new ConcurrentHashMap<String, NffgType> ();
	static private AtomicInteger nffgIdCounter = new AtomicInteger(0);
	
	public static NffgDao getInstance() {
		return nffgDao;
	}
	
	/* DAO methods */
	
	public int readNffgId() {
		return nffgIdCounter.incrementAndGet();
	}
	
	public Collection<NffgType> readNffgs() {
		return nffgMap.values();
	}

	// this method create insert only the info about the nffg, not the info about the nodes or the link
	public void createNffg(NffgType deployedNffg) {
		nffgMap.put(deployedNffg.getName(), deployedNffg);
	}
	
	public void updateNffg() {}
	
	public void deleteNffg() {}
	
	public NffgType queryNffg(String nffgId) {
		return nffgMap.get(nffgId);
	}
	
}
