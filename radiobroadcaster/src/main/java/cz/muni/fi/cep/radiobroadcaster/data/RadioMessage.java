package cz.muni.fi.cep.radiobroadcaster.data;

import java.io.File;
import java.util.Calendar;

import org.springframework.core.io.FileSystemResource;

/**
 * 
 * @author Jan Bruzl
 */
public class RadioMessage{
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Calendar getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Calendar recordDate) {
		this.recordDate = recordDate;
	}

	public FileSystemResource getRadioMessage() {
		File file = new File(audioFileName);
		return new FileSystemResource(file);
	}

}
