package cz.muni.fi.cep.activiti.staffcall.smsresponse;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Root element for sms response
 * 
 * @author Jan Bruzl
 */
@XmlRootElement(name="result")
public class SMSResult implements Serializable{
	private static final long serialVersionUID = 8492396588505825022L;
	private SMSInbox smsInbox;
	
	public SMSInbox getSmsInbox() {
		return smsInbox;
	}
	
	@XmlElement(name="inbox", type=SMSInbox.class)
	public void setSmsInbox(SMSInbox smsInbox) {
		this.smsInbox = smsInbox;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
