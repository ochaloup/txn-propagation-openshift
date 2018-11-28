package io.narayana.test.xaresource;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Singleton class used as a log for actions done during testing.
 */
@Singleton
@LocalBean
@Path("helloworld")
public class TransactionCheckerSingleton {
    private int committed, prepared, rolledback;
    private int  synchronizedBegin, synchronizedBefore, synchronizedAfter,
        synchronizedAfterCommitted, synchronizedAfterRolledBack;
    private Collection<String> messages = new ArrayList<>();

    @GET
    public int getCommitted() {
        return committed;
    }

    public void addCommit() {
        committed++;
    }

    @GET
    public int getPrepared() {
        return prepared;
    }

    public void addPrepare() {
        prepared++;
    }

    @GET
    public int getRolledback() {
        return rolledback;
    }

    public void addRollback() {
        rolledback++;
    }

    @GET
    public boolean isSynchronizedBefore() {
        return synchronizedBefore > 0;
    }

    public void setSynchronizedBefore() {
        synchronizedBefore++;
    }

    @GET
    public boolean isSynchronizedAfter() {
        return synchronizedAfter > 0;
    }

    public void setSynchronizedAfter(boolean isCommit) {
        synchronizedAfter++;
        if(isCommit) {
            synchronizedAfterCommitted++;
        } else {
            synchronizedAfterRolledBack++;
        }
    }

    @GET
    public boolean isSynchronizedBegin() {
        return synchronizedBegin > 0;
    }

    public void setSynchronizedBegin() {
        synchronizedBegin++;
    }

    public void resetCommitted() {
        committed = 0;
    }

    public void resetPrepared() {
        prepared = 0;
    }

    public void resetRolledback() {
        rolledback = 0;
    }

    public void resetSynchronizedBefore() {
        synchronizedBefore = 0;
    }

    public void resetSynchronizedAfter() {
        synchronizedAfter = 0;
        synchronizedAfterCommitted = 0;
        synchronizedAfterRolledBack = 0;
    }

    public void resetSynchronizedBegin() {
        synchronizedBegin = 0;
    }

    @GET
    public int countSynchronizedBefore() {
        return synchronizedBefore;
    }

    @GET
    public int countSynchronizedAfter() {
        return synchronizedAfter;
    }

    @GET
    public int countSynchronizedAfterCommitted() {
        return synchronizedAfterCommitted;
    }

    @GET
    public int countSynchronizedAfterRolledBack() {
        return synchronizedAfterRolledBack;
    }

    @GET
    public int countSynchronizedBegin() {
        return synchronizedBegin;
    }

    public void addMessage(String msg) {
        messages.add(msg);
    }

    @GET
    public Collection<String> getMessages() {
        return messages;
    }

    public void resetMessages() {
        messages.clear();
    }

    @DELETE
    public void resetAll() {
        resetCommitted();
        resetPrepared();
        resetRolledback();
        resetSynchronizedAfter();
        resetSynchronizedBefore();
        resetSynchronizedBegin();
        resetMessages();
    }
}
