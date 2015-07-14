package com.gorillalogic.monkeyconsole.commands;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.gorillalogic.monkeyconsole.tableview.MonkeyTalkTabularEditor;
import com.gorillalogic.monkeyconsole.tableview.TableRow;

public class InsertRowHandler extends ContributionItem {

	protected void doRowExecute(MonkeyTalkTabularEditor monkeyTalkTabularEditor) {
		TableViewer viewer = monkeyTalkTabularEditor.getTv();
		ISelection selection = viewer.getSelection();
		StructuredSelection structuredSelection = (StructuredSelection) selection;
		TableRow tableRow = (TableRow) structuredSelection.getFirstElement();
		for (int i = 0; i < monkeyTalkTabularEditor.getCommands().size(); i++) {
			Object element = viewer.getElementAt(i);
			tableRow.equals(element);
		}
		int row = 0;
		Table table = viewer.getTable();
		for (TableItem item : table.getItems()) {
			Object data = item.getData();
			if (tableRow.equals(data)) {
				break;
			}
			row++;
		}
		monkeyTalkTabularEditor.insertRow(row);
	}
}
