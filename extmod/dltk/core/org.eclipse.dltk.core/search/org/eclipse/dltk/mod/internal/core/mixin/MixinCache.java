/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core.mixin;

import org.eclipse.dltk.mod.core.mixin.IMixinElement;
import org.eclipse.dltk.mod.internal.core.OverflowingLRUCache;
import org.eclipse.dltk.mod.internal.core.util.LRUCache;

/**
 * An LRU cache of <code>ModelElements</code>.
 */
public class MixinCache extends OverflowingLRUCache {
	IMixinElement spaceLimitParent = null;

	/**
	 * Constructs a new element cache of the given size.
	 */
	public MixinCache(int size) {
		super(size);
		fLoadFactor = 0.90;
	}

	/**
	 * Constructs a new element cache of the given size.
	 */
	public MixinCache(int size, int overflow) {
		super(size, overflow);
	}

	/**
	 * Returns true if the element is successfully closed and removed from the
	 * cache, otherwise false.
	 * 
	 * <p>
	 * NOTE: this triggers an external removal of this element by closing the
	 * element.
	 */
	protected boolean close(LRUCacheEntry entry) {
		IMixinElement element = (IMixinElement) entry._fValue;
		if( element instanceof IInternalMixinElement ) {
			((IInternalMixinElement)element).close();
		}
		return true;
	}

	/*
	 * Ensures that there is enough room for adding the given number of
	 * children. If the space limit must be increased, record the parent that
	 * needed this space limit.
	 */
	public void ensureSpaceLimit(int childrenSize, IMixinElement parent) {
		// ensure the children can be put without closing other elements
		int spaceNeeded = 1 + (int) ((1 + fLoadFactor) * (childrenSize + fOverflow));
		if (fSpaceLimit < spaceNeeded) {
			// parent is being opened with more children than the space limit
			shrink(); // remove overflow
			setSpaceLimit(spaceNeeded);
			this.spaceLimitParent = parent;
		}
	}

	/*
	 * Returns a new instance of the receiver.
	 */
	protected LRUCache newInstance(int size, int overflow) {
		return new MixinCache(size, overflow);
	}

	/*
	 * If the given parent was the one that increased the space limit, reset the
	 * space limit to the given default value.
	 */
	public void resetSpaceLimit(int defaultLimit, IMixinElement parent) {
		if (parent.equals(this.spaceLimitParent)) {
			setSpaceLimit(defaultLimit);
			this.spaceLimitParent = null;
		}
	}
}
