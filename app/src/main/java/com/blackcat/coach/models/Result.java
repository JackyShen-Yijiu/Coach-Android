
package com.blackcat.coach.models;

import java.io.Serializable;

public class Result<T> implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    public static final int RESULT_OK = 1;
    
    public int type;
    public T data;
    public String msg;
}
