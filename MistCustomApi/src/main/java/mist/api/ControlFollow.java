package mist.api;

import android.os.RemoteException;
import android.util.Log;

import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import mist.Peer;
import mist.RequestInterface;
import mist.sandbox.Callback;

class ControlFollow {

    static int request(Peer peer, Control.FollowCb callback) {
        final String op = "control.follow";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");

        writer.writeStartDocument();
        writer.writeBinaryData("luid", new BsonBinary(peer.getLocalId()));
        writer.writeBinaryData("ruid", new BsonBinary(peer.getRemoteId()));
        writer.writeBinaryData("rhid", new BsonBinary(peer.getRemoteHostId()));
        writer.writeBinaryData("rsid", new BsonBinary(peer.getRemoteServiceId()));
        writer.writeString("protocol", peer.getProtocol());
        writer.writeBoolean("online", peer.isOnline());
        writer.writeEndDocument();

        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        return RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Control.FollowCb callback;

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
                BsonDocument bson = new RawBsonDocument(dataBson);
                BsonDocument followData = bson.get("data").asDocument();
                BsonValue followValue = followData.get("data");
                String followEpid = followData.getString("id").getValue();
                if (followValue.isBoolean()) {
                    callback.cbBool(followEpid, followValue.asBoolean().getValue());
                }
                if (followValue.isInt32()) {
                    callback.cbInt(followEpid, followValue.asInt32().getValue());
                }
                if (followValue.isDouble()) {
                    float value = (float) followValue.asDouble().getValue();
                    callback.cbFloat(followEpid, value);
                }
                if (followValue.isString()) {
                    callback.cbString(followEpid, followValue.asString().getValue());
                }
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                Log.d(op, "RPC error: " + msg + " code: " + code);
                callback.err(code, msg);
            }

            private Callback init(Control.FollowCb callback) {
                this.callback = callback;
                return this;
            }
        }.init(callback));
    }
}









