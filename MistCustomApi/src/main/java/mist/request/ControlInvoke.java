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
package mist.request;

import android.os.RemoteException;

import org.bson.BSONException;
import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import mist.Peer;
import mist.RequestInterface;
import mist.sandbox.Callback;

class ControlInvoke {

    static int request(Peer peer, String epid, Control.InvokeCb callback) {
       return send(peer, epid, null, null, null, null, null, callback);
    }

    static int request(Peer peer, String epid, String value, Control.InvokeCb callback) {
        return send(peer, epid, value, null, null, null, null, callback);
    }

    static int request(Peer peer, String epid, Boolean value, Control.InvokeCb callback) {
        return send(peer, epid, null, value, null, null, null, callback);
    }

    static int request(Peer peer, String epid, int value, Control.InvokeCb callback) {
        return send(peer, epid, null, null, value, null, null, callback);
    }

    static int request(Peer peer, String epid, float value, Control.InvokeCb callback) {
        return send(peer, epid, null, null, null, value, null, callback);
    }

    static int request(Peer peer, String epid, byte[] value, Control.InvokeCb callback) {
        return send(peer, epid, null, null, null, null, value, callback);
    }

    private static int send(Peer peer, String epid, String stringValue, Boolean boolValue, Integer intValue, Float floatValue, byte[] byteValue, Control.InvokeCb callback) {
        final String op = "mist.control.invoke";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");
        writer.writeStartDocument();
        writer.writeBinaryData("luid", new BsonBinary(peer.getLuid()));
        writer.writeBinaryData("ruid", new BsonBinary(peer.getRuid()));
        writer.writeBinaryData("rhid", new BsonBinary(peer.getRhid()));
        writer.writeBinaryData("rsid", new BsonBinary(peer.getRsid()));
        writer.writeString("protocol", peer.getProtocol());
        writer.writeBoolean("online", peer.isOnline());
        writer.writeEndDocument();

        writer.writeString(epid);

        if (stringValue != null) {
            writer.writeString(stringValue);
        } else if (boolValue != null) {
            writer.writeBoolean(boolValue);
        } else if (intValue != null) {
            writer.writeInt32(intValue);
        } else if (floatValue != null) {
            writer.writeDouble(floatValue);
        } else if (byteValue != null) {
            writer.writeBinaryData(new BsonBinary(byteValue));
        }

        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

       int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Control.InvokeCb callback;

            @Override
            public void ack(byte[] dataBson) throws RemoteException {
                response(dataBson);
                callback.end();
            }

            @Override
            public void sig(byte[] dataBson) throws RemoteException {
                response(dataBson);
            }

            private void response(byte[] dataBson) {
                BsonDocument bson;
                try {
                    bson = new RawBsonDocument(dataBson);
                } catch (BSONException e) {
                    callback.err(mist.request.Callback.BSON_ERROR_CODE, mist.request.Callback.BSON_ERROR_STRING);
                    return;
                }
                if (bson.get("data").isBoolean()) {
                    callback.cbBoolean(bson.get("data").asBoolean().getValue());
                } else if (bson.get("data").isInt32()) {
                    callback.cbInt(bson.get("data").asInt32().getValue());
                } else if (bson.get("data").isDouble()) {
                    float value = (float) bson.get("data").asDouble().getValue();
                    callback.cbFloat(value);
                } else if (bson.get("data").isString()) {
                    callback.cbString(bson.get("data").asString().getValue());
                } else if (bson.get("data").isBinary()) {
                    callback.cbByte(bson.get("data").asBinary().getData());
                } else if (bson.get("data").isArray()) {
                    callback.cbArray(bson.get("data").asArray());
                } else if (bson.get("data").isDocument()) {
                    callback.cbDocument(bson.get("data").asDocument());
                }
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private Callback init(Control.InvokeCb callback) {
                this.callback = callback;
                return this;
            }
        }.init(callback));

        if (requestId == 0) {
            callback.err(0, "request fail");
            MistLog.err(op, requestId, "request fail");
        }

        return requestId;
    }
}
