//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.02.15 alle 11:35:09 PM CET 
//


package it.polito.dp2.NFV.sol3.service.ServiceXML;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.polito.dp2.NFV.sol3.service.ServiceXML package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Catalog_QNAME = new QName("http://www.example.com/nfv", "catalog");
    private final static QName _Hosts_QNAME = new QName("http://www.example.com/nfv", "hosts");
    private final static QName _Nffgs_QNAME = new QName("http://www.example.com/nfv", "nffgs");
    private final static QName _NffgsInfo_QNAME = new QName("http://www.example.com/nfv", "nffgs-info");
    private final static QName _Node_QNAME = new QName("http://www.example.com/nfv", "node");
    private final static QName _Connections_QNAME = new QName("http://www.example.com/nfv", "connections");
    private final static QName _Nffg_QNAME = new QName("http://www.example.com/nfv", "nffg");
    private final static QName _Connection_QNAME = new QName("http://www.example.com/nfv", "connection");
    private final static QName _Link_QNAME = new QName("http://www.example.com/nfv", "link");
    private final static QName _Host_QNAME = new QName("http://www.example.com/nfv", "host");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.polito.dp2.NFV.sol3.service.ServiceXML
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NFV }
     * 
     */
    public NFV createNFV() {
        return new NFV();
    }

    /**
     * Create an instance of {@link HostType }
     * 
     */
    public HostType createHostType() {
        return new HostType();
    }

    /**
     * Create an instance of {@link InfrastructureType }
     * 
     */
    public InfrastructureType createInfrastructureType() {
        return new InfrastructureType();
    }

    /**
     * Create an instance of {@link NffgsInfo }
     * 
     */
    public NffgsInfo createNffgsInfo() {
        return new NffgsInfo();
    }

    /**
     * Create an instance of {@link NodeType }
     * 
     */
    public NodeType createNodeType() {
        return new NodeType();
    }

    /**
     * Create an instance of {@link NffgsType }
     * 
     */
    public NffgsType createNffgsType() {
        return new NffgsType();
    }

    /**
     * Create an instance of {@link CatalogType }
     * 
     */
    public CatalogType createCatalogType() {
        return new CatalogType();
    }

    /**
     * Create an instance of {@link ExtendedHostsType }
     * 
     */
    public ExtendedHostsType createExtendedHostsType() {
        return new ExtendedHostsType();
    }

    /**
     * Create an instance of {@link LinkType }
     * 
     */
    public LinkType createLinkType() {
        return new LinkType();
    }

    /**
     * Create an instance of {@link ExtendedHostType }
     * 
     */
    public ExtendedHostType createExtendedHostType() {
        return new ExtendedHostType();
    }

    /**
     * Create an instance of {@link NffgType }
     * 
     */
    public NffgType createNffgType() {
        return new NffgType();
    }

    /**
     * Create an instance of {@link ConnectionType }
     * 
     */
    public ConnectionType createConnectionType() {
        return new ConnectionType();
    }

    /**
     * Create an instance of {@link ConnectionsType }
     * 
     */
    public ConnectionsType createConnectionsType() {
        return new ConnectionsType();
    }

    /**
     * Create an instance of {@link NFV.NffgList }
     * 
     */
    public NFV.NffgList createNFVNffgList() {
        return new NFV.NffgList();
    }

    /**
     * Create an instance of {@link HostsType }
     * 
     */
    public HostsType createHostsType() {
        return new HostsType();
    }

    /**
     * Create an instance of {@link FunctionType }
     * 
     */
    public FunctionType createFunctionType() {
        return new FunctionType();
    }

    /**
     * Create an instance of {@link DeployedNodeType }
     * 
     */
    public DeployedNodeType createDeployedNodeType() {
        return new DeployedNodeType();
    }

    /**
     * Create an instance of {@link HostType.DeployedNodes }
     * 
     */
    public HostType.DeployedNodes createHostTypeDeployedNodes() {
        return new HostType.DeployedNodes();
    }

    /**
     * Create an instance of {@link InfrastructureType.Hosts }
     * 
     */
    public InfrastructureType.Hosts createInfrastructureTypeHosts() {
        return new InfrastructureType.Hosts();
    }

    /**
     * Create an instance of {@link InfrastructureType.Connections }
     * 
     */
    public InfrastructureType.Connections createInfrastructureTypeConnections() {
        return new InfrastructureType.Connections();
    }

    /**
     * Create an instance of {@link NffgsInfo.NffgInfo }
     * 
     */
    public NffgsInfo.NffgInfo createNffgsInfoNffgInfo() {
        return new NffgsInfo.NffgInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CatalogType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.example.com/nfv", name = "catalog")
    public JAXBElement<CatalogType> createCatalog(CatalogType value) {
        return new JAXBElement<CatalogType>(_Catalog_QNAME, CatalogType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtendedHostsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.example.com/nfv", name = "hosts")
    public JAXBElement<ExtendedHostsType> createHosts(ExtendedHostsType value) {
        return new JAXBElement<ExtendedHostsType>(_Hosts_QNAME, ExtendedHostsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NffgsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.example.com/nfv", name = "nffgs")
    public JAXBElement<NffgsType> createNffgs(NffgsType value) {
        return new JAXBElement<NffgsType>(_Nffgs_QNAME, NffgsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NffgsInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.example.com/nfv", name = "nffgs-info")
    public JAXBElement<NffgsInfo> createNffgsInfo(NffgsInfo value) {
        return new JAXBElement<NffgsInfo>(_NffgsInfo_QNAME, NffgsInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.example.com/nfv", name = "node")
    public JAXBElement<NodeType> createNode(NodeType value) {
        return new JAXBElement<NodeType>(_Node_QNAME, NodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConnectionsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.example.com/nfv", name = "connections")
    public JAXBElement<ConnectionsType> createConnections(ConnectionsType value) {
        return new JAXBElement<ConnectionsType>(_Connections_QNAME, ConnectionsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NffgType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.example.com/nfv", name = "nffg")
    public JAXBElement<NffgType> createNffg(NffgType value) {
        return new JAXBElement<NffgType>(_Nffg_QNAME, NffgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConnectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.example.com/nfv", name = "connection")
    public JAXBElement<ConnectionType> createConnection(ConnectionType value) {
        return new JAXBElement<ConnectionType>(_Connection_QNAME, ConnectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LinkType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.example.com/nfv", name = "link")
    public JAXBElement<LinkType> createLink(LinkType value) {
        return new JAXBElement<LinkType>(_Link_QNAME, LinkType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtendedHostType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.example.com/nfv", name = "host")
    public JAXBElement<ExtendedHostType> createHost(ExtendedHostType value) {
        return new JAXBElement<ExtendedHostType>(_Host_QNAME, ExtendedHostType.class, null, value);
    }

}
