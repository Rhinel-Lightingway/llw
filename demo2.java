package demo;

public class demo2 {
    public static class student{/*Create */
        String name;
        int age;
        int score;//Define p
        /*demo code// */
        student(String n,int a,int s){
            name=n;age=a;score=s;
        }
        public void hello(){
            System.out.println(this.name+"is"+age+"years old and his score is"+score);
        }
    }
    public static void main(String[] args) {
        student stu=new student("llw", 1145, 150);
        stu.hello();
    }
}
