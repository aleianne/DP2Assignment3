//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.02.28 alle 07:36:26 PM CET 
//


package it.polito.dp2.NFV.sol3.service.ServiceXML;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per connectionType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="connectionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hostname1" type="{http://www.example.com/nfv}nfvName"/>
 *         &lt;element name="hostname2" type="{http://www.example.com/nfv}nfvName"/>
 *         &lt;element name="latency" type="{http://www.example.com/nfv}positiveInt"/>
 *         &lt;element name="throughput" type="{http://www.example.com/nfv}throughputType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "connectionType", propOrder = {
    "hostname1",
    "hostname2",
    "latency",
    "throughput"
})
public class ConnectionType {

    @XmlElement(required = true)
    protected String hostname1;
    @XmlElement(required = true)
    protected String hostname2;
    @XmlElement(required = true)
    protected BigInteger latency;
    protected float throughput;

    /**
     * Recupera il valore della proprietà hostname1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostname1() {
        return hostname1;
    }

    /**
     * Imposta il valore della proprietà hostname1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostname1(String value) {
        this.hostname1 = value;
    }

    /**
     * Recupera il valore della proprietà hostname2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostname2() {
        return hostname2;
    }

    /**
     * Imposta il valore della proprietà hostname2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostname2(String value) {
        this.hostname2 = value;
    }

    /**
     * Recupera il valore della proprietà latency.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLatency() {
        return latency;
    }

    /**
     * Imposta il valore della proprietà latency.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLatency(BigInteger value) {
        this.latency = value;
    }

    /**
     * Recupera il valore della proprietà throughput.
     * 
     */
    public float getThroughput() {
        return throughput;
    }

    /**
     * Imposta il valore della proprietà throughput.
     * 
     */
    public void setThroughput(float value) {
        this.throughput = value;
    }

}
