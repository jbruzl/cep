package cz.muni.fi.cep.api.DTO.forms;

import org.activiti.engine.form.AbstractFormType;
import org.activiti.engine.form.FormType;

/**
 * Cep custom {@link FormType} implementation of file type.
 * 
 * @author Jan Bruzl
 */
public class CepFileFormType extends AbstractFormType {

	public String getName() {
	    return "file";
	  }

	  public String getMimeType() {
	    return "text/plain";
	  }

	  public Object convertFormValueToModelValue(String propertyValue) {
	    return propertyValue;
	  }

	  public String convertModelValueToFormValue(Object modelValue) {
	    return (String) modelValue;
	  }

}
