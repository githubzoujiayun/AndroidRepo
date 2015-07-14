package com.gorillalogic.monkeyconsole.commands;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Date;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.json.JSONException;
import org.json.JSONObject;

import com.gorillalogic.cloud.ideversion.CloudConstants;
import com.gorillalogic.cloud.ideversion.Message;
import com.gorillalogic.cloud.ideversion.TaskConstants;
import com.gorillalogic.monkeyconsole.editors.utils.CloudServiceException;
import com.gorillalogic.monkeyconsole.editors.utils.CloudServices;
import com.gorillalogic.monkeyconsole.editors.utils.LoggedCloudEventTypes;
import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkUtils;
import com.gorillalogic.monkeyconsole.editors.utils.ReportRetriever;
import com.gorillalogic.monkeyconsole.editors.utils.RunInCloudDialog;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeyconsole.preferences.CloudMonkeyLabManagerPreferencePage;
import com.gorillalogic.monkeyconsole.preferences.ISetEnabledPreferenceStore;
import com.gorillalogic.monkeyconsole.preferences.PreferenceConstants;
import com.gorillalogic.monkeytalk.utils.FileUtils;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class PlayOnCloudHandler extends MonkeyHandlerBase {

	String ERROR_TITLE = "CloudMonkey Submit Error";

	/**
	 * The constructor.
	 */
	public PlayOnCloudHandler() {
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * the command has been executed, so extract extract the needed information from the application
	 * context.
	 */
	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException {
		Shell dialogShell = HandlerUtil.getActiveShell(event);
		IEditorPart targetEditor;
		try {
			targetEditor = HandlerUtil.getActiveEditorChecked(event);
		} catch (ExecutionException e) {
			targetEditor = null;
		}

		if (targetEditor == null) {
			MessageBox dialog = new MessageBox(dialogShell, SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Play On Cloud");
			String message = "Cannot play on Cloud, there does not seem to be any active editor";
			dialog.setMessage(message);
			dialog.open();
			return null;
		}
		
		// if this is the first time invoking this command, open the lab manager preferences
		ISetEnabledPreferenceStore preferenceStore = FoneMonkeyPlugin.getDefault().getPreferenceStore(); 
		boolean isFirstLmSubmission = preferenceStore.getBoolean(PreferenceConstants.P_ISFIRSTLMSUBMISSION);
		if (isFirstLmSubmission) {
			CloudMonkeyLabManagerPreferencePage cloudPrefsPage = new CloudMonkeyLabManagerPreferencePage();
			cloudPrefsPage.init(HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench());
			
			String cloudPrefsPageId = CloudMonkeyLabManagerPreferencePage.class.getName();
			PreferenceDialog prefDialog = PreferencesUtil.createPreferenceDialogOn(
					dialogShell, cloudPrefsPageId, null, null);
			prefDialog.open();
		}
		preferenceStore.setValue(PreferenceConstants.P_ISFIRSTLMSUBMISSION, false);

		if (MonkeyTalkUtils.isPlayable(targetEditor)) {
			if (targetEditor.isDirty()) {
				MessageBox dialog = new MessageBox(dialogShell, SWT.ICON_ERROR | SWT.YES | SWT.NO);
				dialog.setText("Play On Cloud");
				String message = targetEditor.getEditorInput().getName()
						+ " has unsaved changes. Save it now?";
				dialog.setMessage(message);
				if (dialog.open() == SWT.YES) {
					targetEditor.doSave(null);
				}
			}

			submitToCloud(dialogShell, targetEditor);

		} else {
			MessageBox dialog = new MessageBox(dialogShell, SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Play On Cloud");
			String message = "Cannot play all, you need to have a script or suite as the front editor";
			dialog.setMessage(message);
			dialog.open();
		}

		return null;
	}

	private Job createJob(final Shell dialogShell, final IEditorPart targetEditor,
			final RunInCloudDialog dialog) {
		IFile inputFile = getEditorInput(targetEditor).getFile();
		String inputFileName = inputFile.getName();
		File projectDir = getEditorInput(targetEditor).getPath().toFile().getParentFile();
		File zipFile = null;
		File wrapperMT = null;
		if (inputFileName.endsWith(".mt") || inputFileName.endsWith(".js")) {
			if (dialog.getRunAsSuite()) {
				String wrapperFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.'))
						+ "-suite.mts";
				try {
					wrapperMT = new File(projectDir, wrapperFileName);
					FileUtils.writeFile(wrapperMT, "Test " + inputFileName + " Run");
				} catch (IOException e) {
					e.printStackTrace();
				}
				inputFileName = wrapperFileName;
			}
		}

		try {
			zipFile = FileUtils.zipDirectory(projectDir, false, false,
					Arrays.asList("mt", "mts", "js", "csv", "properties"));
			if (wrapperMT != null) {
				wrapperMT.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		final File zipFileForUpload = zipFile;

		final String currentFileName = inputFileName;
		System.out.println("Suite to run: " + currentFileName);

		System.out.println("Job Type: " + dialog.getJobType());

		Job job = new Job("Submitting MonkeyTalk Cloud Job") {

			@Override
			protected IStatus run(IProgressMonitor arg0) {
				try {
					File appFileForUpload = getFileToUpload(dialog);
					System.out.println("Submitting " + appFileForUpload);
					arg0.beginTask("Submitting Job to CloudMonkey LabManager",
							IProgressMonitor.UNKNOWN);
					JSONObject response2;
					try {
						response2 = CloudServices.submitJob(currentFileName, zipFileForUpload,
								appFileForUpload, dialog.getJobName(), dialog.getThinktime(),
								dialog.getTimeout(), dialog.getDeviceMatrixString(),
								dialog.getJobType());
					} catch (CloudServiceException cse) {
						// this doesn't work. Who wrote this?
						MessageBox dialog = new MessageBox(dialogShell, SWT.ICON_ERROR | SWT.OK);
						dialog.setText(ERROR_TITLE);
						dialog.setMessage(cse.getMessage());
						dialog.open();
						return Status.CANCEL_STATUS;
					}
					final JSONObject response = response2;

					if (dialog.getDownloadReport()) {
						Object data = response.get("data");
						if (data instanceof JSONObject) {
							int jobid = response.getJSONObject("data").getInt("id");
							new ReportRetriever(jobid, dialog.getProject());
						} else if (data instanceof String) {
							final String errorMessage = (String) data;
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									MessageBox dialog = new MessageBox(dialogShell, SWT.ICON_ERROR
											| SWT.OK);
									dialog.setText(ERROR_TITLE);
									dialog.setMessage(errorMessage);
									dialog.open();
								}
							});
							return Status.CANCEL_STATUS;
						}
					}

					// If you want to update the UI
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								MessageBox dialog2;

								try {
									if (response.getString("message").equalsIgnoreCase(
											Message.ERROR)) {
										dialog2 = new MessageBox(dialogShell, SWT.ICON_ERROR
												| SWT.OK);
										dialog2.setMessage(response.getString("data"));
									} else {
										dialog2 = new MessageBox(dialogShell, SWT.ICON_INFORMATION
												| SWT.OK);
										String newJobId = ""
												+ response.getJSONObject("data").getInt("id");
										dialog2.setMessage("Job " + newJobId + " has been started.");

										try {
											MonkeyTalkUtils.openBrowser(
													"Job " + newJobId,
													"http://"
															+ CloudServices.getControllerHost()
															+ ":"
															+ CloudServices.getControllerPort()
															+ CloudConstants.JOB_STATUS
															+ "?username="
															+ FoneMonkeyPlugin
																	.getDefault()
																	.getPreferenceStore()
																	.getString(
																			PreferenceConstants.P_CLOUDUSR)
															+ "&token=" + CloudServices.getToken()
															+ "&id=" + newJobId,
													targetEditor.getEditorSite());
										} catch (PartInitException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (MalformedURLException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (CloudServiceException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									}
								} catch (JSONException e1) {
									e1.printStackTrace();
									throw new RuntimeException(e1);
								}
								dialog2.setText("CloudMonkey");
								dialog2.open();
								// ////
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					});

					return Status.OK_STATUS;
				} catch (Exception ex) {
					ex.printStackTrace();
					return Status.OK_STATUS;
				}
			}
		};
		return job;
	}

	private void submitToCloud(final Shell dialogShell, final IEditorPart targetEditor) {
		try {
			try {
				CloudServices.ping();
			} catch (CloudServiceException cse) {
				MessageBox dialog = new MessageBox(dialogShell, SWT.ICON_ERROR | SWT.OK);
				dialog.setText(ERROR_TITLE);
				dialog.setMessage(cse.getMessage());
				dialog.open();
				return;
			}
			// get the job parameters
			IFile inputFile = getEditorInput(targetEditor).getFile();
			final RunInCloudDialog dialog = new RunInCloudDialog(dialogShell, inputFile);
			dialog.create();
			CloudServices.logEventAsync(LoggedCloudEventTypes.RUNCLOUD_DIALOG_OPEN.toString(),
					dialog.mineUserData().toString(), new Date());

			// see if they cancelled
			if (dialog.open() != Window.OK) {
				CloudServices.logEventAsync(LoggedCloudEventTypes.RUNCLOUD_CANCEL_PRESSED
						.toString(), dialog.mineUserData().toString(), new Date());
				return;
			}

			// they want to try to go ahead
			CloudServices.logEventAsync(LoggedCloudEventTypes.RUNCLOUD_SUBMIT_PRESSED.toString(),
					dialog.mineUserData().toString(), new Date());

			Job job = createJob(dialogShell, targetEditor, dialog);
			job.setUser(true);
			schedule(job, dialogShell, dialog);

			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			try {
				page.showView("com.gorillalogic.monkeyconsole.cloudview.ui.UICloudView");
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	protected void schedule(final Job job, final Shell dialogShell, final RunInCloudDialog dialog) {
		job.schedule();
	}

	protected boolean isAndroid(RunInCloudDialog dialog) {
		String type = dialog.getJobType();
		boolean android = TaskConstants.ANDROID.equals(type);
		return android;
	}

	protected String getSelectedApplicationPath(RunInCloudDialog dialog) {
		String selectedApplicationPath = dialog.getApkName();
		return selectedApplicationPath;
	}

	protected File getFileToUpload(RunInCloudDialog dialog) {
		String selectedApplicationPath = getSelectedApplicationPath(dialog);
		File appFile = new File(selectedApplicationPath);
		if (appFile.isDirectory()) {
			try {
				appFile = FileUtils.zipDirectory(appFile, true, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		final File appFileForUpload = appFile;
		System.out.println("Binary to upload: " + appFile.getPath());
		return appFileForUpload;
	}

	private FileEditorInput getEditorInput(IEditorPart targetEditor) {
		return (FileEditorInput) targetEditor.getEditorInput();
	}
}
