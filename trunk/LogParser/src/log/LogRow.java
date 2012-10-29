package log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import log.LogHelper.Parameter;

public class LogRow {
	
	private int index;
	private StringBuilder data = new StringBuilder();
	private String type = LogHelper.ROW_TYPE_INFO;
	private Map<String, LogParameter> paramsMap = new HashMap<String, LogParameter>();
	private boolean parsed = false;
	
	public void appendValue(String value) {
		data.append(value);
	}
	
	public String getData() {
		return data.toString().replaceAll(LogHelper.NEW_LINE, "\n");
	}
	
	public String getDataWithoutNewLine() {
		return data.toString();
	}
	
	public String getPreview() {
		int length = data.length();
		int endIndex = (length > 255) ? 255 : length;
		return data.substring(0, endIndex);
	}
	
	public String getType() {
		return type;
	}
	
	public LogParameter getParameter(String key) {
		return paramsMap.get(key);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void parse() {
		if (parsed) {return;}
		String dataStr = data.toString().replaceAll("\\r", "").trim();
		if (!dataStr.isEmpty()) {
			for (LogHelper.RowType rowType : LogHelper.rowTypes) {
				if (rowType.matches(dataStr)) {
					type = LogHelper.ROW_TYPE_INFO.equals(rowType.getType()) ? type : rowType.getType();
	
					Matcher matcher = rowType.getPattern().matcher(dataStr);
					if (matcher.find()) {
						List<Parameter> params = LogHelper.paramsMap.get(rowType.getType());
						if (params != null) {
							for (Parameter p : params) {
								String value = matcher.group(p.getGroup()).replaceAll(LogHelper.NEW_LINE, "\n");
								LogParameter param = new LogParameter(p, value);
								paramsMap.put(p.getName(), param);
							}
						}
						
						params = LogHelper.separateParamsMap.get(rowType.getType());
						if (params != null) {
							for (Parameter p : params) {
								Matcher m = p.getPattern().matcher(dataStr);
								if (m.find()) {
									String value = m.group(p.getGroup()).replaceAll(LogHelper.NEW_LINE, "\n");
									LogParameter param = new LogParameter(p, value);
									paramsMap.put(p.getName(), param);									
								}
							}					
						}
					}
				}
			}
		}
		parsed = true;
	}

}
