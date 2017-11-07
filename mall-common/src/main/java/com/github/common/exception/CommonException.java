package com.github.common.exception;

import java.io.Serializable;

public class CommonException extends RuntimeException implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5286342041830639693L;

	public String errorCode;

	public String errorMessage;

    public CommonException(String msg) {
        super(msg);
    }

    public CommonException() {

    }
    
    
    public CommonException(String code, String msg) {
        super(msg);
        this.errorCode = code;
        this.errorMessage = msg;
    }

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	//@Override
	//public synchronized Throwable fillInStackTrace() {
    //   return this;
   // } 
 
	
	
}
