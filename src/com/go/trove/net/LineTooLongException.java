/*
 * LineTooLongException.java
 * 
 * Copyright (c) 2000 Walt Disney Internet Group. All Rights Reserved.
 * 
 * Original author: Brian S O'Neill
 * 
 * $Workfile:: LineTooLongException.java                                      $
 *   $Author:: Briano                                                         $
 * $Revision:: 1                                                              $
 *     $Date:: 00/12/06 11:36a                                                $
 */

package com.go.trove.net;

/******************************************************************************
 * 
 * @author Brian S O'Neill
 * @version
 * <!--$$Revision:--> 1 <!-- $-->, <!--$$JustDate:--> 00/12/06 <!-- $-->
 */
public class LineTooLongException extends java.io.IOException {
    public LineTooLongException(int limit) {
        super("> " + limit);
    }
}
