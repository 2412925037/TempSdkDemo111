package com.joym.PaymentSdkV2.Logic;

import java.util.ArrayList;

/**
 * @brief 内部回调 
 * 
 * */
public interface PaymentInnerCb 
{
	public void InnerResult(int result, ArrayList<String> cbParam);
}
