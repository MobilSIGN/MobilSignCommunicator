package communicator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class Crypto {

    public static final String ALGORITHM = "RSA/ECB/NOPADDING";
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

    public String encrypt(String text) {
        try {
            byte[] bytes = text.getBytes("UTF-8");
            System.out.println(Hex.encodeHexString(bytes));
            
            return Base64.encodeBase64String(this.encryptPom(bytes));
            //return Base64.encodeBase64String(text.getBytes("UTF-8"));
            
//        List<byte[]> chunks = Util.divideByteArray(text.getBytes(), 245);
//        List<String> zasifrovaneChunks = new ArrayList<>();
//        for (byte[] iter : chunks) {
//            zasifrovaneChunks.add(Util.encodeByteToBase64String(this.encryptPom(iter)));
//        }
//        StringBuilder builder = new StringBuilder();
//        for (String iter : zasifrovaneChunks) {
//            builder.append(iter);
//        }
//        return builder.toString();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String decrypt(String text) {
        try {
            return new String(this.decryptPom(Base64.decodeBase64(text)), "UTF-8");
            
//        List<byte[]> chunks = Util.divideByteArray(text.getBytes(), 344);
//        List<byte[]> rozsifrovaneChunks = new ArrayList<>();
//        for (byte[] iter : chunks) {
//            rozsifrovaneChunks.add(this.decryptPom(Util.decodeBase64StringToByte(new String(iter))));
//        }
//        StringBuilder builder = new StringBuilder();
//        for (byte[] iter : rozsifrovaneChunks) {
//            builder.append(new String(iter));
//        }
//        return builder.toString();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String encryptFile(byte[] subor) {
        List<byte[]> chunks = Util.divideByteArray(subor, 245);
        List<String> zasifrovaneChunks = new ArrayList<>();
        for (byte[] iter : chunks) {
            zasifrovaneChunks.add(Util.encodeByteToBase64String(this.encryptPom(iter)));
        }
        StringBuilder builder = new StringBuilder();
        for (String iter : zasifrovaneChunks) {
            builder.append(iter);
        }
        return builder.toString();
    }

    public byte[] decryptFile(String subor) {
        List<byte[]> chunks = Util.divideByteArray(subor.getBytes(), 344);
        List<byte[]> rozsifrovaneChunks = new ArrayList<>();
        for (byte[] iter : chunks) {
            rozsifrovaneChunks.add(this.decryptPom(Util.decodeBase64StringToByte(new String(iter))));
        }
        return Util.splitByteArray(rozsifrovaneChunks, 245);
    }

    /**
     * Encrypt the plain text using public key.
     *
     * @param text : original plain text
     * @param key :The public key
     * @return Encrypted text
     * @throws java.lang.Exception
     */
    public byte[] encryptPom(byte[] text) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, mKey);
            cipherText = cipher.doFinal(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }
    
    public static byte[] encrypt(byte[] data, Key key) {
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }
    
    public static byte[] decrypt(byte[] data, Key key) {
        byte[] dectyptedText = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = cipher.doFinal(data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dectyptedText;
    }
  

    /**
     * Decrypt text using private key.
     *
     * @param text :encrypted text
     * @param key :The private key
     * @return plain text
     * @throws java.lang.Exception
     */
    public byte[] decryptPom(byte[] text) {
        byte[] dectyptedText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, mKey);
            dectyptedText = cipher.doFinal(text);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dectyptedText;
    }

    public static void main(String[] args) {
        byte b[] = new byte[246];
        for (int i = 0; i < 246; i++) {
            b[i] = 1;
        }

        PrivateKey applicationKey = null;
        PublicKey mobileKey = null;
        try {
            KeyPairGenerator generatorRSA = KeyPairGenerator.getInstance("RSA"); // vytvori instanciu generatora RSA klucov
            generatorRSA.initialize(2048, new SecureRandom()); // inicializuje generator 2048 bitovych RSA klucov
            KeyPair keyRSA = generatorRSA.generateKeyPair(); // vygeneruje klucovi par
            applicationKey = keyRSA.getPrivate(); // kluc desktopovej aplikacie je sukromny kluc z klucoveho paru
            mobileKey = (RSAPublicKey) keyRSA.getPublic(); // vrati verejny kluc type RSAPublicKey, lebo z neho mozem dostat modulus 
        } catch (Exception e) {
            e.printStackTrace();
        }


        Crypto c1 = new Crypto(applicationKey);
        Crypto c2 = new Crypto(mobileKey);

        //vyberiem si subor a zasifrujem ho a potom spatne rozsifrujem
        File subor = new File("/home/peter/Downloads/ahoj.txt");
        byte[] zasifSuborPole = Util.fileToByteArray(subor);
        System.out.println("Zacinam sifrovat");
        String zasifSubor = c1.encryptFile(zasifSuborPole);
        System.out.println("koncim sifrovanie");

        System.out.println("zacinam desifrovanie");
        byte[] rozsifSuborPole = c2.decryptFile(zasifSubor);
        System.out.println("koncim desifrovanie");

        System.out.println(zasifSuborPole.length);
        System.out.println(rozsifSuborPole.length);

        //rozsifrovany subor zapisem na disk
        try {
            FileOutputStream fos = new FileOutputStream("/home/peter/Desktop/ahoj.txt");
            fos.write(rozsifSuborPole);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.exit(0);



        long now = System.currentTimeMillis();
        String zaklad = "dapibus. Sed nec posuere arcu. Nulla facilisi. Morbi porttitor pulvinar nisl, vitae blandit elit ultricies eget. Integer molestie viverra metus, quis sagittis lorem dictum et. Vivamus consectetur ipsum non tortor vehicula, id suscipit libero convallis. Vestibulum vulputate quam velit, vitae tincidunt lorem mattis vitae. Phasellus suscipit mi et massa posuere vestibulum. Morbi mollis sed enim id sodales. In lobortis ligula luctus neque molestie venenatis. Quisque id neque ligula. Nunc convallis purus vulputate tincidunt elementum.\n"
                + "\n"
                + "Suspendisse vitae sapien bibendum, mattis lectus sit amet, tincidunt nulla. Phasellus ut urna consectetur, vestibulum eros eu, lobortis turpis. Sed blandit ante sit amet faucibus dapibus. Fusce adipiscing, ipsum pretium gravida vulputate, mauris dolor varius metus, sit amet interdum ipsum ipsum non lacus. Vestibulum eleifend molestie sapien, ut feugiat libero viverra vel. Nulla eget gravida lectus. Nam gravida lobortis est, quis interdum odio vehicula sed.\n"
                + "\n"
                + "Vivamus sagittis nunc eu suscipit sodales. Vivamus faucibus, sapien ut feugiat malesuada, nisi libero viverra lorem, sit amet consectetur lectus nisl non velit. Nam quis aliquam elit, ac suscipit diam. Proin lectus leo, tempor a eleifend nec, porta ultrices nibh. Etiam gravida diam in massa imperdiet scelerisque. Duis eu aliquam elit. Morbi tempus ligula eget luctus varius. Duis luctus dolor elit, eget gravida sem rhoncus sit amet. Donec gravida libero eget porttitor imperdiet. Aliquam sit amet placerat mauris. Fusce auctor cursus semper. Praesent et convallis ipsum. Maecenas suscipit tortor felis, a dictum risus euismod a.\n"
                + "\n"
                + "Proin tortor mauris, fermentum eu nisi quis, volutpat ultrices augue. Nulla gravida pretium vehicula. Donec feugiat est ac enim venenatis tristique. Praesent eu lobortis tellus, vitae adipiscing enim. Duis posuere ultrices purus, et facilisis leo aliquam sit amet. Curabitur suscipit et est consectetur posuere. Morbi ac orci ante. Duis aliquet quam vel libero luctus, in sollicitudin metus varius. Ut felis nunc, ultrices vel ultrices quis, lacinia sit amet lacus. Nunc risus metus, tincidunt id egestas eget, pellentesque ut massa. Ut faucibus lorem nec urna sagittis tincidunt. Quisque quis luctus justo. In velit purus, pulvinar sed vulputate at, ornare pellentesque neque. Nunc aliquet turpis eget metus ultrices, varius egestas leo gravida. Pellentesque nunc urna, vestibulum vitae mollis ut, volutpat quis nisl. Aenean eget lacus lectus.\n"
                + "\n"
                + "Etiam at lorem imperdiet, vulputate lorem id, dignissim erat. Mauris sagittis sagittis placerat. Etiam pellentesque, justo eget rhoncus elementum, orci mauris semper nibh, quis congue diam neque vel lacus. Duis lobortis sed massa id iaculis. Sed feugiat laoreet lacus, commodo tincidunt eros mollis id. Integer eget mi consectetur, lobortis orci sed, consequat orci. Aliquam erat volutpat. In odio justo, rutrum eget elementum non, venenatis ut diam. Suspendisse vestibulum eu lectus eu ultrices. Ut sit amet libero non diam lacinia mollis sit amet rutrum dolor. Suspendisse dolor nulla, interdum eu enim a, adipiscing luctus felis. Vivamus aliquet ipsum ultrices ante aliquet, a egestas eros tincidunt. Sed hendrerit dictum tellus, in convallis orci porttitor sed. Quisque et tempus tellus. Phasellus quis arcu gravida, imperdiet sem eu, porttitor dui. Aenean nec tellus dignissim, lacinia nulla et, vestibulum justo.\n"
                + "\n"
                + "Aenean non faucibus mauris, in ornare lorem. Suspendisse porta tincidunt mattis. Fusce a adipiscing libero, vel facilisis orci. Aliquam rutrum sem in ante auctor, ut accumsan nisi malesuada. Fusce tempor leo purus, lacinia feugiat nisi viverra sit amet. Phasellus et luctus dui, nec volutpat tellus. Maecenas a mattis nunc. Vivamus sodales consectetur congue. Sed hendrerit pulvinar erat vel tempor. Ut non molestie justo. Maecenas cursus tellus eu mi semper elementum sed non augue. Duis eros justo, placerat et luctus ultricies, dapibus in risus. Phasellus rutrum molestie lectus, vitae scelerisque neque suscipit vel. Fusce turpis mauris, consectetur a massa at, interdum porttitor urna.\n"
                + "\n"
                + "Integer nec fermentum purus. Curabitur egestas dignissim quam, nec faucibus sem lacinia eu. Duis in gravida velit. Nam cursus enim sed metus tristique tincidunt. In vel congue lectus, nec adipiscing tellus. Mauris sed tincidunt orci. Sed eleifend consequat ipsum, in aliquam lacus posuere quis. In non lectus et mauris varius semper ut quis purus.\n"
                + "\n"
                + "Nam imperdiet tortor ac semper convallis. Morbi id tincidunt lectus, ut pharetra ipsum. Pellentesque rutrum ultricies convallis. Nunc interdum dui purus. Mauris urna augue, faucibus vitae metus non, hendrerit pulvinar erat. Fusce sodales, felis nec malesuada dictum, justo augue mollis lectus, ut porttitor urna orci id odio. Ut interdum orci nisi, ut convallis quam venenatis ac. Duis euismod varius felis ut lobortis. Integer elementum diam et cursus porttitor. Pellentesque leo mi, adipiscing id ornare in, porttitor ac elit. Mauris sit amet sem in nunc aliquam dapibus. In id nunc metus. Aliquam ac fermentum orci, ut dapibus ante.\n"
                + "\n"
                + "Nunc erat dui, porta in tortor at, pellentesque ullamcorper felis. Fusce dignissim dapibus enim id feugiat. Praesent turpis massa, dapibus eu mattis ac, sollicitudin non urna. Quisque pretium lobortis dui vel vulputate. Duis ultrices tortor vel semper sodales. Etiam quis mi non sapien mollis porttitor ac vel erat. Vestibulum eu porta risus.\n"
                + "\n"
                + "Donec ullamcorper faucibus purus non volutpat. Duis iaculis dui eu risus iaculis, sed posuere risus facilisis. Nam in elementum tortor, sit amet dictum nunc. Mauris tempor sem eget mauris ornare tincidunt sit amet sit amet sapien. Phasellus ornare, lectus ac placerat blandit, ante lacus malesuada mi, ac venenatis quam velit vel nunc. Cras scelerisque sapien tellus. In sit amet faucibus metus, sit amet viverra tellus. Nulla hendrerit sollicitudin vulputate. Vestibulum vulputate urna et blandit hendrerit. Donec iaculis, turpis sed pretium vehicula, dolor tellus cursus orci, vel convallis dui leo in libero. Curabitur nunc mauris, placerat sit amet velit sed, consequat mollis eros. Donec elit elit, dictum nec suscipit eu, malesuada non mauris. Donec cursus velit eget ornare sodales.\n"
                + "\n"
                + "Cras interdum, nunc ut ullamcorper sagittis, nisl est laoreet tortor, ac pharetra est eros vel augue. Praesent bibendum, quam vitae hendrerit vehicula, dui nisi adipiscing purus, sed ultricies elit felis nec justo. Praesent tincidunt eu mi pretium gravida. Nulla metus lacus, elementum a mollis non, convallis in diam. Integer consectetur turpis et vestibulum convallis. Pellentesque et semper nibh. Donec eu dignissim quam. Praesent gravida lectus eget nunc lacinia scelerisque. Suspendisse lacinia massa vel nisl blandit, quis consequat massa fringilla.\n"
                + "\n"
                + "Cras eget leo vitae augue condimentum lobortis ut in libero. Proin laoreet sem a arcu malesuada pellentesque. Etiam commodo, turpis ac ultrices cursus, libero risus accumsan ipsum, sed sodales nisi arcu et arcu. Praesent at enim pharetra, aliquet mi quis, pharetra odio. Cras ut elit magna. Sed nec mauris commodo orci sodales condimentum a sed libero. Aenean quis tempor diam, eget mattis ante. Proin at sollicitudin augue. Nunc vel eros dapibus, luctus ligula nec, facilisis turpis. Vivamus vel nisl nec lorem consequat porttitor. Suspendisse cursus risus vitae est hendrerit, non placerat turpis iaculis. Praesent varius non purus ut lacinia. Vestibulum bibendum et metus vitae tincidunt.\n"
                + "\n"
                + "Nam ligula velit, lacinia vitae convallis et, adipiscing eget neque. Pellentesque ornare ut nulla a bibendum. Aliquam quam sapien, malesuada eget dui nec, dignissim hendrerit augue. Donec venenatis lectus tincidunt augue posuere tincidunt. Pellentesque sodales tincidunt sodales. Etiam pharetra arcu a tempus ornare. Vestibulum pretium, diam a eleifend iaculis, lectus velit congue neque, id pellentesque erat justo ac arcu. Integer facilisis diam eget tellus scelerisque euismod. Cras dapibus dui eu gravida porta. Morbi ac dolor eget ipsum blandit cursus. Etiam aliquet pulvinar velit, vel malesuada diam tempor vel. Nullam ac lorem sed arcu lacinia rutrum.\n"
                + "\n"
                + "Ut sit amet quam justo. Duis ullamcorper nisl vel quam scelerisque, tincidunt tristique ligula faucibus. Praesent tempus ligula accumsan lectus hendrerit condimentum. Nulla nisi sapien, vestibulum quis urna ac, egestas pulvinar massa. Nunc mollis erat sed libero iaculis tincidunt. Pellentesque ac eros nunc. Vestibulum at lectus et mi facilisis aliquam. Aenean in elit quam. Sed ac sapien in dolor tempor varius. Mauris tempor volutpat lectus, at laoreet purus mattis at. Integer accumsan augue orci, gravida rhoncus turpis accumsan ultricies. Nullam tortor ante, iaculis nec felis et, pharetra placerat risus. Donec dictum lorem sed velit viverra congue. Cras porta quis orci a vulputate. Donec vehicula faucibus lectus. Vestibulum elementum commodo eros sodales malesuada.\n"
                + "\n"
                + "Aliquam erat volutpat. Integer purus dolor, molestie vitae nibh euismod, tempus ultricies justo. Ut sollicitudin sagittis dui quis tincidunt. Aenean bibendum lectus leo, tempus volutpat quam tempus ut. Fusce urna odio, dapibus eu lacinia in, tempus at lectus. Vestibulum eu felis quis nunc dignissim aliquam. Mauris laoreet nulla magna, sit amet bibendum neque condimentum eu.\n"
                + "\n"
                + "Vestibulum id turpis vitae neque facilisis rhoncus. Integer ullamcorper, nisl in blandit condimentum, orci magna aliquet diam, a iaculis nisi lorem sit amet orci. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin nec egestas metus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Cras accumsan felis magna, id mattis ipsum euismod vitae. Sed tincidunt tellus eu faucibus tempor. Cras urna arcu, laoreet vel nisl eu, sollicitudin vestibulum dui. Duis vitae mi dui.\n"
                + "\n"
                + "Proin sollicitudin egestas ipsum placerat aliquet. Donec et ipsum congue, luctus tortor vel, rhoncus sapien. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean accumsan mauris ipsum. Donec ornare congue posuere. Morbi tincidunt justo ut ipsum congue, ut malesuada sem molestie. Duis pharetra nisl arcu, id iaculis erat molestie et. Vivamus a enim ut metus sagittis interdum in eu velit. Donec feugiat turpis vel est pharetra ullamcorper. Maecenas id lectus ut neque lacinia aliquam. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Cras sed pulvinar risus.\n"
                + "\n"
                + "Pellentesque ornare, orci a tempus convallis, orci mi porttitor nunc, nec malesuada turpis ipsum et nisl. Morbi a scelerisque eros, eu imperdiet mauris. Aliquam sed euismod velit. Sed lectus ante, vulputate quis porta sit amet, lobortis at turpis. Mauris quis suscipit dolor, vel lacinia lacus. Proin fermentum metus ante, id laoreet urna accumsan vel. Duis vehicula ligula in turpis ullamcorper, id malesuada elit malesuada. Donec nisl libero, eleifend nec gravida sit amet, tincidunt et ipsum. Nullam mollis fringilla euismod. Praesent id quam sit amet nisi pulvinar semper. Quisque vel neque cursus, eleifend eros ac, gravida odio. Nullam laoreet, nisi a consequat facilisis, nulla arcu vestibulum libero, in vulputate nunc elit et massa.\n"
                + "\n"
                + "Vivamus facilisis blandit arcu lacinia vestibulum. Nullam feugiat diam massa, ac interdum nisl tincidunt vitae. Donec sagittis vulputate tellus, in tincidunt dolor porta in. Nunc vulputate, felis vel tempus gravida, dolor est ultrices neque, quis pretium enim dolor ac quam. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum egestas interdum felis. Vivamus adipiscing libero ac arcu varius bibendum. Nulla auctor dolor sit amet arcu semper, fermentum tempus neque varius. Interdum et malesuada fames ac ante ipsum primis in faucibus. Curabitur ullamcorper ut nisi sit amet mattis. Aenean et auctor metus. Aliquam ullamcorper fermentum erat, a convallis elit placerat vel. Nam scelerisque nisl tortor. Donec facilisis elit odio, sed porta massa blandit ut. Mauris vel lorem dolor. Nam porttitor mi ac orci pretium, a aliquam lorem ornare.\n"
                + "\n"
                + "In hac habitasse platea dictumst. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent id diam tincidunt, pellentesque lacus molestie, varius lectus. Sed vitae purus faucibus, rhoncus augue vel, porta tortor. Quisque eu tellus fringilla, viverra nibh id, ultricies dolor. Ut quis venenatis diam, eget consequat dolor. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed ut lorem sit amet ligula vulputate placerat. Vestibulum euismod arcu arcu, in cursus mi bibendum a. Maecenas sodales vehicula odio, sodales congue libero aliquam consequat. Nulla ac dui ut massa dapibus posuere. Proin imperdiet dolor sit amet sem eleifend laoreet. Nullam tempus dolor lorem, quis auctor erat mollis sit amet. Ut mattis, metus non varius suscipit,";
        //  long now1 = System.currentTimeMillis();
        String zasif = c2.encrypt(zaklad);
        //  long now2 = System.currentTimeMillis();
        String rozsif = c1.decrypt(zasif);
        //  long now3 = System.currentTimeMillis();

        //  System.out.println( (now3-now1) );
        //  System.out.println( (now2-now1) );
        //  System.out.println( (now3-now2) );

    }
}
