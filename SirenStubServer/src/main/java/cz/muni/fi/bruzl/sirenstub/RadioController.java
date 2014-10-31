/**
 * 
 */
package cz.muni.fi.bruzl.sirenstub;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Jan Bruzl
 *
 */
@Controller
public class RadioController {
	private static final String basePath = "uploadedSounds/";
	private static final DateFormat dateFormat = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	private final AtomicLong counter = new AtomicLong();

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public @ResponseBody String provideUploadInfo() {
		return "You can upload a file by posting to this same URL.";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody Boolean handleFileUpload(
			@RequestParam(value="user", defaultValue="unknown") String user,
			@RequestParam("file") MultipartFile file) {
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();
				Calendar cal = Calendar.getInstance();
				String filePath = basePath + dateFormat.format(cal.getTime())
						+ " - " + file.getOriginalFilename();
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(filePath)));
				stream.write(bytes);
				stream.close();

				playRadio(filePath);
				System.out.println(counter.incrementAndGet() + " " + user + " - playng sound " + filePath);
				return Boolean.TRUE;
			} catch (Exception e) {
				return Boolean.FALSE;
			}
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * Sounds siren
	 */
	private void playRadio(String radioPath) {

		class SoundRadio implements Runnable {
			private String radioPath;

			public SoundRadio(String path) {
				radioPath = path;
			}

			public void run() {
				InputStream is = null;
				try {
					Clip clip = AudioSystem.getClip();
					
					File f = new File(radioPath);
				    is = new FileInputStream( f );
				    AudioInputStream inputStream = AudioSystem
							.getAudioInputStream(new BufferedInputStream(is));
					clip.open(inputStream);
					clip.start();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}

		Runnable r = new SoundRadio(radioPath);
		r.run();

	}

}
