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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.vex.core.internal.dom.DocumentWriter;

public class OpenCommentDialogAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public void run(final IAction action) {
		final CommentDialog dialog = new CommentDialog(window.getShell());
		if (dialog.open() != Window.OK)
			return;
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		final DocumentWriter documentWriter = new DocumentWriter();
		try {
			documentWriter.write(dialog.getDocument(), buffer);
		} catch (IOException e) {
			MessageDialog.openError(window.getShell(), "Comment", "Cannot show comment: " + e.getMessage());
		}
		final String comment = new String(buffer.toByteArray());
		MessageDialog.openInformation(window.getShell(), "Your Comment", comment);
	}

	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}

	public void selectionChanged(final IAction action, final ISelection selection) {
	}

	public void dispose() {
	}

}