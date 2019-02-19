package com.trainsystem.upperlimb.senior.handtrainsystem2.tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by percyku on 2017/2/3.
 */

public class ProgressDialong extends Activity{

    Context cont;
    ProgressDialog progressDialog;

    public ProgressDialong(Context cont){
        this.cont=cont;
    }


    public  void DialongShow(String str){

        progressDialog=new ProgressDialog(cont);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(str);
        progressDialog.show();

    }

    public void DialongCancel(){
        progressDialog.cancel();
    }
}
