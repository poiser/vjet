/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.ui.search;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.BusyIndicator;


public class SortAction extends Action {
	private int fSortOrder;
	private DLTKSearchResultPage fPage;
	
	public SortAction(String label, DLTKSearchResultPage page, int sortOrder) {
		super(label);
		fPage= page;
		fSortOrder= sortOrder;
	}

	public void run() {
		BusyIndicator.showWhile(fPage.getViewer().getControl().getDisplay(), new Runnable() {
			public void run() {
				fPage.setSortOrder(fSortOrder);
			}
		});
	}

	public int getSortOrder() {
		return fSortOrder;
	}
}
