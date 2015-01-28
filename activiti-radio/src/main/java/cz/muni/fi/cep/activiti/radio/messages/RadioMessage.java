package cz.muni.fi.cep.activiti.radio.messages;

import java.io.InputStream;
import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * Describes interface for class that contains audio message.
 * 
 * @author Jan Bruzl
 */
public interface RadioMessage extends Serializable {
	
	/**
	 * Returns stored audio message as input stream.
	 * 
	 * @return {@link InputStream}
	 */
	public InputStream getRadioMessage();
	
	/**
	 * Returns author of audio message.
	 * @return {@link String}
	 */
	public String getAuthor();

	/**
	 * Returns date when audio message was recorded.
	 * @return {@link String}
	 */
	public GregorianCalendar getRecordDate();
}
