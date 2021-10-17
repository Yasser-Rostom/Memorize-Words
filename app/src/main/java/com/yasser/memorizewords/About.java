package com.yasser.memorizewords;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.yasser.memorizewords.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.BuildConfig;
import mehdi.sakout.aboutpage.Element;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        View aboutPage =new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.icon2).
                        setDescription("This app was developed by Yasser Rostom")
                .addItem(new Element("Version " + BuildConfig.VERSION_NAME, R.drawable.ic_baseline_info_24))
                .addGroup("GET IN TOUCH")
                .addEmail("yasser93rostom@gmail.com")
                .addFacebook("yasser93rostom","Find us on Facebook")


                .addPlayStore("com.yasser.memorizewords", "Rate on Google Play")

                .addGitHub("Yasser-Rostom")
                .create();
        setContentView(aboutPage);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {


            case R.id.close:

               finish();

                return true;



            default:
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_in_top);
    }
}