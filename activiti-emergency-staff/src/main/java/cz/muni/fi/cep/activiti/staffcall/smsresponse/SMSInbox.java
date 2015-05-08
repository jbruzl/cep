package cz.muni.fi.cep.activiti.staffcall.smsresponse;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

public class SMSInbox implements Serializable{
	private static final long serialVersionUID = 5253382301174408346L;
	private SMSDelivery smsDelivery;
	
	public SMSDelivery getSmsDelivery() {
		return smsDelivery;
	}
	
	@XmlElement(name="delivery_sms", type=SMSDelivery.class)
	public void setSmsDelivery(SMSDelivery smsDelivery) {
		this.smsDelivery = smsDelivery;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	

}
