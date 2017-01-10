package mist.api;

import android.os.RemoteException;
import android.util.Log;

import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import mist.Peer;
import mist.RequestInterface;
import mist.sandbox.Callback;

class ControlWrite {

    static void request(Peer peer, String epid, Boolean state, Control.WriteCb callback) {
        send(peer, epid, state, null, null, null, callback);
    }
    static void request(Peer peer, String epid, int state, Control.WriteCb callback) {
        send(peer, epid, null, state, null, null, callback);
    }
    static void request(Peer peer, String epid, float state, Control.WriteCb callback) {
        send(peer, epid, null, null, state, null, callback);
    }
    static void request(Peer peer, String epid, String state, Control.WriteCb callback) {
        send(peer, epid, null, null, null, state, callback);
    }

    private static void send(Peer peer, String epid, Boolean boolState, Integer intState, Float floatState, String stringState, Control.WriteCb callback) {
        final String op = "control.write";

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

        writer.writeString(epid);

        if (boolState != null) {
            writer.writeBoolean(boolState);
        }
        else if (intState != null) {
            writer.writeInt32(intState);
        }
        else if (floatState != null) {
            writer.writeDouble(floatState);
        }
        else if (stringState != null) {
            writer.writeString(stringState);
        }

        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Control.WriteCb callback;

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
                callback.cb();
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                Log.d(op, "RPC error: " + msg + " code: " + code);
                callback.err(code, msg);
            }

            private Callback init(Control.WriteCb callback) {
                this.callback = callback;
                return this;
            }
        }.init(callback));
    }
}