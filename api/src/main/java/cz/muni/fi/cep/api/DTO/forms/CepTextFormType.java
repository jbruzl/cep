package cz.muni.fi.cep.api.DTO.forms;

import org.activiti.engine.form.AbstractFormType;
import org.activiti.engine.form.FormType;

/**
 * Cep custom {@link FormType} implementation of text type.
 * 
 * @author Jan Bruzl
 */
public class CepTextFormType extends AbstractFormType {

	public String getName() {
	    return "text";
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
