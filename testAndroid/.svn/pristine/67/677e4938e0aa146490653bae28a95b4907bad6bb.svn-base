/*  MonkeyTalk - a cross-platform functional testing tool
    Copyright (C) 2012 Gorilla Logic, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package com.gorillalogic.monkeyconsole.tableview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.FocusCellHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import com.gorillalogic.monkeyconsole.editors.utils.MonkeyTalkUtils;
import com.gorillalogic.monkeyconsole.plugin.FoneMonkeyPlugin;
import com.gorillalogic.monkeyconsole.preferences.PreferenceConstants;
import com.gorillalogic.monkeyconsole.tableview.editors.ActionEditingSupport;
import com.gorillalogic.monkeyconsole.tableview.editors.ArgsEditingSupport;
import com.gorillalogic.monkeyconsole.tableview.editors.ComponentEditingSupport;
import com.gorillalogic.monkeyconsole.tableview.editors.MonkeyidEditingSupport;
import com.gorillalogic.monkeyconsole.tableview.editors.ShouldFailEditingSupport;
import com.gorillalogic.monkeyconsole.tableview.editors.ThinktimeEditingSupport;
import com.gorillalogic.monkeyconsole.tableview.editors.TimeoutEditingSupport;
import com.gorillalogic.monkeyconsole.tableview.labelproviders.ActionLabelProvider;
import com.gorillalogic.monkeyconsole.tableview.labelproviders.ArgsLabelProvider;
import com.gorillalogic.monkeyconsole.tableview.labelproviders.ComponentLabelProvider;
import com.gorillalogic.monkeyconsole.tableview.labelproviders.MonkeyidLabelProvider;
import com.gorillalogic.monkeyconsole.tableview.labelproviders.RowNumberLabelProvider;
import com.gorillalogic.monkeyconsole.tableview.labelproviders.ShouldFailLabelProvider;
import com.gorillalogic.monkeyconsole.tableview.labelproviders.ThinktimeLabelProvider;
import com.gorillalogic.monkeyconsole.tableview.labelproviders.TimeoutLabelProvider;
import com.gorillalogic.monkeytalk.Command;
import com.gorillalogic.monkeytalk.api.meta.ScriptType;

/**
 * This class allows you to create and edit Command objects
 */
public class MonkeyTalkTabularEditor extends EditorPart {
	// Table column names/properties
	public static final String ROW = "Row";
	public static final String COMPONENT = "Component";
	public static final String MONKEYID = "MonkeyID";
	public static final String ACTION = "Action";
	public static final String ARGS = "Arguments";
	public static final String TIMEOUT = "Timeout (ms)";
	public static final String THINKTIME = "ThinkTime (ms)";
	public static final String SHOULDFAIL = "Should Fail";

	// The data model, this is marked as final because the labelproviders and
	// editors need access to it
	final protected java.util.List<TableRow> commands;

	protected TableViewer tv = null;
	private Table table = null;
	private String[] limitedComponentSet = null;
	private Map<String, ArrayList<String>> customComponents = null;
	private int menueventsCaught = 0;
	public boolean isDisposed = false;
	private int componentTypeColumnIndex = -1;
	boolean isDirty = false;
	public boolean commandKeyDown = false;

	/**
	 * Constructs a MonkeyTalkTabularEditor
	 */
	public MonkeyTalkTabularEditor() {
		commands = new ArrayList<TableRow>();
	}

