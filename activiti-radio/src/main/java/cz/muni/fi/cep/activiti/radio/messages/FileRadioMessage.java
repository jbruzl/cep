package cz.muni.fi.cep.activiti.radio.messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link RadioMessage} where audio message is stored in
 * external file. Audio is supposed to be in WAV format.
 * 
 * @author Jan Bruzl
 */
public class FileRadioMessage implements RadioMessage {
	private static final long serialVersionUID = 465622643087983258L;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String author;

	private GregorianCalendar recordDate;

	private String audioFileName;

	public String getAudioFileName() {
		return audioFileName;
	}

	public void setAudioFileName(String audioFileName) {
		this.audioFileName = audioFileName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public GregorianCalendar getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(GregorianCalendar recordDate) {
		this.recordDate = recordDate;
	}

	@Override
	public InputStream getRadioMessage() {
		File file = new File(audioFileName);

		try (FileInputStream fis = new FileInputStream(file)) {
			return fis;
		} catch (IOException e) {
			logger.error("Could not load file: {}. Stack trace: {}",
					audioFileName, e.toString());
		}
		return null;
	}

}
