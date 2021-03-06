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

import org.bson.BSONException;
import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jeppe on 10/24/17.
 */

public class Identity implements Serializable {

    private byte[] uid;
    private String alias;
    private boolean privkey;
    private byte[] pubkey;
    private Hosts hosts;
    private BsonDocument meta;

    private static int WISH_UID_LEN = 32;

    private static class Hosts {
        private List<String> transports;

        public List<String> getTransports() {
            return transports;
        }

    }

    public static Identity fromBson(byte[] data) {
        try {
            return fromBson(new RawBsonDocument(data));
        } catch (BSONException e) {
            return null;
        }

    }

    public static Identity fromBson(BsonDocument bsonDocument) {
        Identity identity = new Identity();
        try {
            if (bsonDocument.containsKey("uid")
                    && bsonDocument.containsKey("alias")
                    && bsonDocument.containsKey("privkey")) {

                identity.uid = bsonDocument.getBinary("uid").getData();
                identity.alias = bsonDocument.getString("alias").getValue();
                identity.privkey = bsonDocument.getBoolean("privkey").getValue();

                if (identity.uid.length != WISH_UID_LEN) {
                    return null;
                }

                if (bsonDocument.containsKey("pubkey")) {
                    identity.pubkey = bsonDocument.getBinary("pubkey").getData();
                    if (identity.pubkey.length != WISH_UID_LEN) {
                        return null;
                    }
                }

                if (bsonDocument.containsKey("hosts")) {
                    Hosts host = new Hosts();
                    for (BsonValue bsonHosts : bsonDocument.getArray("hosts")) {
                        if (bsonHosts.isDocument()) {
                            BsonDocument bsonHost = bsonHosts.asDocument();
                            if (bsonHost.containsKey("transports")) {
                                host.transports = new ArrayList<String>();
                                for (BsonValue bsonTransport : bsonHost.getArray("transports")) {
                                    if (bsonTransport.isString()) {
                                        host.transports.add(bsonTransport.asString().getValue());
                                    }
                                }
                            }
                        }
                    }
                    identity.hosts = host;
                }

                if (bsonDocument.containsKey("meta")) {
                    identity.meta = bsonDocument.getDocument("meta");
                }

            } else {
                return null;
            }

        } catch (BSONException e) {
            return null;
        }


        return identity;
    }

    public byte[] toBson() {
        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);

        writer.writeStartDocument();

        writer.writeBinaryData("uid", new BsonBinary(getUid()));
        writer.writeString("alias", getAlias());
        writer.writeBoolean("privkey", isPrivkey());
        writer.writeBinaryData("pubkey", new BsonBinary(getPubkey()));

        if (getHosts() != null) {
            Hosts host = getHosts();
            writer.writeStartArray("hosts");

            if (host.getTransports() != null) {
                writer.writeStartDocument();
                writer.writeStartArray("transports");
                for (String transport : host.getTransports()) {
                    writer.writeString(transport);
                }
                writer.writeEndArray();
                writer.writeEndDocument();
            }

            writer.writeEndArray();
        }

        writer.writeEndDocument();
        writer.flush();

        return buffer.toByteArray();
    }

    public byte[] getUid() {
        return uid;
    }

    public String getAlias() {
        return alias;
    }


    public boolean isPrivkey() {
        return privkey;
    }

    public byte[] getPubkey() {
        return pubkey;
    }

    public Hosts getHosts() {
        return hosts;
    }

    public BsonDocument getMeta() {
        return meta;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null) {
            return false;
        }

        if (getClass() != object.getClass()) {
            return false;
        }

        Identity identity = (Identity) object;

        return Arrays.equals(uid, identity.getUid())
                && Arrays.equals(pubkey, identity.getPubkey())
                && alias.equals(identity.getAlias())
                && privkey == identity.isPrivkey();
    }
}
