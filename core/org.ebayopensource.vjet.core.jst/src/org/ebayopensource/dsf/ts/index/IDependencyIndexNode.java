/*******************************************************************************
 * Copyright (c) 2005-2011 eBay Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.ebayopensource.dsf.ts.index;

import java.util.List;

public interface IDependencyIndexNode<D> {
	/**
	 * Answer the name of this node
	 * @return String
	 */
	String getName();

	/**
	 * Answer what depends on the entity this node represents
	 * @return List<D> empty if none
	 */
	List<D> getDependents();
}
