/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ipc_communicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 *
 * @author jano
 */
public class IPCCommunicator extends Thread {

    //cesta ku C programu, alebo kniznici, ktoru spustime, a s ktorou neskor komunikujeme
    private static String c_path = "/home/jano/NetBeansProjects/IPC_test_c/dist/Debug/GNU-Linux-x86/ipc_test_c";
    
    //prostriedky na komunikaciu
    private BufferedReader citac;
    private BufferedReader citacErr;
    private PrintWriter zapisovac;
    
    //samotny spusteny proces
    private Process proces;  
    
    //delegat, ktory sa bude starat o akcie vyvolane prijatymi spravami
    private IPCHandler delegat;  

    
    public IPCCommunicator(IPCHandler delegat) {
        this.delegat = delegat;   
    }

    
    @Override
    public void run() {
        if(this.delegat == null){
            System.err.println("Nebol nastaveny delegat, koncim");
            return;
        }
        try {
            this.proces = Runtime.getRuntime().exec(c_path);

            InputStream in = this.proces.getInputStream();
            OutputStream out = this.proces.getOutputStream();
            InputStream error = this.proces.getErrorStream();

            this.citac = new BufferedReader(new InputStreamReader(in));
            this.citacErr = new BufferedReader(new InputStreamReader(error));
            this.zapisovac = new PrintWriter(out);

            this.pracuj();

        } catch (IOException ex) {
            System.err.println("IOException");
        }
    }

    /**
     * hlavny cyklus/pracovna slucka vlanka - prijme spravu a posle ju na spracovanie
     * @throws IOException 
     */
    public void pracuj() throws IOException {
        String prijataSprava;
        while (true) {
            prijataSprava = this.citac.readLine();                 
            System.out.println("Prijal som spravu a poslal ju na spracovanie");
            System.out.println("Sprava je: " + prijataSprava);
            this.spracujSpravu(prijataSprava);  
        }
    }
    
    /**
     * metoda spracuje prijatu spravu a ODPOVIE !!!
     * @param sprava - sprava, ktoru treba spracovat
     */
    public void spracujSpravu(String sprava){
        
        //podla typu spravy rozhodnut, aku metodu IPCHandlera zavolame
            this.delegat.test();
        
        this.zapisovac.println("SPRAVA SPRACOVANA");
        this.zapisovac.flush();
        System.out.println("Spracoval som spravu a odoslal odpoved");
    }

}


