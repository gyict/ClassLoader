package gy.learn.clslder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gaoyuan on 17/7/3.
 */
public class MyFileClassLoader extends ClassLoader{
    private static String rooturl;
    public static void setRooturl(String url){
        rooturl = url;
    }
    //加载类， 如果类文件修改过加载，如果没有修改，返回当前的


    protected synchronized Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException
    {
        String fname = getFileName(name);
        // First, check if the class has already been loaded
        Class c = findLoadedClass(fname);
        if (c == null) {
            try {
                /**attention
                 * */
                if ( this!= null) {
                    c = super.loadClass(name, false);
                } else {
                    c = findClass(name);
                }
            } catch (ClassNotFoundException e) {
                // If still not found, then invoke findClass in order
                // to find the class.
                c = findClass(name);
            }
        }
        if (resolve) {
            resolveClass(c);
        }
        return c;
    }
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException{
        Class clz = null;
        String fname = getFileName(name);
        byte[] clsData = getClassData(fname);
        if(clsData == null){
            System.out.println("class not find");
            return null;
        }else {
            clz = defineClass(name,clsData,0,clsData.length);
        }
        return clz;
    }


    byte[] getClassData(String filename){
        FileInputStream fileInputStream;
        ByteArrayOutputStream bos=null;
        try{
            fileInputStream = new FileInputStream(filename);
            //System.out.println(rooturl+"/"+filename);
            bos = new ByteArrayOutputStream();
            int len = 0;
            try {
                while ((len = fileInputStream.read()) != -1) {
                    bos.write(len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return bos.toByteArray();
        }
    }

    //获取要加载 的class文件名
    private String getFileName(String name) {
        // TODO Auto-generated method stub
        return rooturl + "/" + name.replace(".", "/") + ".class";
    }


    protected URL findResource(String name) {
        try {
            URL url = super.findResource(name);
            if (url != null)
                 return url;
            url = new URL("file:///" + converName(name));
            //简化处理，所有资源从文件系统中获取
            return url;
        } catch (MalformedURLException mue) {
            return null;
        }
    }
    private String converName(String name) {
        StringBuffer sb = new StringBuffer(rooturl);
        name = name.replace('.', File.separatorChar);
        sb.append(File.separator + name);
        return sb.toString();
    }

}
