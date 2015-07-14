package com.gorillalogic.monkeytalk.processor.report.detail;

import java.io.File;

import com.gorillalogic.monkeytalk.processor.PlaybackResult;
import com.gorillalogic.monkeytalk.processor.PlaybackStatus;
import com.gorillalogic.monkeytalk.processor.Scope;
import com.gorillalogic.monkeytalk.utils.FileUtils;

/**
 *  * Helper class for runners 
 * - consolidates and writes reports from PlaybackResult objects
 *
 */
public class DetailReportWriter {
	private File xmlReport = null;
	private File htmlReport = null;
	
	/**
	 * write the Detail Reports, XML and HTML
	 * @param result - the result of the run to be reported
	 * @param scope - the scope used for the run to be reported
	 * @param reportDir - target directory for the reports and screenshots
	 * @param runnerVersion - the version string for the runner. if null, the runner version will be "unreported"
	 * @param agentVersion - the version string for the agent. if null, the agent version will be "unreported"
	 */
	public void writeDetailReport(PlaybackResult result, Scope scope, 
			File projectDir, File reportDir, String runnerVersion, String agentVersion) {
		_writeDetailReport(result, scope, projectDir, reportDir, runnerVersion, agentVersion);
	}
	
	/**
	 * @return the File containing the XML detail report, or null if none was created
	 */
	public File getXmlReport() {
		return xmlReport;
	}

	/**
	 * @return the File containing the XML detail report, or null if none was created
	 */
	public File getHtmlReport() {
		return htmlReport;
	}

	protected void setHtmlReport(File htmlReport) {
		this.htmlReport = htmlReport;
	}

	protected void setXmlReport(File xmlReport) {
		this.xmlReport = xmlReport;
	}

	protected void _writeDetailReport(PlaybackResult result, Scope scope, 
			File projectDir, File reportDir, String runnerVersion, String agentVersion) {
		xmlReport = null;
		htmlReport = null;
		if (reportDir == null) {
			return;
		}

		if (result == null) {
			result = new PlaybackResult(PlaybackStatus.ERROR, "no results returned", scope);
		}
		
		try {
			String xmlReport = getDetailReportXml(result, scope, projectDir, 
					reportDir, runnerVersion, agentVersion);
			
			// Save the xml detail report
			setXmlReport(saveXmlDetailReport(reportDir, result, scope, xmlReport));
			
			// Save the hmtl detail report
			setHtmlReport(saveHtmlDetailReport(reportDir, result, scope, xmlReport));

		} catch (Exception e) {
			System.out.println("error writing the detail report: " + e.getMessage());
			e.printStackTrace();
		}
	}

	protected File saveXmlDetailReport(File reportdir, PlaybackResult result, Scope scope, String xmlReport)
			throws Exception {
		String scriptName = "script";
		if (result.getScope()!=null && result.getScope().getFilename()!=null 
						&& result.getScope().getFilename().length()>0) {
			scriptName = new File(result.getScope().getFilename()).getName();
		} else if (scope!=null && scope.getFilename()!=null  && scope.getFilename().length()>0) {
			scriptName = new File(scope.getFilename()).getName();
		}

		File detailReportFile = new File(reportdir, getXMLDetailReportFilename(scriptName));

		FileUtils.writeFile(detailReportFile, xmlReport, true);
		
		return detailReportFile;
	}

	protected File saveHtmlDetailReport(File reportdir, PlaybackResult result, Scope scope, String xmlReport)
			throws Exception {
		
		File detailReportFile = null;
		String scriptName = "script";
		if (result.getScope()!=null && result.getScope().getFilename()!=null 
						&& result.getScope().getFilename().length()>0) {
			scriptName = new File(result.getScope().getFilename()).getName();
		} else if (scope!=null && scope.getFilename()!=null  && scope.getFilename().length()>0) {
			scriptName = new File(scope.getFilename()).getName();
		}
		
		if (result.getScope()!=null && result.getScope().getFilename()!=null) { 
			detailReportFile = new File(reportdir, getHTMLDetailReportFilename(scriptName));
		} else {
			detailReportFile = new File(reportdir, getHTMLDetailReportFilename(scriptName));
			result.setScope(new Scope("DETAIL"));
		}
		
		String detailHtml = getDetailReportHtml(result, xmlReport);
		
		FileUtils.writeFile(detailReportFile, detailHtml, true);
		
		return detailReportFile;
	}

	protected DetailReportHtml createDetailReportHtml() {
		return DetailReportHtmlFactory.createDetailReportHtml();
	}

	protected String getDetailReportXml(PlaybackResult result, Scope scope, File projectDir, File reportDir, 
			String runnerVersion, String agentVersion) {
		String report = null;
		runnerVersion = runnerVersion!=null ? runnerVersion : "unreported";
		agentVersion = agentVersion!=null ? agentVersion : "unreported";
		try {
			report = new ScriptReportHelper().createDetailReport(result, scope, projectDir,
					reportDir, runnerVersion, agentVersion).toXMLDocument();
		} catch (Exception e) {
			e.printStackTrace();
			report = "<detail result=\"ERROR\"><msg><![CDATA[" + "REPORTING ERROR : "
					+ e.getMessage() + "]]></msg></detail>";
		}
		return report;
	}

	protected String getDetailReportHtml(PlaybackResult result, String detailXml) {
		String report = null;
		try {
			report = createDetailReportHtml().createDetailReportHtml(result, detailXml);
		} catch (Exception ex) {
			ex.printStackTrace();
			report = "<html><head><title>ERROR</title></head>" + "<body><h1>REPORTING ERROR</h1>"
					+ "<p>" + ex.getMessage() + "</p></body></html>";
		}
		return report;
	}

	public static String getXMLDetailReportFilename(String filename) {
		String name;
		if (filename != null) {
			name = "DETAIL-" + filename + ".xml";
		} else {
			name = "DETAIL.xml";
		}
		return name;
	}

	public static String getHTMLDetailReportFilename(String filename) {
		String name;
		if (filename != null) {
			name = "DETAIL-" + filename + ".html";
		} else {
			name = "DETAIL.html";
		}
		return name;
	}
	
}
