package ipc_communicator;

public class IPC_test {

    public static void main(String[] args) {
        
        //vytvorenie noveho vlakna s modulom komunikatora a jeho nasledne spustenie
        IPCCommunicator komunikator = new IPCCommunicator(new UkazkaDelegata());
        komunikator.start();
            
    }

}