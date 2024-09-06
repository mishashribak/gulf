package Utility;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.khaleeji.R;


public class Fragment_Process {

    public static void addFragment(FragmentManager supportFragment, Fragment mFragment, Activity mActivity, int container) {
        FragmentTransaction transaction = supportFragment.beginTransaction();
        transaction.add(container, mFragment, mFragment.getClass().getSimpleName());
        transaction.addToBackStack(mFragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public static void addFragment(FragmentManager supportFragment, Fragment mFragment, Bundle bundle,  int container) {
        if (bundle != null) {
            mFragment.setArguments(bundle);
        }
        FragmentTransaction transaction = supportFragment.beginTransaction();
        transaction.add(container, mFragment, mFragment.getClass().getSimpleName());
        transaction.addToBackStack(mFragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public static void replaceFragment(FragmentManager supportFragment,Fragment fragment, Bundle bundle, int layout, boolean addToBackStack) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        FragmentTransaction transaction =supportFragment.beginTransaction();
//        transaction.replace(layout, fragment);
//        if (addToBackStack) {
//            transaction.addToBackStack(null);
//        }
//        transaction.commit();
        transaction.replace(layout, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public static void replaceFragment(FragmentManager supportFragment,Fragment fragment, Bundle bundle, int layout) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        FragmentTransaction transaction =supportFragment.beginTransaction();
        transaction.replace(layout, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }


    public static void replaceFragment(FragmentManager supportFragment, Fragment mFragment, Activity mActivity, int container) {
        FragmentTransaction transaction = supportFragment.beginTransaction();
        transaction.replace(container, mFragment, mFragment.getClass().getSimpleName());
        transaction.addToBackStack(mFragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }


    public static void removeFragment(FragmentManager supportFragment,String tag, Activity mActivity, int container) {


        supportFragment.popBackStack(tag,0);

        //trans.commitAllowingStateLoss();
    }


    public static void removeFragmentAnimation(FragmentManager supportFragment,String tag, Activity mActivity, int container) {


        FragmentManager manager = supportFragment;
        FragmentTransaction trans = manager.beginTransaction();
        Fragment mFragment=manager.findFragmentByTag(tag);

        //trans.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);

        trans.setCustomAnimations(R.anim.slide_out, R.anim.slide_in);

        trans.remove(mFragment).commitAllowingStateLoss();
        //manager.popBackStack(tag,0);

        //trans.commitAllowingStateLoss();
    }

    public static void swipeFragmentAnimation(FragmentManager supportFragment,String tag, Activity mActivity, int container) {

        FragmentManager manager = supportFragment;
        FragmentTransaction trans = manager.beginTransaction();
        Fragment mFragment=manager.findFragmentByTag(tag);

        //trans.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);

        trans.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_s_right_exit);

        trans.remove(mFragment).commitAllowingStateLoss();

        manager.popBackStack();

    //    manager.popBackStack(tag,0);

        //trans.commitAllowingStateLoss();
    }



    public static void replaceFragment_Withrighttoleft(FragmentManager supportFragment,Fragment mFragment, Activity mActivity, int container) {
        FragmentTransaction transaction = supportFragment.beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.slide_out);
        transaction.replace(container, mFragment, mFragment.getClass().getSimpleName());
        transaction.addToBackStack(mFragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }


    public static void replaceFragment_Withlefttoright(FragmentManager supportFragment,Fragment mFragment, Activity mActivity, int container) {
        FragmentTransaction transaction = supportFragment.beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                R.anim.fragment_s_right_exit);
        transaction.replace(container, mFragment, mFragment.getClass().getSimpleName());
        transaction.addToBackStack(mFragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }


    public static void replaceFragmentusingChildFragment(FragmentManager supportFragment,Fragment mFragment, Activity mActivity, int container, FragmentManager childFragmentManager) {
        FragmentTransaction transaction = childFragmentManager.beginTransaction();
        transaction.replace(container, mFragment, mFragment.getClass().getSimpleName());
        transaction.addToBackStack(mFragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public static void clearBackStack(FragmentManager supportFragment){
        supportFragment.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
