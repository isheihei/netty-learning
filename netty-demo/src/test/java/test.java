import cn.Myloader;
import cn.Student;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.LinkedList;

public class test {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //自定义类加载器的加载路径
        Myloader myClassLoader = new Myloader();
        //包名+类名
        Class c = myClassLoader.loadClass("cn.Student");

        if (c != null) {
            Object obj = c.newInstance();
            Method method = c.getMethod("say", null);
            method.invoke(obj, null);
            System.out.println(c.getClassLoader().toString());
//            Student student = new Student();
//            System.out.println(student.toString());
        }

        Student student = new Student();
        student.say();
        System.out.println(Student.class.getClassLoader().toString());

    }
}

