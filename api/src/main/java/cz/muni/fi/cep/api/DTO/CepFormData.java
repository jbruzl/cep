/**
 * 
 */
package cz.muni.fi.cep.api.DTO;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;

/**
 * Cep implementation of {@link FormData}.
 * 
 * @author Jan Bruzl
 *
 */
public class CepFormData implements FormData {
	private String formKey;
	private String deploymentId;
	List<FormProperty> formProperties = new ArrayList<>();;
	
	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public void setFormProperties(List<FormProperty> formProperties) {
		this.formProperties = formProperties;
	}

	@Override
	public String getFormKey() {
		return formKey;
	}

	@Override
	public String getDeploymentId() {
		return deploymentId;
	}

	@Override
	public List<FormProperty> getFormProperties() {
		return formProperties;
	}

}
