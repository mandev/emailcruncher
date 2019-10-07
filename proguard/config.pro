-injars C:\dev\netbeans\EmailCruncher\dist\EmailCruncher.jar
-outjars C:\dev\netbeans\EmailCruncher\dist\EmailCruncher_out.jar

-libraryjars 'C:\Program Files\Java\jdk1.5.0_10\jre\lib\rt.jar'
-libraryjars C:\dev\netbeans\EmailCruncher\lib\launcher.jar
-libraryjars C:\dev\netbeans\EmailCruncher\lib\poi-3.0.jar
-libraryjars C:\dev\netbeans\EmailCruncher\lib\swingx.jar

-dontoptimize 
-dontskipnonpubliclibraryclasses
-overloadaggressively
-defaultpackage ''

-printusage C:\dev\netbeans\EmailCruncher\proguard\usage.txt
-printmapping C:\dev\netbeans\EmailCruncher\proguard\mapping.txt
-printseeds C:\dev\netbeans\EmailCruncher\proguard\seeds.txt

-keep public class com.adlitteram.emailcruncher.Main {
    public static void main(java.lang.String[]);
}

