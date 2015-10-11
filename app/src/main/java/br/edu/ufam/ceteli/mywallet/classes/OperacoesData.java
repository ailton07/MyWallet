package br.edu.ufam.ceteli.mywallet.classes;

/**
 * Created by AiltonFH on 23/09/2015.
 */
public class OperacoesData {

    // Formato da Data
    // yyyymmdd


    static String getDia(String data){

        if(data!=null){
            return(data.substring(6,8));
        }

        return null;
    }

    static int getDia(int data){
        String date;
        if(data!=0){
            date = getDia(String.valueOf(data));
            return Integer.valueOf(date);
        }
        return 0;
    }

    static String getMes(String data){

        if(data!=null){
            return(data.substring(4,6));
        }

        return null;
    }

    static int getMes(int data){
        String date;
        if(data!=0){
            date = getMes(String.valueOf(data));
            return Integer.valueOf(date);
        }
        return 0;
    }

    static String getAno(String data){

        if(data!=null){
            return(data.substring(0,4));
        }

        return null;
    }

    static int getAno(int data){
        String date;
        if(data!=0){
            date = getAno(String.valueOf(data));
            return Integer.valueOf(date);
        }
        return 0;
    }

    static String getDataFormatada(String data){

        if(data!=null){
            return(getDia(data)+"/"+getMes(data)+"/"+getAno(data));
        }

        return null;
    }

    static String getDataFormatada(int data){
        String date= String.valueOf(data);
        if(data!=0){
            //date = (String.valueOf(getDia(data))+"/"+String.valueOf(getMes(data)) + "/" + String.valueOf(getAno(data)));
            return ((getDia(date))+"/"+(getMes(date)) + "/" + (getAno(date)));
            //return date;

        }

        return null;
    }



}
