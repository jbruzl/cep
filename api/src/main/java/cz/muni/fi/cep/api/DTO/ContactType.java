package cz.muni.fi.cep.api.DTO;

/**
 * @author Jan Bruzl
 *
 */
public enum ContactType {
	SMS("SMS"), PHONE("PHONE"), EMAIL("EMAIL");

	private final String value;

	ContactType(String value) {
		this.value = value;
	}

	public static ContactType fromValue(String value) {
		if (value != null) {
			for (ContactType contactType : values()) {
				if (contactType.value.equals(value)) {
					return contactType;
				}
			}
		}
		return getDefault();
	}

	public String toValue() {
		return value;
	}

	private static ContactType getDefault() {
		return EMAIL;
	}

}
