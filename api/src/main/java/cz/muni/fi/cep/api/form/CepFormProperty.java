/**
 * 
 */
package cz.muni.fi.cep.api.form;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.FormType;

/**
 * Cep implementation of {@link FormProperty}.
 * 
 * Added {@link #input} variable for form input representation. So class could
 * be used not only as form description but also as container for submitted
 * form.
 * 
 * @author Jan Bruzl
 *
 */
public class CepFormProperty implements FormProperty {
	private static final long serialVersionUID = -1362637926496741494L;
	private String id;
	private String name;
	private String value;
	private boolean readable;
	private boolean writable;
	private boolean required;
	private FormType formType;
	private Object input;

	public CepFormProperty() {
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setReadable(boolean readable) {
		this.readable = readable;
	}

	public void setWritable(boolean writable) {
		this.writable = writable;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public void setType(FormType type) {
		this.formType = type;
	}

	public void setInput(Object input) {
		this.input = input;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Object getInput() {
		return input;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public FormType getType() {
		return formType;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public boolean isReadable() {
		return readable;
	}

	@Override
	public boolean isWritable() {
		return writable;
	}

	@Override
	public boolean isRequired() {
		return required;
	}

}
