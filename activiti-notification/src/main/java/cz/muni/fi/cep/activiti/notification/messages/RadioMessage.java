package cz.muni.fi.cep.activiti.notification.messages;

import java.util.Calendar;

import org.springframework.core.io.FileSystemResource;

import scala.Serializable;

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
	public Calendar getRecordDate();
}
