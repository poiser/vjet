package vjo.net;

/*
 * @(#)src/classes/sov/java/net/NoRouteToHostException.java, net, asdev, 20070119 1.9
 * ===========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 *
 * IBM SDK, Java(tm) 2 Technology Edition, v5.0
 * (C) Copyright IBM Corp. 1998, 2005. All Rights Reserved
 * ===========================================================================
 */

/*
 * ===========================================================================
 (C) Copyright Sun Microsystems Inc, 1992, 2004. All rights reserved.
 * ===========================================================================
 */






 

import vjo.lang.* ;

/**
 * Signals that an error occurred while attempting to connect a
 * socket to a remote address and port.  Typically, the remote
 * host cannot be reached because of an intervening firewall, or
 * if an intermediate router is down.
 *
 * @since   JDK1.1
 */
public class NoRouteToHostException extends SocketException {
    /**
     * Constructs a new NoRouteToHostException with the specified detail 
     * message as to why the remote host cannot be reached.
     * A detail message is a String that gives a specific 
     * description of this error.
     * @param msg the detail message
     */
    public NoRouteToHostException(String msg) {
	super(msg);
    }

    /**
     * Construct a new NoRouteToHostException with no detailed message.
     */
    public NoRouteToHostException() {}
}
