/*
 * CheckedInterruptedIOException.java
 * 
 * Copyright (c) 2000 Walt Disney Internet Group. All Rights Reserved.
 * 
 * Original author: Brian S O'Neill
 * 
 * $Workfile:: CheckedInterruptedIOException.java                             $
 *   $Author:: Briano                                                         $
 * $Revision:: 2                                                              $
 *     $Date:: 01/01/22 8:11p                                                 $
 */

package com.go.trove.net;

import java.io.*;
import java.net.*;
import java.lang.ref.*;

/******************************************************************************
 * 
 * @author Brian S O'Neill
 * @version
 * <!--$$Revision:--> 2 <!-- $-->, <!--$$JustDate:--> 01/01/22 <!-- $-->
 */
public class CheckedInterruptedIOException extends InterruptedIOException {
    static CheckedInterruptedIOException create
        (InterruptedIOException cause, Socket source) {
        if (cause instanceof CheckedInterruptedIOException) {
            return (CheckedInterruptedIOException)cause;
        }
        else {
            return new CheckedInterruptedIOException(cause, source);
        }
    }

    private InterruptedIOException mCause;
    private String mMessagePrefix;
    private Reference mSource;

    private CheckedInterruptedIOException(InterruptedIOException cause,
                                          Socket source) {
        this(cause, source, cause.getMessage());
    }

    private CheckedInterruptedIOException(InterruptedIOException cause,
                                          Socket source, String message) {
        super(CheckedSocketException.createMessagePrefix(source) +
              ' ' + message);
        mCause = cause;
        mMessagePrefix = CheckedSocketException.createMessagePrefix(source);
        mSource = new WeakReference(source);
    }

    public InterruptedIOException getCause() {
        return mCause;
    }

    /**
     * Returns null if source socket has been reclaimed by the garbage
     * collector.
     */
    public Socket getSource() {
        return (Socket)mSource.get();
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream ps) {
        synchronized (ps) {
            ps.print(mMessagePrefix);
            ps.print(": ");
            mCause.printStackTrace(ps);
        }
    }

    public void printStackTrace(PrintWriter pw) {
        synchronized (pw) {
            pw.print(mMessagePrefix);
            pw.print(": ");
            mCause.printStackTrace(pw);
        }
    }
}
