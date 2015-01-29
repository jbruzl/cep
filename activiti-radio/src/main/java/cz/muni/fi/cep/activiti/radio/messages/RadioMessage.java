package cz.muni.fi.cep.activiti.radio.messages;

import java.io.Serializable;
import java.util.GregorianCalendar;

import org.springframework.core.io.FileSystemResource;

/**
 * Describes interface for class that contains audio message.
 * 
 * @author Jan Bruzl
 */
public interface RadioMessage extends Serializable {
	
	/**
	 * Returns stored audio message as input stream.
	 * 
	 * @return {@link FileSystemResource}
	 */
	public FileSystemResource getRadioMessage();
	
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