	/**
	 * Creates the main window's contents
	 * 
	 * @param parent
	 *            the main window
	 * @return Control
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		// Add the TableViewer
		tv = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER);

		tv.setContentProvider(new CommandContentProvider());
		tv.setInput(commands);

		table = tv.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		TableColumnLayout layout = new TableColumnLayout();
		composite.setLayout(layout);

		createTableViewerColumns(layout);

		tv.getTable().addListener(SWT.MenuDetect, new Listener() {
			@Override
			public void handleEvent(Event event) {

				if (menueventsCaught != event.time) {
					new ContextMenu(MonkeyTalkTabularEditor.this, event).show();
					menueventsCaught = event.time;
				}

			}
		});
		table.addListener(SWT.MouseDoubleClick, new Listener() {
			@Override
			public void handleEvent(Event e) {
				TableItem item = table.getItem(new Point(e.x, e.y));
				if (item == null) {

				} else { // click link case
					String comp = ((TableRow) item.getData()).getComponentType();
					if (comp != null
							&& (comp.equalsIgnoreCase("Script") || comp.equalsIgnoreCase("Test")
									|| comp.equalsIgnoreCase("SetUp") || comp
										.equalsIgnoreCase("Teardown"))) {
						try {

							IEditorPart ieditorpart = MonkeyTalkTabularEditor.this.getEditorSite()
									.getPage().getActiveEditor();
							String dotMt = ".mt";

							String editor = "com.gorillalogic.monkeyconsole.editors.FoneMonkeyTestEditor";
							if (((TableRow) item.getData()).getMonkeyId().contains(".mt")
									|| ((TableRow) item.getData()).getMonkeyId().contains(".js")) {
								dotMt = "";
							}

							if (((TableRow) item.getData()).getMonkeyId().contains(".js")) {
								editor = "org.eclipse.wst.jsdt.ui.CompilationUnitEditor";
							}
							IFile fileToBeOpened = ((IFileEditorInput) ieditorpart.getEditorInput())
									.getFile().getProject()
									.getFile(((TableRow) item.getData()).getMonkeyId() + dotMt);

							IEditorInput editorInput = new FileEditorInput(fileToBeOpened);

							MonkeyTalkTabularEditor.this.getEditorSite().getPage()
									.openEditor(editorInput, editor);
							commandKeyDown = false;
						} catch (CoreException e2) {
							e2.printStackTrace();
						}
					}
				}

			}

		});
		table.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event e) {
				TableItem item = table.getItem(new Point(e.x, e.y));
				if (item == null) {
					if (e.y < (table.getItemCount() * table.getItemHeight())
							+ table.getItemHeight()) {
						appendRow();
					}
				} else { // click link case
					String comp = ((TableRow) item.getData()).getComponentType();
					if (comp != null
							&& commandKeyDown
							&& tv.getCell(new Point(e.x, e.y)).getColumnIndex() == componentTypeColumnIndex
							&& (comp.equalsIgnoreCase("Script") || comp.equalsIgnoreCase("Test")
									|| comp.equalsIgnoreCase("Run")
									|| comp.equalsIgnoreCase("RunWith")
									|| comp.equalsIgnoreCase("SetUp") || comp
										.equalsIgnoreCase("Teardown"))) {
						try {

							IEditorPart ieditorpart = MonkeyTalkTabularEditor.this.getEditorSite()
									.getPage().getActiveEditor();
							IFile fileToBeOpened = ((IFileEditorInput) ieditorpart.getEditorInput())
									.getFile().getProject()
									.getFile(tv.getCell(new Point(e.x, e.y)).getText() + ".mt");

							IEditorInput editorInput = new FileEditorInput(fileToBeOpened);
							MonkeyTalkTabularEditor.this
									.getEditorSite()
									.getPage()
									.openEditor(editorInput,
											"com.gorillalogic.monkeyconsole.editors.FoneMonkeyTestEditor");
							commandKeyDown = false;
						} catch (CoreException e2) {
							e2.printStackTrace();
						}
					}
				}

			}
		});
		// FOCUS TRAVERSAL
		FocusCellHighlighter highlighter = new FocusCellHighlighter(tv) {

		};

		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(tv,
				highlighter) {

		};

		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(
				tv) {
			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR)
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};

		TableViewerEditor.create(tv, focusCellManager, actSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.keyCode == SWT.CTRL || arg0.keyCode == SWT.COMMAND) {
					commandKeyDown = true;
				}

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.keyCode == SWT.CTRL || arg0.keyCode == SWT.COMMAND) {
					commandKeyDown = false;
				}
				if (commandKeyDown) {
					if (arg0.character == 'P' || arg0.character == 'p') {
						if (FoneMonkeyPlugin.getDefault().getController().isCurrentlyConnected()
								&& !FoneMonkeyPlugin.getDefault().getController().isRecordingON()
								&& getLimitedComponentSet() == null) {
							int from = tv.getTable().getSelectionIndex();
							from = from - getBlankCommandOffset(from);
							int to = tv.getTable().getSelectionIndex()
									+ tv.getTable().getSelectionCount();
							to = to - getBlankCommandOffset(from);
							deleteBlankRows();
							FoneMonkeyPlugin.getDefault().getController()
									.startReplayRange(from, to);
						}
					} else if (arg0.keyCode == 120) { // "x"
						doCommandEditor();
					}
				}
				// System.out.println(arg0.keyCode);
				// System.out.println("--->'" + arg0.character + "'<----");
				if (arg0.keyCode == SWT.DEL || arg0.keyCode == 8) {
					deleteRows(getTv().getTable().getSelectionIndices());
				}
			}

		});
		int ops = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };
		tv.addDropSupport(ops, transfers, new GadgetTableDropAdapter(tv));
		return composite;
	}

	protected void createTableViewerColumns(TableColumnLayout layout) {
		createRowNumberColumn(layout);
		createTableViewerDataColumns(layout);
	}

	protected void createRowNumberColumn(TableColumnLayout layout) {
		// Row Number Column
		TableViewerColumn row_col = createTableViewerColumn(ROW, 100);
		row_col.setLabelProvider(new RowNumberLabelProvider(commands));
		layout.setColumnData(row_col.getColumn(), new ColumnWeightData(10));
	}

	protected void createTableViewerDataColumns(TableColumnLayout layout) {
		// Component Type Column
		TableViewerColumn component_col = createTableViewerColumn(COMPONENT, 100);
		component_col.setLabelProvider(new ComponentLabelProvider());
		component_col.setEditingSupport(new ComponentEditingSupport(tv, limitedComponentSet,
				customComponents) {
			@Override
			public void dataChanged() {
				MonkeyTalkTabularEditor.this.doDataChanged();
			};
		});
		layout.setColumnData(component_col.getColumn(), new ColumnWeightData(30));
		componentTypeColumnIndex = tv.getTable().getColumnCount() - 1;

		TableViewerColumn monkeyid_col = createTableViewerColumn(MONKEYID, 100);
		monkeyid_col.setLabelProvider(new MonkeyidLabelProvider());
		monkeyid_col.setEditingSupport(new MonkeyidEditingSupport(tv) {
			@Override
			public void dataChanged() {
				MonkeyTalkTabularEditor.this.doDataChanged();
			};
		});
		layout.setColumnData(monkeyid_col.getColumn(), new ColumnWeightData(25));

		TableViewerColumn action_col = createTableViewerColumn(ACTION, 100);
		action_col.setLabelProvider(new ActionLabelProvider());
		action_col.setEditingSupport(new ActionEditingSupport(tv) {
			@Override
			public void dataChanged() {
				MonkeyTalkTabularEditor.this.doDataChanged();
			};
		}.setCustomActions(customComponents));
		layout.setColumnData(action_col.getColumn(), new ColumnWeightData(30));

		TableViewerColumn args_col = createTableViewerColumn(ARGS, 100);
		args_col.setLabelProvider(new ArgsLabelProvider());
		args_col.setEditingSupport(new ArgsEditingSupport(tv) {
			@Override
			public void dataChanged() {
				MonkeyTalkTabularEditor.this.doDataChanged();
			};
		});
		layout.setColumnData(args_col.getColumn(), new ColumnWeightData(40));

		TableViewerColumn timeout_col = createTableViewerColumn(TIMEOUT, 100);
		timeout_col.setLabelProvider(new TimeoutLabelProvider());
		timeout_col.setEditingSupport(new TimeoutEditingSupport(tv) {
			@Override
			public void dataChanged() {
				MonkeyTalkTabularEditor.this.doDataChanged();
			};
		});
		layout.setColumnData(timeout_col.getColumn(), new ColumnWeightData(20));

		TableViewerColumn thinktime_col = createTableViewerColumn(THINKTIME, 100);
		thinktime_col.setLabelProvider(new ThinktimeLabelProvider());
		thinktime_col.setEditingSupport(new ThinktimeEditingSupport(tv) {
			@Override
			public void dataChanged() {
				MonkeyTalkTabularEditor.this.doDataChanged();
			};
		});
		layout.setColumnData(thinktime_col.getColumn(), new ColumnWeightData(20));

		String extencion = FilenameUtils.getExtension(((FileEditorInput) getEditorInput())
				.getPath().toFile().getName());
		if (extencion.equals("mt")) {
			TableViewerColumn shouldfail_col = createTableViewerColumn(SHOULDFAIL, 100);
			shouldfail_col.getColumn().setAlignment(SWT.CENTER);
			shouldfail_col.setLabelProvider(new ShouldFailLabelProvider(tv));
			shouldfail_col.setEditingSupport(new ShouldFailEditingSupport(tv) {
				@Override
				public void dataChanged() {
					MonkeyTalkTabularEditor.this.doDataChanged();
				};
			});

			layout.setColumnData(shouldfail_col.getColumn(), new ColumnWeightData(15));
		}
	}

	protected TableViewerColumn createTableViewerColumn(String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tv, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	/**
	 * Append a row to the bottom of the table
	 * 
	 * @return true if the row was added, false if not
	 */
	public void appendRow() {
		TableRow c = new TableRow();
		c.setAction("");
		c.setArgsAndModifiers("");
		c.setComponentType("");
		c.setMonkeyId("");
		commands.add(c);
		doDataChanged();
		tv.refresh();
	}

