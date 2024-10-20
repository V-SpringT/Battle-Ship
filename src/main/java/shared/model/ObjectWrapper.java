/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.model;

import java.io.Serializable;
 
public class ObjectWrapper  implements Serializable{
    public static final int LOGIN_USER = 1;
    public static final int REPLY_LOGIN_USER = 2;

    // just test main form    
    public static final int REPLY_MAIN_USER = 3;
//    public static final int EDIT_CUSTOMER = 3;
//    public static final int SEARCH_CUSTOMER_BY_NAME = 5;
//    public static final int REPLY_SEARCH_CUSTOMER = 6;
    public static final int SERVER_INFORM_CLIENT_NUMBER = 7;
    
    
     
    private int performative;
    private Object data;
    public ObjectWrapper() {
        super();
    }
    public ObjectWrapper(int performative, Object data) {
        super();
        this.performative = performative;
        this.data = data;
    }
    public int getPerformative() {
        return performative;
    }
    public void setPerformative(int performative) {
        this.performative = performative;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }   

    @Override
    public String toString() {
        return "ObjectWrapper{" + "performative=" + performative + ", data=" + data + '}';
    }
}
