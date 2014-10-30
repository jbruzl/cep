package cz.muni.fi.bruzl.ces.entity;

/**
 * Message type enum.
 * 
 * @author Jan Bruzl
 */
public enum MessageType {
	NOTIFICATION("notification"),
	WARNING("warning"), 
	EMERGENCY("emergency");
	
	private final String value;
	
	MessageType(String value) {
		this.value = value;
	}
	
	public static MessageType fromValue(String value) {
		if(value != null) {
			for(MessageType messageType : values()) {
				if(messageType.value.equals(value)) {
					return messageType;
				}
			}
		}
		return getDefault();
	}
	
	public String toValue() {
		return value;
	}

	private static MessageType getDefault() {
		return NOTIFICATION;
	}
	
}
