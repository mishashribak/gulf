package com.app.khaleeji.Activity;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import com.app.khaleeji.Fragments.SettingsFragment;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.ActivitySettingBinding;

import Utility.Fragment_Process;

public class SettingActivity extends BaseActivity {

    private ActivitySettingBinding mbinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        Fragment_Process.replaceFragment(getSupportFragmentManager(), SettingsFragment.newInstance(), this, mbinding.framelayoutMain.getId());
    }

    public void back(){
        int backstack_count = getSupportFragmentManager().getBackStackEntryCount();
        if (backstack_count <= 1) {
            finish();
        } else if (backstack_count > 1) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

}
