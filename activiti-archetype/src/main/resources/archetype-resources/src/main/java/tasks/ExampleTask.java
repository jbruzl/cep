#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.tasks;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Jan Bruzl
 *
 */
@Component
public class ExampleTask implements JavaDelegate {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
	}
}
