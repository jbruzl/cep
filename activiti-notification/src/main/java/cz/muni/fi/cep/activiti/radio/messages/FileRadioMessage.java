package cz.muni.fi.cep.activiti.radio.messages;

import java.io.File;
import java.util.Calendar;

import org.springframework.core.io.FileSystemResource;

/**
 * Implementation of {@link RadioMessage} where audio message is stored in
 * external file. Audio is supposed to be in WAV format.
 * 
 * @author Jan Bruzl
 */
public class FileRadioMessage implements RadioMessage {
	private static final long serialVersionUID = 465622643087983258L;

	private String author;

	private Calendar recordDate;

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
	public Calendar getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Calendar recordDate) {
		this.recordDate = recordDate;
	}

	@Override
	public FileSystemResource getRadioMessage() {
		File file = new File(audioFileName);
		return new FileSystemResource(file);
	}

}
