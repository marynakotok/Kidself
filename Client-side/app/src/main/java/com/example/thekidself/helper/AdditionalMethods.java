package com.example.thekidself.helper;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thekidself.R;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AdditionalMethods{

    Boolean setTypePassword;
    public static final String AES = "AES";
    //static char[] SYMBOLS = (new String("^$*.[]{}()?-\"!@#%&/\\,><':;|_~`")).toCharArray();
    static char[] LOWERCASE = (new String("abcdefghijklmnopqrstuvwxyz")).toCharArray();
    static char[] UPPERCASE = (new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ")).toCharArray();
    static char[] NUMBERS = (new String("0123456789")).toCharArray();
    //static char[] ALL_CHARS = (new String("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789^$*.[]{}()?-\"!@#%&/\\,><':;|_~`")).toCharArray();
    static Random rand = new SecureRandom();


    public String Encryption(String dataToEncrypt, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] byteKey = hexStringToByteArray(key);
        SecretKeySpec sks = new SecretKeySpec(byteKey, AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
        byte[] encrypted = cipher.doFinal(dataToEncrypt.getBytes());
        return byteArrayToHexString(encrypted);
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) { sb.append('0'); }
            sb.append(Integer.toHexString(v));
        }return sb.toString().toUpperCase();
    }

    public String Decryption(String dataToDecrypt, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] byteKey = hexStringToByteArray(key);
        SecretKeySpec sks = new SecretKeySpec(byteKey, AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, sks);
        byte[] decrypted = cipher.doFinal(hexStringToByteArray(dataToDecrypt));
        return new String(decrypted);
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v; }return b;
    }

    public String GeneratePassword(int length)
    {
        assert length >= 4;
        char[] password = new char[length];

        password[0] = LOWERCASE[rand.nextInt(LOWERCASE.length)];
        password[1] = UPPERCASE[rand.nextInt(UPPERCASE.length)];
        password[2] = NUMBERS[rand.nextInt(NUMBERS.length)];
        password[3] = NUMBERS[rand.nextInt(NUMBERS.length)];

        for (int i = 4; i < length; i++) {
            password[i] = NUMBERS[rand.nextInt(NUMBERS.length)];
        }

        for (int i = 0; i < password.length; i++) {
            int randomPosition = rand.nextInt(password.length);
            char temp = password[i];
            password[i] = password[randomPosition];
            password[randomPosition] = temp;
        }

        return new String(password);
    }

    public boolean EmailValidator(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public String DBValidator(String db) throws ParseException {

        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Date dateDB = sdf.parse(db);
        Date dateCurrent = sdf.parse(date);
        System.out.println(dateDB);
        System.out.println(dateCurrent);

        Calendar calDB = Calendar.getInstance();
        Calendar calCurrent = Calendar.getInstance();
        calDB.setTime(dateDB);
        calCurrent.setTime(dateCurrent);

        if (calDB.after(calCurrent)) {
            result = "after";
        }

        if (calDB.before(calCurrent)) {
            result =  "before";
        }

        if (calDB.equals(calCurrent)) {
            result = "equal";
        }
        return result;
    }

    public boolean PasswordValidator(String password) {
        return password.length() >=6;
    }

    public int calculateAge(Date birthDate)
    {
        int years = 0;
        int months = 0;
        int days = 0;

        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());

        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);

        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;

        months = currMonth - birthMonth;
        if (months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            years--;
            months = 11;
        }

        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        }
        else
        {
            days = 0;
            if (months == 12)
            {
                years++;
                months = 0;
            }
        }

        return years;
    }

    public void setLinks(TextView view, final Class GoToClass, final int GoToColor, int startPoint, int endPoint)
    {

        String textClickable = view.getText().toString();
        SpannableString stringClickable = new SpannableString(textClickable);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                try {
                    Intent intent = new Intent(widget.getContext(), GoToClass);
                    widget.getContext().startActivity(intent);
                } catch (Exception except) {
                    String TAG = "No class";
                    Log.e(TAG,"Ooops activity changing problem " + except.getMessage());
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(GoToColor);
                ds.setUnderlineText(true);
            }
        };

        stringClickable.setSpan(clickableSpan, startPoint,endPoint, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(stringClickable);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void HideUnhidePassword(final ImageView Eye, final EditText Password)
    { setTypePassword = false;
    Eye.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View view) {
            if (setTypePassword == false) {
                setTypePassword = true; //true - opened, false - closed
                Password.setTransformationMethod(null);
                if (Password.getText().length() > 0)
                    Password.setSelection(Password.getText().length());
                Eye.setBackground(view.getResources().getDrawable(R.drawable.eye_hide));
                } else
                    { setTypePassword = false;
                    Password.setTransformationMethod(new PasswordTransformationMethod());
                    if (Password.getText().length()>0)
                        Password.setSelection(Password.getText().length());
                    Eye.setBackground(view.getResources().getDrawable(R.drawable.eye_view)); }}});}
}



