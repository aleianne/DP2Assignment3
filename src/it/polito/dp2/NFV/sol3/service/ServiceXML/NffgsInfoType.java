//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.02.28 alle 07:36:26 PM CET 
//


package it.polito.dp2.NFV.sol3.service.ServiceXML;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java per nffgsInfoType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="nffgsInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nffg-info" maxOccurs="unbounded">
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
@XmlType(name = "nffgsInfoType", propOrder = {
    "nffgInfo"
})
public class NffgsInfoType {

    @XmlElement(name = "nffg-info", required = true)
    protected List<NffgsInfoType.NffgInfo> nffgInfo;

    /**
     * Gets the value of the nffgInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nffgInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNffgInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NffgsInfoType.NffgInfo }
     * 
     * 
     */
    public List<NffgsInfoType.NffgInfo> getNffgInfo() {
        if (nffgInfo == null) {
            nffgInfo = new ArrayList<NffgsInfoType.NffgInfo>();
        }
        return this.nffgInfo;
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
