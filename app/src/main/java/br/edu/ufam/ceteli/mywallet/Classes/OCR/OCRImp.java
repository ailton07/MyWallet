package br.edu.ufam.ceteli.mywallet.Classes.OCR;

import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AiltonFH on 17/09/2015.
 *  // HP
 */
public class OCRImp {

    CommsEngine commsEngine;
    private  final  String idol_ocr_service = "https://api.idolondemand.com/1/api/async/ocrdocument/v1?";
    private  final  String idol_ocr_job_result = "https://api.idolondemand.com/1/job/result/";
    private String jobID = "";

    String mImageFullPathAndName = "";
    TextView estabelecimento, valor;

    ProgressBar pbOCRReconizing;

    String localImagePath = "";


    public OCRImp(CommsEngine commsEngine, TextView estabelecimento, TextView valor, String mImageFullPathAndName, ProgressBar pbOCRReconizing) {
        this.commsEngine = commsEngine;
        this.estabelecimento = estabelecimento;
        this.valor = valor;
        this.mImageFullPathAndName = mImageFullPathAndName;
        this.pbOCRReconizing = pbOCRReconizing;
    }

    public String getLocalImagePath() {
        return localImagePath;
    }

    public void setLocalImagePath(String localImagePath) {
        this.localImagePath = localImagePath;
    }

    public CommsEngine getCommsEngine() {
        return commsEngine;
    }

    public void setCommsEngine(CommsEngine commsEngine) {
        this.commsEngine = commsEngine;
    }

    public TextView getEstabelecimento() {
        return estabelecimento;
    }

    public void setEstabelecimento(TextView estabelecimento) {
        this.estabelecimento = estabelecimento;
    }

    public TextView getValor() {
        return valor;
    }

    public void setValor(TextView valor) {
        this.valor = valor;
    }

    public String getmImageFullPathAndName() {
        return mImageFullPathAndName;
    }

    public void setmImageFullPathAndName(String mImageFullPathAndName) {
        this.mImageFullPathAndName = mImageFullPathAndName;
    }

    public ProgressBar getPbOCRReconizing() {
        return pbOCRReconizing;
    }

    public void setPbOCRReconizing(ProgressBar pbOCRReconizing) {
        this.pbOCRReconizing = pbOCRReconizing;
    }

    public void DoStartOCR() {
        pbOCRReconizing.setVisibility(View.VISIBLE);
        if (jobID.length() > 0)
            getResultByJobId();
        else if (!mImageFullPathAndName.isEmpty()){
            Map<String,String> map =  new HashMap<String,String>();
            map.put("file", mImageFullPathAndName);
            //map.put("file", (picNameText.getText().toString()));
            String fileType = "image/jpeg";
            map.put("mode", "document_photo");
            commsEngine.ServicePostRequest(idol_ocr_service, fileType, map, new OnServerRequestCompleteListener() {
                @Override
                public void onServerRequestComplete(String response) {
                    try {
                        JSONObject mainObject = new JSONObject(response);
                        if (!mainObject.isNull("jobID")) {
                            jobID = mainObject.getString("jobID");
                            getResultByJobId();
                        } else
                            ParseSyncResponse(response);
                    } catch (Exception ex) {}
                }
                @Override
                public void onErrorOccurred(String error) {
                    // handle error
                }
            });
        } else
            //Toast.makeText(this, "Please select an image.", Toast.LENGTH_LONG).show();
            Log.d("OCR", "Se'lecione uma imagem");
    }

    private void getResultByJobId() {
        String param = idol_ocr_job_result + jobID + "?";
        commsEngine.ServiceGetRequest(param, "", new
                OnServerRequestCompleteListener() {
                    @Override
                    public void onServerRequestComplete(String response) {
                        ParseAsyncResponse(response);
                    }

                    @Override
                    public void onErrorOccurred(String error) {
                        // handle error
                    }
                });
    }


        public Bitmap decodeFile(File file) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 1;
        int mImageRealWidth = options.outWidth;
        int mImageRealHeight = options.outHeight;
        Bitmap pic = null;
        try {
            pic = BitmapFactory.decodeFile(file.getPath(), options);
        } catch (Exception ex) {
            Log.e("MainActivity", ex.getMessage());
        }
        return pic;
    }

    public Bitmap rescaleBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public String SaveImage(Bitmap image)
    {
        String fileName = localImagePath + "imagetoocr.jpg";
        try {

            File file = new File(fileName);
            FileOutputStream fileStream = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.JPEG, 100, fileStream);
            try {
                fileStream.flush();
                fileStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileName;
    }


    private void ParseSyncResponse(String response) {

        if (response == null) {
            //Toast.makeText(this, "Unknown error occurred. Try again", Toast.LENGTH_LONG).show();
            Log.d("OCR", "Erro");
            return;
        }
        try {
            JSONObject mainObject = new JSONObject(response);
            JSONArray textBlockArray = mainObject.getJSONArray("text_block");
            int count = textBlockArray.length();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    JSONObject texts = textBlockArray.getJSONObject(i);
                    String text = texts.getString("text");
                    // ivSelectedImg.setVisibility(View.GONE);
                    //  llResultContainer.setVisibility(View.VISIBLE);
                    OCRResposta reposta = new OCRResposta(text);
                    Log.v("ETSS", text);
                    estabelecimento.setText(reposta.getEmpresa());
                    valor.setText(reposta.getTotal());
                }
            }
            else
                //Toast.makeText(this, "Not available", Toast.LENGTH_LONG).show();
                Log.d("OCR", "Erro");
        } catch (Exception ex){}
    }
    private void ParseAsyncResponse(String response) {
        pbOCRReconizing.setVisibility(View.GONE);
        if (response == null) {
            //Toast.makeText(this, "Unknown error occurred. Try again", Toast.LENGTH_LONG).show();
            Log.d("OCR", "Erro");
            return;
        }
        try {
            JSONObject mainObject = new JSONObject(response);
            JSONArray textBlockArray = mainObject.getJSONArray("actions");
            int count = textBlockArray.length();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    JSONObject actions = textBlockArray.getJSONObject(i);
                    String action = actions.getString("action");
                    String status = actions.getString("status");
                    JSONObject result = actions.getJSONObject("result");
                    JSONArray textArray = result.getJSONArray("text_block");
                    count = textArray.length();
                    if (count > 0) {
                        for (int n = 0; n < count; n++) {
                            JSONObject texts = textArray.getJSONObject(n);
                            String text = texts.getString("text");
                            //    ivSelectedImg.setVisibility(View.GONE);
                            //llResultContainer.setVisibility(View.VISIBLE);
                            OCRResposta reposta = new OCRResposta(text);
                            //edTextResult.setText(text);
                            Log.v("ETSS", reposta.getEmpresa());
                            Log.v("ETSS", reposta.getTotal());
                            estabelecimento.setText(reposta.getEmpresa());
                            valor.setText(reposta.getTotal());

                        }
                    }
                }
            } else {
               // Toast.makeText(this, "Not available", Toast.LENGTH_LONG).show();
                Log.v("OCR", "Erro");
            }
        } catch (Exception ex) {
        }
    }
}
