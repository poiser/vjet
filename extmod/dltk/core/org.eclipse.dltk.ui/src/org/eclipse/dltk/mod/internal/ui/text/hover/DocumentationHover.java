/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.ui.text.hover;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.mod.core.IMember;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.internal.ui.BrowserInformationControl;
import org.eclipse.dltk.mod.internal.ui.text.HTMLPrinter;
import org.eclipse.dltk.mod.internal.ui.text.HTMLTextPresenter;
import org.eclipse.dltk.mod.internal.ui.text.IInformationControlExtension4;
import org.eclipse.dltk.mod.ui.ScriptElementLabels;
import org.eclipse.dltk.mod.ui.documentation.IScriptDocumentationProvider;
import org.eclipse.dltk.mod.ui.documentation.ScriptDocumentationAccess;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;

/**
 * Provides documentation as hover info for Script elements and keywords.
 * 
 * 
 */
public class DocumentationHover extends AbstractScriptEditorTextHover implements
		IInformationProviderExtension2, ITextHoverExtension {

	private final long LABEL_FLAGS = // ScriptElementLabels.ALL_FULLY_QUALIFIED
	ScriptElementLabels.M_PRE_RETURNTYPE
			| ScriptElementLabels.M_PARAMETER_TYPES
			| ScriptElementLabels.M_PARAMETER_NAMES
			| ScriptElementLabels.M_EXCEPTIONS
			| ScriptElementLabels.F_PRE_TYPE_SIGNATURE
			| ScriptElementLabels.M_PRE_TYPE_PARAMETERS
			| ScriptElementLabels.T_TYPE_PARAMETERS
			| ScriptElementLabels.USE_RESOLVED;
	private final long LOCAL_VARIABLE_FLAGS = LABEL_FLAGS
			& ~ScriptElementLabels.F_FULLY_QUALIFIED
			| ScriptElementLabels.F_POST_QUALIFIED;

	/**
	 * The hover control creator.
	 * 
	 * 
	 */
	private IInformationControlCreator fHoverControlCreator;
	/**
	 * The presentation control creator.
	 * 
	 * 
	 */
	private IInformationControlCreator fPresenterControlCreator;

	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (fPresenterControlCreator == null) {
			fPresenterControlCreator = new AbstractReusableInformationControlCreator() {
				public IInformationControl doCreateInformationControl(
						Shell parent) {
					int shellStyle = SWT.RESIZE | SWT.TOOL;
					int style = SWT.V_SCROLL | SWT.H_SCROLL;
					if (BrowserInformationControl.isAvailable(parent))
						return new BrowserInformationControl(parent,
								shellStyle, style);
					else
						return new DefaultInformationControl(parent,
								shellStyle, style, new HTMLTextPresenter(false));
				}
			};
		}
		return fPresenterControlCreator;
	}

	public IInformationControlCreator getHoverControlCreator() {
		if (fHoverControlCreator == null) {
			fHoverControlCreator = new AbstractReusableInformationControlCreator() {
				public IInformationControl doCreateInformationControl(
						Shell parent) {
					if (BrowserInformationControl.isAvailable(parent))
						return new BrowserInformationControl(parent, SWT.TOOL
								| SWT.NO_TRIM, SWT.NONE, EditorsUI
								.getTooltipAffordanceString());
					else
						return new DefaultInformationControl(parent, SWT.NONE,
								new HTMLTextPresenter(true), EditorsUI
										.getTooltipAffordanceString());
				}

				public boolean canReuse(IInformationControl control) {
					boolean canReuse = super.canReuse(control);
					if (canReuse
							&& control instanceof IInformationControlExtension4)
						((IInformationControlExtension4) control)
								.setStatusText(EditorsUI
										.getTooltipAffordanceString());
					return canReuse;

				}
			};
		}
		return fHoverControlCreator;
	}

	protected String getHoverInfo(String nature, IModelElement[] result) {
		StringBuffer buffer = new StringBuffer();
		int nResults = result.length;
		if (nResults == 0)
			return null;

		boolean hasContents = false;
		if (nResults > 1) {

			for (String s : getInfoText(result)) {
				HTMLPrinter.addSmallHeader(buffer, s);
			}
			HTMLPrinter.addParagraph(buffer, "<hr>"); //$NON-NLS-1$
			IModelElement last = null;
			for (int i = 0; i < result.length; i++) {
				// HTMLPrinter.startBulletList(buffer);

				IModelElement curr = result[i];

				if (curr.equals(last)) {
					continue;
				}

				if (curr instanceof IMember) {
					IMember member = (IMember) curr;

					Reader reader;
					try {
						reader = ScriptDocumentationAccess
								.getHTMLContentReader(nature, member, true,
										true);

						// Provide hint why there's no doc
						// if (reader == null) {
						// // reader= new
						// //
						// StringReader(DLTKHoverMessages.ScriptdocHover_noAttachedInformation);
						// continue;
						// }

					} catch (ModelException ex) {
						return null;
					}

					if (reader != null) {
						// HTMLPrinter.addBullet(buffer, getInfoText(curr));
						// HTMLPrinter.addParagraph(buffer, "<br>");
						if (hasContents) {
							HTMLPrinter.addParagraph(buffer, "<hr>"); //$NON-NLS-1$
						}
						HTMLPrinter.addParagraph(buffer, reader);
					}
					hasContents = true;
				}
				// HTMLPrinter.endBulletList(buffer);
				last = curr;
			}

		} else {

			IModelElement curr = result[0];
			if (curr instanceof IMember) {
				IMember member = (IMember) curr;
				HTMLPrinter.addSmallHeader(buffer, getInfoText(member));
				Reader reader;
				try {
					reader = ScriptDocumentationAccess.getHTMLContentReader(
							nature, member, true, true);

					// Provide hint why there's no doc
					if (reader == null) {
						reader = new StringReader(
								ScriptHoverMessages.ScriptdocHover_noAttachedInformation);
					}

				} catch (ModelException ex) {
					return null;
				}

				if (reader != null) {
					HTMLPrinter.addParagraph(buffer, reader);
				}
				hasContents = true;
			}/*
			 * else if (curr.getElementType() == IModelElement.LOCAL_VARIABLE ||
			 * curr.getElementType() == IModelElement.TYPE_PARAMETER) {
			 * HTMLPrinter.addSmallHeader(buffer, getInfoText(curr));
			 * hasContents= true; }
			 */
		}

		IScriptDocumentationProvider[] providers = ScriptDocumentationAccess
				.getContributedProviders();
		for (IScriptDocumentationProvider p : providers) {
			p.clear();
		}

		if (!hasContents)
			return null;

		if (buffer.length() > 0) {
			HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
			HTMLPrinter.addPageEpilog(buffer);
			return buffer.toString();
		}

		return null;
	}

	protected String getHoverInfo(String nature, String content) {
		StringBuffer buffer = new StringBuffer();
		Reader reader;
		try {
			reader = ScriptDocumentationAccess.getHTMLContentReader(nature,
					content);

			if (reader != null) {
				HTMLPrinter.addParagraph(buffer, reader);
				if (buffer.length() > 0) {
					HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
					HTMLPrinter.addPageEpilog(buffer);
					return buffer.toString();
				}
			}
		} catch (ModelException ex) {
			// TODO: log
		}

		return null;
	}

	private String[] getInfoText(IModelElement[] member) {

		List<String> infoText = new ArrayList<String>();
		for (IModelElement elem : member) {
			infoText.add(getInfoText(elem));
		}

		return infoText.toArray(new String[] {});
		// return getInfoText(member);
	}

	private String getInfoText(IModelElement member) {
		long flags = member.getElementType() == IModelElement.FIELD ? LOCAL_VARIABLE_FLAGS
				: LABEL_FLAGS;
		String label = ScriptElementLabels.getDefault().getElementLabel(member,
				flags);
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < label.length(); i++) {
			char ch = label.charAt(i);
			if (ch == '<') {
				buf.append("&lt;"); //$NON-NLS-1$
			} else if (ch == '>') {
				buf.append("&gt;"); //$NON-NLS-1$
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}
}
