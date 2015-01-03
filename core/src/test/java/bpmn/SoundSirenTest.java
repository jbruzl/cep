/**
 * 
 */
package bpmn;

import org.activiti.engine.delegate.BpmnError;
import org.junit.Test;

/**
 * @author Jan Bruzl
 *
 */
public class SoundSirenTest extends ActivitiBasicTest{	
	@Test
	@org.activiti.engine.test.Deployment(resources = { "bpmn/SoundSiren.bpmn" })
	public void testSoundSiren() {
		try {
			runtimeService.startProcessInstanceByKey("SoundSiren");
		}catch(BpmnError ex) {
			System.err.println("Server not responding! Check that.");
		}
	}
}
