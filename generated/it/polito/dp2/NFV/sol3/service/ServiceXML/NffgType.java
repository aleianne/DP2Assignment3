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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java per nffgType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="nffgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="node" type="{http://www.example.com/nfv}nodeType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="nffg-name" use="required" type="{http://www.example.com/nfv}nfvName" />
 *       &lt;attribute name="deploy-time" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nffgType", propOrder = {
    "node"
})
public class NffgType {

    @XmlElement(required = true)
    protected List<NodeType> node;
    @XmlAttribute(name = "nffg-name", required = true)
    protected String nffgName;
    @XmlAttribute(name = "deploy-time")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar deployTime;

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
     * {@link NodeType }
     * 
     * 
     */
    public List<NodeType> getNode() {
        if (node == null) {
            node = new ArrayList<NodeType>();
        }
        return this.node;
    }

    /**
     * Recupera il valore della proprietà nffgName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNffgName() {
        return nffgName;
    }

    /**
     * Imposta il valore della proprietà nffgName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNffgName(String value) {
        this.nffgName = value;
    }

    /**
     * Recupera il valore della proprietà deployTime.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDeployTime() {
        return deployTime;
    }

    /**
     * Imposta il valore della proprietà deployTime.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDeployTime(XMLGregorianCalendar value) {
        this.deployTime = value;
    }

}
