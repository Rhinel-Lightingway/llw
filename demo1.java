package demo;

import java.util.*;
public class demo1 {
    /*
     






     */
    public static void main(String args[]){
        stu stud=new stu();
        stud.a="//";
        stud.a="/*";
        stud.a="*/";
        int totalLines = 0;         
        int commentLines = 0;       //comment
        if(totalLines!=0){ /*this is a comment */
            System.out.println("//This is a comment");
            System.out.println("//This is a comment");
        }
        else{
            System.out.println("/*This is not a comment*/");
        }
    }
    //define a class

    public static class stu{
        String a;
        public void isEven(){
            if(a=="//"||a=="/*"){
                System.out.println("//This is not a comment");
            }
            else{
                System.out.println("/*this is a comment?NO!");


            }
        }
    }
}