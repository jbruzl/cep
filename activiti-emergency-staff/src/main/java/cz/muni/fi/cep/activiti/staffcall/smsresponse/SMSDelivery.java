package cz.muni.fi.cep.activiti.staffcall.smsresponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;


/**
 * Delivery element for sms response
 * 
 * @author Jan Bruzl
 */
public class SMSDelivery implements Serializable{
	private static final long serialVersionUID = -1890323425974872556L;
	private List<SMSItem> items = new ArrayList<>();

	public List<SMSItem> getItems() {
		return items;
	}

	@XmlElement(name="item", type=SMSItem.class,required=false)
	public void setItems(List<SMSItem> items) {
		this.items = items;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
