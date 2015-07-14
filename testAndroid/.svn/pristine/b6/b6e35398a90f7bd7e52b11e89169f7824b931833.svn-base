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
package com.gorillalogic.monkeyconsole.tableview.editors;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.CCombo;

import com.gorillalogic.monkeyconsole.tableview.TableRow;
import com.gorillalogic.monkeytalk.api.meta.API;

public class ActionEditingSupport extends MonkeyTalkEditingSupport {

	private final TableViewer viewer;
	private ComboBoxCellEditor editor;
	private Map<String, ArrayList<String>> customActions = null;

	public ActionEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if (((TableRow) element).isComment()) {
			return null;
		}
		String[] commands = null;
		String componentType = ((TableRow) element).getComponentType();
		if (API.getComponent(componentType) != null) {
			commands = API.getComponent(componentType).getActionNames()
					.toArray(new String[API.getComponent(componentType).getActionNames().size()]);
		} else if (customActions != null && customActions.containsKey(componentType)) {
			commands = customActions.get(componentType).toArray(
					new String[customActions.get(componentType).size()]);
		} else {
			commands = new String[] {};
		}
		editor = new ComboBoxCellEditor(viewer.getTable(), commands) {
			@Override
			protected Object doGetValue() {
				return ((CCombo) getControl()).getText();
			}

			@Override
			protected void doSetValue(Object value) {
				((CCombo) getControl()).setText((String) value);
			}

		};
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object) Gets the value from
	 * the row and initializes the editor value with this value
	 */
	@Override
	protected Object getValue(Object element) {
		return ((TableRow) element).getAction();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
	 * after the user commits the change it is set on the row in this method
	 */
	@Override
	protected void storeValue(Object element, Object value) {
		((TableRow) element).setAction((String) value);
		dataChanged();
		viewer.refresh();
	}

	public void dataChanged() {

	};

	public ActionEditingSupport setCustomActions(Map<String, ArrayList<String>> actions) {
		this.customActions = actions;
		return this;
	}
}