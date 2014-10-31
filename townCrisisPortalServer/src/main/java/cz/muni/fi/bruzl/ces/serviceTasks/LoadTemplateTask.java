/**
 * 
 */
package cz.muni.fi.bruzl.ces.serviceTasks;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	@SuppressWarnings("unchecked")
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
			logger.error("Template file not found. {}", ex.getMessage());
		}			
		
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		
		Map <String, String> bindings = null;
		try {
			bindings = (Map<String, String>) execution.getVariable("templateVariable");
		}catch(Exception ex) {
			logger.error("Supplied incorrect format of template variables.");
		}
		Template template = engine.createTemplate(templateResource);
		Writable templateWritable = template.make(bindings);
		String mailTemplate = null;
		try {
			mailTemplate = templateWritable.toString();
		}catch(RuntimeException ex) {
			logger.error("Supplied template variables not suitable for template.");
			throw new BpmnError("InvalidInputParameters", "Missing mail variables");
		}
		
		execution.setVariable("mailTemplate", mailTemplate);
	}
}
