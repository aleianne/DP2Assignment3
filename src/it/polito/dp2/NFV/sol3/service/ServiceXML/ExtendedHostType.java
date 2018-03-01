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
 * <p>Classe Java per extendedHostType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="extendedHostType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.example.com/nfv}hostType">
 *       &lt;sequence>
 *         &lt;element name="totalVNFallocated" type="{http://www.example.com/nfv}positiveInt"/>
 *         &lt;element name="storageUsed" type="{http://www.example.com/nfv}positiveInt"/>
 *         &lt;element name="memoryUsed" type="{http://www.example.com/nfv}positiveInt"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "extendedHostType", propOrder = {
    "totalVNFallocated",
    "storageUsed",
    "memoryUsed"
})
public class ExtendedHostType
    extends HostType
{

    @XmlElement(required = true)
    protected BigInteger totalVNFallocated;
    @XmlElement(required = true)
    protected BigInteger storageUsed;
    @XmlElement(required = true)
    protected BigInteger memoryUsed;

    /**
     * Recupera il valore della proprietà totalVNFallocated.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalVNFallocated() {
        return totalVNFallocated;
    }

    /**
     * Imposta il valore della proprietà totalVNFallocated.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalVNFallocated(BigInteger value) {
        this.totalVNFallocated = value;
    }

    /**
     * Recupera il valore della proprietà storageUsed.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStorageUsed() {
        return storageUsed;
    }

    /**
     * Imposta il valore della proprietà storageUsed.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStorageUsed(BigInteger value) {
        this.storageUsed = value;
    }

    /**
     * Recupera il valore della proprietà memoryUsed.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMemoryUsed() {
        return memoryUsed;
    }

    /**
     * Imposta il valore della proprietà memoryUsed.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMemoryUsed(BigInteger value) {
        this.memoryUsed = value;
    }

}
