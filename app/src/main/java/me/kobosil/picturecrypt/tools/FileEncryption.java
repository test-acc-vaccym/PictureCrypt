package me.kobosil.picturecrypt.tools;

import android.Manifest;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.util.TimingLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import me.kobosil.picturecrypt.MainActivity;

/**
 * Created by roman on 29.02.2016.
 */
public class FileEncryption {


    /*public void test(){
        File myDir = MainActivity.getMainActivity().getFilesDir();
        File file_ori = new File(Environment.getExternalStorageDirectory() + "/Pictures/Instagram/IMG_20160115_170328.jpg");
        File file_crypted = new File(myDir + "/IMAG0002.jpg.crypt");
        File file_decrypted = new File(myDir + "/IMAG0002_decrypted.jpg");

        String string = "hello world!7777";
        try {
          /*  FileOutputStream fos = new FileOutputStream(file_ori);
            fos.write(string.getBytes());
            fos.flush();
            fos.close();
            timings.addSplit("write file");*//*
            encrypt(file_ori, file_crypted, "test1");
            Log.d("fcrypt", "encrypted " + file_crypted.getAbsolutePath());
            decrypt(file_crypted, file_decrypted, "test1");
            Log.d("fcrypt", "decrypted " + file_decrypted.getAbsolutePath());
        }catch (Exception e){
e.printStackTrace();
        }
    }*/

    public static void encrypt(File in, File out,  byte[] password) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        // Here you read the cleartext.
        FileInputStream fis = new FileInputStream(in);
        // This stream write the encrypted text. This stream will be wrapped by another stream.
        FileOutputStream fos = new FileOutputStream(out);

        // Length is 16 byte
        // Careful when taking user input!!! http://stackoverflow.com/a/3452620/1188357
        SecretKeySpec sks = new SecretKeySpec(password, "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
    }

    public static void decrypt(File in, File out, byte[] password) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        FileInputStream fis = new FileInputStream(in);

        FileOutputStream fos = new FileOutputStream(out);
        SecretKeySpec sks = new SecretKeySpec(password, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }

    public static byte[] getHash(String password)  {
        byte[] key = (getDeviceID() + password).getBytes();
        try{
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
        }catch (Exception e){
            e.printStackTrace();
        }
        return key;
    }

    public static String getDeviceID(){
        return Settings.Secure.getString(MainActivity.getMainActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}