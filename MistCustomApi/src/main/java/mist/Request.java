/**
 * Copyright (C) 2020, ControlThings Oy Ab
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * @license Apache-2.0
 */
package mist;

import org.bson.BsonDocument;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by jeppe on 2/27/17.
 */

public class Request implements Serializable{

    private byte[] luid;
    private byte[] ruid;
    private String alias;
    private byte[] pubkey;
    private BsonDocument meta;

    public byte[] getLuid() {
        return luid;
    }

    public void setLuid(byte[] luid) {
        this.luid = luid;
    }

    public byte[] getRuid() {
        return ruid;
    }

    public void setRuid(byte[] ruid) {
        this.ruid = ruid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public byte[] getPubkey() {
        return pubkey;
    }

    public void setPubkey(byte[] pubkey) {
        this.pubkey = pubkey;
    }

    public BsonDocument getMeta() {
        return meta;
    }

    public void setMeta(BsonDocument meta) {
        this.meta = meta;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Request)) {
            return false;
        }

        Request that = (Request) other;

        return Arrays.equals(this.luid, that.luid)
                && Arrays.equals(this.ruid, that.ruid)
                && Arrays.equals(this.pubkey, that.pubkey)
                && this.alias.equals(that.alias);
    }
}
