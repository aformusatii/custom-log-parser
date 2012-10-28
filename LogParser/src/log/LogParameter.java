package log;

public class LogParameter {
	private LogHelper.Parameter param;
	private String value;

	public LogParameter(LogHelper.Parameter param, String value) {
		this.param = param;
		this.value = value;
	}
	
	public LogHelper.Parameter getParam() {
		return param;
	}

	public void setParam(LogHelper.Parameter param) {
		this.param = param;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
