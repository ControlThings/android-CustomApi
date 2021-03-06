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

import org.bson.BsonDocument;

import java.util.List;

import mist.Peer;
import mist.Request;


/**
 * Created by jeppe on 11/30/16.
 */

public class Identity {

    /**
     *
     * @param alias String alias
     * @param callback Identity.CreateCb
     * @return
     */
    public static int create(String alias, CreateCb callback) {
        return IdentityCreate.request(null, alias, callback);
    }

    public static int create(Peer peer, String alias, CreateCb callback) {
        if (peer == null) {
            return 0;
        }
        return IdentityCreate.request(peer, alias, callback);
    }

    /**
     *
     * @param callback Identity.ListCb
     * @return
     */
    public static int list(ListCb callback) {
        return IdentityList.request(null, callback);
    }

    public static int list(Peer peer, ListCb callback) {
        if (peer == null) {
            return 0;
        }
        return IdentityList.request(peer, callback);
    }

    public static int get(byte[] uid, GetCb callback) {
        return IdentityGet.request(null, uid, callback);
    };

    public static int get(Peer peer, byte[] uid, GetCb callback) {
        if (peer == null) {
            return 0;
        }
        return IdentityGet.request(peer, uid, callback);
    };

    public static int update(mist.Identity identity, String alias, UpdateCb callback) {
        return IdentityUpdate.request(null, identity, alias, null, callback);
    }

    public static int update(mist.Identity identity, BsonDocument meta, UpdateCb callback) {
        return IdentityUpdate.request(null, identity, null, meta, callback);
    }

    public static int update(Peer peer, mist.Identity identity, String alias, UpdateCb callback) {
        if (peer == null) {
            return 0;
        }
        return IdentityUpdate.request(peer, identity, alias, null, callback);
    }

    public static int update(Peer peer, mist.Identity identity, BsonDocument meta, UpdateCb callback) {
        if (peer == null) {
            return 0;
        }
        return IdentityUpdate.request(peer, identity, null, meta, callback);
    }

    public static int friendRequest(byte[] uid, BsonDocument contact, FriendRequestCb callback) {
        return IdentityFriendRequest.request(null, uid, contact, callback);
    }

    public static void friendRequest(Peer peer, byte[] uid, BsonDocument contact, FriendRequestCb callback) {
        if (peer == null) {
            return;
        }
        IdentityFriendRequest.request(peer, uid, contact, callback);
    }

    public static int friendRequestAccept(byte[] luid,  byte[] ruid, FriendRequestAcceptCb callback) {
       return IdentityFriendRequestAccept.request(null, luid, ruid, callback);
    }

    public static int friendRequestAccept(Peer peer, byte[] luid,  byte[] ruid, FriendRequestAcceptCb callback) {
        if (peer == null) {
            return 0;
        }
        return IdentityFriendRequestAccept.request(peer, luid, ruid, callback);
    }

    public static int friendRequestList(FriendRequestListCb callback) {
       return IdentityFriendRequestList.request(null, callback);
    }

    public static int friendRequestList(Peer peer, FriendRequestListCb callback) {
        if (peer == null) {
            return 0;
        }
        return  IdentityFriendRequestList.request(peer, callback);
    }

    public static int friendRequestDecline(byte[] luid, byte[] ruid, FriendRequestDeclineCb callback) {
        return IdentityFriendRequestDecline.request(null, luid, ruid, callback);
    }

    public static int friendRequestDecline(Peer peer, byte[] luid, byte[] ruid, FriendRequestDeclineCb callback) {
        if (peer == null) {
            return 0;
        }
        return IdentityFriendRequestDecline.request(peer, luid, ruid, callback);
    }

    public abstract static class CreateCb extends Callback {
        public abstract void cb (mist.Identity identity);
    }

    public abstract static class ListCb extends Callback {
        public abstract void cb(List<mist.Identity> identityList);
    }

    public abstract static class GetCb extends Callback {
        public abstract void cb (mist.Identity identity);
    }

    public abstract static class UpdateCb extends Callback {
        public abstract void cb(mist.Identity identity);
    }

    public abstract static class FriendRequestCb extends Callback {
        public abstract void cb(boolean state);
    }

    public abstract static class FriendRequestAcceptCb extends Callback {
        public abstract void cb(boolean state);
    }

    public abstract static class FriendRequestListCb extends Callback {
        public abstract void cb(List<Request> requests);
    }

    public abstract static class FriendRequestDeclineCb extends Callback {
        public abstract void cb(boolean value);
    }

}
