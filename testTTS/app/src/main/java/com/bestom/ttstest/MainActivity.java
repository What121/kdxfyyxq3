package com.bestom.ttstest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TabHost;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Activity mActivity;
    private Context mContext;

    private FileReader mFileReader;
    private TextToSpeech mTextToSpeech;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity=this;
        mContext= this;

        initFile();
        try {
            mFileReader=new FileReader(new File(mContext.getDataDir().getPath()+ File.separator+"zhet.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initFile(){
        //配置文件
        if(!AssetFileUtil.getInstance(mContext).checkFilesExist(new String[]{"testvoice.txt","zhet.txt"} ,0)){
            (AssetFileUtil.getInstance(mContext)).copyFilesFromAssets(new String[]{"testvoice.txt","zhet.txt"},0);//copies files from assests to data file
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //region TextToSpeech code
                mTextToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onInit(int i) {
                        if (i == TextToSpeech.SUCCESS) {

                            for ( Locale locale :  mTextToSpeech.getAvailableLanguages()){
                                Log.d(TAG, "locale: "+locale.getDisplayLanguage());
                            }

                            for (TextToSpeech.EngineInfo engineInfo : mTextToSpeech.getEngines()){
                                Log.d(TAG, "EngineInfo: "+engineInfo.toString());
                            }

                            mTextToSpeech.setPitch(100);

                            mTextToSpeech.speak("测试语音 谭贵你这个大傻子。测试第一遍。", TextToSpeech.QUEUE_ADD, null);
                            mTextToSpeech.speak("测试语音 谭贵你这个大傻子。测试第二遍。", TextToSpeech.QUEUE_ADD, null);
                            mTextToSpeech.speak("测试语音 谭贵你这个大傻子。测试第三遍。", TextToSpeech.QUEUE_ADD, null);
                            mTextToSpeech.speak("测试 阅读文件。正在打开文件，请稍等。", TextToSpeech.QUEUE_ADD, null);

                            char[] buf=new char[1024];
                            //read(char [])返回读到的字符个数
                            int num=0;
                            while(true) //读取文件并把它存入buf中，用num返回读到字符的个数，一直读到结尾
                            {
                                try {
                                    if ((num=mFileReader.read(buf))!=-1){
                                        System.out.print((new String(buf,0,num)));//字符数组里仍有空白没有读入的位置，所以不要了
                                        //new String(字符串，开始位置，结尾位置)
                                        mTextToSpeech.speak(new String(buf,0,num), TextToSpeech.QUEUE_ADD, null);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    break;
                                }
                            }
                        }
                    }
                },"com.iflytek.speechcloud");

                //endregion
            }
        }).start();

    }


    @Override
    protected void onStop() {
        super.onStop();
        mTextToSpeech.stop();
        mTextToSpeech.shutdown();
    }
}
