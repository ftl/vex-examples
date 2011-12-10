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

import java.io.IOException;
import java.net.URL;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.vex.core.internal.core.ElementName;
import org.eclipse.vex.core.internal.css.StyleSheet;
import org.eclipse.vex.core.internal.css.StyleSheetReader;
import org.eclipse.vex.core.internal.dom.Document;
import org.eclipse.vex.core.internal.dom.Element;
import org.eclipse.vex.core.internal.dom.RootElement;
import org.eclipse.vex.core.internal.dom.Validator;
import org.eclipse.vex.core.internal.validator.WTPVEXValidator;
import org.eclipse.vex.ui.internal.swt.VexWidget;

/**
 * @author Florian Thienel
 */
public class CommentDialog extends TitleAreaDialog {

	private final Document document;
	
	private final StyleSheet styleSheet;
	
	private Button boldButton;

	private Button italicButton;

	private Button bugButton;

	private Button openBugButton;
	
	private VexWidget vexWidget;
	
	public CommentDialog(Shell parentShell) {
		super(parentShell);
		document = createDocument();
		styleSheet = createStyleSheet();
	}

	private static Document createDocument() {
		final Document document = new Document(new RootElement("comment"));
		document.setValidator(createValidator());
		return document;
	}
	
	private static Validator createValidator() {
		final URL dtdUrl = CommentDialog.class.getResource("comment.dtd");
		return new WTPVEXValidator(dtdUrl);
	}
	
	private static StyleSheet createStyleSheet() {
		final URL styleSheetUrl = CommentDialog.class.getResource("comment.css");
		final StyleSheetReader reader = new StyleSheetReader();
		try {
			return reader.read(styleSheetUrl);
		} catch (IOException e) {
			throw new AssertionError("Cannot read the stylesheet: " + e.getMessage());
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Comment");
	}
	
	@Override
	protected Control createContents(Composite parent) {
		final Control contents = super.createContents(parent);
		setTitle("Comment");
		setMessage("Enter your comment");
		return contents;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite root = new Composite((Composite) super.createDialogArea(parent), SWT.NONE);
		root.setLayout(new GridLayout(4, false));
		root.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		boldButton = new Button(root, SWT.PUSH);
		boldButton.setText("Bold");
		boldButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boldButtonSelected();
			}
		});
		
		italicButton = new Button(root, SWT.PUSH);
		italicButton.setText("Italic");
		italicButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				italicButtonSelected();
			}
		});
		
		bugButton = new Button(root, SWT.PUSH);
		bugButton.setText("Bug");
		bugButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bugButtonSelected();
			}
		});
		
		openBugButton = new Button(root, SWT.PUSH);
		openBugButton.setText("Open Bug");
		openBugButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openBugButtonSelected();
			}
		});
		
		vexWidget = new VexWidget(root, SWT.V_SCROLL);
		vexWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		vexWidget.setDocument(document, styleSheet);
		vexWidget.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				vexWidgetSelectionChanged();
			}
		});
		
		vexWidget.setFocus();
		updateButtonEnablement();
		
		return root;
	}
	
	private void boldButtonSelected() {
		vexWidget.insertElement(new Element("b"));
		vexWidget.setFocus();
	}

	private void italicButtonSelected() {
		vexWidget.insertElement(new Element("i"));
		vexWidget.setFocus();
	}

	private void bugButtonSelected() {
		final InputDialog inputDialog = new InputDialog(this.getShell(), "Insert Bug", "Enter the Bugzilla ID", "", new IInputValidator() {
			public String isValid(String newText) {
				try {
					Integer.parseInt(newText);
				} catch (NumberFormatException e) {
					return e.getLocalizedMessage();
				}
				return null;
			}
		});
		
		if (inputDialog.open() != Window.OK) {
			vexWidget.setFocus();
			return;
		}

		final Element bugElement = new Element("bug");
		bugElement.setAttribute("id", inputDialog.getValue());
		vexWidget.insertElement(bugElement);
		vexWidget.setFocus();
	}
	
	private void openBugButtonSelected() {
		final Element bugElement = findBugElement(vexWidget.getCurrentElement());
		if (bugElement == null)
			return;
		final String bugId = bugElement.getAttribute("id").getValue();
		Program.launch("https://bugs.eclipse.org/bugs/show_bug.cgi?id=" + bugId);
	}

	private void vexWidgetSelectionChanged() {
		updateButtonEnablement();
	}

	private void updateButtonEnablement() {
		boolean boldAllowed = false;
		boolean italicAllowed = false;
		boolean bugAllowed = false;
		for (ElementName elementName : vexWidget.getValidInsertElements()) {
			if (elementName.getLocalName().equals("b"))
				boldAllowed = true;
			else if (elementName.getLocalName().equals("i"))
				italicAllowed = true;
			else if (elementName.getLocalName().equals("bug"))
				bugAllowed = true;
		}
		
		boldButton.setEnabled(boldAllowed);
		italicButton.setEnabled(italicAllowed);
		bugButton.setEnabled(bugAllowed);
		
		openBugButton.setEnabled(isInBugElement(vexWidget.getCurrentElement()));
	}
	
	private static boolean isInBugElement(final Element element) {
		return findBugElement(element) != null;
	}
	
	private static Element findBugElement(final Element element) {
		if (element == null)
			return null;
		if (!element.getLocalName().equals("bug"))
			return findBugElement(element.getParent());
		return element;
	}
	
}
