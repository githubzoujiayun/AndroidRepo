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
package com.gorillalogic.monkeytalk.api;

/**
 * A component that provides a grid view of data. iOS: UICollectionView. Android: GridView.
 * 
 * @prop detail(int row, int section) - the detail text of the item per row (and per section on iOS)
 * 
 * @types script
 */
public interface Grid extends ItemSelector {

	/**
	 * Scroll to a row by index.
	 * 
	 * @param index
	 *          the index of the row to scroll to.
	 */
	public void scrollToIndex(int index);

	/**
	 * Scroll to the specified coordinates.
	 * 
	 * @param x
	 *          the x-coordinate (horizontal)
	 * @param y
	 *          the y-coordinate (vertical)
	 */
	public void scroll(int x, int y);

}