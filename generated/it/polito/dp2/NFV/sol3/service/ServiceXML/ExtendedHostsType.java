//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.02.15 alle 11:35:09 PM CET 
//


package it.polito.dp2.NFV.sol3.service.ServiceXML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per extendedHostsType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="extendedHostsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="extendesHostType" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "extendedHostsType", propOrder = {
    "extendesHostType"
})
public class ExtendedHostsType {

    @XmlElement(required = true)
    protected Object extendesHostType;

    /**
     * Recupera il valore della proprietà extendesHostType.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getExtendesHostType() {
        return extendesHostType;
    }

    /**
     * Imposta il valore della proprietà extendesHostType.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setExtendesHostType(Object value) {
        this.extendesHostType = value;
    }

}
