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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java per nffgsInfo complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="nffgsInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nffg-info">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="nffg-name" type="{http://www.example.com/nfv}nfvName"/>
 *                   &lt;element name="deploy-date" type="{http://www.w3.org/2001/XMLSchema}date"/>
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
@XmlType(name = "nffgsInfo", propOrder = {
    "nffgInfo"
})
public class NffgsInfo {

    @XmlElement(name = "nffg-info", required = true)
    protected NffgsInfo.NffgInfo nffgInfo;

    /**
     * Recupera il valore della proprietà nffgInfo.
     * 
     * @return
     *     possible object is
     *     {@link NffgsInfo.NffgInfo }
     *     
     */
    public NffgsInfo.NffgInfo getNffgInfo() {
        return nffgInfo;
    }

    /**
     * Imposta il valore della proprietà nffgInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link NffgsInfo.NffgInfo }
     *     
     */
    public void setNffgInfo(NffgsInfo.NffgInfo value) {
        this.nffgInfo = value;
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
     *         &lt;element name="nffg-name" type="{http://www.example.com/nfv}nfvName"/>
     *         &lt;element name="deploy-date" type="{http://www.w3.org/2001/XMLSchema}date"/>
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
        "nffgName",
        "deployDate"
    })
    public static class NffgInfo {

        @XmlElement(name = "nffg-name", required = true)
        protected String nffgName;
        @XmlElement(name = "deploy-date", required = true)
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar deployDate;

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
         * Recupera il valore della proprietà deployDate.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getDeployDate() {
            return deployDate;
        }

        /**
         * Imposta il valore della proprietà deployDate.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setDeployDate(XMLGregorianCalendar value) {
            this.deployDate = value;
        }

    }

}