	/**
	 * Append a row to the bottom of the table
	 * 
	 * @return true if the row was added, false if not
	 */
	public void appendRow(Command c) {
		TableRow r = new TableRow(c);

		commands.add(r);
		doDataChanged();
		tv.refresh();
		table.setTopIndex(table.getItemCount() - 1);
	}

	/**
	 * Append a row using coalescing
	 * 
	 * @param c
	 * @param useCooalessing
	 */
	public void appendRow(Command cr, boolean useCooalessing) {
		TableRow newrow = new TableRow(cr);
		if (getCommands().size() > 0) {
			Command lastRow = null;
			if (useCooalessing
					&& FoneMonkeyPlugin.getDefault().getPreferenceStore()
							.getString(PreferenceConstants.P_EVENTSTOCOMBINE).toLowerCase()
							.contains(newrow.getAction().toLowerCase())) {
				lastRow = getCommands().get(getCommands().size() - 1);
				if (null != lastRow && lastRow.getAction().equalsIgnoreCase(newrow.getAction())
						&& lastRow.getComponentType().equalsIgnoreCase(newrow.getComponentType())
						&& lastRow.getCommandName().equalsIgnoreCase(newrow.getCommandName())
						&& lastRow.getMonkeyId().equalsIgnoreCase(newrow.getMonkeyId())) {
					commands.set(getCommands().size() - 1, newrow);
				} else {
					commands.add(newrow);
				}
			} else if (FoneMonkeyPlugin
					.getDefault()
					.getPreferenceStore()
					.getString(PreferenceConstants.P_DUPLICATEEVENTSSUPRESS)
					.toLowerCase()
					.contains(
							newrow.getComponentType().toLowerCase() + "."
									+ newrow.getAction().toLowerCase())) {
				lastRow = getCommands().get(getCommands().size() - 1);
				if (lastRow == null || !lastRow.toString().equals(newrow.toString())) {
					commands.add(newrow);
				}
			} else {
				commands.add(newrow);
			}
		} else {
			commands.add(newrow);
		}
		doDataChanged();
		tv.refresh();
		table.setTopIndex(table.getItemCount() - 1);
	}

