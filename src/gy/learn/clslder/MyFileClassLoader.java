package gy.learn.clslder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by gaoyuan on 17/7/3.
 */
public class MyFileClassLoader extends ClassLoader{
    private static String rooturl;
    public static void setRooturl(String url){
        rooturl = url;
    }
    private Map<String, byte[]> map = new HashMap<String,byte[]>(64);;
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
        this.preReadJarFile();
        String fname = getFileName(name);
        byte[] clsData;

        if(map.containsKey(fname)){
            clsData = map.get(fname);
        }
        else{
            clsData = getClassData(fname);
        }
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

    /**
     * 读取一个jar包内的class文件，并存在当前加载器的map中
     * @param jar
     * @throws IOException
     */
    public void readJAR(JarFile jar) throws IOException{
        Enumeration<JarEntry> en = jar.entries();
        while (en.hasMoreElements()){
            JarEntry je = en.nextElement();
            String name = je.getName();
            if (name.endsWith(".class")){
                String clss = name.replace(".class", "").replaceAll("/", ".");
                if(this.findLoadedClass(clss) != null) continue;

                InputStream input = jar.getInputStream(je);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int bufferSize = 4096;
                byte[] buffer = new byte[bufferSize];
                int bytesNumRead = 0;
                while ((bytesNumRead = input.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesNumRead);
                }
                byte[] cc = baos.toByteArray();
                input.close();

                String rclss = clss.replace(".", "/");
                //System.out.println(rooturl+"/"+jar.getName()+clss);
                String aftername = rooturl+"/"+rclss+".class";
                //System.out.println("mapclassname:"+aftername);
                map.put(aftername, cc);//暂时保存下来
            }
        }
    }

    public void preReadJarFile(){
        List<File> list = scanDir();
        for(File f : list){
            JarFile jar;
            try {
                jar = new JarFile(f);
                readJAR(jar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 扫描lib下面的所有jar包
     * @return
     */
    private List<File> scanDir() {
        List<File> list = new ArrayList<File>();
        File[] files = new File(rooturl).listFiles();
        for (File f : files) {
            if (f.isFile() && f.getName().endsWith(".jar")){
                list.add(f);
                //System.out.println(f.getName());
            }

        }
        // System.out.println(list.size());
        return list;
    }

    /**
     * 添加一个jar包到加载器中去。
     * @param jarPath
     * @throws IOException
     */
    public void addJar(String jarPath) throws IOException{
        File file = new File(jarPath);
        if(file.exists()){
            JarFile jar = new JarFile(file);
            readJAR(jar);
        }
    }


}
