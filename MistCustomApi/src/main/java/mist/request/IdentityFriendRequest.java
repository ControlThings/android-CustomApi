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
import org.bson.BsonDocumentReader;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;


import mist.Peer;
import mist.RequestInterface;

/**
 * Created by jeppe on 9/28/16.
 */
class IdentityFriendRequest {
    static int request(Peer peer, byte[] uid, BsonDocument contact, Identity.FriendRequestCb callback) {
        final String op = "wish.identity.friendRequest";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");

        if (peer != null) {
            writer.pipe(new BsonDocumentReader(new RawBsonDocument(peer.toBson())));
        } else {
            writer.writeNull();
        }

        writer.writeBinaryData(new BsonBinary(uid));

        try {
            if (contact.containsKey("data") && contact.containsKey("meta")) {
                writer.writeStartDocument();
                writer.writeBinaryData("data", contact.getBinary("data"));
                writer.writeBinaryData("meta", contact.getBinary("meta"));
                writer.writeEndDocument();
            } else {
                callback.err(757, "invalide contact");
                return 0;
            }
        } catch (BSONException e) {
            callback.err(757, e.getMessage());
            return 0;
        }

        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new mist.sandbox.Callback.Stub() {
            private Identity.FriendRequestCb callback;

            @Override
            public void ack(byte[] data) throws RemoteException {
                response(data);
                callback.end();
            }

            @Override
            public void sig(byte[] data) throws RemoteException {

            }

            private void response(byte[] dataBson) {
                boolean state;
                try {
                    BsonDocument bson = new RawBsonDocument(dataBson);
                    state = bson.get("data").asBoolean().getValue();
                } catch (BSONException e) {
                    callback.err(mist.request.Callback.BSON_ERROR_CODE, mist.request.Callback.BSON_ERROR_STRING);
                    return;
                }
                callback.cb(state);
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private mist.sandbox.Callback init(Identity.FriendRequestCb callback) {
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
