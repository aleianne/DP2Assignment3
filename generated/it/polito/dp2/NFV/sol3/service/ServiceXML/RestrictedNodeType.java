//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.03.05 alle 10:10:31 AM CET 
//


package it.polito.dp2.NFV.sol3.service.ServiceXML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per restrictedNodeType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="restrictedNodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.example.com/nfv}nfvName"/>
 *         &lt;element name="VNF" type="{http://www.example.com/nfv}nfvName"/>
 *         &lt;element name="hostname" type="{http://www.example.com/nfv}nfvName"/>
 *         &lt;element name="nf-fg" type="{http://www.example.com/nfv}nfvName"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "restrictedNodeType", propOrder = {
    "name",
    "vnf",
    "hostname",
    "nfFg"
})
public class RestrictedNodeType {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(name = "VNF", required = true)
    protected String vnf;
    @XmlElement(required = true)
    protected String hostname;
    @XmlElement(name = "nf-fg", required = true)
    protected String nfFg;

    /**
     * Recupera il valore della proprietà name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il valore della proprietà name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Recupera il valore della proprietà vnf.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVNF() {
        return vnf;
    }

    /**
     * Imposta il valore della proprietà vnf.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVNF(String value) {
        this.vnf = value;
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
     * Recupera il valore della proprietà nfFg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNfFg() {
        return nfFg;
    }

    /**
     * Imposta il valore della proprietà nfFg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNfFg(String value) {
        this.nfFg = value;
    }

}
