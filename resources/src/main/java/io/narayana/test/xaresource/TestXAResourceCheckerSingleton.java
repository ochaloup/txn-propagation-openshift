package io.narayana.test.xaresource;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Singleton class used as a log for actions done during testing.
 */
@Singleton
@LocalBean
@Path("xaresource-checker")
public class TestXAResourceCheckerSingleton {
    private int committed, prepared, rolledback;
    private int  synchronizedBegin, synchronizedBefore, synchronizedAfter,
        synchronizedAfterCommitted, synchronizedAfterRolledBack;
    private Collection<String> messages = new ArrayList<>();

    @GET
    @Path("committed")
    public int getCommitted() {
        return committed;
    }

    public void addCommit() {
        committed++;
    }

    @GET
    @Path("prepared")
    public int getPrepared() {
        return prepared;
    }

    public void addPrepare() {
        prepared++;
    }

    @GET
    @Path("rolledback")
    public int getRolledback() {
        return rolledback;
    }

    public void addRollback() {
        rolledback++;
    }

    public boolean isSynchronizedBefore() {
        return synchronizedBefore > 0;
    }

    public void setSynchronizedBefore() {
        synchronizedBefore++;
    }

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
    @Path("synchronizedbefore")
    public int countSynchronizedBefore() {
        return synchronizedBefore;
    }

    @GET
    @Path("synchronizedafter")
    public int countSynchronizedAfter() {
        return synchronizedAfter;
    }

    @GET
    @Path("synchronizedaftercommitted")
    public int countSynchronizedAfterCommitted() {
        return synchronizedAfterCommitted;
    }

    @GET
    @Path("synchronizedafterrolledback")
    public int countSynchronizedAfterRolledBack() {
        return synchronizedAfterRolledBack;
    }

    @GET
    @Path("synchronizedbegin")
    public int countSynchronizedBegin() {
        return synchronizedBegin;
    }

    public void addMessage(String msg) {
        messages.add(msg);
    }

    @GET @Produces("application/json")
    @Path("messages")
    public Collection<String> getMessages() {
        return messages;
    }

    public void resetMessages() {
        messages.clear();
    }

    @DELETE
    @Path("resetall")
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
