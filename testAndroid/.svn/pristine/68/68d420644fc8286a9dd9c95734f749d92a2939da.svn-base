package com.gorillalogic.monkeyconsole.editors.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IProject;
import org.json.JSONObject;

import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeytalk.utils.FileUtils;

/**
 * Uses a {@link ScheduledExecutorService} to poll the {@link CloudServices} for the status of a
 * job. When the job is done the zipped report is downloaded to a temp directory within the
 * project's report directory. The downloaded report is expanded and then moved to a job qualified
 * sub-directory of the project's report directory. The temp directory is deleted and the project is
 * refreshed.
 * 
 * @author j0nm00re
 */
public class ReportRetriever implements Runnable {
	static final private int POLL_FREQUENCY_SECONDS = 5;
	static final private int MAX_POLL_FAILURES = 5;
	final private String jobid;
	final private IProject project;
	final private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private int failures = 0;

	public ReportRetriever(int jobid, IProject project) {
		this.jobid = Integer.toString(jobid);
		this.project = project;
		executor.scheduleAtFixedRate(this, POLL_FREQUENCY_SECONDS, POLL_FREQUENCY_SECONDS,
				TimeUnit.SECONDS);
	}

	/**
	 * Executed every {@link #POLL_FREQUENCY_SECONDS} by {@link #executor}.
	 */
	@Override
	public void run() {
		if (isDone()) {
			executor.shutdown();
			downloadReport();
		}
	}

	/**
	 * Poll for the job status. Stops {@link ReportRetriever#executor} if more than
	 * {@link #MAX_POLL_FAILURES} occur while polling.
	 * 
	 * @return true only when {@link CloudServices} successfully reports that the job is done.
	 */
	private boolean isDone() {
		try {

			// Get the status of the job.
			JSONObject response = CloudServices.getJobStatus(jobid);
			JobStatus status = response == null ? null : JobStatus.valueOf(response);
			return status == JobStatus.done;

		} catch (Exception e) {

			FoneMonkeyPlugin.getDefault().logError(e);

			// Don't want to run forever if we can't connect to CloudMonkey.
			if (++failures > MAX_POLL_FAILURES) {
				executor.shutdown();
			}

			// Not done if there is an exception
			return false;
		}
	}

	/**
	 * Downloads and expands the job's report and refreshes the project.
	 */
	private void downloadReport() {
		try {

			// Get the url of the job's report archive
			JSONObject response = CloudServices.getJobResults(jobid);
			JSONObject data = response == null ? null : response.getJSONObject("data");
			String rawArchiveUrl = data == null ? null : data.getString("archive");

			// Job is done, but there is no report.
			if (rawArchiveUrl == null || rawArchiveUrl.trim().isEmpty()) {
				FoneMonkeyPlugin.getDefault().log(
						"No reports for Job " + jobid + " on " + CloudServices.getControllerUrlRoot() + ".");
				return;
			}

			// Download the archive to a temp directory withing reports.
			URL url = new URL(rawArchiveUrl);
			InputStream in = url.openStream();
			URI uri = project.getLocationURI();
			File projectDir = new File(uri);
			File reportsDir = new File(projectDir, "reports");
			File tempDir = new File(reportsDir, "temp");
			File archive = new File(tempDir, url.getFile());
			FileUtils.makeDir(archive.getParentFile());
			FileUtils.writeFile(archive, in);

			// Expand the archive in the temp dir.
			File expanded = archive.getParentFile();
			FileUtils.unzipFile(archive, expanded);

			// Move the expanded archive into a job qualified report dir
			File report = new File(projectDir, url.getFile()).getParentFile();
			File[] expandeds = expanded.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith("cloud-");
				}
			});
			expanded = expandeds[0];
			report.mkdirs();
			expanded.renameTo(report);

			// Clean up the temp dir and refresh the project.
			FileUtils.deleteDir(tempDir);
			project.refreshLocal(IProject.DEPTH_INFINITE, null);

		} catch (Exception e) {
			FoneMonkeyPlugin.getDefault().logError(e);
		}
	}
}
