package com.gorillalogic.monkeytalk.processor.report.detail.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import com.gorillalogic.monkeytalk.processor.PlaybackResult;
import com.gorillalogic.monkeytalk.processor.PlaybackStatus;
import com.gorillalogic.monkeytalk.processor.Scope;
import com.gorillalogic.monkeytalk.processor.report.detail.DetailReportWriter;
import com.gorillalogic.monkeytalk.utils.FileUtils;
import com.gorillalogic.monkeytalk.utils.TestHelper;

public class DetailReportWriterTest extends TestHelper {

	@After
	public void after() throws IOException {
		cleanup();
	}

	@Test
	public void testWriteDetailReportWithNullVersions() throws Exception {
		File dir = tempDir();
		File projectDir = new File(dir, "project");
		projectDir.mkdirs();
		File reportDir = new File(dir, "report");

		doWriteDetailReport(projectDir, reportDir, "fake-o-runner v0.0", "bogo-agent v999.999");
	}

	@Test
	public void testWriteDetailReport() throws Exception {
		File dir = tempDir();
		File projectDir = new File(dir, "project");
		projectDir.mkdirs();
		File reportDir = new File(dir, "report");

		doWriteDetailReport(projectDir, reportDir, null, null);
	}

	private void doWriteDetailReport(File projectDir, File reportDir, String runnerVersion,
			String agentVersion) throws Exception {
		Scope scope = new Scope("foo.mt");
		PlaybackResult result = new PlaybackResult(PlaybackStatus.OK, "everything is great", scope,
				null, "R0lGODlhAQABAIAAAAUEBAAAACwAAAAAAQABAAACAkQBADs=");
		DetailReportWriter writer = new DetailReportWriter();
		writer.writeDetailReport(result, scope, projectDir, reportDir, runnerVersion, agentVersion);

		// / set these after running, for validation
		runnerVersion = runnerVersion != null ? runnerVersion : "unreported";
		agentVersion = agentVersion != null ? agentVersion : "unreported";

		File xmlReport = writer.getXmlReport();
		assertThat(xmlReport, notNullValue());
		assertThat(xmlReport.exists(), is(true));
		assertThat(xmlReport.getName(), is("DETAIL-foo.mt.xml"));
		assertThat(xmlReport.getParentFile().getAbsolutePath(), is(reportDir.getAbsolutePath()));
		String xml = FileUtils.readFile(xmlReport);
		assertThat(xml, containsString("<detail "));
		assertThat(xml, containsString("projectPath=\"" + projectDir.getAbsolutePath()));
		assertThat(xml, containsString("runner=\"" + runnerVersion + "\""));
		assertThat(xml, containsString("agent=\"" + agentVersion + "\""));

		File htmlReport = writer.getHtmlReport();
		assertThat(htmlReport, notNullValue());
		assertThat(htmlReport.exists(), is(true));
		assertThat(htmlReport.getParentFile().getAbsolutePath(), is(reportDir.getAbsolutePath()));
		assertThat(htmlReport.getName(), is("DETAIL-foo.mt.html"));
		xml = FileUtils.readFile(htmlReport);
		assertThat(xml, containsString("<h1>DETAIL-foo.mt.html</h1>"));
		assertThat(xml, containsString("projectPath=\"" + projectDir.getAbsolutePath()));
		assertThat(xml, containsString(">" + agentVersion + ", " + runnerVersion + "<"));

		File screenshotsDir = new File(reportDir, "screenshots");
		assertThat(screenshotsDir.exists(), is(true));
		assertThat(screenshotsDir.list().length, is(1));
	}
}