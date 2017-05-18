package com.joym.PaymentSdkV2;

/**
 * @brief Upper payment parameters
 * 
 * */
public class PaymentParam
{
	/* Amount of payment */
	private int mPrice = 0;
	/* Payment unit */
	private int mUnit = 0;
	/* Charging point */
	private int mChargePt;
	/* Whether free */
	private boolean mFree = false;
	
	public PaymentParam(int mChargePt)
	{
		this.mChargePt = mChargePt;
	}

	public int getmPrice() 
	{
		return mPrice;
	}

	public int getmUnit()
	{
		return mUnit;
	}

	public int getmChargePt() 
	{
		return mChargePt;
	}

	public boolean ismFree() 
	{
		return mFree;
	}
}
