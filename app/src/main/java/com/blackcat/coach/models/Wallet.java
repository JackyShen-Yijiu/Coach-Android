package com.blackcat.coach.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zou on 15/11/17.
 */
public class Wallet implements Serializable {
    public List<WalletRecord> list;
    public int wallet;
}
