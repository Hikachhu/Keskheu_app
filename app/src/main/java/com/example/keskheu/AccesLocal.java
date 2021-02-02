package com.example.keskheu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;

import static java.lang.Math.toIntExact;

public class AccesLocal {
    private String nomBase="bdKeskheu.sqlite";
    private Integer versionBase=1;
    private MySQLiteOpenHelper accesBD;
    private SQLiteDatabase bd;

    public AccesLocal(Context context){
        accesBD=new MySQLiteOpenHelper(context,nomBase,null,versionBase);
        this.accesBD=accesBD;
        this.bd=bd;
    }

    public void ajout(@NotNull Question question){
        bd=accesBD.getWritableDatabase();
        String req = "insert into Questions(Id,Question) values ";
        req+="("+question.getId()+",\""+question.getQuestion()+"\")";
        bd.execSQL(req);
    }

    public Question rcmpDenied(){
        bd=accesBD.getReadableDatabase();
        Question question =null;
        String req ="select * from Questions";
        Cursor curseur = bd.rawQuery(req,null);
        curseur.moveToLast();
        if(!curseur.isAfterLast()){
            Integer Id=curseur.getInt(0);
            String vale_question=curseur.getString(1);
            question=new Question(vale_question,Id);
        }
        curseur.close();
        return question;
    }

    public String rcmpNumbers(Integer number){
        bd=accesBD.getReadableDatabase();
        Question question =null;
        String req ="select * from Questions";
        Cursor courser = bd.rawQuery(req,null);
        courser.move(number);
        if(!courser.isAfterLast()){
            int Id=courser.getInt(0);
            String valeur_question=courser.getString(1);
            question=new Question(valeur_question,Id);
        }
        courser.close();
        return question.getQuestion();
    }

    public int getNumber(){
        String countQuery = "SELECT  * FROM Questions";
        bd = accesBD.getReadableDatabase();
        Cursor cursor = bd.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
