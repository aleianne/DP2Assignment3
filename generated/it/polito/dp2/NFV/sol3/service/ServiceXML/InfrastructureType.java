//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.03.05 alle 10:10:31 AM CET 
//


package it.polito.dp2.NFV.sol3.service.ServiceXML;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per infrastructureType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="infrastructureType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hosts">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="host" type="{http://www.example.com/nfv}hostType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="connections">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="connection" type="{http://www.example.com/nfv}connectionType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "infrastructureType", propOrder = {
    "hosts",
    "connections"
})
public class InfrastructureType {

    @XmlElement(required = true)
    protected InfrastructureType.Hosts hosts;
    @XmlElement(required = true)
    protected InfrastructureType.Connections connections;

    /**
     * Recupera il valore della proprietà hosts.
     * 
     * @return
     *     possible object is
     *     {@link InfrastructureType.Hosts }
     *     
     */
    public InfrastructureType.Hosts getHosts() {
        return hosts;
    }

    /**
     * Imposta il valore della proprietà hosts.
     * 
     * @param value
     *     allowed object is
     *     {@link InfrastructureType.Hosts }
     *     
     */
    public void setHosts(InfrastructureType.Hosts value) {
        this.hosts = value;
    }

    /**
     * Recupera il valore della proprietà connections.
     * 
     * @return
     *     possible object is
     *     {@link InfrastructureType.Connections }
     *     
     */
    public InfrastructureType.Connections getConnections() {
        return connections;
    }

    /**
     * Imposta il valore della proprietà connections.
     * 
     * @param value
     *     allowed object is
     *     {@link InfrastructureType.Connections }
     *     
     */
    public void setConnections(InfrastructureType.Connections value) {
        this.connections = value;
    }


    /**
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="connection" type="{http://www.example.com/nfv}connectionType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "connection"
    })
    public static class Connections {

        @XmlElement(required = true)
        protected List<ConnectionType> connection;

        /**
         * Gets the value of the connection property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the connection property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getConnection().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ConnectionType }
         * 
         * 
         */
        public List<ConnectionType> getConnection() {
            if (connection == null) {
                connection = new ArrayList<ConnectionType>();
            }
            return this.connection;
        }

    }


    /**
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="host" type="{http://www.example.com/nfv}hostType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "host"
    })
    public static class Hosts {

        @XmlElement(required = true)
        protected List<HostType> host;

        /**
         * Gets the value of the host property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the host property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getHost().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link HostType }
         * 
         * 
         */
        public List<HostType> getHost() {
            if (host == null) {
                host = new ArrayList<HostType>();
            }
            return this.host;
        }

    }

}
