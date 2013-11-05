package communicator;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.binary.Base64;

public class Crypto {

    public static final String ALGORITHM = "RSA";
    private Cipher mCipher;
    private Key mKey;

    // Parameter key je bud PublicKey alebo PrivateKey.
    public Crypto(Key key) {
        mKey = key;
        try {
            mCipher = Cipher.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] encrypt(byte[] text) {
        byte[] cipherText = null;
        try {
            mCipher.init(Cipher.ENCRYPT_MODE, mKey);
            cipherText = mCipher.doFinal(text);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            System.err.println("Encrypt padol.");
        }
        return cipherText;
    }

    public String encrypt(String text) {
        byte[] b = null;
        List<byte[]> list = null;
        List<byte[]> encryptedList = null;
        try {
            list = divideByteArray(text.getBytes("UTF-8"), 245);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
        encryptedList = new ArrayList<>();
        for (byte[] iter : list) {
            encryptedList.add(this.encrypt(iter));
        }
        //        try {
        //            return new String(b, "UTF-8");
        //        } catch (UnsupportedEncodingException ex) {
        //            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        //        }
        return encodeByteToBase64String(splitByteArray(encryptedList, 245));
    }

    // todo
    // delenie na 245 bajtov
    // urobit decode stringu na bajty a naopak
    public byte[] decrypt(byte[] text) {
        byte[] dectyptedText = null;
        try {
            mCipher.init(Cipher.DECRYPT_MODE, mKey);
            dectyptedText = mCipher.doFinal(text);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            System.err.println("Decrypt padol 1.");
        }
        return dectyptedText;
    }

    public String decrypt(String text) {
        byte[] b = null;
        try {
            b = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<byte[]> list = divideByteArray(b, 245);
        List<byte[]> list2 = new ArrayList<>();
        for (byte[] iter : list) {
            list2.add(this.decrypt(iter));
        }
        byte[] vysl = splitByteArray(list2, 245);
        return encodeByteToBase64String(vysl);
    }

    public static void main(String[] args) {
        byte b[] = new byte[246];
        for (int i = 0; i < 246; i++) {
            b[i] = 1;
        }
        String str = "952t9852udifg dfsi fius dfius ffsfsfsdo sofj sodfjsj fsiojdf sof osindf oisndfiosn fosnf sndfoisdnfo insfodi nsofn oisnf iosnfiosnf iosnfoidsnfoisnfoisnfoisnfoisn foisnfiosn foisn fosi nfos noinsfoi snoifsnonsf onsfo nsoidfn sionfdiosnsfoindosi n";
        System.out.println(str);
//        System.out.println(str);
////        for (int i = 0; i < 244; i++) {
////            str += new char(i);
////        }
//        List<byte[]> l = new ArrayList<>();
//        byte[] bytes = str.getBytes();
//        for (byte iter : bytes) {
//            System.out.print(iter + ", ");
//        }
//        l = divideByteArray(bytes, 245);
//        System.out.println("L ma prvkov: " + l.size());
//        byte[] bytes2 = splitByteArray(l, 245);
//        for (byte iter : bytes2) {
//            System.out.print(iter + ", ");
//        }
//        System.out.println("");
//        System.out.println(new String(bytes2));
//       try {
//            String s = "hahaha";
//            System.out.println(s);
//            byte[] b = s.getBytes("UTF-8");
//            System.out.println(b);
//            try {
//                String str = new String(b, "UTF-8");
//                System.out.println(str);
//            } catch (UnsupportedEncodingException ex) {
//                Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    private static byte[] splitByteArray(List<byte[]> l, int pieceSize) {
        int size = 0;
        for (byte[] iter : l) {
            size += iter.length;
        }
        byte[] temp = new byte[size];
        for (int i = 0; i < l.size(); i++) {
            for (int j = 0; j < l.get(i).length; j++) {
                temp[i * pieceSize + j] = l.get(i)[j];
            }
//            System.arraycopy(l.get(i), 0, temp, i * pieceSize, l.get(i).length);
        }
        return temp;
    }

    private static List<byte[]> divideByteArray(byte[] b, int pieceSize) {
        List<byte[]> list = new ArrayList<>();
        byte[] temp = null;
        boolean zacalSom = false;
        System.out.println("mATH: " + Math.ceil(b.length / (pieceSize * 1.0)));
        System.out.println("Dlzka pola je " + b.length);
        for (int i = 0; i < Math.ceil(b.length / (pieceSize * 1.0)); i++) {
            System.out.println("###");
            if (i * pieceSize + pieceSize < b.length) {
                temp = new byte[pieceSize];
                System.arraycopy(b, i * pieceSize, temp, 0, pieceSize);
                System.out.println("IDEM 1 OD " + i * pieceSize + " kolko: " + pieceSize);
            } else {
                if (b.length <= pieceSize) {
                    temp = new byte[b.length];
                    System.arraycopy(b, i * pieceSize, temp, 0, b.length);
                    System.out.println("IDEM 2 OD " + i * pieceSize + " kolko " + b.length);
                } else {
                    temp = new byte[b.length - i * pieceSize];
                    System.arraycopy(b, i * pieceSize, temp, 0, b.length - i * pieceSize);
                    System.out.println("IDEM 3 OD " + i * pieceSize + " kolko " + (b.length - i * pieceSize));
                }
            }
            list.add(temp);
        }

//        for (int i = 0; i < b.length; i++) {
//            if (zacalSom && (i % pieceSize == 0 || i == b.length- 1)) {
//                list.add(temp);
//                
//            } else {
//                temp[i % pieceSize] = b[i];
//            }
//           zacalSom = true; 
//        }
        return list;
    }

    /**
     * dekoduje base64 retazec na subor a ten vrati v podobe pola bajtov
     *
     * @param string retazet reprezentujuci subor
     * @return rozkodovany subor vo forme pola bajtov
     */
    public byte[] decodeBase64StringToByte(String string) {
        return Base64.decodeBase64(string);
    }

    public String encodeByteToBase64String(byte[] b) {
        return Base64.encodeBase64String(b);
    }
}
