/**
 * 
 */
package cz.muni.fi.bruzl.ces.serviceTasks;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.file.Paths.get;

/**
 * @author Jan Bruzl
 *
 */
public class LoadTemplateTask implements JavaDelegate {
	private Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.activiti.engine.delegate.JavaDelegate#execute(org.activiti.engine
	 * .delegate.DelegateExecution)
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String templatePath = (String) execution.getVariable("templatePath");
		String templateResource = null;
		
		//TODO change resource load	
		try(InputStream is = this.getClass().getResourceAsStream("/"+templatePath )){
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder out = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            out.append(line);
	        }
	        templateResource = out.toString();
	        reader.close();
		}catch(FileNotFoundException ex){
			ex.printStackTrace();			
		}			
		
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		//TODO add support for input parameters - load bindings as process variable
		Map <String, String> bindings = (Map<String, String>) execution.getVariable("templateVariable");
		
		Template template = engine.createTemplate(templateResource);
		Writable templateWritable = template.make(bindings);
		String mailTemplate = null;
		try {
			mailTemplate = templateWritable.toString();
		}catch(RuntimeException ex) {
			throw new BpmnError("InvalidInputParameters", "Missing mail variables");
		}
		
		execution.setVariable("mailTemplate", mailTemplate);
	}
}