	/**
	 * 
	 * @param rowToInsertAbove
	 */
	public void insertRow(int rowToInsertAbove) {
		TableRow c = new TableRow();
		c.setAction("");
		c.setArgsAndModifiers("");
		c.setComponentType("");
		c.setMonkeyId("");
		commands.add(rowToInsertAbove, c);
		doDataChanged();
		tv.refresh();

	}

	/**
	 * Delete the rows provided
	 * 
	 * @param start
	 *            the first row to delete 0 indexed
	 * @param end
	 *            the last row to delete 0 indexed
	 */
	public void deleteRows(int start, int end) {
		for (int i = end; i > start; i--) {
			commands.remove(i);
			doDataChanged();
		}
		tv.refresh();
	}

	/**
	 * Delete a random assortment of rows
	 * 
	 * @param rowsToDelete
	 *            a SORTED low to high collection of rows to be deleted
	 */
	public void deleteRows(int[] rowsToDelete) {
		for (int i = rowsToDelete.length - 1; i >= 0; i--) {
			commands.remove(rowsToDelete[i]);
			doDataChanged();
		}
		tv.refresh();
	}

	/**
	 * Convinence function for deleting one row
	 * 
	 * @param rowNumberthe
	 *            row to be deleted
	 */
	public void deleteRow(int rowNumber) {
		int[] i = new int[1];
		i[0] = rowNumber;
		deleteRows(i);
	}

	/**
	 * Convinence function for deleting all rows
	 */
	public void clear() {
		commands.removeAll(commands);
		doDataChanged();
		tv.refresh();
	}

	@Override
	public void doSave(IProgressMonitor arg0) {
		setDirty(false);
	}

	@Override
	public void doSaveAs() {
		setDirty(false);
	}

	@Override
	public void init(IEditorSite arg0, IEditorInput arg1) throws PartInitException {
		setSite(arg0);
		setInput(arg1);

	}

	@Override
	public void createPartControl(Composite parent) {
		createContents(parent);
	}

