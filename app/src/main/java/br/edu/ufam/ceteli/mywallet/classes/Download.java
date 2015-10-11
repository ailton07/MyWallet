package br.edu.ufam.ceteli.mywallet.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ListView;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by AiltonFH on 13/09/2015.
 */
public class Download {

    ProgressDialog dialog;
    Future<File> downloading;

    AdapterListView adapter;
    ListView listView;
    Context context;


    public Download(AdapterListView adapter, ListView listView, Context context) {
        this.adapter = adapter;
        this.listView = listView;
        this.context = context;
    }

    public Download(Context context) {

        this.context = context;
    }



    public String hash(String email){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(email.getBytes());

            byte[] hash = md.digest();
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                if ((0xff & hash[i]) < 0x10) {
                    hexString.append("0"
                            + Integer.toHexString((0xFF & hash[i])));
                } else {
                    hexString.append(Integer.toHexString(0xFF & hash[i]));
                }
            }
            String saida = hexString.toString();

            return saida;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void baixa(String email){

        Context applicationContext = context;

        Log.d("download", "Inicio");

        Log.d("download", hash(email));

        downloading = Ion.with(applicationContext)
                //.load("http://192.168.2.104/uploads/"+hash(email))
                .load("http://158.69.194.250:8080/uploads/" + hash(email))
                .progressDialog(dialog = ProgressDialog.show(applicationContext, " ", "Baixando arquivo...", true))
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        //System.out.println("" + downloaded + " / " + total);
                        Log.d("download", String.valueOf(downloaded));
                    }
                })
                .write(new File("/data/data/br.edu.ufam.ceteli.mywallet/databases/teste.db"))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        // download done...
                        // do stuff with the File or error
                        dialog.dismiss();
                        Log.d("download", "Fimmmm");

                        if((adapter!=null)&(listView!=null)) {
                            restartArryaListAdapter();
                        }

                    }
                });




    }

    public void restartArryaListAdapter(){

        List<Entrada> values = Entrada.getComments();

        adapter = new AdapterListView(context, values);
        listView.setAdapter(adapter);

    }

}
