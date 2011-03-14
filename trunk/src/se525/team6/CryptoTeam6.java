package se525.team6;
/* Class for general use of encrypting and decrypting our data
 * a. gojdas
*/
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class CryptoTeam6 {
	private static final String cryptoAlgFull = "AES/CBC/PKCS5Padding";
	private static String _Key = "d7d373be095b26ccc6b6cb1d910d631d";

	protected static String Encrypt(String PlainText) {
	   String encryptedText = "";
		SecretKey secKey = stringToKey(_Key);
      try {
         // Encrypt
         Cipher cipherE = Cipher.getInstance(cryptoAlgFull);
         cipherE.init(Cipher.ENCRYPT_MODE, secKey);
         byte[] iv = new byte[cipherE.getBlockSize()];

         iv = cipherE.getIV();
         byte[] plainText = PlainText.getBytes("utf-8");
         byte[] cipherText = cipherE.doFinal(plainText);
         ByteBuffer target = ByteBuffer.wrap(new byte[iv.length + cipherText.length]);
         target.put(iv);
         target.put(cipherText);

         encryptedText = Base64.encodeToString(target.array(), Base64.NO_WRAP);
      }
      catch (InvalidKeyException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (UnsupportedEncodingException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (IllegalBlockSizeException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (BadPaddingException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (NoSuchAlgorithmException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (NoSuchPaddingException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return encryptedText;
	}
	
   protected static String Decrypt(String CipherText) {
     String decryptedText = "";
     SecretKey secKey = stringToKey(_Key);
     try {
        // decrypt
        Cipher cipherD = Cipher.getInstance(cryptoAlgFull);
        int blockSize = cipherD.getBlockSize();
        byte[] iv = new byte[blockSize];

        byte[] iv_ciph = Base64.decode(CipherText, Base64.NO_WRAP);
        System.arraycopy(iv_ciph, 0, iv, 0, blockSize);
        
        int ciphLength = iv_ciph.length - blockSize;
        byte[] ciph = new byte[ciphLength];
        System.arraycopy(iv_ciph, blockSize, ciph, 0, ciphLength);

        IvParameterSpec ivSpec = new IvParameterSpec (iv);
        cipherD.init (Cipher.DECRYPT_MODE, secKey, ivSpec);
        
        byte[] plainTextDec = cipherD.doFinal (ciph);
        decryptedText = new String(plainTextDec);        
     }
     catch (InvalidKeyException e) {
        e.printStackTrace();
     }
     catch (IllegalBlockSizeException e) {
        e.printStackTrace();
     }
     catch (BadPaddingException e) {
        e.printStackTrace();
     }
     catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
     }
     catch (NoSuchPaddingException e) {
        e.printStackTrace();
     }
     catch (InvalidAlgorithmParameterException e) {
         e.printStackTrace();
      }

     return decryptedText;
   }
		
	//generate the secret key by calling this method with a string
	private static SecretKey stringToKey(String secKeyString) {
		final String cryptoAlg = "AES";
		byte[] secKeyData = new byte[secKeyString.length() / 2];
		for (int i = 0; i < secKeyData.length; i++) {
			secKeyData[i] = (byte) Integer.parseInt(
					secKeyString.substring(2 * i, 2 * i + 1), 16);
		}
		SecretKeySpec secKeySpec = new SecretKeySpec(secKeyData, cryptoAlg);
		return secKeySpec;
	}	
}
