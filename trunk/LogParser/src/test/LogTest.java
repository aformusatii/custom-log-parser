package test;

import log.LogFile;
import log.LogHelper;
import log.LogReader;
import log.LogRow;


public final class LogTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		LogHelper.loadProperties();
		LogFile file = LogReader.readFile("G:\\Development\\Projects\\MAP\\Files\\test.log");
		
		for (LogRow row : file.getRows()) {
			row.parse();
			
			//System.out.println(row.getIndex() +  "-> " + row.getParameterValue("SESSION"));
			System.out.println(row.getIndex() +  "-> " + row.getParameterValue("TEST1"));
			
			/* LogParameter param = row.getParameter("REQUEST_2");
			if (param != null) {
				System.out.println(row.getIndex() +  "-> " + param.getValue());				
			} */
			
			/* param = row.getParameter("RESPONSE_2");
			if (param != null) {
				System.out.println(row.getIndex() +  "-> " + param.getValue());				
			}	*/		
		}
	}

}