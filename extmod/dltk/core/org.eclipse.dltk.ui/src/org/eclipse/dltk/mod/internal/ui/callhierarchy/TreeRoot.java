/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 * 			(report 36180: Callers/Callees view)
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.ui.callhierarchy;

public class TreeRoot {
    public static final Object EMPTY_ROOT = new Object();
    private Object fRoot;

    /**
     * Constructor for TreeRoot.
     */
    public TreeRoot(Object root) {
        this.fRoot = root;
    }

    Object getRoot() {
        return fRoot;
    }
}
