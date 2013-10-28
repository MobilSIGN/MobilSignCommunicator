package communicator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import sun.misc.BASE64Decoder;

public class Crypto {

    public static final String ALGORITHM = "RSA";
    private Cipher mCipher;
//    private PublicKey mPublicKey;
//    private PrivateKey mPrivateKey;
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

//    public Crypto(PublicKey publicKey) {
//        mPublicKey = publicKey;
//        try {
//            mCipher = Cipher.getInstance(ALGORHYTM);
//        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
//            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public Crypto(PrivateKey privateKey) {
//        mPrivateKey = privateKey;
//        try {
//            mCipher = Cipher.getInstance(ALGORHYTM);
//        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
//            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
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
        try {
            b = this.encrypt(text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            return new String(b, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
            if (b == null) {
                System.out.println("1 null");
            }
            b = this.decrypt(b);
            if (b == null) {
                System.out.println("2 null");
            }
        } catch (Exception e) {
            System.err.println("Decrypt padol 2.");
        }
        try {
            return new String(b, "UTF-8");
//            return decodeToBASE64(b);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            String s = "hahaha";
            System.out.println(s);
            byte[] b = s.getBytes("UTF-8");
            System.out.println(b);
            try {
                String str = new String(b, "UTF-8");
                System.out.println(str);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
