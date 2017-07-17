package gy.learn.clslder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Properties;



/**
 * Created by gaoyuan on 17/7/3.
 */
public class Entrance {
    //private static MyFileClassLoader myFileClassLoader = new MyFileClassLoader();
    private static ManageClassLoader manageClassLoader = new ManageClassLoader();
    
    public static void main(String[] args) {
        while (true){
            try{
                manageClassLoader.setRootUrl("/Users/gaoyuan/Desktop/Gaoy/Java/JavaLoaderHelper");
                //myFileClassLoader.setRooturl("/Users/gaoyuan/Desktop/Gaoy/Java/Ali/Proone/target/classes");
                String classname = "Output";
                Class cls = manageClassLoader.loadClass(classname);
                if(cls!=null){
                    System.out.println(cls.getClassLoader());
                    Method method = cls.getMethod("sayHello", String.class);
                    System.out.println(method.invoke(cls.newInstance(), "Minsheng"));

                    String file = classname+ ".properties";
                    InputStream in = new FileInputStream(new File(ManageClassLoader.rootUrl+"/"+file));
                    Properties prop = new Properties();
                    prop.load(in);

                    Enumeration pronames = prop.propertyNames();
                    while (pronames.hasMoreElements()){
                        String key = (String)pronames.nextElement();
                        String value = prop.getProperty(key);
                        //System.out.println(key+" "+value);
                    }

//                    Configuration config = new XMLConfiguration("com/styspace/config.xml");
//                    String name = config.getString("Account.name");
//                    System.out.println("name:" + name);

                    File xmlFile = new File(ManageClassLoader.rootUrl+"/"+classname+".xml");    // properties文件放在e盘下（windows）
                    FileInputStream pInStream = null;
                    try {
                        pInStream = new FileInputStream(xmlFile);
                        Properties p = new Properties();
                        p.loadFromXML(pInStream);
                        Enumeration enumeration = p.propertyNames();
                        while (enumeration.hasMoreElements()){
                            String key = (String) enumeration.nextElement();
                            String value = p.getProperty(key);
                            System.out.println(key+" "+value);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    System.out.println("null");
                }
                Thread.sleep(20*1000);
//
            }catch (Exception e){
                e.printStackTrace();
            }


        }

    }

}
