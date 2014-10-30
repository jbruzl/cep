/**
 * 
 */
package cz.muni.fi.bruzl.sirenstub;

import java.util.concurrent.atomic.AtomicLong;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jan Bruzl
 *
 */
@Controller
@RestController
public class SirenCallController {
	private final AtomicLong counter = new AtomicLong();
	private final String sirenSoundPath = "/sirenSoundTest.wav";

	@RequestMapping("/soundSiren")
	public void soundSiren(
			@RequestParam(value = "user", defaultValue = "") String user,
			@RequestParam(value = "password", defaultValue = "") String password) {
		playSiren();
	}

	/**
	 * Sounds siren
	 */
	private void playSiren() {
		new Thread(new Runnable() {
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem
							.getAudioInputStream(SirenCallController.class
									.getResourceAsStream(sirenSoundPath));
					clip.open(inputStream);
					clip.start();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}
}
