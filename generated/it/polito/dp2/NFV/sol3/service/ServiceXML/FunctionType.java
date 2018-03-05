//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.03.05 alle 10:10:31 AM CET 
//


package it.polito.dp2.NFV.sol3.service.ServiceXML;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per functionType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="functionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.example.com/nfv}nfvName"/>
 *         &lt;element name="type" type="{http://www.example.com/nfv}function_enumeration"/>
 *         &lt;element name="requiredStorage" type="{http://www.example.com/nfv}positiveInt"/>
 *         &lt;element name="requiredMemory" type="{http://www.example.com/nfv}positiveInt"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "functionType", propOrder = {
    "name",
    "type",
    "requiredStorage",
    "requiredMemory"
})
public class FunctionType {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected FunctionEnumeration type;
    @XmlElement(required = true)
    protected BigInteger requiredStorage;
    @XmlElement(required = true)
    protected BigInteger requiredMemory;

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
     * Recupera il valore della proprietà type.
     * 
     * @return
     *     possible object is
     *     {@link FunctionEnumeration }
     *     
     */
    public FunctionEnumeration getType() {
        return type;
    }

    /**
     * Imposta il valore della proprietà type.
     * 
     * @param value
     *     allowed object is
     *     {@link FunctionEnumeration }
     *     
     */
    public void setType(FunctionEnumeration value) {
        this.type = value;
    }

    /**
     * Recupera il valore della proprietà requiredStorage.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRequiredStorage() {
        return requiredStorage;
    }

    /**
     * Imposta il valore della proprietà requiredStorage.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRequiredStorage(BigInteger value) {
        this.requiredStorage = value;
    }

    /**
     * Recupera il valore della proprietà requiredMemory.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRequiredMemory() {
        return requiredMemory;
    }

    /**
     * Imposta il valore della proprietà requiredMemory.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRequiredMemory(BigInteger value) {
        this.requiredMemory = value;
    }

}
