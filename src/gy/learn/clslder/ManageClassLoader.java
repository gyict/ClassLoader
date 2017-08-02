package gy.learn.clslder;

import java.io.File;
import java.io.IOException;

/**
 * Created by gaoyuan on 17/7/4.
 */
public class ManageClassLoader {
    private MyFileClassLoader myFileClassLoader = new MyFileClassLoader();
    public static String rootUrl ;
    Long lastModified = 0l;
    Class c = null;
    public static void setRootUrl(String url){
        rootUrl = url;
        MyFileClassLoader.setRooturl(url);
    }


    //加载类， 如果类文件修改过加载，如果没有修改，返回当前的
    public Class loadClass(String name) throws ClassNotFoundException, IOException {

        if (isClassModified(name)){
            myFileClassLoader =  new MyFileClassLoader();

            return c = myFileClassLoader.findClass(name);
        }
        return c;
    }

    //判断是否被修改过
    private boolean isClassModified(String filename) {
        boolean returnValue = false;
        String filename2=filename.replace('.','/');
        File file=new File(rootUrl+'/'+filename2+".class");
        // System.out.println(file.getAbsolutePath());

        if (file.exists())
        {
            if (file.lastModified() > lastModified) {
                lastModified = file.lastModified();
                returnValue = true;
            }
        }else{
            return true;
        }


        // System.out.println(file.lastModified()+" "+lastModified);

        return returnValue;
    }



}
