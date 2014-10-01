package communicator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;

public class Util {

    public static final String TYPE_SEND = "SEND:";
    public static final String TYPE_PAIR = "PAIR:";
    public static final String TYPE_RESP = "RESP:";
    public static final String TYPE_MPUB = "MPUB:"; // mobile public

    //enumy na operacny system a architekturu
    public static enum PCOperacnySystem {

        LINUX, WINDOWS, UNKNOWN
    };

    public static enum PCArchitektura {

        BIT32, BIT64, UNKNOWN
    };

    //funkcia na zistenie operacneho systemu
    public static PCOperacnySystem getOS() {
        String name = System.getProperty("os.name");        
        if (name.contains("Linux")) {
            return PCOperacnySystem.LINUX;
        } else if (name.contains("Windows")) {
            return PCOperacnySystem.WINDOWS;
        } else {
            return PCOperacnySystem.UNKNOWN;
        }
    }

    //funkcia na zistenie kolko bitovy je operacny system
    public static PCArchitektura getArch() {
        String name = System.getProperty("os.arch");
        if (name.contains("x86")) {
            return PCArchitektura.BIT32;
        } else if (name.contains("64")) {
            return PCArchitektura.BIT64;
        } else {
            return PCArchitektura.UNKNOWN;
        }
    }

    public static byte[] fileToByteArray(File file) {
        byte[] b;
        try {
            RandomAccessFile f = new RandomAccessFile(file, "r");
            b = new byte[(int) f.length()];
            f.read(b);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return b;
    }

    /**
     * dekoduje base64 retazec na subor a ten vrati v podobe pola bajtov
     *
     * @param string retazet reprezentujuci subor
     * @return rozkodovany subor vo forme pola bajtov
     */
    public static byte[] decodeBase64StringToByte(String string) {
        return Base64.decodeBase64(string);
    }

    public static String encodeByteToBase64String(byte[] b) {
        return Base64.encodeBase64String(b);
    }

    public static byte[] splitByteArray(List<byte[]> l, int pieceSize) {
        int size = 0;
        for (byte[] iter : l) {
            size += iter.length;
        }
        byte[] temp = new byte[size];
        for (int i = 0; i < l.size(); i++) {
            for (int j = 0; j < l.get(i).length; j++) {
                temp[i * pieceSize + j] = l.get(i)[j];
            }
        }
        return temp;
    }

    public static List<byte[]> divideByteArray(byte[] b, int pieceSize) {
        List<byte[]> list = new ArrayList<>();
        byte[] temp;
        boolean zacalSom = false;
        for (int i = 0; i < Math.ceil(b.length / (pieceSize * 1.0)); i++) {
            if (i * pieceSize + pieceSize < b.length) {
                temp = new byte[pieceSize];
                System.arraycopy(b, i * pieceSize, temp, 0, pieceSize);
            } else {
                if (b.length <= pieceSize) {
                    temp = new byte[b.length];
                    System.arraycopy(b, i * pieceSize, temp, 0, b.length);
                } else {
                    temp = new byte[b.length - i * pieceSize];
                    System.arraycopy(b, i * pieceSize, temp, 0, b.length - i * pieceSize);
                }
            }
            list.add(temp);
        }

        return list;
    }

    public static String dajSHA1CheckSum(String datafile) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            FileInputStream fis = new FileInputStream(datafile);
            byte[] dataBytes = new byte[1024];

            int nread;

            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }

            byte[] mdbytes = md.digest();

            //convert the byte to hex format
            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            //System.out.println("Digest(in hex format):: " + sb.toString());
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
