/**
 * Created by hwu on 2018-01-24.
 */

public class MyEquals {


    String name;

    int n;


    public boolean equals(Object obj){
        // if compare to itself
        if(obj==this){
            return true;
        }

        if(!(obj instanceof MyEquals)){
            return false;
        }


        MyEquals m=(MyEquals)obj;


        if(!name.equals(m.name)){
            return false;
        }


        if(!(n == m.n)){
            return false;
        }


        return true;

    }



}
