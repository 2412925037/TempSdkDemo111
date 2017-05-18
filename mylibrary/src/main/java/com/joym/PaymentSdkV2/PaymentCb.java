package com.joym.PaymentSdkV2;

/**
 * @brief The callback billing results to the upper
 * 
 * */
public interface PaymentCb 
{
	public void PaymentResult(int resultCode, String[] cbParam);
}