	public void doDataChanged() {
		isDirty = true;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void setFocus() {
		// Must set focus somewhere or Project Navigator gets whacked
		table.setFocus();
	}

	public IFileEditorInput getFileEditorInput() {
		if (getEditorInput() instanceof IFileEditorInput) {
			return (IFileEditorInput) getEditorInput();
		}
		return null;
	}

	public IFile getFile() {
		IFileEditorInput ifei = getFileEditorInput();
		if (ifei != null) {
			return ifei.getFile();
		}
		return null;
	}

	public String getPath() {
		IFile file = getFile();
		if (file != null) {
			return file.getFullPath().toString();
		}
		return null;
	}

	public String getProjectRelativePath() {
		IFile file = getFile();
		if (file != null) {
			return file.getProjectRelativePath().toString();
		}
		return null;
	}

	public String getFileName() {
		IFile file = getFile();
		if (file != null) {
			return file.getName();
		}
		return null;
	}

	public String getExtension() {
		IFile file = getFile();
		if (file != null) {
			return file.getFileExtension();
		}
		return null;
	}

	public IProject getProject() {
		IFile file = getFile();
		if (file != null) {
			return file.getProject();
		}
		return null;
	}

	public boolean isScript() {
		return "mt".equals(getExtension());
	}

	public boolean isSuite() {
		return "mts".equals(getExtension());
	}

	public void deleteBlankRows() {
		MonkeyTalkUtils.runOnGUI(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < commands.size(); i++) {
					if (commands.get(i).toString().equalsIgnoreCase("* * *")) {
						deleteRow(i);
					}
				}
			}
		}, getSite().getShell().getDisplay());

	}

	public int getBlankCommandOffset(int row) {
		int retVal = 0;
		for (int i = 0; i < commands.size(); i++) {
			if (commands.get(i).toString().equalsIgnoreCase("* * *")) {
				if (i < row) {
					retVal++;
				}
			}
		}

		return retVal;
	}

	public java.util.List<Command> getCommands() {
		List<Command> retCommands = new ArrayList<Command>();
		for (int i = 0; i < commands.size(); i++) {
			if (!commands.get(i).toString().equalsIgnoreCase("* * *")) {
				retCommands.add(commands.get(i));
			}
		}

		return retCommands;
	}

	public void setCommands(java.util.List<Command> commandsparam) {
		commands.removeAll(commands);
		for (Command c : commandsparam) {
			commands.add(new TableRow(c));
		}
		tv.setInput(commands);
		tv.refresh();
	}

	public String getCommandsAsString() {
		String result = "";
		for (Command c : getCommands()) {
			result += c.toString() + "\n";
		}
		if (result.length() > 0) {
			// remove tailing newline character
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	public TableViewer getTv() {
		return tv;
	}

	public void setTv(TableViewer tv) {
		this.tv = tv;
	}

	public void setSelection(int i) {
		tv.getTable().setSelection(i);
	}

	public void markRowAsError(int i) {
	}

	public String[] getLimitedComponentSet() {
		return limitedComponentSet;
	}

	public void setLimitedComponentSet(String[] limitedComponentSet) {
		this.limitedComponentSet = limitedComponentSet;
	}

	public void setCustomComponents(Map<String, ArrayList<String>> components) {
		customComponents = components;
	}

	@Override
	public void dispose() {
		super.dispose();
		isDisposed = true;
	}

	public boolean isDisposed() {
		return isDisposed;
	}

	public static interface CommandEditor {
		public void setCommand(Command command);

		public Command getCommand();

		public void setScriptType(ScriptType scriptType);

		public int open();
	}

	protected static Class<?> commandEditorClass = null;

	public static void setCommandEditorClass(Class<?> commandEditorClass) {
		MonkeyTalkTabularEditor.commandEditorClass = commandEditorClass;
	}

	CommandEditor commandEditor = null;

	protected void doCommandEditor() {
		if (MonkeyTalkTabularEditor.commandEditorClass != null) {
			try {
				int ndx = tv.getTable().getSelectionIndex();
				if (ndx < commands.size()) {
					if (commandEditor == null) {
						commandEditor = (CommandEditor) commandEditorClass.newInstance();
					}
					Command tempCommand = commands.get(ndx).clone();
					commandEditor.setCommand(tempCommand);
					ScriptType scriptType = ScriptType.from(getFile().getFileExtension());
					commandEditor.setScriptType(scriptType);
					int editorResponse = commandEditor.open();
					// this.getSite().getPage().activate(this);
					if (editorResponse == 0) {
						tempCommand = commandEditor.getCommand();
						Command targetCommand = commands.get(ndx);
						targetCommand.setComponentType(tempCommand.getComponentType());
						targetCommand.setMonkeyId(tempCommand.getMonkeyId());
						targetCommand.setAction(tempCommand.getAction());
						targetCommand.setArgsAndModifiers(tempCommand.getArgsAsString() + " "
								+ tempCommand.getModifiersAsString());
						doDataChanged();
						tv.refresh();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
