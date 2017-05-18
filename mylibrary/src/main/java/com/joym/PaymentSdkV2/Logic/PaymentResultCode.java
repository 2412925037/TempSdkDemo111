package com.joym.PaymentSdkV2.Logic;

/**
 * @brief payment result
 * 
 * */
public class PaymentResultCode
{
	/* payment success */
	public static final int PAYMENT_SUCCESS = 1;
	/* payment failed */
	public static final int PAYMENT_FAILED = 2;
	/* user cancel */
	public static final int PAYMENT_USR_CANCEL = 4;
	/* Do not support the current billing methods */
	public static final int PAYMENT_NOT_SUPPORT = 8;
	/* Have bought */
	public static final int PAYMENT_BUYED = 16;
	/* Support the current billing */
	public static final int PAYMENT_SUPPORT = 32;
	/* Internal error */
	public static final int PAYMENT_INNER_ERR = 64;
	/* restore success */
	public static final int PAYMENT_RESTORE_OK = 128;
	/* Not China mobile users */
	public static final int PAYMENT_NOT_CMCC = 256;
	/* TODO ...*/
}
