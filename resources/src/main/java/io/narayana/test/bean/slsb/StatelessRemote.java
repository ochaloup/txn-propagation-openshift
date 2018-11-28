package io.narayana.test.bean.slsb;

import java.rmi.RemoteException;

public interface StatelessRemote {
    int transactionStatus() throws RemoteException;
    int call() throws RemoteException;
}
