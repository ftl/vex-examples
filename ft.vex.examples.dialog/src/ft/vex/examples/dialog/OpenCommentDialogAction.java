/*******************************************************************************
 * Copyright (c) 2011 Florian Thienel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		Florian Thienel - initial API and implementation
 *******************************************************************************/
package ft.vex.examples.dialog;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class OpenCommentDialogAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public void run(final IAction action) {
		new CommentDialog(window.getShell()).open();
	}

	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}

	public void selectionChanged(final IAction action, final ISelection selection) {
	}

	public void dispose() {
	}

}