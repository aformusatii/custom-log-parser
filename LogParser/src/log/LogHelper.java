package log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LogHelper {
	
	public static Pattern ROW_PATTERN;
	public static List<RowType> rowTypes = new ArrayList<RowType>();
	public static HashMap<String, List<Parameter>> paramsMap = new HashMap<String, List<Parameter>>();
	public static HashMap<String, List<Parameter>> separateParamsMap = new HashMap<String, List<Parameter>>();
	public static final String ROW_TYPE_INFO = "INFO";
	public static final int DEFAULT_PAGE_SIZE = 100;
	public static final int LOG_FILE_EXPIRY_TIMEOUT = 20;
	public static final String NEW_LINE = "&_new_line";

	public static void loadProperties() {
		Properties props = new Properties();
		try {
			props.load(LogHelper.class.getResourceAsStream("..\\log.properties"));
			
			Pattern typePattern = Pattern.compile("row\\.type\\.(.*?)\\.pattern");
			Pattern paramPattern = Pattern.compile("row\\.type\\.(.*?)\\.param\\.(.*?)\\.group");
			Pattern singleParamPattern = Pattern.compile("row\\.type\\.(.*?)\\.separate\\.param\\.(.*?)\\.group");
			
			for (Map.Entry<Object, Object> entry : props.entrySet()) {
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				
				Matcher typeMatcher = typePattern.matcher(key);
				if (typeMatcher.matches()) {
					typeMatcher.reset().find();
					String type = typeMatcher.group(1);

					RowType rowType = (new LogHelper()).new RowType();
					rowType.setPatternStr(value);
					rowType.setType(type);					
					rowTypes.add(rowType);
				}
				
				Matcher paramMatcher = paramPattern.matcher(key);
				if (paramMatcher.matches()) {
					paramMatcher.reset().find();
					String type = paramMatcher.group(1);
					String name = paramMatcher.group(2);
					
					List<Parameter> params;
					if (paramsMap.containsKey(type)) {
						params = paramsMap.get(type);
					} else {
						params = new ArrayList<LogHelper.Parameter>();
						paramsMap.put(type, params);
					}

					Parameter param = (new LogHelper()).new Parameter();
					param.setType(type);
					param.setName(name);
					param.setGroup(Integer.valueOf(value));
					params.add(param);
				}

				Matcher singleParamMatcher = singleParamPattern.matcher(key);
				if (singleParamMatcher.matches()) {
					singleParamMatcher.reset().find();
					String type = singleParamMatcher.group(1);
					String name = singleParamMatcher.group(2);
					
					List<Parameter> params;
					if (separateParamsMap.containsKey(type)) {
						params = separateParamsMap.get(type);
					} else {
						params = new ArrayList<LogHelper.Parameter>();
						separateParamsMap.put(type, params);
					}					
					
					Parameter param = (new LogHelper()).new Parameter();
					param.setType(type);
					param.setName(name);
					param.setGroup(Integer.valueOf(value));
					String pattern = props.getProperty("row.type." + type + ".separate.param." + name + ".pattern");
					param.setPatternStr(pattern);
					params.add(param);
				}
			}
			
			ROW_PATTERN = Pattern.compile(props.getProperty("row.pattern"));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public class RowType {
		private String type;
		private String patternStr;
		private Pattern pattern;
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getPatternStr() {
			return patternStr;
		}

		public void setPatternStr(String patternStr) {
			this.patternStr = patternStr;
		}

		public Pattern getPattern() {
			if (pattern == null) {
				pattern = Pattern.compile(patternStr);
				return pattern;
			}
			return pattern;
		}

		public void setPattern(Pattern pattern) {
			this.pattern = pattern;
		}
		
		public boolean matches(String value) {
			return getPattern().matcher(value).matches();
		}		
	}
	
	public class Parameter {
		private String type;
		private String name;
		private int group;
		private String patternStr;
		private Pattern pattern;

		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public int getGroup() {
			return group;
		}
		
		public void setGroup(int group) {
			this.group = group;
		}

		public String getPatternStr() {
			return patternStr;
		}

		public void setPatternStr(String patternStr) {
			this.patternStr = patternStr;
		}

		public Pattern getPattern() {
			if (pattern == null) {
				pattern = Pattern.compile(patternStr);
				return pattern;
			}
			return pattern;
		}

		public void setPattern(Pattern pattern) {
			this.pattern = pattern;
		}
		
		public boolean matches(String value) {
			return getPattern().matcher(value).matches();
		}
	}
	
	public static String getSubstring(String value, String beginStr, String endStr) {
		int beginIndex = -1;
		if (beginStr != null) {
			beginIndex = value.indexOf(beginStr);	
		}
		beginIndex = (beginIndex > -1) ? beginIndex : 0;
		
		int endIndex = -1;
		if (endStr != null) {
			endIndex = value.indexOf(endStr);	
		}
		endIndex = (endIndex > -1) ? endIndex : value.length();		
		
		return value.substring(beginIndex, endIndex);
	}	
	
	public static String getStackTrace(Exception e) {
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    e.printStackTrace(printWriter);
	    return result.toString();
    }
	
	public static boolean isNotBlank(String value) {
		return !isBlank(value);
	}
	
	public static boolean isBlank(String value) {
		return (value == null) || ("".equals(value.trim()));
	}
	
	public static int calculateNumberOfPages(int total, int pageSize) {
		int pages = (total / pageSize) + (((total % pageSize) > 0) ? 1 : 0);
		pages = (pages < 1) ? 1 : pages;
		return pages;
	}
	
}