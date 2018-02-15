//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.02.15 alle 11:35:09 PM CET 
//


package it.polito.dp2.NFV.sol3.service.ServiceXML;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per hostType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="hostType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="deployed-nodes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="node" type="{http://www.example.com/nfv}deployedNodeType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="availableMemory" type="{http://www.example.com/nfv}positiveInt"/>
 *         &lt;element name="availableStorage" type="{http://www.example.com/nfv}positiveInt"/>
 *         &lt;element name="maxVNF" type="{http://www.example.com/nfv}positiveInt"/>
 *         &lt;element name="hostname" type="{http://www.example.com/nfv}nfvName"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hostType", propOrder = {
    "deployedNodes",
    "availableMemory",
    "availableStorage",
    "maxVNF",
    "hostname"
})
@XmlSeeAlso({
    ExtendedHostType.class
})
public class HostType {

    @XmlElement(name = "deployed-nodes", required = true)
    protected HostType.DeployedNodes deployedNodes;
    @XmlElement(required = true)
    protected BigInteger availableMemory;
    @XmlElement(required = true)
    protected BigInteger availableStorage;
    @XmlElement(required = true)
    protected BigInteger maxVNF;
    @XmlElement(required = true)
    protected String hostname;

    /**
     * Recupera il valore della proprietà deployedNodes.
     * 
     * @return
     *     possible object is
     *     {@link HostType.DeployedNodes }
     *     
     */
    public HostType.DeployedNodes getDeployedNodes() {
        return deployedNodes;
    }

    /**
     * Imposta il valore della proprietà deployedNodes.
     * 
     * @param value
     *     allowed object is
     *     {@link HostType.DeployedNodes }
     *     
     */
    public void setDeployedNodes(HostType.DeployedNodes value) {
        this.deployedNodes = value;
    }

    /**
     * Recupera il valore della proprietà availableMemory.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAvailableMemory() {
        return availableMemory;
    }

    /**
     * Imposta il valore della proprietà availableMemory.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAvailableMemory(BigInteger value) {
        this.availableMemory = value;
    }

    /**
     * Recupera il valore della proprietà availableStorage.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAvailableStorage() {
        return availableStorage;
    }

    /**
     * Imposta il valore della proprietà availableStorage.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAvailableStorage(BigInteger value) {
        this.availableStorage = value;
    }

    /**
     * Recupera il valore della proprietà maxVNF.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxVNF() {
        return maxVNF;
    }

    /**
     * Imposta il valore della proprietà maxVNF.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxVNF(BigInteger value) {
        this.maxVNF = value;
    }

    /**
     * Recupera il valore della proprietà hostname.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Imposta il valore della proprietà hostname.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostname(String value) {
        this.hostname = value;
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
     *         &lt;element name="node" type="{http://www.example.com/nfv}deployedNodeType" maxOccurs="unbounded"/>
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
        "node"
    })
    public static class DeployedNodes {

        @XmlElement(required = true)
        protected List<DeployedNodeType> node;

        /**
         * Gets the value of the node property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the node property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getNode().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DeployedNodeType }
         * 
         * 
         */
        public List<DeployedNodeType> getNode() {
            if (node == null) {
                node = new ArrayList<DeployedNodeType>();
            }
            return this.node;
        }

    }

}
