//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.03.01 alle 11:20:23 AM CET 
//


package it.polito.dp2.NFV.sol3.service.ServiceXML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per extendedLinkType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="extendedLinkType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.example.com/nfv}linkType">
 *       &lt;sequence>
 *         &lt;element name="overwrite" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "extendedLinkType", propOrder = {
    "overwrite"
})
public class ExtendedLinkType
    extends LinkType
{

    protected boolean overwrite;

    /**
     * Recupera il valore della proprietà overwrite.
     * 
     */
    public boolean isOverwrite() {
        return overwrite;
    }

    /**
     * Imposta il valore della proprietà overwrite.
     * 
     */
    public void setOverwrite(boolean value) {
        this.overwrite = value;
    }

}
