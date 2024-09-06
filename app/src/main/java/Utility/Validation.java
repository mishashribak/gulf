package Utility;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.TextView;

import com.app.khaleeji.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Constants.Bundle_Identifier;

public class Validation {


    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static boolean isValidPhone(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return Patterns.PHONE.matcher(target).matches();
        }
    }


    public final static boolean isValidUsername(String str) {
        try {
            Pattern pattern = Pattern.compile("^[A-Za-z0-9_.ء-ي]+$");
            return pattern.matcher(str).matches();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public final static boolean isValidFullname(String str) {
        try {
            Pattern pattern = Pattern.compile("^[A-Za-zء-ي\\s]+$");
            return pattern.matcher(str).matches();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String isEditTextContainEmail(EditText argEditText) {

        try {
            Pattern pattern = Pattern.compile("'/(?<!\\w)#\\S+/'");
            Matcher matcher = pattern.matcher(argEditText.getText());

            return matcher.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static void setTags(final FragmentManager manager,EditText pTextView, String pTagString, final Activity activity) {
        SpannableString string = new SpannableString(pTagString);

        String s = pTagString; // note two spaces between 'a' and 'test'
        String[] a = s.split(" ");

        int start = -1;

        for (int i = 0; i < pTagString.length(); i++) {

            if (pTagString.charAt(i) == '#'||pTagString.charAt(i)=='h') {
                start = i;
            }

            else if (pTagString.charAt(i) == ' ' || (i == pTagString.length() - 1 && start != -1)) {
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++; // case for if hash is last word and there is no
                        // space after word
                    }

                    final String tag = pTagString.substring(start, i);


                    final boolean b = URLUtil.isValidUrl(tag);


                    if (b==true||tag.charAt(0)=='#') {
                        string.setSpan(new ClickableSpan() {

                            @Override
                            public void onClick(View widget) {
                                Log.d("Hash", String.format("Clicked %s!", tag));

                                if(b){
                                    Intent intent=new Intent(Intent.ACTION_VIEW);
                                    if(tag.contains("http://")||tag.contains("https://")) {
                                        intent.setData(Uri.parse(tag));
                                        activity.startActivity(intent);
                                    }else{
                                        intent.setData(Uri.parse("https://" +tag));
                                    }
                                }else{
                                   /* Intent intent=new Intent(Intent.ACTION_VIEW);
                                    intent.setFetchHotspotAndFrndsDetailData(Uri.parse("https://www.google.com"));
                                    activity.startActivity(intent);
                                    */
                                     String tags = tag.replace("#","");
//                                     Fragment_Process.replaceFragment(manager,MeetupTabFragment.newInstance(Bundle_Identifier.HASHTAG_SEARCH,tags), activity, R.id.framelayout_main);
                                 //   EventBus.getDefault().post(new onTab_Visiblity(false));
                                      //  Fragment_Process.replaceFragmentusingChildFragment(SearchTabFragments.newInstance(0),activity, R.id.fl_child_conatiner, ((AppCompatActivity)activity).getChildFragmentManager());
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                // link color
                                ds.setColor(Color.parseColor("#33b5e5"));
                                ds.setUnderlineText(false);
                            }
                        }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        start = -1;
                    }
                }
            }
        }

        pTextView.setMovementMethod(LinkMovementMethod.getInstance());
        pTextView.setText(string);
    }


    public static void setTags(final FragmentManager manager,TextView pTextView, String pTagString, final Activity activity) {
        SpannableString string = new SpannableString(pTagString);

        String s = pTagString; // note two spaces between 'a' and 'test'
        String[] a = s.split(" ");

        int start = -1;

        for (int i = 0; i < pTagString.length(); i++) {

            if (pTagString.charAt(i) == '#'||pTagString.charAt(i)=='h') {
                start = i;
            }

            else if (pTagString.charAt(i) == ' ' || (i == pTagString.length() - 1 && start != -1)) {
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++; // case for if hash is last word and there is no
                        // space after word
                    }

                    final String tag = pTagString.substring(start, i);


                    final boolean b = URLUtil.isValidUrl(tag);

                    if (b==true||tag.charAt(0)=='#') {
                        string.setSpan(new ClickableSpan() {

                            @Override
                            public void onClick(View widget) {
                                Log.d("Hash", String.format("Clicked %s!", tag));

                                if(b){
                                    Intent intent=new Intent(Intent.ACTION_VIEW);
                                    if(tag.contains("http://")||tag.contains("https://")) {
                                        intent.setData(Uri.parse(tag));
                                        activity.startActivity(intent);
                                    }else{
                                        intent.setData(Uri.parse("https://" +tag));
                                    }
                                }else{
                                   /* Intent intent=new Intent(Intent.ACTION_VIEW);
                                    intent.setFetchHotspotAndFrndsDetailData(Uri.parse("https://www.google.com"));
                                    activity.startActivity(intent);
                                    */
                                    String tags = tag.replace("#","");
//                                    Fragment_Process.replaceFragment(manager,MeetupTabFragment.newInstance(Bundle_Identifier.HASHTAG_SEARCH,tags), activity, R.id.framelayout_main);
                                    //EventBus.getDefault().post(new onTab_Visiblity(false));
                                    //  Fragment_Process.replaceFragmentusingChildFragment(SearchTabFragments.newInstance(0),activity, R.id.fl_child_conatiner, ((AppCompatActivity)activity).getChildFragmentManager());


                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                // link color
                                ds.setColor(Color.parseColor("#0000ff"));
                                ds.setUnderlineText(false);
                            }
                        }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        start = -1;
                    }
                }
            }
        }

        pTextView.setMovementMethod(LinkMovementMethod.getInstance());
        pTextView.setText(string);
    }

    public static List<String> extractUrls(String text)
    {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }


     public static  String spannableString(String s){
         String[] a = s.split(" ");
         for(int i=0;i<a.length;i++){
             if(a[i].length()>3){

                 if(a[i].trim().substring(0,3).equalsIgnoreCase("www")){
                     String url  = "https://"+a[i];

                     boolean b =    URLUtil.isValidUrl(url) ;
                     if(b){
                         a[i]=url;
                     }

                 }
             }

         }

         StringBuilder sb = new StringBuilder();
         for(String s1 : a)
             sb.append(s1).append(" ");
         String  bio =   sb.toString().trim();
         return bio;
     }

}
