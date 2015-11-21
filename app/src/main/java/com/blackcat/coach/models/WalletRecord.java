package com.blackcat.coach.models;

import java.io.Serializable;

/**
 * Created by zou on 15/11/17.
 */
public class WalletRecord implements Serializable {
    public String createtime;
    public int amount;
    public int type;
    public int seqindex;
}
