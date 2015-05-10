package cz.muni.fi.cep.activiti.staffcall.smsresponse;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

/**
 * Item element for sms response
 * 
 * @author Jan Bruzl
 */
public class SMSItem implements Serializable{
	private static final long serialVersionUID = 8484501481598017438L;
	private String number;
	private Date time;
	private String message;
	
	public String getNumber() {
		return number;
	}
	
	@XmlElement(name="number", type=String.class, required=true)
	public void setNumber(String number) {
		this.number = number;
	}
	
	public Date getTime() {
		return time;
	}
	
	@XmlElement(name="time", type=Date.class, required=true)
	public void setTime(Date time) {
		this.time = time;
	}
	
	public String getMessage() {
		return message;
	}
	
	@XmlElement(name="message",type=String.class, required=true)
	public void setMessage(String message) {
		this.message = message;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
